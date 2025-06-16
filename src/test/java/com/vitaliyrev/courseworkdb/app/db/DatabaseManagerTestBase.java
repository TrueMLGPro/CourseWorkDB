package com.vitaliyrev.courseworkdb.app.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerTestBase {
	protected HikariDataSource dataSource;
	protected Connection testConnection;

	@BeforeEach
	void setupTestDatabase() throws Exception {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl("jdbc:postgresql://localhost:5432/course_work_test_db");
		config.setUsername("course_work_test_db_admin");
		config.setPassword("courseworktestdb123");
		config.setMaximumPoolSize(100);
		config.setConnectionTimeout(1000);
		config.setIdleTimeout(600000);
		dataSource = new HikariDataSource(config);

		testConnection = dataSource.getConnection();
		initializeSchema(testConnection);
	}

	protected void initializeSchema(Connection conn) throws SQLException {
		try (Statement stmt = conn.createStatement()) {
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

	@AfterEach
	void cleanTestDatabase() throws SQLException {
		if (testConnection != null) {
			testConnection.close();
			testConnection = null;
		}
		if (dataSource != null) {
			dataSource.close();
			dataSource = null;
		}
	}
}
