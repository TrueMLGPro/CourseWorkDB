package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;

// Головний клас
public class Main {
	DatabaseManager dbManager; // Клас для управління БД
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
		try {
			this.dbManager = new DatabaseManager(
				"jdbc:postgresql://localhost:5432/course_work_db",
				"course_work_db_admin",
				"courseworkdbadmin123"
			);
			loadAllData();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Не вдалося приєднатись до бази даних: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
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
		int hoursPerAccident = 2;
		double experienceBonusPerYear = 200.0;

		double totalSalary = 0;

		for (Accident accident : accidents) {
			if ((accident.getDate().after(startDate) && accident.getDate().before(endDate)) && accident.getBrigade() != null) {
				for (Master master : masters) {
					if (master.getIndex() == masterIndex && master.getBrigade() != null) {
						if (master.getBrigade().getNumber() == accident.getBrigade().getNumber()) {
							double accidentCost = accident.getCost();
							double experienceBonus = master.getExperience() * experienceBonusPerYear;
							double effectiveCost = accidentCost + experienceBonus;
							totalSalary += effectiveCost * hoursPerAccident;
						}
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
	public int getAccidentsBetweenDates(Date startDate, Date endDate) throws IllegalArgumentException {
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

	// Набір ідентифікаторів аварій
	public List<String> getAccidentIds() {
		Set<String> accidentIds = new HashSet<>();
		for (Accident accident : accidents) {
			accidentIds.add(accident.getAccidentId());
		}
		return new ArrayList<>(accidentIds);
	}

	// Набір ідентифікаторів аварій клієнтів
	public List<String> getClientAccidentIds() {
		Set<String> accidentIds = new HashSet<>();
		for (Client client : clients) {
			accidentIds.add(client.getAccidentId());
		}
		return new ArrayList<>(accidentIds);
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
	private void loadAllData() {
		try {
			List<Brigade> dbBrigades = dbManager.loadBrigades();
			Map<Integer, Brigade> brigadeMap = dbBrigades.stream()
					.collect(Collectors.toMap(Brigade::getNumber, b -> b));

			// Load clients first
			this.clients = dbManager.loadClients();
			Map<Integer, Client> clientMap = clients.stream()
					.collect(Collectors.toMap(Client::getIndex, c -> c));

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
			dbManager.connection.setAutoCommit(false);

			// Collect brigade numbers
			Set<Integer> brigadeNumbers = new HashSet<>();
			for (Master master : masters) {
				brigadeNumbers.add(master.getBrigade().getNumber());
			}
			for (Accident accident : accidents) {
				brigadeNumbers.add(accident.getBrigade().getNumber());
			}

			// Save in proper order
			dbManager.saveBrigades(brigadeNumbers);
			dbManager.saveAccidents(accidents);  // Save accidents first
			dbManager.saveClients(clients);      // Then save clients
			dbManager.saveMasters(masters);

			dbManager.connection.commit();
		} catch (SQLException e) {
			try {
				dbManager.connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to save data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				dbManager.connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
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

	// Головний метод
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new UserInterface(new Main()));
	}
}