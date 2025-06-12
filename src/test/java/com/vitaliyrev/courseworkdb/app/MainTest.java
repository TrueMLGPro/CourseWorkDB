package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;
import com.vitaliyrev.courseworkdb.app.db.DatabaseManagerTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

class MainTest extends DatabaseManagerTestBase {
	private Main main;
	private DatabaseManager dbManager;

	@BeforeEach
	void setUp() throws SQLException {
		dbManager = new DatabaseManager(
				System.getProperty("db.url"),
				System.getProperty("db.user"),
				System.getProperty("db.password")
		);
		main = new Main(dbManager);
		dbManager.initializeDatabase();

		Brigade brigade2 = createTestBrigade(2);
		Master master = createTestMaster(999, brigade2);
		main.getMasters().add(master);
	}

	@AfterEach
	void tearDown() throws SQLException {
		if (testConnection != null &&!testConnection.isClosed()) {
			try (Statement stmt = testConnection.createStatement()) {
				stmt.execute("TRUNCATE TABLE clients, accidents, masters, existing_brigades CASCADE");
			}
		}
		dbManager.close();
	}

	private Brigade createTestBrigade(int number) {
		return new Brigade(number);
	}

	private Master createTestMaster(int id, Brigade brigade) {
		return new Master(id, "Майстер" + id, "Адреса" + id, "098765432" + id,
				5, "Зайнятий", brigade, 1);
	}

	private Client createTestClient(int id, int dept) {
		return new Client(id, "Клієнт" + id,
				"АдресаКлієнта" + id, "098765432" + id, dept);
	}

	private Accident createTestAccident(int id, Brigade brigade, Date date) {
		return new Accident(id, "АдресаАварії" + id,
				"Крах крана", id * 100.0, date, brigade, "У процесі", "098765432" + id, id);
	}

	@Test
	void shouldSaveAllDataWithMultipleEntries() throws SQLException {
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
				createTestAccident(1, brigade1, accidentDate1),
				createTestAccident(2, brigade2, accidentDate2)
		);

		List<Client> clients = List.of(
				createTestClient(1, 5),
				createTestClient(2, 5)
		);

		main.setMasters(masters);
		main.setClients(clients);
		main.setAccidents(accidents);

		main.saveAllData();

		verifyTableCount("existing_brigades", 2);
		verifyTableCount("masters", 3);
		verifyTableCount("accidents", 2);
		verifyTableCount("clients", 2);

		try (Statement stmt = testConnection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT number FROM existing_brigades ORDER BY number")) {
			assertTrue(rs.next());
			assertEquals(1, rs.getInt("number"));
			assertTrue(rs.next());
			assertEquals(2, rs.getInt("number"));
			assertFalse(rs.next());
		}

		try (PreparedStatement ps = testConnection.prepareStatement(
				"SELECT c.id, a.id AS accident_id_from_accidents " +
						"FROM clients c JOIN accidents a ON c.phone_number = a.client_phone_number")) {
			ResultSet rs = ps.executeQuery();
			int count = 0;
			while (rs.next()) {
				count++;
			}
			assertEquals(2, count);
		}
	}

	@Test
	void shouldHandleEmptyLists() throws SQLException {
		main.setMasters(new ArrayList<>());
		main.setClients(new ArrayList<>());
		main.setAccidents(new ArrayList<>());

		main.saveAllData();

		verifyTableCount("existing_brigades", 0);
		verifyTableCount("masters", 0);
		verifyTableCount("accidents", 0);
		verifyTableCount("clients", 0);
	}

	@Test
	void shouldCalculateMasterSalaryCorrectly() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1),
				createTestMaster(2, brigade2)
		);

		Date now = new Date();
		Date accidentDate1 = new Date(now.getTime() - 5000);
		Date accidentDate2 = new Date(now.getTime() - 4000);
		Date accidentDate3 = new Date(now.getTime() - 3000);

		List<Accident> accidents = List.of(
				createTestAccident(1, brigade1, accidentDate1),
				createTestAccident(2, brigade1, accidentDate2),
				createTestAccident(3, brigade2, accidentDate3)
		);

		List<Client> clients = List.of(
				createTestClient(1, 5),
				createTestClient(2, 5),
				createTestClient(3, 5)
		);

		masters.get(0).setBrigade(brigade1);
		masters.get(1).setBrigade(brigade2);

		// Set accident costs
		accidents.get(0).setCost(150.0);
		accidents.get(1).setCost(250.0);
		accidents.get(2).setCost(350.0);

		main.setMasters(masters);
		main.setAccidents(accidents);
		main.setClients(clients);

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
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1),
				createTestMaster(2, brigade1),
				createTestMaster(3, brigade2)
		);

		main.setMasters(masters);

		assertEquals(2, main.getBrigadeCount());
	}

	@Test
	void shouldCalculateAverageAccidentCost() {
		Brigade brigade = createTestBrigade(1);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);
		Date accidentTime3 = new Date(now.getTime() - 400000);

		List<Accident> accidents = List.of(
				createTestAccident(1, brigade, accidentTime1),
				createTestAccident(2, brigade, accidentTime2),
				createTestAccident(3, brigade, accidentTime3)
		);

		List<Client> clients = List.of(
				createTestClient(1, 5),
				createTestClient(2, 5),
				createTestClient(3, 5)
		);

		accidents.get(0).setCost(100.0);
		accidents.get(1).setCost(200.0);
		accidents.get(2).setCost(300.0);

		main.setAccidents(accidents);
		main.setClients(clients);

		assertEquals(200.0, main.getAverageAccidentCost(), 0.01);
	}

	@Test
	void shouldHandleNullValuesGracefully() {
		main.setMasters(null);
		main.setClients(null);
		main.setAccidents(null);

		assertDoesNotThrow(() -> main.saveAllData());
		assertEquals(0, main.getBrigadeCount());
		assertEquals(0.0, main.getAverageAccidentCost(), 0.01);
	}

	@Test
	void shouldCalculateSalaryForDateRange() {
		Brigade brigade = createTestBrigade(1);
		Master master = createTestMaster(1, brigade);

		Date now = new Date();
		Date past = new Date(now.getTime() - 1000000);

		Date relAccidentTime = new Date(now.getTime() - 500000);
		Date unrelAccidentTime = new Date(now.getTime() - 1500000);

		Accident relevantAccident = createTestAccident(1, brigade, relAccidentTime);
		relevantAccident.setCost(500.0);

		Accident irrelevantAccident = createTestAccident(2, brigade, unrelAccidentTime);
		irrelevantAccident.setCost(500.0);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);

		main.setMasters(List.of(master));
		main.setAccidents(List.of(relevantAccident, irrelevantAccident));
		main.setClients(List.of(client1, client2));

		double salary = main.getMasterSalaryBetweenDates(1, past, now);

		assertEquals(1500.0, salary, 0.01);
	}

	@Test
	void testGetMastersWithBrigadeInfo() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		List<Master> masters = List.of(
				createTestMaster(1, brigade1),
				createTestMaster(2, brigade1),
				createTestMaster(3, brigade2)
		);

		main.setMasters(masters);
		main.saveAllData();

		String result = main.getMastersWithBrigadeInfo();

		assertNotNull(result);
		assertTrue(result.contains("Майстер1 | Бригада: 1 | Статус: Зайнятий"));
		assertTrue(result.contains("Майстер2 | Бригада: 1 | Статус: Зайнятий"));
		assertTrue(result.contains("Майстер3 | Бригада: 2 | Статус: Зайнятий"));
	}

	@Test
	void testFindClientsByNamePattern() {
		Brigade brigade = createTestBrigade(1);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade, accidentTime2);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);

		List<Accident> accidents = List.of(accident1, accident2);
		List<Client> clients = List.of(client1, client2);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		String result = main.findClientsByNamePattern("Клієнт1");

		assertNotNull(result);
		assertTrue(result.contains("Клієнт1 | Тел: 0987654321 | Відділ: 5"));
	}

	@Test
	void testGetAccidentsBetweenDates() {
		Brigade brigade = createTestBrigade(1);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade, accidentTime2);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);

		List<Accident> accidents = List.of(accident1, accident2);
		List<Client> clients = List.of(client1, client2);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		String result = main.getAccidentsBetweenDates(accidentTime1, now);

		assertNotNull(result);
		assertTrue(result.contains("АдресаАварії1 | 100,00 грн | " + main.getDateFormat().format(accidentTime1)));
		assertTrue(result.contains("АдресаАварії2 | 200,00 грн | " + main.getDateFormat().format(accidentTime2)));
	}

	@Test
	void testGetAccidentCountLastWeek() {
		Brigade brigade = createTestBrigade(1);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade, accidentTime2);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);

		List<Accident> accidents = List.of(accident1, accident2);
		List<Client> clients = List.of(client1, client2);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		int result = main.getAccidentCountLastWeek();

		assertEquals(2, result);
	}

	@Test
	void testGetAccidentCountPerBrigade() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade1, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade1, accidentTime2);
		Accident accident3 = createTestAccident(3, brigade2, accidentTime1);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);
		Client client3 = createTestClient(3, 5);

		List<Accident> accidents = List.of(accident1, accident2, accident3);
		List<Client> clients = List.of(client1, client2, client3);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		Map<Integer, Integer> result = main.getAccidentCountPerBrigade();

		assertNotNull(result);
		assertEquals(2, (int) result.get(1));
		assertEquals(1, (int) result.get(2));
	}

	@Test
	void testGetBrigadesWithMaxAccidents() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade1, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade1, accidentTime2);
		Accident accident3 = createTestAccident(3, brigade2, accidentTime1);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);
		Client client3 = createTestClient(3, 5);

		List<Accident> accidents = List.of(accident1, accident2, accident3);
		List<Client> clients = List.of(client1, client2, client3);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		List<Integer> result = main.getBrigadesWithMaxAccidents();

		assertNotNull(result);
		assertTrue(result.contains(1));
	}

	@Test
	void testGetTopBrigadePerAccidentType() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);
		Date accidentTime2 = new Date(now.getTime() - 450000);

		Accident accident1 = createTestAccident(1, brigade1, accidentTime1);
		Accident accident2 = createTestAccident(2, brigade1, accidentTime2);
		Accident accident3 = createTestAccident(3, brigade2, accidentTime1);

		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);
		Client client3 = createTestClient(3, 5);

		accident1.setType("Крах труби");
		accident2.setType("Крах труби");
		accident3.setType("Крах крана");

		List<Accident> accidents = List.of(accident1, accident2, accident3);
		List<Client> clients = List.of(client1, client2, client3);

		main.setAccidents(accidents);
		main.setClients(clients);
		main.saveAllData();

		Map<String, Integer> result = main.getTopBrigadePerAccidentType();

		assertNotNull(result);
		assertEquals(1, (int) result.get("Крах труби"));
		assertEquals(2, (int) result.get("Крах крана"));
	}

	@Test
	void testGetInactiveBrigadesLeftJoin() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);

		Accident accident = createTestAccident(1, brigade1, accidentTime1);
		Client client = createTestClient(1, 5);

		main.setAccidents(List.of(accident));
		main.setClients(List.of(client));
		main.saveAllData();

		List<Integer> result = main.getInactiveBrigadesLeftJoin();

		assertNotNull(result);
		assertTrue(result.contains(2));
	}

	@Test
	void testGetInactiveBrigadesIn() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);

		Accident accident = createTestAccident(1, brigade1, accidentTime1);
		Client client = createTestClient(1, 5);

		main.setAccidents(List.of(accident));
		main.setClients(List.of(client));
		main.saveAllData();

		List<Integer> result = main.getInactiveBrigadesIn();

		assertNotNull(result);
		assertTrue(result.contains(2));
	}

	@Test
	void testGetInactiveBrigadesExists() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);

		Accident accident = createTestAccident(1, brigade1, accidentTime1);
		Client client = createTestClient(1, 5);

		main.setAccidents(List.of(accident));
		main.setClients(List.of(client));
		main.saveAllData();

		List<Integer> result = main.getInactiveBrigadesExists();

		assertNotNull(result);
		assertTrue(result.contains(2));
	}

	@Test
	void testGetBrigadesWithActivityStatus() {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);

		Date now = new Date();
		Date accidentTime1 = new Date(now.getTime() - 500000);

		Accident accident = createTestAccident(1, brigade1, accidentTime1);
		Client client = createTestClient(1, 5);

		// Set accident status
		accident.setStatus("Вирішено");

		main.setAccidents(List.of(accident));
		main.setClients(List.of(client));
		main.saveAllData();

		List<String> result = main.getBrigadesWithActivityStatus();

		assertNotNull(result);
		assertTrue(result.contains("Бригада 1: Найактивніша бригада"));
		assertTrue(result.contains("Бригада 2: Не має активних аварій"));
	}

	private void verifyTableCount(String tableName, int expectedCount) throws SQLException {
		// Ensure we can see committed data
		testConnection.setAutoCommit(true);
		try (Statement stmt = testConnection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
			assertTrue(rs.next(), "No results for table: " + tableName);
			assertEquals(expectedCount, rs.getInt(1));
			assertFalse(rs.next());
		} finally {
			// Reset to transaction mode for other tests
			testConnection.setAutoCommit(false);
		}
	}
}