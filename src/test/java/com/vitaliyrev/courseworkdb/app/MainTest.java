package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;
import com.vitaliyrev.courseworkdb.app.db.DatabaseManagerTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest extends DatabaseManagerTestBase {
	private Main main;

	@BeforeEach
	void setUp() throws SQLException {
		// Create real database manager with test connection
		DatabaseManager dbManager = new DatabaseManager(
			System.getProperty("db.url"),
			System.getProperty("db.user"),
			System.getProperty("db.password")
		);
		main = new Main(dbManager);

		// Initialize database schema
		dbManager.initializeDatabase();

		// Start transaction for tests that need it
		testConnection.setAutoCommit(false);
	}

	@AfterEach
	void tearDown() throws SQLException {
		if (testConnection != null && !testConnection.isClosed()) {
			testConnection.rollback();
			testConnection.setAutoCommit(true);
			try (Statement stmt = testConnection.createStatement()) {
				stmt.execute("TRUNCATE TABLE clients, accidents, masters, brigades CASCADE");
			} finally {
				testConnection.setAutoCommit(false);
			}
		}
	}

	private Brigade createTestBrigade(int number) {
		return new Brigade(number);
	}

	private Master createTestMaster(int id, Brigade brigade) {
		return new Master(id, "Мастер" + id, "Адреса" + id, "098765432" + id,
				5, "Зайнятий", brigade, 1);
	}

	private Client createTestClient(int id, String accidentId, int dept) {
		return new Client(id, accidentId, "Клієнт" + id,
				"АдресаКлієнта" + id, "098765432" + id, dept);
	}

	private Accident createTestAccident(int id, String accidentId, Brigade brigade, Date date) {
		return new Accident(id, accidentId, "АдресаАварії" + id,
				"Тип" + id, id * 100.0, date, brigade, "У процесі");
	}

	@Test
	void shouldSaveAllDataWithMultipleEntries() throws SQLException {
		// Arrange
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1),
				createTestMaster(2, brigade1),
				createTestMaster(3, brigade2)
		);

		long now = System.currentTimeMillis();
		Date accidentDate1 = new Date(now - 5000);
		Date accidentDate2 = new Date(now - 4000);

		List<Accident> accidents = List.of(
				createTestAccident(1, "1-ACC", brigade1, accidentDate1),
				createTestAccident(2, "2-ACC", brigade2, accidentDate2)
		);

		List<Client> clients = List.of(
				createTestClient(1, "1-ACC", 5),
				createTestClient(2, "2-ACC", 5)
		);

		main.setMasters(masters);
		main.setClients(clients);
		main.setAccidents(accidents);

		// Act
		main.saveAllData();
		testConnection.commit(); // Commit to make data visible

		// Assert
		verifyTableCount("brigades", 2);
		verifyTableCount("masters", 3);
		verifyTableCount("accidents", 2);
		verifyTableCount("clients", 2);

		try (Statement stmt = testConnection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT number FROM brigades ORDER BY number")) {
			assertTrue(rs.next());
			assertEquals(1, rs.getInt("number"));
			assertTrue(rs.next());
			assertEquals(2, rs.getInt("number"));
			assertFalse(rs.next());
		}

		try (PreparedStatement ps = testConnection.prepareStatement(
				"SELECT c.id, a.accident_id FROM clients c JOIN accidents a ON c.accident_id = a.accident_id")) {
			ResultSet rs = ps.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
				int clientId = rs.getInt("id");
				String accidentId = rs.getString("accident_id");
				assertEquals(clientId + "-ACC", accidentId);
			}
			assertEquals(2, count);
		}
	}

	@Test
	void shouldHandleEmptyLists() throws SQLException {
		// Arrange
		main.setMasters(new ArrayList<>());
		main.setClients(new ArrayList<>());
		main.setAccidents(new ArrayList<>());

		// Act
		main.saveAllData();
		testConnection.commit(); // Commit to make data visible

		// Assert
		verifyTableCount("brigades", 0);
		verifyTableCount("masters", 0);
		verifyTableCount("accidents", 0);
		verifyTableCount("clients", 0);
	}

	@Test
	void shouldCalculateMasterSalaryCorrectly() {
		// Arrange
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1), // Qualification 1
				createTestMaster(2, brigade2)  // Qualification 2
		);

		Date now = new Date();
		Date accidentDate1 = new Date(now.getTime() - 5000);
		Date accidentDate2 = new Date(now.getTime() - 4000);
		Date accidentDate3 = new Date(now.getTime() - 3000);

		List<Accident> accidents = List.of(
				createTestAccident(1, "1-ACC", brigade1, accidentDate1),
				createTestAccident(2, "2-ACC", brigade1, accidentDate2),
				createTestAccident(3, "3-ACC", brigade2, accidentDate3)
		);

		masters.get(0).setBrigade(brigade1);
		masters.get(1).setBrigade(brigade2);

		// Set accident costs
		accidents.get(0).setCost(150.0);
		accidents.get(1).setCost(250.0);
		accidents.get(2).setCost(350.0);

		main.setMasters(masters);
		main.setAccidents(accidents);

		Date startDate = new Date(now.getTime() - 10000);
		Date endDate = new Date(now.getTime());

		// Act & Assert
		double salary1 = main.getMasterSalaryBetweenDates(1, startDate, endDate);
		assertEquals(2400.0, salary1, 0.01);

		double salary2 = main.getMasterSalaryBetweenDates(2, startDate, endDate);
		assertEquals(1350.0, salary2, 0.01);
	}

	@Test
	void shouldCountBrigadesCorrectly() {
		// Arrange
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1),
				createTestMaster(2, brigade1),
				createTestMaster(3, brigade2)
		);

		main.setMasters(masters);

		// Act & Assert
		assertEquals(2, main.getBrigadeCount());
	}

	@Test
	void shouldCalculateAverageAccidentCost() {
		// Arrange
		Brigade brigade = createTestBrigade(1);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);
		Date accidentTime3 = new Date(now.getTime() - 400000);

		List<Accident> accidents = List.of(
				createTestAccident(1, "1-ACC", brigade, accidentTime1),
				createTestAccident(2, "2-ACC", brigade, accidentTime2),
				createTestAccident(3, "3-ACC", brigade, accidentTime3)
		);

		accidents.get(0).setCost(100.0);
		accidents.get(1).setCost(200.0);
		accidents.get(2).setCost(300.0);

		main.setAccidents(accidents);

		// Act & Assert
		assertEquals(200.0, main.getAverageAccidentCost(), 0.01);
	}

	@Test
	void shouldHandleNullValuesGracefully() {
		// Arrange
		main.setMasters(null);
		main.setClients(null);
		main.setAccidents(null);

		// Act & Assert
		assertDoesNotThrow(() -> main.saveAllData());
		assertEquals(0, main.getBrigadeCount());
		assertEquals(0.0, main.getAverageAccidentCost(), 0.01);
	}

	@Test
	void shouldCalculateSalaryForDateRange() {
		// Arrange
		Brigade brigade = createTestBrigade(1);
		Master master = createTestMaster(1, brigade); // Qualification 1

		Date now = new Date();
		Date past = new Date(now.getTime() - 1000000);

		Date relAccidentTime = new Date(now.getTime() - 500000);
		Date unrelAccidentTime = new Date(now.getTime() - 1500000);

		Accident relevantAccident = createTestAccident(1, "1-ACC", brigade, relAccidentTime);
		relevantAccident.setCost(500.0);

		Accident irrelevantAccident = createTestAccident(2, "2-ACC", brigade, unrelAccidentTime);
		irrelevantAccident.setCost(500.0);

		main.setMasters(List.of(master));
		main.setAccidents(List.of(relevantAccident, irrelevantAccident));

		// Act
		double salary = main.getMasterSalaryBetweenDates(1, past, now);

		// Assert - Should only include one accident
		assertEquals(1500.0, salary, 0.01);
	}

	private void verifyTableCount(String tableName, int expectedCount) throws SQLException {
		// Ensure we can see committed data
		testConnection.setAutoCommit(true);
		try (Statement stmt = testConnection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
			assertTrue(rs.next(), "No results for table: " + tableName);
			assertEquals(expectedCount, rs.getInt(1), "Count mismatch in table: " + tableName);
			assertFalse(rs.next());
		} finally {
			// Reset to transaction mode for other tests
			testConnection.setAutoCommit(false);
		}
	}
}