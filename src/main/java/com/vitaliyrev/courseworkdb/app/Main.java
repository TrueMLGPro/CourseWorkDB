package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;

// Головний клас
public class Main {
	protected DatabaseManager dbManager; // Клас для управління БД
	private String url;
	private String user;
	private String password;
	private static UserInterface userInterface;

	private List<Master> masters; // Список майстрів
	private List<Client> clients; // Список клієнтів
	private List<Accident> accidents; // Список аварій
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK); // Формат дати
	private static final List<String> BRIGADE_AND_DEPARTMENT_NUMBERS = Arrays.asList("1", "2", "3", "4", "5"); // Номери бригад та відділів
	private static final List<String> MASTER_STATUSES = Arrays.asList( // Стани майстрів
			"Вільний",
			"Зайнятий",
			"Призначений",
			"Усуває аварію",
			"Очікує перевірки"
	);
	private static final List<String> ACCIDENT_TYPES = Arrays.asList( // Типи аварій
			"Новий",
			"Крах труби",
			"Крах крана",
			"Крах насосу",
			"Крах водонаборної системи",
			"Крах водонапорної системи",
			"Крах водозаборної системи",
			"Крах фільтраційної системи",
			"Крах системи очищення води",
			"Крах системи нагріву води",
			"Крах системи теплообміну",
			"Крах системи забору води",
			"Крах системи під'єднання"
	);
	private static final List<String> ACCIDENT_STATUSES = Arrays.asList( // Стани аварій
			"Новий",
			"Прийнято",
			"У процесі",
			"Очікує перевірки",
			"Вирішено",
			"Повторний виклик",
			"Відмінено"
	);

	public Main() {
		this(
			System.getProperty("db.url", "jdbc:postgresql://localhost:5432/course_work_db"),
			System.getProperty("db.user", "course_work_db_admin"),
			System.getProperty("db.password", "courseworkdbadmin123")
		);
		userInterface = new UserInterface(this);
	}

	// For tests
	public Main(DatabaseManager dbManager) {
		this.dbManager = dbManager;
		loadAllData();
	}

	public Main(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;

		try {
			this.dbManager = new DatabaseManager(url, user, password);
			loadAllData();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Не вдалося приєднатись до бази даних: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private <T> T executeQuery(String sql, ResultSetProcessor<T> processor) {
		try (Connection conn = this.dbManager.getConnection();
		     Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(sql)) {
			return processor.process(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private <T> T executeParamQuery(String sql, Object param, ResultSetProcessor<T> processor) {
		try (Connection conn = this.dbManager.getConnection();
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

	private <T> T executeParamQuery(String sql, Object param1, Object param2, ResultSetProcessor<T> processor) {
		try (Connection conn = this.dbManager.getConnection();
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

	private <T> T executeScalarQuery(String sql, ResultSetProcessor<T> processor) {
		try (Connection conn = this.dbManager.getConnection();
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

	private List<Integer> executeIdListQuery(String sql) {
		return executeQuery(sql, rs -> {
			List<Integer> result = new ArrayList<>();
			while (rs.next()) {
				result.add(rs.getInt(1));
			}
			return result;
		});
	}

	private <T> T executeResilientQuery(Supplier<T> querySupplier) {
		try {
			if (!this.dbManager.isConnectionValid()) {
				System.out.println("Connection invalid. Reconnecting...");
				reconnect();
			}
			T result = querySupplier.get();
			if (userInterface != null) {
				userInterface.updateConnectionStatus();
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@FunctionalInterface
	private interface ResultSetProcessor<T> {
		T process(ResultSet rs) throws SQLException;
	}

	public void reconnect() throws SQLException {
		if (dbManager != null) {
			dbManager.close();
		}
		dbManager = new DatabaseManager(url, user, password);
		loadAllData();
	}

	// Гетери для списків
	public List<Master> getMasters() {
		return masters;
	}

	public List<Client> getClients() {
		return clients;
	}

	public List<Accident> getAccidents() {
		return accidents;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	// Сеттери для списків, використовуються тільки для проведення тестів
	public void setMasters(List<Master> masters) {
		this.masters = masters != null ? masters : new ArrayList<>();
	}

	public void setClients(List<Client> clients) {
		this.clients = clients != null ? clients : new ArrayList<>();
	}

	public void setAccidents(List<Accident> accidents) {
		this.accidents = accidents != null ? accidents : new ArrayList<>();
	}

	// Методи для отримання наступних індексів
	public int getNextMasterIndex() {
		if (masters.isEmpty()) {
			return 1;
		}
		return masters.stream().mapToInt(Entity::getIndex).max().orElse(0) + 1;
	}

	public int getNextAccidentIndex() {
		if (accidents.isEmpty()) {
			return 1;
		}
		return accidents.stream().mapToInt(Entity::getIndex).max().orElse(0) + 1;
	}

	public int getNextClientIndex() {
		if (clients.isEmpty()) {
			return 1;
		}
		return clients.stream().mapToInt(Entity::getIndex).max().orElse(0) + 1;
	}

	// Обчислення заробітної плати майстра за період
	public double getMasterSalaryBetweenDates(int masterIndex, Date startDate, Date endDate) {
		double experienceBonusPerYear = 200.0;
		double totalSalary = 0;

		for (Accident accident : accidents) {
			Date accidentDate = accident.getDate();
			if (accidentDate != null && accidentDate.compareTo(startDate) >= 0 &&
					accidentDate.compareTo(endDate) <= 0 && accident.getBrigade() != null) {

				for (Master master : masters) {
					if (master.getIndex() == masterIndex && master.getBrigade() != null &&
							master.getBrigade().getNumber() == accident.getBrigade().getNumber()) {

						double accidentCost = accident.getCost();
						double experienceBonus = master.getExperience() * experienceBonusPerYear;
						totalSalary += accidentCost + experienceBonus;
					}
				}
			}
		}
		return totalSalary;
	}

	// Кількість бригад
	public int getBrigadeCount() {
		Set<Integer> brigadeNumbers = new HashSet<>();
		for (Master master : masters) {
			if (master.getBrigade() != null) {
				brigadeNumbers.add(master.getBrigade().getNumber());
			}
		}
		return brigadeNumbers.size();
	}

	// Кількість аварій за період
	public int getAccidentCountBetweenDates(Date startDate, Date endDate) throws IllegalArgumentException {
		if (startDate == null || endDate == null) return 0;
		if (startDate.after(endDate))
			throw new IllegalArgumentException("Початкова дата не може бути більшою за кінцеву дату.");
		int count = 0;
		for (Accident accident : accidents) {
			if (!accident.getDate().before(startDate) && !accident.getDate().after(endDate)) {
				count++;
			}
		}
		return count;
	}

	// Середня вартість аварії
	public double getAverageAccidentCost() {
		if (accidents.isEmpty()) return 0;
		double totalCost = 0;
		for (Accident accident : accidents) {
			totalCost += accident.getCost();
		}
		return round(totalCost / accidents.size(), 2);
	}

	// Набір типів аварій
	public Set<String> getExistingAccidentTypes() {
		Set<String> types = new HashSet<>();
		for (Accident accident : accidents) {
			types.add(accident.getType());
		}
		return types;
	}

	// Набір існуючих номерів бригад
	public List<String> getUsedBrigadeNumbers() {
		Set<Integer> brigadeNumbers = new HashSet<>();
		for (Master master : masters) {
			if (master.getBrigade() != null) {
				brigadeNumbers.add(master.getBrigade().getNumber());
			}
		}
		for (Accident accident : accidents) {
			if (accident.getBrigade() != null) {
				brigadeNumbers.add(accident.getBrigade().getNumber());
			}
		}
		List<String> numbers = new ArrayList<>();
		for (Integer number : brigadeNumbers) {
			numbers.add(number.toString());
		}
		return numbers;
	}

	public List<String> getUsedDepartmentNumbers() {
		Set<Integer> departmentNumbers = new HashSet<>();
		for (Master master : masters) {
			departmentNumbers.add(master.getDepartmentNumber());
		}
		for (Accident accident : accidents) {
			if (accident.getClient() != null) {
				departmentNumbers.add(accident.getClient().getDepartmentNumber());
			}
		}
		List<String> numbers = new ArrayList<>();
		for (Integer number : departmentNumbers) {
			numbers.add(number.toString());
		}
		return numbers;
	}

	// Номери бригад та відділів
	public List<String> getBrigadeAndDepartmentNumbers() {
		return BRIGADE_AND_DEPARTMENT_NUMBERS;
	}

	// Статуси майстрів
	public List<String> getMasterStatuses() {
		return MASTER_STATUSES;
	}

	// Статуси аварій
	public List<String> getAccidentStatuses() {
		return ACCIDENT_STATUSES;
	}

	// Типи аварій
	public List<String> getAccidentTypes() {
		return ACCIDENT_TYPES;
	}

	// Отримання майстра за індексом
	public Master getMasterByIndex(int index) {
		for (Master master : masters) {
			if (master.getIndex() == index) {
				return master;
			}
		}
		return null;
	}

	// Отримання клієнта за ідентифікатором аварії
	public Client getClientByAccidentId(String accidentId) {
		for (Client client : clients) {
			if (client.getAccidentId().equals(accidentId)) {
				return client;
			}
		}
		return null;
	}

	// Отримання аварії за ідентифікатором
	public Accident getAccidentById(String accidentId) {
		for (Accident accident : accidents) {
			if (accident.getAccidentId().equals(accidentId)) {
				return accident;
			}
		}
		return null;
	}

	// Завантаження всіх даних
	public void loadAllData() {
		try {
			List<Brigade> dbBrigades = dbManager.loadBrigades();
			Map<Integer, Brigade> brigadeMap = dbBrigades.stream()
					.collect(Collectors.toMap(Brigade::getNumber, b -> b));

			// Load clients first
			this.clients = dbManager.loadClients();
			Map<String, Client> clientMap = clients.stream()
					.collect(Collectors.toMap(Client::getAccidentId, c -> c));

			this.masters = dbManager.loadMasters(brigadeMap);
			this.accidents = dbManager.loadAccidents(brigadeMap, clientMap);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Помилка при завантаженні даних з БД: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Збереження всіх даних
	public void saveAllData() {
		try {
			// Collect brigade numbers
			Set<Integer> brigadeNumbers = new HashSet<>();
			if (masters != null) {
				for (Master master : masters) {
					brigadeNumbers.add(master.getBrigade().getNumber());
				}
			}
			if (accidents != null) {
				for (Accident accident : accidents) {
					brigadeNumbers.add(accident.getBrigade().getNumber());
				}
			}

			// Save in proper order with foreign key dependencies
			dbManager.saveBrigades(brigadeNumbers);
			if (clients != null) dbManager.saveClients(clients);
			if (accidents != null) dbManager.saveAccidents(accidents);
			if (masters != null) dbManager.saveMasters(masters);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to save data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Метод для округлення числа
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	// Метод для генерації випадкового рядка
	public static String generateRandomString(int length) {
		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(CHARACTERS.length());
			sb.append(CHARACTERS.charAt(index));
		}
		return sb.toString();
	}

	// SQL
	// 1
	public String getMastersWithBrigadeInfo() {
		return executeResilientQuery(() -> {
			String sql = "SELECT m.name AS master_name, b.number AS brigade_number, m.status " +
					"FROM masters m " +
					"JOIN brigades b ON m.brigade_number = b.number " +
					"ORDER BY m.name";
			return executeQuery(sql, rs -> {
				StringBuilder sb = new StringBuilder();
				while (rs.next()) {
					sb.append(rs.getString("master_name"))
							.append(" | Бригада: ")
							.append(rs.getInt("brigade_number"))
							.append(" | Статус: ")
							.append(rs.getString("status"))
							.append("\n");
				}
				return sb.toString();
			});
		});
	}

	// 2
	public String findClientsByNamePattern(String pattern) {
		return executeResilientQuery(() -> {
			String sql = "SELECT name, phone_number, department_number " +
					"FROM clients WHERE name LIKE ?";
			return executeParamQuery(sql, pattern + "%", rs -> {
				StringBuilder result = new StringBuilder();
				do {
					result.append(rs.getString("name"))
							.append(" | Тел: ")
							.append(rs.getString("phone_number"))
							.append(" | Відділ: ")
							.append(rs.getInt("department_number"))
							.append("\n");
				} while (rs.next());
				return result.toString();
			});
		});
	}

	// 3
	public String getAccidentsBetweenDates(Date start, Date end) {
		return executeResilientQuery(() -> {
			String sql = "SELECT accident_id, address, cost, date " +
					"FROM accidents WHERE date BETWEEN ? AND ?";
			return executeParamQuery(sql,
					new java.sql.Date(start.getTime()),
					new java.sql.Date(end.getTime()),
					rs -> {
						StringBuilder sb = new StringBuilder();
						while (rs.next()) {
							sb.append(String.format("%s | %s | %.2f грн | %s%n",
									rs.getString("accident_id"),
									rs.getString("address"),
									rs.getDouble("cost"),
									getDateFormat().format(rs.getDate("date"))));
						}
						return sb.toString();
					});
		});
	}

	// 4
	public int getAccidentCountLastWeek() {
		return executeResilientQuery(() -> {
			String sql = "SELECT COUNT(*) FROM accidents " +
					"WHERE date BETWEEN CURRENT_DATE - 7 AND CURRENT_DATE";
			return executeScalarQuery(sql, rs -> rs.getInt(1));
		});
	}

	// 5
	public Map<Integer, Integer> getAccidentCountPerBrigade() {
		return executeResilientQuery(() -> {
			String sql = "SELECT brigade_number, COUNT(*) as accident_count " +
					"FROM accidents GROUP BY brigade_number";
			return executeQuery(sql, rs -> {
				Map<Integer, Integer> result = new HashMap<>();
				while (rs.next()) {
					result.put(rs.getInt("brigade_number"), rs.getInt("accident_count"));
				}
				return result;
			});
		});
	}

	// 6
	public List<Integer> getBrigadesWithMaxAccidents() {
		return executeResilientQuery(() -> {
			String sql = "SELECT brigade_number " +
					"FROM accidents " +
					"GROUP BY brigade_number " +
					"HAVING COUNT(*) >= ALL (SELECT COUNT(*) FROM accidents GROUP BY brigade_number)";
			return executeQuery(sql, rs -> {
				List<Integer> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getInt("brigade_number"));
				}
				return result;
			});
		});
	}

	// 7
	public Map<String, Integer> getTopBrigadePerAccidentType() {
		return executeResilientQuery(() -> {
			String sql = "SELECT a1.type, a1.brigade_number " +
					"FROM accidents a1 " +
					"WHERE a1.brigade_number = (SELECT a2.brigade_number " +
					"                           FROM accidents a2 " +
					"                           WHERE a2.type = a1.type " +
					"                           GROUP BY a2.brigade_number " +
					"                           ORDER BY COUNT(*) DESC " +
					"                           LIMIT 1) " +
					"GROUP BY a1.type, a1.brigade_number";
			return executeQuery(sql, rs -> {
				Map<String, Integer> result = new HashMap<>();
				while (rs.next()) {
					result.put(rs.getString("type"), rs.getInt("brigade_number"));
				}
				return result;
			});
		});
	}

	// 8
	public List<Integer> getInactiveBrigadesLeftJoin() {
		return executeResilientQuery(() -> {
			String sql = "SELECT b.number " +
					"FROM brigades b " +
					"LEFT JOIN accidents a ON b.number = a.brigade_number " +
					"WHERE a.id IS NULL";
			return executeIdListQuery(sql);
		});
	}

	public List<Integer> getInactiveBrigadesIn() {
		return executeResilientQuery(() -> {
			String sql = "SELECT number FROM brigades " +
					"WHERE number NOT IN (SELECT brigade_number FROM accidents)";
			return executeIdListQuery(sql);
		});
	}

	public List<Integer> getInactiveBrigadesExists() {
		return executeResilientQuery(() -> {
			String sql = "SELECT number FROM brigades b " +
					"WHERE NOT EXISTS (SELECT 1 FROM accidents a WHERE a.brigade_number = b.number)";
			return executeIdListQuery(sql);
		});
	}

	// 9
	public List<String> getBrigadesWithActivityStatus() {
		return executeResilientQuery(() -> {
			String sql = "SELECT number, 'Найактивніша бригада' AS comment " +
					"FROM brigades WHERE number = (SELECT brigade_number FROM accidents GROUP BY brigade_number ORDER BY COUNT(*) DESC LIMIT 1) " +
					"UNION " +
					"SELECT number, 'Не має активних аварій' AS comment " +
					"FROM brigades WHERE number NOT IN (SELECT DISTINCT brigade_number FROM accidents WHERE status NOT IN ('Вирішено', 'Відмінено'))";
			return executeQuery(sql, rs -> {
				List<String> result = new ArrayList<>();
				while (rs.next()) {
					result.add("Бригада " + rs.getInt("number") + ": " + rs.getString("comment"));
				}
				return result;
			});
		});
	}

	// Головний метод
	public static void main(String[] args) {
		new Main();
		SwingUtilities.invokeLater(() -> userInterface.frame.setVisible(true));
	}
}