package com.vitaliyrev.courseworkdb.app.db;

import com.vitaliyrev.courseworkdb.app.Accident;
import com.vitaliyrev.courseworkdb.app.Brigade;
import com.vitaliyrev.courseworkdb.app.Client;
import com.vitaliyrev.courseworkdb.app.Master;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
	public final Connection connection;

	public DatabaseManager(String url, String user, String password) throws SQLException {
		this.connection = DriverManager.getConnection(url, user, password);
		initializeDatabase();
	}

	private void initializeDatabase() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			// Create tables if not exists
			stmt.execute("CREATE TABLE IF NOT EXISTS brigades (number INTEGER PRIMARY KEY)");

			stmt.execute("CREATE TABLE IF NOT EXISTS masters (" +
					"id SERIAL PRIMARY KEY, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"experience INTEGER, " +
					"status VARCHAR(255), " +
					"brigade_number INTEGER REFERENCES brigades(number), " +
					"department_number INTEGER)");

			stmt.execute("CREATE TABLE IF NOT EXISTS accidents (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) UNIQUE, " +
					"address VARCHAR(255), " +
					"type VARCHAR(255), " +
					"cost DOUBLE PRECISION, " +
					"date DATE, " +
					"brigade_number INTEGER REFERENCES brigades(number), " +
					"status VARCHAR(255))");

			stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) REFERENCES accidents(accident_id), " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"department_number INTEGER)");
		}
	}

	// Load data from database
	public List<Brigade> loadBrigades() throws SQLException {
		List<Brigade> brigades = new ArrayList<>();
		try (Statement stmt = connection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT number FROM brigades")) {
			while (rs.next()) {
				brigades.add(new Brigade(rs.getInt("number")));
			}
		}
		return brigades;
	}

	public List<Master> loadMasters(Map<Integer, Brigade> brigadeMap) throws SQLException {
		List<Master> masters = new ArrayList<>();
		try (Statement stmt = connection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM masters")) {
			while (rs.next()) {
				int brigadeNumber = rs.getInt("brigade_number");
				Brigade brigade = brigadeMap.get(brigadeNumber);
				if (brigade == null) {
					brigade = new Brigade(brigadeNumber);
					brigadeMap.put(brigadeNumber, brigade);
				}
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

	public List<Accident> loadAccidents(Map<Integer, Brigade> brigadeMap) throws SQLException {
		List<Accident> accidents = new ArrayList<>();
		try (Statement stmt = connection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT * FROM accidents")) {
			while (rs.next()) {
				int brigadeNumber = rs.getInt("brigade_number");
				Brigade brigade = brigadeMap.get(brigadeNumber);
				if (brigade == null) {
					brigade = new Brigade(brigadeNumber);
					brigadeMap.put(brigadeNumber, brigade);
				}
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
				accidents.add(accident);
			}
		}
		return accidents;
	}

	public List<Client> loadClients() throws SQLException {
		List<Client> clients = new ArrayList<>();
		try (Statement stmt = connection.createStatement();
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

	// Save data to database
	public void saveBrigades(Set<Integer> brigadeNumbers) throws SQLException {
		String sql = "INSERT INTO brigades (number) VALUES (?) ON CONFLICT (number) DO NOTHING";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			for (Integer number : brigadeNumbers) {
				pstmt.setInt(1, number);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		}
	}

	public void saveMasters(List<Master> masters) throws SQLException {
		String sql = "INSERT INTO masters (id, name, address, phone_number, experience, status, brigade_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"name=EXCLUDED.name, address=EXCLUDED.address, phone_number=EXCLUDED.phone_number, " +
				"experience=EXCLUDED.experience, status=EXCLUDED.status, brigade_number=EXCLUDED.brigade_number, " +
				"department_number=EXCLUDED.department_number";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

	public void saveAccidents(List<Accident> accidents) throws SQLException {
		String sql = "INSERT INTO accidents (id, accident_id, address, type, cost, date, brigade_number, status) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"accident_id=EXCLUDED.accident_id, address=EXCLUDED.address, type=EXCLUDED.type, " +
				"cost=EXCLUDED.cost, date=EXCLUDED.date, brigade_number=EXCLUDED.brigade_number, status=EXCLUDED.status";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

	public void saveClients(List<Client> clients) throws SQLException {
		String sql = "INSERT INTO clients (id, accident_id, name, address, phone_number, department_number) " +
				"VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET " +
				"accident_id=EXCLUDED.accident_id, name=EXCLUDED.name, address=EXCLUDED.address, " +
				"phone_number=EXCLUDED.phone_number, department_number=EXCLUDED.department_number";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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

	public void close() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
}