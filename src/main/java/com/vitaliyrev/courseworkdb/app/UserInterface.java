package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

// Клас "Інтерфейс користувача"
public class UserInterface {
	private final Main main; // Посилання на головний об'єкт програми
	private JFrame frame; // Основне вікно програми
	private final JTextArea resultArea; // Текстова область для виводу результатів
	private JTextField masterStartDateField; // Поле для введення початкової дати для майстрів
	private JTextField masterEndDateField; // Поле для введення кінцевої дати для майстрів
	private JTextField addMasterNameField; // Поле для введення імені та прізвища майстра
	private JTextField addMasterAddressField; // Поле для введення адреси майстра
	private JTextField addMasterPhoneField; // Поле для введення номеру телефону майстра
	private JTextField addMasterExperienceField; // Поле для введення стажу майстра
	private JComboBox<String> addMasterStatusField; // Вибіркове поле для вибору стану майстра
	private JComboBox<String> addMasterBrigadeField; // Вибіркове поле для вибору номера бригади майстра
	private JComboBox<String> addMasterDepartmentField; // Вибіркове поле для вибору номера відділення майстра
	private JComboBox<String> masterIndexField; // Вибіркове поле для вибору індекса майстра
	private JTextField accidentStartDateField; // Поле для введення початкової дати для аварій
	private JTextField accidentEndDateField; // Поле для введення кінцевої дати для аварій
	private JTextField addAccidentDateField; // Поле для введення дати аварії
	private JComboBox<String> addAccidentTypeField; // Вибіркове поле для вибору типу аварії
	private JTextField addAccidentCostField; // Поле для введення вартості аварії
	private JComboBox<String> addAccidentStatusField; // Вибіркове поле для вибору стану аварії
	private JTextField addAccidentAddressField; // Поле для введення адреси аварії
	private JComboBox<String> addAccidentBrigadeField; // Вибіркове поле для вибору номера бригади аварії
	private JComboBox<String> accidentIdField; // Вибіркове поле для вибору ID аварії
	private JTextField addClientNameField; // Поле для введення імені та прізвища клієнта
	private JTextField addClientAddressField; // Поле для введення адреси клієнта
	private JTextField addClientPhoneField; // Поле для введення номеру телефону клієнта
	private JComboBox<String> addClientDepartmentField; // Вибіркове поле для вибору номера відділення клієнта
	private JComboBox<String> clientAccidentIdField; // Вибіркове поле для вибору ID аварії клієнта

	private enum FIELD_TYPES {
		MASTER_FIELD, CLIENT_FIELD, ACCIDENT_FIELD
	}

	public UserInterface(Main main) {
		this.main = main;
		this.resultArea = createResultsTextArea(); // Створення текстової області для результатів
		createAndShowGUI(); // Створення та відображення графічного інтерфейсу
	}

	private void createAndShowGUI() {
		frame = new JFrame("Водопостачальні послуги");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Майстри", createMastersTab());
		tabbedPane.addTab("Клієнти", createClientsTab());
		tabbedPane.addTab("Аварії", createAccidentsTab());

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, createResultsPanel());
		splitPane.setResizeWeight(0.5);

		frame.add(splitPane, BorderLayout.CENTER);
		frame.pack();
		Dimension windowDimens = new Dimension(frame.getWidth() + frame.getWidth() / 2, frame.getHeight() + frame.getHeight() / 4);
		frame.setMinimumSize(windowDimens);
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitApplication(); // Обробка закриття вікна
			}
		});

		frame.setVisible(true);
	}

	private JPanel createMastersTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(8, 1));

		JButton countMastersButton = new JButton("Кількість майстрів");
		JButton masterSalaryButton = new JButton("Заробіток майстра з... по...");
		JButton countBrigadesButton = new JButton("Кількість бригад");
		JButton addMasterButton = new JButton("Додати майстра");
		JButton deleteMasterButton = new JButton("Видалити майстра");
		JButton updateMasterButton = new JButton("Оновити майстра");
		JButton listMastersButton = new JButton("Список майстрів");
		JButton exitButton = new JButton("Вихід");

		Dimension buttonSize = new Dimension(250, 20);
		countMastersButton.setPreferredSize(buttonSize);
		masterSalaryButton.setPreferredSize(buttonSize);
		countBrigadesButton.setPreferredSize(buttonSize);
		addMasterButton.setPreferredSize(buttonSize);
		deleteMasterButton.setPreferredSize(buttonSize);
		updateMasterButton.setPreferredSize(buttonSize);
		listMastersButton.setPreferredSize(buttonSize);
		exitButton.setPreferredSize(buttonSize);

		buttonPanel.add(countMastersButton);
		buttonPanel.add(masterSalaryButton);
		buttonPanel.add(countBrigadesButton);
		buttonPanel.add(addMasterButton);
		buttonPanel.add(deleteMasterButton);
		buttonPanel.add(updateMasterButton);
		buttonPanel.add(listMastersButton);
		buttonPanel.add(exitButton);

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		addField(fieldsPanel, gbc, "Ім'я та прізвище:", addMasterNameField = new JTextField(30));
		addField(fieldsPanel, gbc, "Адреса:", addMasterAddressField = new JTextField(30));
		addField(fieldsPanel, gbc, "Номер телефону:", addMasterPhoneField = new JTextField(30));
		addField(fieldsPanel, gbc, "Стаж:", addMasterExperienceField = new JTextField(30));
		addField(fieldsPanel, gbc, "Стан:", addMasterStatusField = new JComboBox<>(main.getMasterStatuses().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер бригади:", addMasterBrigadeField = new JComboBox<>(main.getBrigadeAndDepartmentNumbers().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер відділення:", addMasterDepartmentField = new JComboBox<>(main.getBrigadeAndDepartmentNumbers().toArray(new String[0])));

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Індекс майстра:", masterIndexField = new JComboBox<>());

		masterIndexField.addActionListener(e -> {
			String selectedId = (String) masterIndexField.getSelectedItem();
			if (selectedId != null) {
				int masterIndex = Integer.parseInt(selectedId);
				loadMasterData(masterIndex); // Завантаження даних про вибраного майстра
			}
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Початкова дата (dd/MM/yyyy):", masterStartDateField = new JTextField(30));
		addField(fieldsPanel, gbc, "Кінцева дата (dd/MM/yyyy):", masterEndDateField = new JTextField(30));

		populateMasterComboBoxes(); // Заповнення вибіркових полів даними про майстрів

		panel.add(buttonPanel, BorderLayout.EAST);
		panel.add(fieldsPanel, BorderLayout.CENTER);

		countMastersButton.addActionListener(e -> countMasters()); // Обробка натискання кнопки "Кількість майстрів"
		masterSalaryButton.addActionListener(e -> calculateMasterSalary()); // Обробка натискання кнопки "Заробіток майстра з... по..."
		countBrigadesButton.addActionListener(e -> countBrigades()); // Обробка натискання кнопки "Кількість бригад"
		addMasterButton.addActionListener(e -> addMaster()); // Обробка натискання кнопки "Додати майстра"
		deleteMasterButton.addActionListener(e -> deleteMaster()); // Обробка натискання кнопки "Видалити майстра"
		updateMasterButton.addActionListener(e -> updateMaster()); // Обробка натискання кнопки "Оновити майстра"
		listMastersButton.addActionListener(e -> listMasters()); // Обробка натискання кнопки "Список майстрів"
		exitButton.addActionListener(e -> exitApplication()); // Обробка натискання кнопки "Вихід"

		return panel;
	}

	private JPanel createAccidentsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(8, 1));

		JButton countAccidentsButton = new JButton("Кількість аварій з... по...");
		JButton averageCostButton = new JButton("Середня вартість усунення аварії");
		JButton accidentTypesButton = new JButton("Типи аварій у записах");
		JButton addAccidentButton = new JButton("Додати аварію");
		JButton deleteAccidentButton = new JButton("Видалити аварію");
		JButton listAccidentsButton = new JButton("Список аварій");
		JButton updateAccidentButton = new JButton("Оновити аварію");
		JButton exitButton = new JButton("Вихід");

		Dimension buttonSize = new Dimension(250, 20);
		countAccidentsButton.setPreferredSize(buttonSize);
		averageCostButton.setPreferredSize(buttonSize);
		accidentTypesButton.setPreferredSize(buttonSize);
		addAccidentButton.setPreferredSize(buttonSize);
		deleteAccidentButton.setPreferredSize(buttonSize);
		listAccidentsButton.setPreferredSize(buttonSize);
		updateAccidentButton.setPreferredSize(buttonSize);
		exitButton.setPreferredSize(buttonSize);

		buttonPanel.add(countAccidentsButton);
		buttonPanel.add(averageCostButton);
		buttonPanel.add(accidentTypesButton);
		buttonPanel.add(addAccidentButton);
		buttonPanel.add(deleteAccidentButton);
		buttonPanel.add(updateAccidentButton);
		buttonPanel.add(listAccidentsButton);
		buttonPanel.add(exitButton);

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		addField(fieldsPanel, gbc, "Дата (dd/MM/yyyy):", addAccidentDateField = new JTextField(30));
		addField(fieldsPanel, gbc, "Адреса:", addAccidentAddressField = new JTextField(30));
		addField(fieldsPanel, gbc, "Тип аварії:", addAccidentTypeField = new JComboBox<>(main.getAccidentTypes().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Вартість:", addAccidentCostField = new JTextField(30));
		addField(fieldsPanel, gbc, "Стан:", addAccidentStatusField = new JComboBox<>(main.getAccidentStatuses().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер бригади:", addAccidentBrigadeField = new JComboBox<>(main.getUsedDepartmentNumbers().toArray(new String[0])));

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "ID аварії:", accidentIdField = new JComboBox<>());
		accidentIdField.addActionListener(e -> {
			String selectedId = (String) accidentIdField.getSelectedItem();
			if (selectedId != null) {
				loadAccidentData(selectedId); // Завантаження даних про вибрану аварію
			}
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Початкова дата (dd/MM/yyyy):", accidentStartDateField = new JTextField(30));
		addField(fieldsPanel, gbc, "Кінцева дата (dd/MM/yyyy):", accidentEndDateField = new JTextField(30));

		populateAccidentComboBoxes(); // Заповнення вибіркових полів даними про аварії

		panel.add(buttonPanel, BorderLayout.EAST);
		panel.add(fieldsPanel, BorderLayout.CENTER);

		countAccidentsButton.addActionListener(e -> countAccidents()); // Обробка натискання кнопки "Кількість аварій з... по..."
		averageCostButton.addActionListener(e -> averageAccidentCost()); // Обробка натискання кнопки "Середня вартість усунення аварії"
		accidentTypesButton.addActionListener(e -> accidentTypes()); // Обробка натискання кнопки "Типи аварій"
		addAccidentButton.addActionListener(e -> addAccident()); // Обробка натискання кнопки "Додати аварію"
		deleteAccidentButton.addActionListener(e -> deleteAccident()); // Обробка натискання кнопки "Видалити аварію"
		listAccidentsButton.addActionListener(e -> listAccidents()); // Обробка натискання кнопки "Список аварій"
		updateAccidentButton.addActionListener(e -> updateAccident()); // Обробка натискання кнопки "Оновити аварію"
		exitButton.addActionListener(e -> exitApplication()); // Обробка натискання кнопки "Вихід"

		return panel;
	}

	private JPanel createClientsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(6, 1));

		JButton countClientsButton = new JButton("Кількість клієнтів");
		JButton addClientButton = new JButton("Додати клієнта");
		JButton deleteClientButton = new JButton("Видалити клієнта");
		JButton updateClientButton = new JButton("Оновити клієнта");
		JButton listClientsButton = new JButton("Список клієнтів");
		JButton exitButton = new JButton("Вихід");

		Dimension buttonSize = new Dimension(250, 20);
		countClientsButton.setPreferredSize(buttonSize);
		addClientButton.setPreferredSize(buttonSize);
		deleteClientButton.setPreferredSize(buttonSize);
		updateClientButton.setPreferredSize(buttonSize);
		listClientsButton.setPreferredSize(buttonSize);
		exitButton.setPreferredSize(buttonSize);

		buttonPanel.add(countClientsButton);
		buttonPanel.add(addClientButton);
		buttonPanel.add(deleteClientButton);
		buttonPanel.add(updateClientButton);
		buttonPanel.add(listClientsButton);
		buttonPanel.add(exitButton);

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		addField(fieldsPanel, gbc, "Ім'я та прізвище:", addClientNameField = new JTextField(30));
		addField(fieldsPanel, gbc, "Адреса:", addClientAddressField = new JTextField(30));
		addField(fieldsPanel, gbc, "Номер телефону:", addClientPhoneField = new JTextField(30));
		addField(fieldsPanel, gbc, "Номер відділення:", addClientDepartmentField = new JComboBox<>(main.getUsedDepartmentNumbers().toArray(new String[0])));

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "ID аварії:", clientAccidentIdField = new JComboBox<>());

		clientAccidentIdField.addActionListener(e -> {
			String selectedId = (String) clientAccidentIdField.getSelectedItem();
			if (selectedId != null) {
				loadClientData(selectedId); // Завантаження даних про вибраного клієнта
			}
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		populateClientComboBoxes(); // Заповнення вибіркових полів даними про клієнтів

		panel.add(buttonPanel, BorderLayout.EAST);
		panel.add(fieldsPanel, BorderLayout.CENTER);

		countClientsButton.addActionListener(e -> countClients()); // Обробка натискання кнопки "Кількість клієнтів"
		addClientButton.addActionListener(e -> addClient()); // Обробка натискання кнопки "Додати клієнта"
		deleteClientButton.addActionListener(e -> deleteClient()); // Обробка натискання кнопки "Видалити клієнта"
		updateClientButton.addActionListener(e -> updateClient()); // Обробка натискання кнопки "Оновити клієнта"
		listClientsButton.addActionListener(e -> listClients()); // Обробка натискання кнопки "Список клієнтів"
		exitButton.addActionListener(e -> exitApplication()); // Обробка натискання кнопки "Вихід"

		return panel;
	}

	private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.4;
		panel.add(new JLabel(label), gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.6;
		panel.add(field, gbc);
	}

	private JTextArea createResultsTextArea() {
		JTextArea resultArea = new JTextArea();
		resultArea.setEditable(false);

		Font monospacedFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
		resultArea.setFont(monospacedFont);

		return resultArea;
	}

	private JScrollPane createResultsPanel() {
		return new JScrollPane(resultArea);
	}

	private void clearAddFields(FIELD_TYPES entity) {
		switch (entity) {
			case MASTER_FIELD:
				addMasterNameField.setText("");
				addMasterAddressField.setText("");
				addMasterPhoneField.setText("");
				addMasterExperienceField.setText("");
				addMasterStatusField.setSelectedIndex(-1);
				addMasterBrigadeField.setSelectedIndex(-1);
				addMasterDepartmentField.setSelectedIndex(-1);
				break;
			case ACCIDENT_FIELD:
				addAccidentDateField.setText("");
				addAccidentAddressField.setText("");
				addAccidentTypeField.setSelectedIndex(-1);
				addAccidentCostField.setText("");
				addAccidentStatusField.setSelectedIndex(-1);
				addAccidentBrigadeField.setSelectedIndex(-1);
				break;
			case CLIENT_FIELD:
				addClientNameField.setText("");
				addClientAddressField.setText("");
				addClientPhoneField.setText("");
				addClientDepartmentField.setSelectedIndex(-1);
				break;
		}
	}

	private void clearDeleteFields(FIELD_TYPES entity) {
		switch (entity) {
			case MASTER_FIELD:
				masterIndexField.setSelectedIndex(-1);
				break;
			case ACCIDENT_FIELD:
				accidentIdField.setSelectedIndex(-1);
				break;
			case CLIENT_FIELD:
				clientAccidentIdField.setSelectedIndex(-1);
				break;
		}
	}

	private Date parseDate(String dateStr) {
		try {
			return main.getDateFormat().parse(dateStr);
		} catch (ParseException e) {
			showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
			return null;
		}
	}

	private void countMasters() {
		resultArea.setText("Кількість майстрів: " + main.getMasters().size());
	}

	private void calculateMasterSalary() {
		Date startDate = parseDate(masterStartDateField.getText());
		Date endDate = parseDate(masterEndDateField.getText());
		int masterIndex;
		try {
			validateField(masterIndexField, "Індекс майстра");
			masterIndex = Integer.parseInt(masterIndexField.getSelectedItem().toString());
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
			return;
		} catch (Exception ex) {
			showErrorOptionPane("Помилка: " + ex.getMessage());
			return;
		}

		if (startDate != null && endDate != null && masterIndex != -1) {
			double salary = main.getMasterSalaryBetweenDates(masterIndex, startDate, endDate);
			resultArea.setText("Заробіток майстра з індексом " + masterIndex + " з " + main.getDateFormat().format(startDate) + " по " + main.getDateFormat().format(endDate) + " : " + salary);
		} else {
			resultArea.setText("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
		}
	}

	private void countBrigades() {
		resultArea.setText("Кількість бригад: " + main.getBrigadeCount());
	}

	private void addMaster() {
		try {
			validateField(addMasterNameField, "Ім'я та прізвище");
			validateField(addMasterAddressField, "Адреса");
			validateField(addMasterPhoneField, "Номер телефону");
			validateField(addMasterExperienceField, "Стаж");
			validateField(addMasterStatusField, "Стан");
			validateField(addMasterBrigadeField, "Номер бригади");
			validateField(addMasterDepartmentField, "Номер відділення");

			String name = addMasterNameField.getText();
			String address = addMasterAddressField.getText();
			String phoneNumber = addMasterPhoneField.getText();
			int experience = Integer.parseInt(addMasterExperienceField.getText());
			String status = (String) addMasterStatusField.getSelectedItem();
			int brigadeNumber = Integer.parseInt((String) addMasterBrigadeField.getSelectedItem());
			int departmentNumber = Integer.parseInt((String) addMasterDepartmentField.getSelectedItem());

			int newIndex = main.getNextMasterIndex();
			Brigade brigade = new Brigade(brigadeNumber);
			Master master = new Master(newIndex, name, address, phoneNumber, experience, status, brigade, departmentNumber);
			main.getMasters().add(master);
			brigade.addMaster(master);
			main.saveAllData();
			showSuccessOptionPane("Майстра додано успішно!");
			clearAddFields(FIELD_TYPES.MASTER_FIELD);
			populateMasterComboBoxes();
			populateClientComboBoxes();
			populateAccidentComboBoxes();
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка додавання майстра: " + ex.getMessage());
		}
	}

	private void deleteMaster() {
		try {
			validateField(masterIndexField, "Індекс майстра");

			int masterIndex = Integer.parseInt(masterIndexField.getSelectedItem().toString());
			Master masterToRemove = main.getMasterByIndex(masterIndex);
			if (masterToRemove != null) {
				main.getMasters().remove(masterToRemove);
				masterToRemove.getBrigade().removeMaster(masterToRemove);
				main.saveAllData();
				showSuccessOptionPane("Майстра з індексом " + masterIndex + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.MASTER_FIELD);
				populateMasterComboBoxes();
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Майстра з індексом " + masterIndex + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка видалення майстра: " + ex.getMessage());
		}
	}

	private void updateMaster() {
		try {
			validateField(addMasterNameField, "Ім'я та прізвище");
			validateField(addMasterAddressField, "Адреса");
			validateField(addMasterPhoneField, "Номер телефону");
			validateField(addMasterExperienceField, "Стаж");
			validateField(addMasterStatusField, "Стан");
			validateField(addMasterBrigadeField, "Номер бригади");
			validateField(addMasterDepartmentField, "Номер відділення");
			validateField(masterIndexField, "Індекс майстра");

			int masterIndex = Integer.parseInt(masterIndexField.getSelectedItem().toString());
			Master masterToUpdate = main.getMasterByIndex(masterIndex);
			if (masterToUpdate != null) {
				String name = addMasterNameField.getText();
				String address = addMasterAddressField.getText();
				String phoneNumber = addMasterPhoneField.getText();
				int experience = Integer.parseInt(addMasterExperienceField.getText());
				String status = (String) addMasterStatusField.getSelectedItem();
				int brigadeNumber = Integer.parseInt((String) addMasterBrigadeField.getSelectedItem());
				int departmentNumber = Integer.parseInt((String) addMasterDepartmentField.getSelectedItem());

				masterToUpdate.setName(name);
				masterToUpdate.setAddress(address);
				masterToUpdate.setPhoneNumber(phoneNumber);
				masterToUpdate.setExperience(experience);
				masterToUpdate.setStatus(status);
				masterToUpdate.setBrigade(new Brigade(brigadeNumber));
				masterToUpdate.setDepartmentNumber(departmentNumber);

				main.saveAllData();
				showSuccessOptionPane("Майстра з індексом " + masterIndex + " оновлено успішно!");
				clearAddFields(FIELD_TYPES.MASTER_FIELD);
				populateMasterComboBoxes();
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Майстра з індексом " + masterIndex + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка оновлення майстра: " + ex.getMessage());
		}
	}

	private void listMasters() {
		StringBuilder sb = new StringBuilder("Список майстрів:\n");

		int indexWidth = "Індекс".length();
		int nameWidth = "Ім'я та прізвище".length();
		int addressWidth = "Адреса".length();
		int phoneWidth = "Номер телефону".length();
		int experienceWidth = "Стаж".length();
		int statusWidth = "Стан".length();
		int brigadeWidth = "Номер бригади".length();
		int departmentWidth = "Номер відділення".length();

		for (Master master : main.getMasters()) {
			indexWidth = Math.max(indexWidth, String.valueOf(master.getIndex()).length());
			nameWidth = Math.max(nameWidth, master.getName().length());
			addressWidth = Math.max(addressWidth, master.getAddress().length());
			phoneWidth = Math.max(phoneWidth, master.getPhoneNumber().length());
			experienceWidth = Math.max(experienceWidth, (master.getExperience() + " років").length());
			statusWidth = Math.max(statusWidth, master.getStatus().length());
			brigadeWidth = Math.max(brigadeWidth, String.valueOf(master.getBrigade().getNumber()).length());
			departmentWidth = Math.max(departmentWidth, String.valueOf(master.getDepartmentNumber()).length());
		}

		String format = String.format("%%-%ds %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds\n",
				indexWidth, nameWidth, addressWidth, phoneWidth, experienceWidth, statusWidth, brigadeWidth, departmentWidth);

		sb.append(String.format(format, "Індекс", "Ім'я та прізвище", "Адреса", "Номер телефону", "Стаж", "Стан", "Номер бригади", "Номер відділення"));
		sb.append(String.format(format, "-".repeat(indexWidth), "-".repeat(nameWidth), "-".repeat(addressWidth), "-".repeat(phoneWidth), "-".repeat(experienceWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth), "-".repeat(departmentWidth)));

		for (Master master : main.getMasters()) {
			sb.append(String.format(format,
					master.getIndex(),
					master.getName(),
					master.getAddress(),
					master.getPhoneNumber(),
					master.getExperience() + " років",
					master.getStatus(),
					master.getBrigade().getNumber(),
					master.getDepartmentNumber()
			));
		}
		resultArea.setText(sb.toString());
	}

	private void countAccidents() {
		Date startDate = parseDate(accidentStartDateField.getText());
		Date endDate = parseDate(accidentEndDateField.getText());
		if (startDate != null && endDate != null) {
			int count;
			try {
				count = main.getAccidentsBetweenDates(startDate, endDate);
				resultArea.setText("Кількість аварій з " + main.getDateFormat().format(startDate) + " по " + main.getDateFormat().format(endDate) + ": " + count);
			} catch (IllegalArgumentException ex) {
				showErrorOptionPane(ex.getMessage());
				resultArea.setText("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
			}
		} else {
			resultArea.setText("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
		}
	}

	private void averageAccidentCost() {
		resultArea.setText("Середня вартість усунення аварії: " + main.getAverageAccidentCost());
	}

	private void accidentTypes() {
		Set<String> types = main.getExistingAccidentTypes();
		StringBuilder sb = new StringBuilder("Типи аварій у записах:\n");
		for (String type : types) {
			sb.append(type).append("\n");
		}
		resultArea.setText(sb.toString());
	}

	private void addAccident() {
		try {
			validateField(addAccidentDateField, "Дата");
			validateField(addAccidentAddressField, "Адреса");
			validateField(addAccidentTypeField, "Тип аварії");
			validateField(addAccidentCostField, "Вартість");
			validateField(addAccidentStatusField, "Стан");
			validateField(addAccidentBrigadeField, "Номер бригади");
			validateField(accidentIdField, "ID аварії");

			Date date = parseDate(addAccidentDateField.getText());
			String address = addAccidentAddressField.getText();
			String type = (String) addAccidentTypeField.getSelectedItem();
			double cost = Double.parseDouble(addAccidentCostField.getText());
			String status = (String) addAccidentStatusField.getSelectedItem();
			int brigadeNumber = Integer.parseInt((String) addAccidentBrigadeField.getSelectedItem());

			int newIndex = main.getNextAccidentIndex();
			String newAccidentId = newIndex + "-" + Main.generateRandomString(8);

			Brigade brigade = new Brigade(brigadeNumber);
			Accident accident = new Accident(newIndex, newAccidentId, address, type, cost, date, brigade, status);
			main.getAccidents().add(accident);
			main.saveAllData();
			showSuccessOptionPane("Аварію додано успішно!");
			clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
			populateAccidentComboBoxes();
			populateClientComboBoxes();
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка додавання аварії: " + ex.getMessage());
		}
	}

	private void deleteAccident() {
		try {
			validateField(accidentIdField, "ID аварії");

			String accidentId = (String) accidentIdField.getSelectedItem();
			Accident accidentToRemove = main.getAccidentById(accidentId);
			if (accidentToRemove != null) {
				main.getAccidents().remove(accidentToRemove);

				Client clientToRemove = main.getClientByAccidentId(accidentId);
				if (clientToRemove != null) {
					main.getClients().remove(clientToRemove);
				}
				main.saveAllData();
				showSuccessOptionPane("Аварію з ID " + accidentId + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAccidentComboBoxes();
				populateClientComboBoxes();
			} else {
				showErrorOptionPane("Аварію з ID " + accidentId + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка видалення аварії: " + ex.getMessage());
		}
	}

	private void updateAccident() {
		try {
			validateField(addAccidentDateField, "Дата");
			validateField(addAccidentAddressField, "Адреса");
			validateField(addAccidentTypeField, "Тип аварії");
			validateField(addAccidentCostField, "Вартість");
			validateField(addAccidentStatusField, "Стан");
			validateField(addAccidentBrigadeField, "Номер бригади");
			validateField(accidentIdField, "ID аварії");

			String accidentId = (String) accidentIdField.getSelectedItem();
			Accident accidentToUpdate = main.getAccidentById(accidentId);
			if (accidentToUpdate != null) {
				Date date = parseDate(addAccidentDateField.getText());
				String address = addAccidentAddressField.getText();
				String type = (String) addAccidentTypeField.getSelectedItem();
				double cost = Double.parseDouble(addAccidentCostField.getText());
				String status = (String) addAccidentStatusField.getSelectedItem();
				int brigadeNumber = Integer.parseInt((String) addAccidentBrigadeField.getSelectedItem());

				accidentToUpdate.setDate(date);
				accidentToUpdate.setAddress(address);
				accidentToUpdate.setType(type);
				accidentToUpdate.setCost(cost);
				accidentToUpdate.setStatus(status);
				accidentToUpdate.setBrigade(new Brigade(brigadeNumber));

				Client clientToUpdate = main.getClientByAccidentId(accidentId);
				if (clientToUpdate != null) {
					clientToUpdate.setAddress(address);
					clientToUpdate.setName(addClientNameField.getText());
					clientToUpdate.setPhoneNumber(addClientPhoneField.getText());
					clientToUpdate.setDepartmentNumber(Integer.parseInt((String) addClientDepartmentField.getSelectedItem()));
				}

				main.saveAllData();
				showSuccessOptionPane("Аварію з ID " + accidentId + " оновлено успішно!");
				clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAccidentComboBoxes();
				populateClientComboBoxes();
			} else {
				showErrorOptionPane("Аварію з ID " + accidentId + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка оновлення аварії: " + ex.getMessage());
		}
	}

	private void listAccidents() {
		StringBuilder sb = new StringBuilder("Список аварій:\n");

		int idWidth = "ID аварії".length();
		int dateWidth = "Дата".length();
		int addressWidth = "Адреса".length();
		int typeWidth = "Тип аварії".length();
		int costWidth = "Вартість".length();
		int statusWidth = "Стан".length();
		int brigadeWidth = "Номер бригади".length();

		for (Accident accident : main.getAccidents()) {
			idWidth = Math.max(idWidth, accident.getAccidentId().length());
			dateWidth = Math.max(dateWidth, main.getDateFormat().format(accident.getDate()).length());
			addressWidth = Math.max(addressWidth, accident.getAddress().length());
			typeWidth = Math.max(typeWidth, accident.getType().length());
			costWidth = Math.max(costWidth, (accident.getCost() + " грн").length());
			statusWidth = Math.max(statusWidth, accident.getStatus().length());
			brigadeWidth = Math.max(brigadeWidth, String.valueOf(accident.getBrigade().getNumber()).length());
		}

		String format = String.format("%%-%ds %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds %%-%ds\n",
				idWidth, dateWidth, addressWidth, typeWidth, costWidth, statusWidth, brigadeWidth);

		sb.append(String.format(format, "ID аварії", "Дата", "Адреса", "Тип аварії", "Вартість", "Стан", "Номер бригади"));
		sb.append(String.format(format, "-".repeat(idWidth), "-".repeat(dateWidth), "-".repeat(addressWidth), "-".repeat(typeWidth), "-".repeat(costWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth)));

		for (Accident accident : main.getAccidents()) {
			sb.append(String.format(format,
					accident.getAccidentId(),
					main.getDateFormat().format(accident.getDate()),
					accident.getAddress(),
					accident.getType(),
					accident.getCost() + " грн",
					accident.getStatus(),
					accident.getBrigade().getNumber()
			));
		}
		resultArea.setText(sb.toString());
	}

	private void countClients() {
		resultArea.setText("Кількість клієнтів: " + main.getClients().size());
	}

	private void addClient() {
		try {
			validateField(addClientNameField, "Ім'я та прізвище");
			validateField(addClientAddressField, "Адреса");
			validateField(addClientPhoneField, "Номер телефону");
			validateField(addClientDepartmentField, "Номер відділення");

			String name = addClientNameField.getText();
			String address = addClientAddressField.getText();
			String phoneNumber = addClientPhoneField.getText();
			int departmentNumber = Integer.parseInt((String) addClientDepartmentField.getSelectedItem());

			int newIndex = main.getNextClientId();
			String accidentId = (String) clientAccidentIdField.getSelectedItem();

			if (accidentId == null) {
				int newAccidentIndex = main.getNextAccidentIndex();
				accidentId = newAccidentIndex + "-" + Main.generateRandomString(8);

				Date currentDate = new Date();
				String accidentType = "Новий";
				double accidentCost = 0.0;
				String accidentStatus = "Новий";
				int brigadeNumber = 1;

				Brigade brigade = new Brigade(brigadeNumber);
				Accident accident = new Accident(newAccidentIndex, accidentId, address,
						accidentType, accidentCost, currentDate,
						brigade, accidentStatus);
				main.getAccidents().add(accident);
			}

			Client client = new Client(newIndex, accidentId, name, address, phoneNumber, departmentNumber);
			main.getClients().add(client);

			Accident accident = main.getAccidentById(accidentId);
			if (accident != null) {
				accident.setAccidentIdToClientId(client);
				main.getAccidents().add(accident);
			}

			main.saveAllData();
			showSuccessOptionPane("Клієнта додано успішно!");
			clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			populateClientComboBoxes();
			populateAccidentComboBoxes();
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка додавання клієнта: " + ex.getMessage());
		}
	}

	private void deleteClient() {
		try {
			validateField(clientAccidentIdField, "ID аварії");

			String accidentId = (String) clientAccidentIdField.getSelectedItem();
			Client clientToRemove = main.getClientByAccidentId(accidentId);
			if (clientToRemove != null) {
				main.getClients().remove(clientToRemove);

				Accident accidentToRemove = main.getAccidentById(accidentId);
				if (accidentToRemove != null) {
					main.getAccidents().remove(accidentToRemove);
				}
				main.saveAllData();
				showSuccessOptionPane("Клієнта з ID аварії " + accidentId + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.CLIENT_FIELD);
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Клієнта з ID аварії " + accidentId + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка видалення клієнта: " + ex.getMessage());
		}
	}

	private void updateClient() {
		try {
			validateField(addClientNameField, "Ім'я та прізвище");
			validateField(addClientAddressField, "Адреса");
			validateField(addClientPhoneField, "Номер телефону");
			validateField(addClientDepartmentField, "Номер відділення");
			validateField(clientAccidentIdField, "ID аварії");

			String accidentId = (String) clientAccidentIdField.getSelectedItem();
			Client clientToUpdate = main.getClientByAccidentId(accidentId);
			if (clientToUpdate != null) {
				clientToUpdate.setName(addClientNameField.getText());
				clientToUpdate.setAddress(addClientAddressField.getText());
				clientToUpdate.setPhoneNumber(addClientPhoneField.getText());
				clientToUpdate.setDepartmentNumber(Integer.parseInt((String) addClientDepartmentField.getSelectedItem()));

				Accident accidentToUpdate = main.getAccidentById(accidentId);
				if (accidentToUpdate != null) {
					accidentToUpdate.setAddress(addClientAddressField.getText());
				}

				main.saveAllData();
				showSuccessOptionPane("Клієнта з ID аварії " + accidentId + " оновлено успішно!");
				clearAddFields(FIELD_TYPES.CLIENT_FIELD);
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Клієнта з ID аварії " + accidentId + " не знайдено.");
			}
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane(ex.getMessage());
		} catch (Exception ex) {
			showErrorOptionPane("Помилка оновлення клієнта: " + ex.getMessage());
		}
	}

	private void listClients() {
		StringBuilder sb = new StringBuilder("Список клієнтів:\n");

		int accidentIdWidth = "ID аварії".length();
		int nameWidth = "Ім'я та прізвище".length();
		int addressWidth = "Адреса".length();
		int phoneWidth = "Номер телефону".length();
		int departmentWidth = "Номер відділення".length();

		for (Client client : main.getClients()) {
			accidentIdWidth = Math.max(accidentIdWidth, client.getAccidentId().length());
			nameWidth = Math.max(nameWidth, client.getName().length());
			addressWidth = Math.max(addressWidth, client.getAddress().length());
			phoneWidth = Math.max(phoneWidth, client.getPhoneNumber().length());
			departmentWidth = Math.max(departmentWidth, String.valueOf(client.getDepartmentNumber()).length());
		}

		String format = String.format("%%-%ds %%-%ds %%-%ds %%-%ds %%-%ds\n",
				accidentIdWidth, nameWidth, addressWidth, phoneWidth, departmentWidth);

		sb.append(String.format(format, "ID аварії", "Ім'я та прізвище", "Адреса", "Номер телефону", "Номер відділення"));
		sb.append(String.format(format, "-".repeat(accidentIdWidth), "-".repeat(nameWidth), "-".repeat(addressWidth), "-".repeat(phoneWidth), "-".repeat(departmentWidth)));

		for (Client client : main.getClients()) {
			sb.append(String.format(format,
					client.getAccidentId(),
					client.getName(),
					client.getAddress(),
					client.getPhoneNumber(),
					client.getDepartmentNumber()
			));
		}
		resultArea.setText(sb.toString());
	}

	private void exitApplication() {
		int option = JOptionPane.showConfirmDialog(null,
				"Ви хочете зберегти дані перед виходом?",
				"Підтвердження",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (option == JOptionPane.YES_OPTION) {
			main.saveAllData();
			showSuccessOptionPane("Дані збережено!");
		} else if (option == JOptionPane.NO_OPTION) {
			// Закриваємо додаток
		} else if (option == JOptionPane.CANCEL_OPTION) {
			return;
		}

		// Закриття зв'язку з БД
		try {
			main.dbManager.close();
		} catch (SQLException e) {
			e.printStackTrace();
			showErrorOptionPane("Не вдалося закрити зв'язок з БД");
		}

		// Закриття вікна
		frame.dispose();
	}

	private void validateField(JComponent field, String fieldName) {
		if (field instanceof JTextField textField) {
			if (textField.getText().trim().isEmpty()) {
				throw new IllegalArgumentException("Поле " + fieldName + " не може бути порожнім.");
			}
			switch (fieldName) {
				case "Ім'я та прізвище" -> validateNameField(textField, fieldName);
				case "Номер телефону" -> validatePhoneNumberField(textField, fieldName);
				case "Адреса" -> validateAddressField(textField, fieldName);
				case "Стаж", "Вартість" -> validateNumberField(textField, fieldName);
			}
		} else if (field instanceof JComboBox<?> comboBox) { // FIXME
//			if (comboBox.getSelectedIndex() == -1) {
//				throw new IllegalArgumentException("Поле " + fieldName + " не може бути порожнім.");
//			}
		} else {
			throw new IllegalArgumentException("Невідомий тип поля: " + fieldName);
		}
	}

	private void validateNameField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		for (char c : text.toCharArray()) {
			if (!Character.isLetter(c) && !Character.isSpaceChar(c)) {
				throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки літери та пробіли.");
			}
		}
	}

	private void validatePhoneNumberField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		if (!text.matches("\\+?[0-9\\s-]+")) {
			throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки цифри, пробіли, тире та символ плюс.");
		}
	}

	private void validateAddressField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		if (!text.matches("[\\p{L}0-9\\s.]+")) {
			throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки літери, цифри, пробіли та крапки.");
		}
	}

	private void validateNumberField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		for (char c : text.toCharArray()) {
			if (Character.isLetter(c) || Character.isSpaceChar(c)) {
				throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки цифри.");
			}
		}
	}

	private void showSuccessOptionPane(String message) {
		JOptionPane.showMessageDialog(frame, message, "Успіх", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showErrorOptionPane(String message) {
		JOptionPane.showMessageDialog(frame, message, "Помилка", JOptionPane.ERROR_MESSAGE);
	}

	private void populateMasterComboBoxes() {
		String[] masterIndices = main.getMasters().stream()
				.map(master -> String.valueOf(master.getIndex()))
				.toArray(String[]::new);

		masterIndexField.setModel(new DefaultComboBoxModel<>(masterIndices));

		String[] brigadeNumbers = main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
		addMasterBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));

		String[] departmentNumbers = main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
		addMasterDepartmentField.setModel(new DefaultComboBoxModel<>(departmentNumbers));
	}

	private void populateAccidentComboBoxes() {
		List<String> accidentIds = main.getAccidents().stream()
				.map(Accident::getAccidentId)
				.sorted((id1, id2) -> {
					int num1 = Integer.parseInt(id1.split("-")[0]);
					int num2 = Integer.parseInt(id2.split("-")[0]);
					return Integer.compare(num1, num2);
				})
				.toList();

		accidentIdField.setModel(new DefaultComboBoxModel<>(accidentIds.toArray(new String[0])));

		String[] brigadeNumbers = main.getUsedBrigadeNumbers().toArray(new String[0]);
		String[] accidentTypes = main.getAccidentTypes().toArray(new String[0]);

		addAccidentTypeField.setModel(new DefaultComboBoxModel<>(accidentTypes));
		addAccidentBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));
	}

	private void populateClientComboBoxes() {
		Set<String> accidentIds = new HashSet<>();

		accidentIds.addAll(main.getAccidents().stream()
				.map(Accident::getAccidentId)
				.toList());

		accidentIds.addAll(main.getClients().stream()
				.map(Client::getAccidentId)
				.toList());

		List<String> sortedIds = new ArrayList<>(accidentIds);
		sortedIds.sort((id1, id2) -> {
			int num1 = Integer.parseInt(id1.split("-")[0]);
			int num2 = Integer.parseInt(id2.split("-")[0]);
			return Integer.compare(num1, num2);
		});

		clientAccidentIdField.setModel(new DefaultComboBoxModel<>(sortedIds.toArray(new String[0])));

		String[] departmentNumbers = main.getUsedDepartmentNumbers().toArray(new String[0]);
		addClientDepartmentField.setModel(new DefaultComboBoxModel<>(departmentNumbers));
	}

	private void loadMasterData(int masterIndex) {
		Master master = main.getMasterByIndex(masterIndex);
		if (master != null) {
			addMasterNameField.setText(master.getName());
			addMasterAddressField.setText(master.getAddress());
			addMasterPhoneField.setText(master.getPhoneNumber());
			addMasterExperienceField.setText(String.valueOf(master.getExperience()));
			addMasterStatusField.setSelectedItem(master.getStatus());
			addMasterBrigadeField.setSelectedItem(String.valueOf(master.getBrigade().getNumber()));
			addMasterDepartmentField.setSelectedItem(String.valueOf(master.getDepartmentNumber()));
		} else {
			clearAddFields(FIELD_TYPES.MASTER_FIELD);
		}
	}

	private void loadClientData(String accidentId) {
		Client client = main.getClientByAccidentId(accidentId);

		// Always get the address from the accident first
		Accident accident = main.getAccidentById(accidentId);
		String address = (accident != null) ? accident.getAddress() : "";

		if (client != null) {
			// Use accident address if available, otherwise use client address
			String displayAddress = !address.isEmpty() ? address : client.getAddress();

			addClientNameField.setText(client.getName());
			addClientAddressField.setText(displayAddress);
			addClientPhoneField.setText(client.getPhoneNumber());
			addClientDepartmentField.setSelectedItem(String.valueOf(client.getDepartmentNumber()));
		} else {
			// No client exists, but accident might
			clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			addClientAddressField.setText(address);
		}
	}

	private void loadAccidentData(String accidentId) {
		Accident accident = main.getAccidentById(accidentId);
		if (accident != null) {
			addAccidentDateField.setText(main.getDateFormat().format(accident.getDate()));
			addAccidentAddressField.setText(accident.getAddress());
			addAccidentTypeField.setSelectedItem(accident.getType());
			addAccidentCostField.setText(String.valueOf(accident.getCost()));
			addAccidentStatusField.setSelectedItem(accident.getStatus());
			addAccidentBrigadeField.setSelectedItem(String.valueOf(accident.getBrigade().getNumber()));
		} else {
			clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
		}
	}
}