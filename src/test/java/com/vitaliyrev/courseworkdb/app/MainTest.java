package com.vitaliyrev.courseworkdb.app;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;
import com.vitaliyrev.courseworkdb.app.db.DatabaseManagerTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
		clearDatabase();
	}

	@AfterEach
	void tearDown() throws SQLException {
		clearDatabase();
		dbManager.close();
	}

	private void clearDatabase() throws SQLException {
		if (testConnection != null && !testConnection.isClosed()) {
			try (Statement stmt = testConnection.createStatement()) {
				stmt.execute("TRUNCATE TABLE accidents, clients, masters, existing_brigades CASCADE");
			}
		}
	}

	private Brigade createTestBrigade(int number) {
		return new Brigade(number);
	}

	private Master createTestMaster(int id, Brigade brigade, int experience) {
		return new Master(id, "Майстер" + id, "Адреса" + id, "098765432" + id, experience, "Вільний", brigade, 1);
	}

	private Client createTestClient(int id, int dept) {
		return new Client(id, "Клієнт" + id, "АдресаКлієнта" + id, "098765432" + id, dept);
	}

	private Accident createTestAccident(int id, Brigade brigade, Client client, Date date, double cost) {
		return new Accident(id, "АдресаАварії" + id, "Крах крана", cost, date, brigade, "У процесі", client.getPhoneNumber(), id);
	}

	@Test
	void shouldSaveAndLoadMultipleEntities() throws SQLException {
		Brigade brigade1 = createTestBrigade(1);
		Brigade brigade2 = createTestBrigade(2);
		Master master1 = createTestMaster(1, brigade1, 5);
		Master master2 = createTestMaster(2, brigade2, 10);
		Client client1 = createTestClient(1, 5);
		Client client2 = createTestClient(2, 5);
		Date now = new Date();
		Accident accident1 = createTestAccident(1, brigade1, client1, now, 100.0);
		Accident accident2 = createTestAccident(2, brigade2, client2, now, 200.0);

		main.addMaster(master1);
		main.addMaster(master2);
		main.addClient(client1);
		main.addClient(client2);
		main.addAccident(accident1);
		main.addAccident(accident2);

		assertEquals(2, main.getMasters().size());
		assertEquals(2, main.getClients().size());
		assertEquals(2, main.getAccidents().size());

		verifyTableCount("existing_brigades", 2);
		verifyTableCount("masters", 2);
		verifyTableCount("clients", 2);
		verifyTableCount("accidents", 2);
	}

	@Test
	void shouldCalculateMasterSalaryCorrectly() throws SQLException {
		Brigade brigade1 = createTestBrigade(1);
		Master master1 = createTestMaster(1, brigade1, 5);
		Client client1 = createTestClient(1, 1);
		Client client2 = createTestClient(2, 1);

		main.addMaster(master1);
		main.addClient(client1);
		main.addClient(client2);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 15);
		Date date1 = cal.getTime();
		cal.set(2025, Calendar.FEBRUARY, 15);
		Date date2 = cal.getTime();

		Accident acc1 = createTestAccident(1, brigade1, client1, date1, 150.0);
		Accident acc2 = createTestAccident(2, brigade1, client2, date2, 250.0);
		main.addAccident(acc1);
		main.addAccident(acc2);

		cal.set(2025, Calendar.JANUARY, 1);
		Date startDate = cal.getTime();
		cal.set(2025, Calendar.MARCH, 1);
		Date endDate = cal.getTime();

		double salary = main.getMasterSalaryBetweenDates(master1.getIndex(), startDate, endDate);
		assertEquals(2400.0, salary, 0.01);
	}

	@Test
	void shouldCountBrigadesCorrectly() throws SQLException {
		main.addMaster(createTestMaster(1, createTestBrigade(1), 5));
		main.addMaster(createTestMaster(2, createTestBrigade(1), 5));
		main.addMaster(createTestMaster(3, createTestBrigade(2), 5));
		assertEquals(2, main.getBrigadeCount());
	}

	@Test
	void shouldCalculateAverageAccidentCost() throws SQLException {
		Brigade brigade = createTestBrigade(1);
		Client client1 = createTestClient(1, 1);
		Client client2 = createTestClient(2, 1);
		main.addClient(client1);
		main.addClient(client2);
		main.addAccident(createTestAccident(1, brigade, client1, new Date(), 100.0));
		main.addAccident(createTestAccident(2, brigade, client2, new Date(), 300.0));

		assertEquals(200.0, main.getAverageAccidentCost(), 0.01);
	}

	@Test
	void shouldGetAccidentCountBetweenDates() throws SQLException {
		Brigade brigade = createTestBrigade(1);
		Client client = createTestClient(1, 1);
		main.addClient(client);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 10);
		Date dateIn = cal.getTime();
		cal.set(2024, Calendar.DECEMBER, 10);
		Date dateOut = cal.getTime();
		main.addAccident(createTestAccident(1, brigade, client, dateIn, 100));
		main.addAccident(createTestAccident(2, brigade, client, dateOut, 100));

		cal.set(2025, Calendar.JANUARY, 1);
		Date startDate = cal.getTime();
		cal.set(2025, Calendar.JANUARY, 31);
		Date endDate = cal.getTime();

		assertEquals(1, main.getAccidentCountBetweenDates(startDate, endDate));
	}

	@Test
	void testGetMastersWithBrigadeInfo() throws SQLException {
		Brigade brigade1 = createTestBrigade(1);
		Master master1 = createTestMaster(1, brigade1, 5);
		main.addMaster(master1);

		String result = main.getMastersWithBrigadeInfo();
		assertNotNull(result);
		assertTrue(result.contains("Майстер1 | Бригада: 1 | Статус: Вільний"));
	}

	@Test
	void testFindClientsByNamePattern() throws SQLException {
		Client client1 = createTestClient(1, 5);
		client1.setName("Іван Петренко");
		Client client2 = createTestClient(2, 5);
		client2.setName("Іван Сидоренко");
		main.addClient(client1);
		main.addClient(client2);

		String result = main.findClientsByNamePattern("Іван");
		assertNotNull(result);
		assertTrue(result.contains("Іван Петренко"));
		assertTrue(result.contains("Іван Сидоренко"));
	}

	private void verifyTableCount(String tableName, int expectedCount) throws SQLException {
		testConnection.setAutoCommit(true);
		try (Statement stmt = testConnection.createStatement();
		     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
			assertTrue(rs.next(), "No results for table: " + tableName);
			assertEquals(expectedCount, rs.getInt(1));
		} finally {
			testConnection.setAutoCommit(false);
		}
	}
}