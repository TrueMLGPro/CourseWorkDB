package com.vitaliyrev.courseworkdb.app.db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerTestBase {
	protected Connection testConnection;

	@BeforeEach
	void setupTestDatabase() throws Exception {
		String url = "jdbc:postgresql://localhost:5432/course_work_test_db";
		String user = "course_work_test_db_admin";
		String password = "courseworktestdb123";
		testConnection = DriverManager.getConnection(url, user, password);
		testConnection.setAutoCommit(false);

		initializeSchema(testConnection);
	}

	protected void initializeSchema(Connection conn) throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			// 1. Create brigades table first
			stmt.execute("CREATE TABLE IF NOT EXISTS brigades (number INTEGER PRIMARY KEY)");

			// 2. Create accidents table with ON DELETE CASCADE
			stmt.execute("CREATE TABLE IF NOT EXISTS accidents (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) UNIQUE, " +
					"address VARCHAR(255), " +
					"type VARCHAR(255), " +
					"cost DOUBLE PRECISION, " +
					"date DATE, " +
					"brigade_number INTEGER REFERENCES brigades(number) ON DELETE CASCADE, " +
					"status VARCHAR(255))");

			// 3. Create clients table with ON DELETE CASCADE
			stmt.execute("CREATE TABLE IF NOT EXISTS clients (" +
					"id SERIAL PRIMARY KEY, " +
					"accident_id VARCHAR(255) REFERENCES accidents(accident_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
					"name VARCHAR(255), " +
					"address VARCHAR(255), " +
					"phone_number VARCHAR(255), " +
					"department_number INTEGER)");

			// 4. Add client reference to accidents table with ON DELETE CASCADE
			stmt.execute("ALTER TABLE accidents ADD COLUMN IF NOT EXISTS client_id INTEGER");
			stmt.execute("ALTER TABLE accidents DROP CONSTRAINT IF EXISTS accidents_client_id_fkey");
			stmt.execute("ALTER TABLE accidents ADD CONSTRAINT accidents_client_id_fkey " +
					"FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED");

			// 5. Create masters table
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
		conn.commit();
	}

	@AfterEach
	void cleanTestDatabase() throws SQLException {
		if (testConnection != null) {
			testConnection.rollback(); // Rollback any changes (keeps schema but removes test data)
			testConnection.close();
		}
	}
}
