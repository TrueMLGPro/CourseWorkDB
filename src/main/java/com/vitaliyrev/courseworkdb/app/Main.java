package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

import com.vitaliyrev.courseworkdb.app.*;

// Головний клас
public class Main {
	private final List<Master> masters; // Список майстрів
	private final List<Client> clients; // Список клієнтів
	private final List<Accident> accidents; // Список аварій
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
		this.masters = new ArrayList<>();
		this.clients = new ArrayList<>();
		this.accidents = new ArrayList<>();
		loadAllData(); // Завантаження даних з файлів при створенні об'єкту
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

	public int getNextClientId() {
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

	// Набір номерів бригад
	public List<String> getBrigadeNumbers() {
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

	// Набір номерів відділів майстрів
	public List<String> getMasterDepartmentNumbers() {
		return BRIGADE_AND_DEPARTMENT_NUMBERS;
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
		loadMasters();
		loadClients();
		loadAccidents();
	}

	// Завантаження майстрів з файлу
	private void loadMasters() {
		masters.addAll(loadEntities("data/masters.txt", Master.class));
		Map<Integer, Brigade> brigades = new HashMap<>();
		for (Master master : masters) {
			String[] parts = master.toString().split(";");
			int brigadeNumber = Integer.parseInt(parts[6]);
			int departmentNumber = Integer.parseInt(parts[7]);
			Brigade brigade = brigades.getOrDefault(brigadeNumber, new Brigade(brigadeNumber));
			master.setBrigade(brigade);
			master.setDepartmentNumber(departmentNumber);
			brigade.addMaster(master);
			brigades.put(brigadeNumber, brigade);
		}
	}

	// Завантаження клієнтів з файлу
	private void loadClients() {
		clients.addAll(loadEntities("data/clients.txt", Client.class));
	}

	// Завантаження аварій з файлу
	private void loadAccidents() {
		accidents.addAll(loadEntities("data/accidents.txt", Accident.class));
	}

	// Загальний метод для завантаження сутностей з файлу
	private <T extends Entity> List<T> loadEntities(String filename, Class<T> clazz) {
		List<T> entities = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(";");
				T entity = clazz.getDeclaredConstructor(String[].class).newInstance(new Object[]{parts});
				entities.add(entity);
			}
		} catch (Exception e) {
			String message = "Помилка завантаження даних з файлу " + filename + ": " + e.getMessage();
			System.err.println(message);
			JOptionPane.showMessageDialog(null, message, "Помилка", JOptionPane.ERROR_MESSAGE);
		}
		return entities;
	}

	// Збереження всіх даних
	public void saveAllData() {
		List<String> failedFiles = new ArrayList<>();

		if (!saveEntities("data/masters.txt", masters, failedFiles)) {
			failedFiles.add("data/masters.txt");
		}
		if (!saveEntities("data/clients.txt", clients, failedFiles)) {
			failedFiles.add("data/clients.txt");
		}
		if (!saveEntities("data/accidents.txt", accidents, failedFiles)) {
			failedFiles.add("data/accidents.txt");
		}

		if (!failedFiles.isEmpty()) {
			StringBuilder errorMessage = new StringBuilder("Помилка збереження даних у файлах:\n");
			for (String file : failedFiles) {
				errorMessage.append(file).append("\n");
			}
			JOptionPane.showMessageDialog(null, errorMessage.toString(), "Помилка", JOptionPane.ERROR_MESSAGE);
		}
	}

	// Загальний метод для збереження сутностей у файл
	private <T extends Entity> boolean saveEntities(String filename, List<T> entities, List<String> failedFiles) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			for (T entity : entities) {
				bw.write(entity.toString());
				bw.newLine();
			}
			return true;
		} catch (IOException e) {
			String message = "Помилка збереження даних у файл " + filename + ": " + e.getMessage();
			System.err.println(message);
			failedFiles.add(filename);
			return false;
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
		SwingUtilities.invokeLater(() -> {
			Main main = new Main();
			new UserInterface(main);
		});
	}
}