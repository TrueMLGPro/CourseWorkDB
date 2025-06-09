package com.vitaliyrev.courseworkdb.app.db;

import com.vitaliyrev.courseworkdb.app.Accident;
import com.vitaliyrev.courseworkdb.app.Brigade;
import com.vitaliyrev.courseworkdb.app.Client;
import com.vitaliyrev.courseworkdb.app.Master;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

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
			// 1. Створити таблицю brigades
			stmt.execute("CREATE TABLE IF NOT EXISTS brigades (number INTEGER PRIMARY KEY)");

			// 2. Створити таблицю accidents (без посилання на клієнтів)
			stmt.execute("CREATE TABLE IF NOT EXISTS accidents (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) UNIQUE, " +
					"address VARCHAR(255), " +
					"type VARCHAR(255), " +
					"cost DOUBLE PRECISION, " +
					"date DATE, " +
					"brigade_number INTEGER REFERENCES brigades(number) ON DELETE CASCADE, " +
					"status VARCHAR(255))");

			// 3. Створити таблицю clients (з посиланням на аварію через accident_id)
			stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) UNIQUE REFERENCES accidents(accident_id) ON DELETE CASCADE, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"department_number INTEGER)");

			// 4. Створити таблицю masters
			stmt.execute("CREATE TABLE IF NOT EXISTS masters (" +
					"id SERIAL PRIMARY KEY, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"experience INTEGER, " +
					"status VARCHAR(255), " +
					"brigade_number INTEGER REFERENCES brigades(number) ON DELETE CASCADE, " +
					"department_number INTEGER)");
		}
	}

	// Завантаження номерів бригад з БД
	public List<Brigade> loadBrigades() throws SQLException {
		List<Brigade> brigades = new ArrayList<>();
		try (Statement stmt = this.getConnection().createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT number FROM brigades")) {
			while (rs.next()) {
				brigades.add(new Brigade(rs.getInt("number")));
			}
		}
		return brigades;
	}

	// Завантаження мастерів з БД
	public List<Master> loadMasters(Map<Integer, Brigade> brigadeMap) throws SQLException {
		List<Master> masters = new ArrayList<>();
		try (Statement stmt = this.getConnection().createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM masters")) {
			while (rs.next()) {
				int brigadeNumber = rs.getInt("brigade_number");
				Brigade brigade = brigadeMap.computeIfAbsent(brigadeNumber, Brigade::new);
				Master master = new Master(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("address"),
						rs.getString("phone_number"),
						rs.getInt("experience"),
						rs.getString("status"),
						brigade,
						rs.getInt("department_number")
				);
				brigade.addMaster(master);
				masters.add(master);
			}
		}
		return masters;
	}

	// Завантаження аварій з БД
	public List<Accident> loadAccidents(Map<Integer, Brigade> brigadeMap, Map<String, Client> clientMap) throws SQLException {
		List<Accident> accidents = new ArrayList<>();
		try (Statement stmt = this.getConnection().createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM accidents")) {
			while (rs.next()) {
				int brigadeNumber = rs.getInt("brigade_number");
				Brigade brigade = brigadeMap.computeIfAbsent(brigadeNumber, Brigade::new);

				Accident accident = new Accident(
						rs.getInt("id"),
						rs.getString("accident_id"),
						rs.getString("address"),
						rs.getString("type"),
						rs.getDouble("cost"),
						rs.getDate("date"),
						brigade,
						rs.getString("status")
				);

				// Link client using accident_id
				String accidentId = accident.getAccidentId();
				if (accidentId != null && clientMap.containsKey(accidentId)) {
					Client client = clientMap.get(accidentId);
					accident.setClient(client);
					client.setAccident(accident);
				}

				accidents.add(accident);
			}
		}
		return accidents;
	}

	// Завантаження клієнтів з БД
	public List<Client> loadClients() throws SQLException {
		List<Client> clients = new ArrayList<>();
		try (Statement stmt = this.getConnection().createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
			while (rs.next()) {
				clients.add(new Client(
						rs.getInt("id"),
						rs.getString("accident_id"),
						rs.getString("name"),
						rs.getString("address"),
						rs.getString("phone_number"),
						rs.getInt("department_number")
				));
			}
		}
		return clients;
	}

	// Збереження даних бригад до БД
	public void saveBrigades(Set<Integer> brigadeNumbers) throws SQLException {
		String sql = "INSERT INTO brigades (number) VALUES (?) ON CONFLICT (number) DO NOTHING";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Integer number : brigadeNumbers) {
				pstmt.setInt(1, number);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	// Збереження даних мастерів до БД
	public void saveMasters(List<Master> masters) throws SQLException {
		String sql = "INSERT INTO masters (id, name, address, phone_number, experience, status, brigade_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, phone_number=EXCLUDED.phone_number, " +
				"experience=EXCLUDED.experience, status=EXCLUDED.status, brigade_number=EXCLUDED.brigade_number, " +
				"department_number=EXCLUDED.department_number";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Master master : masters) {
				pstmt.setInt(1, master.getIndex());
				pstmt.setString(2, master.getName());
				pstmt.setString(3, master.getAddress());
				pstmt.setString(4, master.getPhoneNumber());
				pstmt.setInt(5, master.getExperience());
				pstmt.setString(6, master.getStatus());
				pstmt.setInt(7, master.getBrigade().getNumber());
				pstmt.setInt(8, master.getDepartmentNumber());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	// Збереження даних аварій до БД
	public void saveAccidents(List<Accident> accidents) throws SQLException {
		String sql = "INSERT INTO accidents (id, accident_id, address, type, cost, date, brigade_number, status) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
				"ON CONFLICT (id) DO UPDATE SET " +
				"accident_id = EXCLUDED.accident_id, address = EXCLUDED.address, " +
				"type = EXCLUDED.type, cost = EXCLUDED.cost, date = EXCLUDED.date, " +
				"brigade_number = EXCLUDED.brigade_number, status = EXCLUDED.status";

		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Accident accident : accidents) {
				pstmt.setInt(1, accident.getIndex());
				pstmt.setString(2, accident.getAccidentId());
				pstmt.setString(3, accident.getAddress());
				pstmt.setString(4, accident.getType());
				pstmt.setDouble(5, accident.getCost());
				pstmt.setDate(6, new java.sql.Date(accident.getDate().getTime()));
				pstmt.setInt(7, accident.getBrigade().getNumber());
				pstmt.setString(8, accident.getStatus());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	// Збереження даних клієнтів до БД
	public void saveClients(List<Client> clients) throws SQLException {
		String sql = "INSERT INTO clients (id, accident_id, name, address, phone_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"accident_id=EXCLUDED.accident_id, name=EXCLUDED.name, address=EXCLUDED.address, " +
				"phone_number=EXCLUDED.phone_number, department_number=EXCLUDED.department_number";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			for (Client client : clients) {
				pstmt.setInt(1, client.getIndex());
				pstmt.setString(2, client.getAccidentId());
				pstmt.setString(3, client.getName());
				pstmt.setString(4, client.getAddress());
				pstmt.setString(5, client.getPhoneNumber());
				pstmt.setInt(6, client.getDepartmentNumber());
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	public void deleteMaster(int masterId) throws SQLException {
		String sql = "DELETE FROM masters WHERE id = ?";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			pstmt.setInt(1, masterId);
			pstmt.executeUpdate();
		}
	}

	public void deleteAccident(int accidentIndex) throws SQLException {
		String sql = "DELETE FROM accidents WHERE id = ?";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			pstmt.setInt(1, accidentIndex);
			pstmt.executeUpdate();
		}
	}

	public void deleteClient(int clientId) throws SQLException {
		String sql = "DELETE FROM clients WHERE id = ?";
		try (PreparedStatement pstmt = this.getConnection().prepareStatement(sql)) {
			pstmt.setInt(1, clientId);
			pstmt.executeUpdate();
		}
	}

	// Закриття зв'язку з БД
	public void close() throws SQLException {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}