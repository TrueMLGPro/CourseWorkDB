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
