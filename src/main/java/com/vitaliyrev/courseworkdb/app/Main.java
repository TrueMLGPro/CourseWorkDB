package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;

// Головний клас
public class Main {
	protected DatabaseManager dbManager; // Клас для управління БД
	private String url;
	private String user;
	private String password;
	public static UserInterface userInterface; // Для тестів

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK); // Формат дати
	private static final List<String> BRIGADE_AND_DEPARTMENT_NUMBERS = Arrays.asList("1", "2", "3", "4", "5"); // Номери бригад та відділів
	private static final List<String> MASTER_STATUSES = Arrays.asList( // Стани майстрів
			"Вільний",
			"Зайнятий",
			"Усуває аварію",
			"Очікує перевірки"
	);
	private static final List<String> ACCIDENT_TYPES = Arrays.asList( // Типи аварій
			"Новий",
			"Крах труби",
			"Крах крана",
			"Крах насосу",
			"Крах водонапорної системи",
			"Крах фільтраційної системи",
			"Крах системи очищення води",
			"Крах системи нагріву води",
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

	// Для тестів
	public Main(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}

	public Main(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;

		try {
			this.dbManager = new DatabaseManager(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Не вдалося приєднатись до бази даних: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public void reconnect() throws SQLException {
		if (dbManager != null) {
			dbManager.close();
		}
		dbManager = new DatabaseManager(url, user, password);
	}

	// Гетери для даних
	public List<Master> getMasters() throws SQLException {
		return dbManager.loadMasters();
	}

	public List<Client> getClients() throws SQLException {
		return dbManager.loadClients();
	}

	public List<Accident> getAccidents() throws SQLException {
		return dbManager.loadAccidents();
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	// Методи для отримання наступних індексів
	public int getNextMasterIndex() throws SQLException {
		return dbManager.getNextId("masters");
	}

	public int getNextAccidentIndex() throws SQLException {
		return dbManager.getNextId("accidents");
	}

	public int getNextClientIndex() throws SQLException {
		return dbManager.getNextId("clients");
	}

	public int getNextAccidentIndexForClient(String phoneNumber) throws SQLException {
		return dbManager.getNextAccidentIndexForClient(phoneNumber);
	}

	// Операції управління даними, що викликають відповідні методи в DatabaseManager
	public void addMaster(Master master) throws SQLException {
		dbManager.saveMaster(master);
	}

	public void updateMaster(Master master) throws SQLException {
		dbManager.saveMaster(master);
	}

	public void deleteMaster(int masterId) throws SQLException {
		dbManager.deleteMaster(masterId);
	}

	public void addClient(Client client) throws SQLException {
		dbManager.saveClient(client);
	}

	public void updateClient(Client client) throws SQLException {
		dbManager.saveClient(client);
	}

	public void deleteClient(int clientId) throws SQLException {
		dbManager.deleteClient(clientId);
	}

	public void addAccident(Accident accident) throws SQLException {
		dbManager.saveAccident(accident);
	}

	public void updateAccident(Accident accident) throws SQLException {
		dbManager.saveAccident(accident);
	}

	public void deleteAccident(int accidentId) throws SQLException {
		dbManager.deleteAccident(accidentId);
	}

	// Обчислення заробітної плати майстра за період
	public double getMasterSalaryBetweenDates(int masterIndex, Date startDate, Date endDate) {
		try {
			return dbManager.getMasterSalaryBetweenDates(masterIndex, startDate, endDate);
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка розрахунку зарплати: " + e.getMessage());
			return 0;
		}
	}

	// Кількість бригад
	public int getBrigadeCount() {
		try {
			return dbManager.getBrigadeCount();
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка підрахунку бригад: " + e.getMessage());
			return 0;
		}
	}

	// Кількість аварій за період
	public int getAccidentCountBetweenDates(Date startDate, Date endDate) throws IllegalArgumentException {
		if (startDate == null || endDate == null) return 0;
		if (startDate.after(endDate))
			throw new IllegalArgumentException("Початкова дата не може бути більшою за кінцеву дату.");
		try {
			return dbManager.getAccidentCountBetweenDates(startDate, endDate);
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка підрахунку аварій: " + e.getMessage());
			return 0;
		}
	}

	// Середня вартість аварії
	public double getAverageAccidentCost() {
		try {
			return round(dbManager.getAverageAccidentCost(), 2);
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка розрахунку середньої вартості: " + e.getMessage());
			return 0;
		}
	}

	// Набір типів аварій
	public Set<String> getExistingAccidentTypes() {
		try {
			return dbManager.getExistingAccidentTypes();
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка отримання типів аварій: " + e.getMessage());
			return new HashSet<>();
		}
	}

	// Список існуючих номерів бригад
	public List<String> getUsedBrigadeNumbers() {
		try {
			return dbManager.getUsedBrigadeNumbers();
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка отримання номерів бригад: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// Список існуючих номерів відділів
	public List<String> getUsedDepartmentNumbers() {
		try {
			return dbManager.getUsedDepartmentNumbers();
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка отримання номерів відділів: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// Номери бригад та відділів
	public static List<String> getBrigadeAndDepartmentNumbers() {
		return BRIGADE_AND_DEPARTMENT_NUMBERS;
	}

	// Статуси майстрів
	public static List<String> getMasterStatuses() {
		return MASTER_STATUSES;
	}

	// Статуси аварій
	public static List<String> getAccidentStatuses() {
		return ACCIDENT_STATUSES;
	}

	// Типи аварій
	public static List<String> getAccidentTypes() {
		return ACCIDENT_TYPES;
	}

	// Методи отримання сутностей
	public Master getMasterByName(String masterName) throws SQLException {
		return dbManager.getMasterByName(masterName);
	}

	public Client getClientByName(String clientName) throws SQLException {
		return dbManager.getClientByName(clientName);
	}

	public List<String> getClientPhoneNumbers() {
		return dbManager.getClientPhoneNumbers();
	}

	public Accident getAccidentByPhoneNumAccIndex(String phoneNumber, int accidentIndex) throws SQLException {
		return dbManager.getAccidentByPhoneNumAccIndex(phoneNumber, accidentIndex);
	}

	public List<Integer> getAccidentIndicesForClient(String phoneNumber) {
		try {
			return dbManager.getAccidentIndicesForClient(phoneNumber);
		} catch (SQLException e) {
			e.printStackTrace();
			showError("Помилка отримання індексів аварій: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(null, message, "Помилка", JOptionPane.ERROR_MESSAGE);
	}

	// Метод для округлення числа
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	// SQL запити
	// 1
	public String getMastersWithBrigadeInfo() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT m.name AS master_name, b.number AS brigade_number, ms.status AS status " +
					"FROM masters m " +
					"JOIN existing_brigades b ON m.brigade_number = b.number " +
					"JOIN master_statuses ms ON m.status_id = ms.id " +
					"ORDER BY m.name";
			return dbManager.executeQuery(sql, rs -> {
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
		}, this);
	}

	// 2
	public String findClientsByNamePattern(String pattern) {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT name, phone_number, department_number " +
					"FROM clients WHERE name LIKE ?";
			return dbManager.executeParamQuery(sql, pattern + "%", rs -> {
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
		}, this);
	}

	// 3
	public String getAccidentsBetweenDates(Date start, Date end) {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT address, cost, date, at.type AS type, as1.status AS status " +
					"FROM accidents a " +
					"JOIN accident_types at ON a.type_id = at.id " +
					"JOIN accident_statuses as1 ON a.status_id = as1.id " +
					"WHERE date BETWEEN ? AND ?";
			return dbManager.executeParamQuery(sql,
					new java.sql.Date(start.getTime()),
					new java.sql.Date(end.getTime()),
					rs -> {
						StringBuilder sb = new StringBuilder();
						while (rs.next()) {
							sb.append(String.format("%s | %.2f грн | %s | Тип: %s | Статус: %s%n",
									rs.getString("address"),
									rs.getDouble("cost"),
									getDateFormat().format(rs.getDate("date")),
									rs.getString("type"),
									rs.getString("status")));
						}
						return sb.toString();
					});
		}, this);
	}

	// 4
	public int getAccidentCountLastWeek() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT COUNT(*) FROM accidents " +
					"WHERE date BETWEEN CURRENT_DATE - 7 AND CURRENT_DATE";
			return dbManager.executeScalarQuery(sql, rs -> rs.getInt(1));
		}, this);
	}

	// 5
	public Map<Integer, Integer> getAccidentCountPerBrigade() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT brigade_number, COUNT(*) as accident_count " +
					"FROM accidents GROUP BY brigade_number";
			return dbManager.executeQuery(sql, rs -> {
				Map<Integer, Integer> result = new HashMap<>();
				while (rs.next()) {
					result.put(rs.getInt("brigade_number"), rs.getInt("accident_count"));
				}
				return result;
			});
		}, this);
	}

	// 6
	public List<Integer> getBrigadesWithMaxAccidents() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT brigade_number " +
					"FROM accidents " +
					"GROUP BY brigade_number " +
					"HAVING COUNT(*) >= ALL (SELECT COUNT(*) FROM accidents GROUP BY brigade_number)";
			return dbManager.executeQuery(sql, rs -> {
				List<Integer> result = new ArrayList<>();
				while (rs.next()) {
					result.add(rs.getInt("brigade_number"));
				}
				return result;
			});
		}, this);
	}

	// 7
	public Map<String, Integer> getTopBrigadePerAccidentType() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT at.type, a.brigade_number " +
					"FROM accidents a " +
					"JOIN accident_types at ON a.type_id = at.id " +
					"WHERE a.brigade_number = (SELECT a1.brigade_number " +
					"                           FROM accidents a1 " +
					"                           WHERE a1.type_id = a.type_id " +
					"                           GROUP BY a1.brigade_number " +
					"                           ORDER BY COUNT(*) DESC " +
					"                           LIMIT 1) " +
					"GROUP BY at.type, a.brigade_number";
			return dbManager.executeQuery(sql, rs -> {
				Map<String, Integer> result = new HashMap<>();
				while (rs.next()) {
					result.put(rs.getString("type"), rs.getInt("brigade_number"));
				}
				return result;
			});
		}, this);
	}

	// 8
	public List<Integer> getInactiveBrigadesLeftJoin() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT b.number " +
					"FROM existing_brigades b " +
					"LEFT JOIN accidents a ON b.number = a.brigade_number " +
					"WHERE a.id IS NULL";
			return dbManager.executeIdListQuery(sql);
		}, this);
	}

	public List<Integer> getInactiveBrigadesIn() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT number FROM existing_brigades " +
					"WHERE number NOT IN (SELECT brigade_number FROM accidents)";
			return dbManager.executeIdListQuery(sql);
		}, this);
	}

	public List<Integer> getInactiveBrigadesExists() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT number FROM existing_brigades b " +
					"WHERE NOT EXISTS (SELECT 1 FROM accidents a WHERE a.brigade_number = b.number)";
			return dbManager.executeIdListQuery(sql);
		}, this);
	}

	// 9
	public List<String> getBrigadesWithActivityStatus() {
		return dbManager.executeResilientQuery(() -> {
			String sql = "SELECT number, 'Найактивніша бригада' AS comment " +
					"FROM existing_brigades WHERE number = (SELECT brigade_number FROM accidents GROUP BY brigade_number ORDER BY COUNT(*) DESC LIMIT 1) " +
					"UNION " +
					"SELECT number, 'Не має активних аварій' AS comment " +
					"FROM existing_brigades WHERE number NOT IN (SELECT DISTINCT brigade_number FROM accidents WHERE status_id IN (SELECT id FROM accident_statuses WHERE status NOT IN ('Вирішено', 'Відмінено')))";
			return dbManager.executeQuery(sql, rs -> {
				List<String> result = new ArrayList<>();
				while (rs.next()) {
					result.add("Бригада " + rs.getInt("number") + ": " + rs.getString("comment"));
				}
				return result;
			});
		}, this);
	}

	// Головний метод
	public static void main(String[] args) {
		new Main();
		SwingUtilities.invokeLater(() -> userInterface.frame.setVisible(true));
	}
}