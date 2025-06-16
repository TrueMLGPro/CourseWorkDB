package com.vitaliyrev.courseworkdb.app.db;

import com.vitaliyrev.courseworkdb.app.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

// Клас "Менеджер БД"
public class DatabaseManager {
	private final HikariDataSource dataSource;

	public DatabaseManager(String url, String user, String password) throws SQLException {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);
		config.setMaximumPoolSize(250);
		config.setConnectionTimeout(1000);
		config.setIdleTimeout(600000);
		this.dataSource = new HikariDataSource(config);

		initializeDatabase();
		populateStatusTables();
	}

	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	public boolean isConnectionValid() {
		try (Connection conn = this.getConnection()) {
			return conn.isValid(3);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Ініціалізація БД; створення таблиць у базі, якщо їх не існує
	public void initializeDatabase() throws SQLException {
		try (Statement stmt = this.getConnection().createStatement()) {
			// 1. Створити таблицю existing_brigades
			stmt.execute("CREATE TABLE IF NOT EXISTS existing_brigades (number INTEGER PRIMARY KEY)");

			// 2. Створити таблицю master_statuses
			stmt.execute("CREATE TABLE IF NOT EXISTS master_statuses (" +
					"id SERIAL PRIMARY KEY, " +
					"status VARCHAR(255) UNIQUE)");

			// 3. Створити таблицю accident_types
			stmt.execute("CREATE TABLE IF NOT EXISTS accident_types (" +
					"id SERIAL PRIMARY KEY, " +
					"type VARCHAR(255) UNIQUE)");

			// 4. Створити таблицю accident_statuses
			stmt.execute("CREATE TABLE IF NOT EXISTS accident_statuses (" +
					"id SERIAL PRIMARY KEY, " +
					"status VARCHAR(255) UNIQUE)");

			// 5. Створити таблицю clients
			stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
					"id SERIAL PRIMARY KEY, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255) UNIQUE, " +
					"department_number INTEGER)");

			// 6. Створити таблицю masters
			stmt.execute("CREATE TABLE IF NOT EXISTS masters (" +
					"id SERIAL PRIMARY KEY, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"experience INTEGER, " +
					"status_id INTEGER REFERENCES master_statuses(id), " +
					"brigade_number INTEGER REFERENCES existing_brigades(number), " +
					"department_number INTEGER)");

			// 7. Створити таблицю accidents
			stmt.execute("CREATE TABLE IF NOT EXISTS accidents (" +
					"id SERIAL PRIMARY KEY, " +
					"address VARCHAR(255), " +
					"type_id INTEGER REFERENCES accident_types(id), " +
					"cost DOUBLE PRECISION, " +
					"date DATE, " +
					"brigade_number INTEGER REFERENCES existing_brigades(number), " +
					"status_id INTEGER REFERENCES accident_statuses(id), " +
					"client_phone_number VARCHAR(255) REFERENCES clients(phone_number), " +
					"accident_index INTEGER, " +
					"UNIQUE (client_phone_number, accident_index))");
		}
	}

	private Map<Integer, Brigade> getBrigadeMap() throws SQLException {
		List<Brigade> dbBrigades = loadFromDatabase("SELECT number FROM existing_brigades", rs -> new Brigade(rs.getInt("number")));
		return dbBrigades.stream().collect(Collectors.toMap(Brigade::getNumber, b -> b));
	}
	// Завантаження мастерів з БД
	public List<Master> loadMasters() throws SQLException {
		Map<Integer, Brigade> brigadeMap = getBrigadeMap();
		return loadFromDatabase("SELECT * FROM masters", rs -> mapRowToMaster(rs, brigadeMap));
	}

	// Завантаження аварій з БД
	public List<Accident> loadAccidents() throws SQLException {
		Map<Integer, Brigade> brigadeMap = getBrigadeMap();
		return loadFromDatabase("SELECT * FROM accidents", rs -> mapRowToAccident(rs, brigadeMap));
	}

	// Завантаження клієнтів з БД
	public List<Client> loadClients() throws SQLException {
		return loadFromDatabase("SELECT * FROM clients", this::mapRowToClient);
	}

	public int getNextId(String tableName) throws SQLException {
		String sql = "SELECT MAX(id) FROM " + tableName;
		Integer maxId = executeScalarQuery(sql, rs -> rs.getInt(1));
		return (maxId == null ? 0 : maxId) + 1;
	}
	public int getNextAccidentIndexForClient(String phoneNumber) throws SQLException {
		String sql = "SELECT MAX(accident_index) FROM accidents WHERE client_phone_number = ?";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, phoneNumber);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) + 1;
				}
				return 1;
			}
		}
	}

	public void saveMaster(Master master) throws SQLException {
		// Ensure brigade exists
		saveExistingBrigade(master.getBrigade().getNumber());

		String sql = "INSERT INTO masters (id, name, address, phone_number, experience, status_id, brigade_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, phone_number=EXCLUDED.phone_number, " +
				"experience=EXCLUDED.experience, status_id=EXCLUDED.status_id, " +
				"brigade_number=EXCLUDED.brigade_number, department_number=EXCLUDED.department_number";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			int statusId = getStatusId(master.getStatus());
			pstmt.setInt(1, master.getIndex());
			pstmt.setString(2, master.getName());
			pstmt.setString(3, master.getAddress());
			pstmt.setString(4, master.getPhoneNumber());
			pstmt.setInt(5, master.getExperience());
			pstmt.setInt(6, statusId);
			pstmt.setInt(7, master.getBrigade().getNumber());
			pstmt.setInt(8, master.getDepartmentNumber());
			pstmt.executeUpdate();
		}
	}


	public void saveAccident(Accident accident) throws SQLException {
		saveExistingBrigade(accident.getBrigade().getNumber());

		String sql = "INSERT INTO accidents (id, address, type_id, cost, date, brigade_number, status_id, client_phone_number, accident_index) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"address=EXCLUDED.address, type_id=EXCLUDED.type_id, cost=EXCLUDED.cost, " +
				"date=EXCLUDED.date, brigade_number=EXCLUDED.brigade_number, " +
				"status_id=EXCLUDED.status_id, client_phone_number=EXCLUDED.client_phone_number, " +
				"accident_index=EXCLUDED.accident_index";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			int typeId = getTypeId(accident.getType());
			int statusId = getAccidentStatusId(accident.getStatus());
			pstmt.setInt(1, accident.getIndex());
			pstmt.setString(2, accident.getAddress());
			pstmt.setInt(3, typeId);
			pstmt.setDouble(4, accident.getCost());
			pstmt.setDate(5, new java.sql.Date(accident.getDate().getTime()));
			pstmt.setInt(6, accident.getBrigade().getNumber());
			pstmt.setInt(7, statusId);
			pstmt.setString(8, accident.getClientPhoneNumber());
			pstmt.setInt(9, accident.getAccidentIndex());
			pstmt.executeUpdate();
		}
	}

	public void saveClient(Client client) throws SQLException {
		String sql = "INSERT INTO clients (id, name, address, phone_number, department_number)" +
				"VALUES (?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, " +
				"phone_number=EXCLUDED.phone_number, department_number=EXCLUDED.department_number";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, client.getIndex());
			pstmt.setString(2, client.getName());
			pstmt.setString(3, client.getAddress());
			pstmt.setString(4, client.getPhoneNumber());
			pstmt.setInt(5, client.getDepartmentNumber());
			pstmt.executeUpdate();
		}
	}

	public void saveExistingBrigade(int brigadeNumber) throws SQLException {
		String sql = "INSERT INTO existing_brigades (number) VALUES (?) ON CONFLICT (number) DO NOTHING";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, brigadeNumber);
			pstmt.executeUpdate();
		}
	}

	public Master getMasterByIndex(int index) throws SQLException {
		Map<Integer, Brigade> brigadeMap = getBrigadeMap();
		return executeParamQuery("SELECT * FROM masters WHERE id = ?", index, rs -> mapRowToMaster(rs, brigadeMap));
	}

	public Master getMasterByName(String name) throws SQLException {
		Map<Integer, Brigade> brigadeMap = getBrigadeMap();
		return executeParamQuery("SELECT * FROM masters WHERE name = ?", name, rs -> mapRowToMaster(rs, brigadeMap));
	}

	public Client getClientByName(String name) throws SQLException {
		return executeParamQuery("SELECT * FROM clients WHERE name = ?", name, this::mapRowToClient);
	}

	public List<String> getClientPhoneNumbers() {
		String sql = "SELECT phone_number FROM clients";
		return executeQuery(sql, rs -> {
			List<String> phoneNumbers = new ArrayList<>();
			while (rs.next()) {
				phoneNumbers.add(rs.getString("phone_number"));
			}
			return phoneNumbers;
		});
	}

	public Accident getAccidentByPhoneNumAccIndex(String phoneNumber, int accidentIndex) throws SQLException {
		Map<Integer, Brigade> brigadeMap = getBrigadeMap();
		String sql = "SELECT * FROM accidents WHERE client_phone_number = ? AND accident_index = ?";
		return executeParamQuery(sql, phoneNumber, accidentIndex, rs -> mapRowToAccident(rs, brigadeMap));
	}

	public List<Integer> getAccidentIndicesForClient(String phoneNumber) throws SQLException {
		String sql = "SELECT accident_index FROM accidents WHERE client_phone_number = ? ORDER BY accident_index";
		try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, phoneNumber);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Integer> indices = new ArrayList<>();
				while (rs.next()) {
					indices.add(rs.getInt(1));
				}
				return indices;
			}
		}
	}

	public double getMasterSalaryBetweenDates(int masterIndex, Date startDate, Date endDate) throws SQLException {
		double experienceBonusPerYear = 200.0;
		Master master = getMasterByIndex(masterIndex);
		if (master == null) return 0.0;
		double experienceBonus = master.getExperience() * experienceBonusPerYear;

		String sql = "SELECT SUM(cost) FROM accidents " +
				"WHERE brigade_number = ? AND date BETWEEN ? AND ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, master.getBrigade().getNumber());
			pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
			pstmt.setDate(3, new java.sql.Date(endDate.getTime()));

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					double totalCost = rs.getDouble(1);
					String countSql = "SELECT COUNT(*) FROM accidents WHERE brigade_number = ? AND date BETWEEN ? AND ?";
					try (PreparedStatement countPstmt = conn.prepareStatement(countSql)) {
						countPstmt.setInt(1, master.getBrigade().getNumber());
						countPstmt.setDate(2, new java.sql.Date(startDate.getTime()));
						countPstmt.setDate(3, new java.sql.Date(endDate.getTime()));
						try (ResultSet countRs = countPstmt.executeQuery()) {
							if (countRs.next()) {
								int accidentCount = countRs.getInt(1);
								return totalCost + (accidentCount * experienceBonus);
							}
						}
					}
				}
				return 0.0;
			}
		}
	}

	public int getBrigadeCount() throws SQLException {
		return executeScalarQuery("SELECT COUNT(*) FROM existing_brigades", rs -> rs.getInt(1));
	}

	public int getAccidentCountBetweenDates(Date startDate, Date endDate) throws SQLException {
		String sql = "SELECT COUNT(*) FROM accidents WHERE date BETWEEN ? AND ?";
		return executeParamQuery(sql, new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()), rs -> rs.getInt(1));
	}

	public double getAverageAccidentCost() throws SQLException {
		return executeScalarQuery("SELECT AVG(cost) FROM accidents", rs -> rs.getDouble(1));
	}
	public Set<String> getExistingAccidentTypes() throws SQLException {
		String sql = "SELECT DISTINCT t.type FROM accident_types t JOIN accidents a ON t.id = a.type_id";
		return new HashSet<>(executeQuery(sql, rs -> {
			List<String> types = new ArrayList<>();
			while (rs.next()) {
				types.add(rs.getString(1));
			}
			return types;
		}));
	}
	public List<String> getUsedBrigadeNumbers() throws SQLException {
		String sql = "SELECT number FROM existing_brigades ORDER BY number";
		return executeQuery(sql, rs -> {
			List<String> numbers = new ArrayList<>();
			while (rs.next()) {
				numbers.add(String.valueOf(rs.getInt(1)));
			}
			return numbers;
		});
	}
	public List<String> getUsedDepartmentNumbers() throws SQLException {
		String sql = "SELECT DISTINCT department_number FROM masters ORDER BY department_number";
		return executeQuery(sql, rs -> {
			List<String> numbers = new ArrayList<>();
			while (rs.next()) {
				numbers.add(String.valueOf(rs.getInt(1)));
			}
			return numbers;
		});
	}

	private Master mapRowToMaster(ResultSet rs, Map<Integer, Brigade> brigadeMap) throws SQLException {
		int brigadeNumber = rs.getInt("brigade_number");
		Brigade brigade = brigadeMap.computeIfAbsent(brigadeNumber, Brigade::new);

		int statusId = rs.getInt("status_id");
		String status = getMasterStatusById(statusId);

		Master master = new Master(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("address"),
				rs.getString("phone_number"),
				rs.getInt("experience"),
				status,
				brigade,
				rs.getInt("department_number")
		);
		brigade.addMaster(master);
		return master;
	}

	private Accident mapRowToAccident(ResultSet rs, Map<Integer, Brigade> brigadeMap) throws SQLException {
		int brigadeNumber = rs.getInt("brigade_number");
		Brigade brigade = brigadeMap.computeIfAbsent(brigadeNumber, Brigade::new);

		int typeId = rs.getInt("type_id");
		String type = getAccidentTypeById(typeId);

		int statusId = rs.getInt("status_id");
		String status = getAccidentStatusById(statusId);

		return new Accident(
				rs.getInt("id"),
				rs.getString("address"),
				type,
				rs.getDouble("cost"),
				rs.getDate("date"),
				brigade,
				status,
				rs.getString("client_phone_number"),
				rs.getInt("accident_index")
		);
	}

	private Client mapRowToClient(ResultSet rs) throws SQLException {
		return new Client(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("address"),
				rs.getString("phone_number"),
				rs.getInt("department_number")
		);
	}

	public void deleteMaster(int masterId) throws SQLException {
		deleteFromDatabase("DELETE FROM masters WHERE id = ?", masterId);
	}

	public void deleteAccident(int accidentId) throws SQLException {
		deleteFromDatabase("DELETE FROM accidents WHERE id = ?", accidentId);
	}

	public void deleteClient(int clientId) throws SQLException {
		Client client = executeParamQuery("SELECT * from clients WHERE id = ?", clientId, this::mapRowToClient);
		if (client != null) {
			deleteFromDatabase("DELETE FROM accidents WHERE client_phone_number = ?", client.getPhoneNumber());
			deleteFromDatabase("DELETE FROM clients WHERE id = ?", clientId);
		}
	}

	private int getStatusId(String status) throws SQLException {
		return executeParamQuery("SELECT id FROM master_statuses WHERE status = ?", status, rs -> rs.getInt("id"));
	}

	private int getTypeId(String type) throws SQLException {
		return executeParamQuery("SELECT id FROM accident_types WHERE type = ?", type, rs -> rs.getInt("id"));
	}

	private int getAccidentStatusId(String status) throws SQLException {
		return executeParamQuery("SELECT id FROM accident_statuses WHERE status = ?", status, rs -> rs.getInt("id"));
	}

	private String getMasterStatusById(int statusId) throws SQLException {
		return executeParamQuery("SELECT status FROM master_statuses WHERE id =?", statusId, rs -> rs.getString("status"));
	}

	private String getAccidentTypeById(int typeId) throws SQLException {
		return executeParamQuery("SELECT type FROM accident_types WHERE id =?", typeId, rs -> rs.getString("type"));
	}

	private String getAccidentStatusById(int statusId) throws SQLException {
		return executeParamQuery("SELECT status FROM accident_statuses WHERE id =?", statusId, rs -> rs.getString("status"));
	}

	private <T> List<T> loadFromDatabase(String query, ResultSetMapper<T> mapper) throws SQLException {
		List<T> result = new ArrayList<>();
		try (Statement stmt = this.getConnection().createStatement();
		     ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				result.add(mapper.map(rs));
			}
		}
		return result;
	}

	private void deleteFromDatabase(String query, Object id) throws SQLException {
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setObject(1, id);
			pstmt.executeUpdate();
		}
	}

	@FunctionalInterface
	interface ResultSetMapper<T> {
		T map(ResultSet rsm) throws SQLException;
	}

	@FunctionalInterface
	public interface ResultSetProcessor<T> {
		T process(ResultSet rs) throws SQLException;
	}

	public <T> T executeQuery(String sql, ResultSetProcessor<T> processor) {
		try (Connection conn = getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
			return processor.process(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T executeParamQuery(String sql, Object param, ResultSetProcessor<T> processor) {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, param);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return processor.process(rs);
				} else {
					return null;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T executeParamQuery(String sql, Object param1, Object param2, ResultSetProcessor<T> processor) {
		try (Connection conn = getConnection();
		     PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, param1);
			stmt.setObject(2, param2);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return processor.process(rs);
				}
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> T executeScalarQuery(String sql, ResultSetProcessor<T> processor) {
		try (Connection conn = getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return processor.process(rs);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Integer> executeIdListQuery(String sql) {
		return executeQuery(sql, rs -> {
			List<Integer> result = new ArrayList<>();
			while (rs.next()) {
				result.add(rs.getInt(1));
			}
			return result;
		});
	}

	public <T> T executeResilientQuery(Supplier<T> querySupplier, Main main) {
		try {
			if (!isConnectionValid()) {
				main.reconnect();

				if (Main.userInterface != null) {
					Main.userInterface.updateConnectionStatus();
				}
			}
			return querySupplier.get();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void populateStatusTables() throws SQLException {
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement masterStmt = conn.prepareStatement("INSERT INTO master_statuses (status) VALUES (?) ON CONFLICT (status) DO NOTHING");
			     PreparedStatement accidentTypeStmt = conn.prepareStatement("INSERT INTO accident_types (type) VALUES (?) ON CONFLICT (type) DO NOTHING");
			     PreparedStatement accidentStatusStmt = conn.prepareStatement("INSERT INTO accident_statuses (status) VALUES (?) ON CONFLICT (status) DO NOTHING")) {

				for (String status : Main.getMasterStatuses()) {
					masterStmt.setString(1, status);
					masterStmt.addBatch();
				}
				masterStmt.executeBatch();

				for (String type : Main.getAccidentTypes()) {
					accidentTypeStmt.setString(1, type);
					accidentTypeStmt.addBatch();
				}
				accidentTypeStmt.executeBatch();

				for (String status : Main.getAccidentStatuses()) {
					accidentStatusStmt.setString(1, status);
					accidentStatusStmt.addBatch();
				}
				accidentStatusStmt.executeBatch();
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}
		}
	}


	// Закриття зв'язку з БД
	public void close() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}