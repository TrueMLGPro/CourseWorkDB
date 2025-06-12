package com.vitaliyrev.courseworkdb.app.db;

import com.vitaliyrev.courseworkdb.app.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
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

	// Завантаження використовуваних номерів бригад з БД
	public List<Brigade> loadExistingBrigades() throws SQLException {
		return loadFromDatabase("SELECT number FROM existing_brigades", rs -> new Brigade(rs.getInt("number")));
	}

	// Завантаження мастерів з БД
	public List<Master> loadMasters(Map<Integer, Brigade> brigadeMap) throws SQLException {
		return loadFromDatabase("SELECT * FROM masters", rs -> {
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
		});
	}

	// Завантаження аварій з БД
	public List<Accident> loadAccidents(Map<Integer, Brigade> brigadeMap) throws SQLException {
		return loadFromDatabase("SELECT * FROM accidents", rs -> {
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
		});
	}

	// Завантаження клієнтів з БД
	public List<Client> loadClients() throws SQLException {
		return loadFromDatabase("SELECT * FROM clients", rs -> new Client(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("address"),
				rs.getString("phone_number"),
				rs.getInt("department_number")
		));
	}

	// Збереження даних бригад до БД
	public void saveExistingBrigades(Set<Integer> brigadeNumbers) throws SQLException {
		saveToDatabase("INSERT INTO existing_brigades (number) VALUES (?) ON CONFLICT (number) DO NOTHING",
				brigadeNumbers.stream().map(Brigade::new).collect(Collectors.toList()),
				(pstmt, brigade) -> pstmt.setInt(1, brigade.getNumber()));
	}

	// Збереження даних мастерів до БД
	public void saveMasters(List<Master> masters) throws SQLException {
		String sql = "INSERT INTO masters (id, name, address, phone_number, experience, status_id, brigade_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, phone_number=EXCLUDED.phone_number, " +
				"experience=EXCLUDED.experience, status_id=EXCLUDED.status_id, " +
				"brigade_number=EXCLUDED.brigade_number, department_number=EXCLUDED.department_number";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Master master : masters) {
				int statusId = getStatusId(master.getStatus());
				pstmt.setInt(1, master.getIndex());
				pstmt.setString(2, master.getName());
				pstmt.setString(3, master.getAddress());
				pstmt.setString(4, master.getPhoneNumber());
				pstmt.setInt(5, master.getExperience());
				pstmt.setInt(6, statusId);
				pstmt.setInt(7, master.getBrigade().getNumber());
				pstmt.setInt(8, master.getDepartmentNumber());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	// Збереження даних аварій до БД
	public void saveAccidents(List<Accident> accidents) throws SQLException {
		String sql = "INSERT INTO accidents (id, address, type_id, cost, date, brigade_number, status_id, client_phone_number, accident_index) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"address=EXCLUDED.address, type_id=EXCLUDED.type_id, cost=EXCLUDED.cost, " +
				"date=EXCLUDED.date, brigade_number=EXCLUDED.brigade_number, " +
				"status_id=EXCLUDED.status_id, client_phone_number=EXCLUDED.client_phone_number, " +
				"accident_index=EXCLUDED.accident_index";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Accident accident : accidents) {
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
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	// Збереження даних клієнтів до БД
	public void saveClients(List<Client> clients) throws SQLException {
		saveToDatabase("INSERT INTO clients (id, name, address, phone_number, department_number)" +
				"VALUES (?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, " +
				"phone_number=EXCLUDED.phone_number, department_number=EXCLUDED.department_number",
				clients, (pstmt, client) -> {
			pstmt.setInt(1, client.getIndex());
			pstmt.setString(2, client.getName());
			pstmt.setString(3, client.getAddress());
			pstmt.setString(4, client.getPhoneNumber());
			pstmt.setInt(5, client.getDepartmentNumber());
		});
	}

	public void deleteMaster(int masterId) throws SQLException {
		deleteFromDatabase("DELETE FROM masters WHERE id = ?", masterId);
	}

	public void deleteAccident(int accidentId) throws SQLException {
		deleteFromDatabase("DELETE FROM accidents WHERE id = ?", accidentId);
	}

	public void deleteClient(int clientId) throws SQLException {
		deleteFromDatabase("DELETE FROM clients WHERE id = ?", clientId);
	}

	// Отримання ідентифікатора статусу майстра
	private int getStatusId(String status) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT id FROM master_statuses WHERE status = ?")) {
			pstmt.setString(1, status);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("id");
				} else {
					throw new SQLException("Статус майстра не знайдено");
				}
			}
		}
	}

	// Отримання ідентифікатора типу аварії
	private int getTypeId(String type) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT id FROM accident_types WHERE type = ?")) {
			pstmt.setString(1, type);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("id");
				} else {
					throw new SQLException("Тип аварії не знайдено");
				}
			}
		}
	}

	// Отримання ідентифікатора статусу аварії
	private int getAccidentStatusId(String status) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT id FROM accident_statuses WHERE status = ?")) {
			pstmt.setString(1, status);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("id");
				} else {
					throw new SQLException("Статус аварії не знайдено");
				}
			}
		}
	}

	// Отримання статусу майстра за ідентифікатором
	private String getMasterStatusById(int statusId) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT status FROM master_statuses WHERE id =?")) {
			pstmt.setInt(1, statusId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("status");
				} else {
					throw new SQLException("Статус майстра не знайдено");
				}
			}
		}
	}

	// Отримання типу аварії за ідентифікатором
	private String getAccidentTypeById(int typeId) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT type FROM accident_types WHERE id =?")) {
			pstmt.setInt(1, typeId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("type");
				} else {
					throw new SQLException("Тип аварії не знайдено");
				}
			}
		}
	}

	// Отримання статусу аварії за ідентифікатором
	private String getAccidentStatusById(int statusId) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT status FROM accident_statuses WHERE id =?")) {
			pstmt.setInt(1, statusId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("status");
				} else {
					throw new SQLException("Статус аварії не знайдено");
				}
			}
		}
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

	private <T> void saveToDatabase(String query, List<T> objects, PreparedStatementMapper<T> mapper) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(query)) {
			for (T object : objects) {
				mapper.map(pstmt, object);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	private void deleteFromDatabase(String query, int id) throws SQLException {
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}

	@FunctionalInterface
	interface ResultSetMapper<T> {
		T map(ResultSet rsm) throws SQLException;
	}

	@FunctionalInterface
	interface PreparedStatementMapper<T> {
		void map(PreparedStatement pstmt, T object) throws SQLException;
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
				return processor.process(rs);
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
		try (PreparedStatement masterStmt = this.getConnection().prepareStatement("INSERT INTO master_statuses (status) VALUES (?) ON CONFLICT (status) DO NOTHING");
		     PreparedStatement accidentTypeStmt = this.getConnection().prepareStatement("INSERT INTO accident_types (type) VALUES (?) ON CONFLICT (type) DO NOTHING");
		     PreparedStatement accidentStatusStmt = this.getConnection().prepareStatement("INSERT INTO accident_statuses (status) VALUES (?) ON CONFLICT (status) DO NOTHING")) {

			// Master statuses
			for (String status : Main.getMasterStatuses()) {
				masterStmt.setString(1, status);
				masterStmt.addBatch();
			}
			masterStmt.executeBatch();

			// Accident types
			for (String type : Main.getAccidentTypes()) {
				accidentTypeStmt.setString(1, type);
				accidentTypeStmt.addBatch();
			}
			accidentTypeStmt.executeBatch();

			// Accident statuses
			for (String status : Main.getAccidentStatuses()) {
				accidentStatusStmt.setString(1, status);
				accidentStatusStmt.addBatch();
			}
			accidentStatusStmt.executeBatch();
		}
	}

	// Закриття зв'язку з БД
	public void close() throws SQLException {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}