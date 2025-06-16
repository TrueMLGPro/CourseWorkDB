package com.vitaliyrev.courseworkdb.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.*;

// Клас "Інтерфейс користувача"
public class UserInterface {
	private final Main main; // Посилання на головний об'єкт програми
	public JFrame frame; // Основне вікно програми
	private final JTextArea resultArea; // Текстова область для виводу результатів
	private JLabel connectionStatusLabel; // Статус підключення до БД
	private JTextField masterStartDateField; // Поле для введення початкової дати для майстрів
	private JTextField masterEndDateField; // Поле для введення кінцевої дати для майстрів
	private JTextField addMasterNameField; // Поле для введення імені та прізвища майстра
	private JTextField addMasterAddressField; // Поле для введення адреси майстра
	private JTextField addMasterPhoneField; // Поле для введення номеру телефону майстра
	private JTextField addMasterExperienceField; // Поле для введення стажу майстра
	private JComboBox<String> addMasterStatusField; // Вибіркове поле для вибору стану майстра
	private JComboBox<String> addMasterBrigadeField; // Вибіркове поле для вибору номера бригади майстра
	private JComboBox<String> addMasterDepartmentField; // Вибіркове поле для вибору номера відділення майстра
	private JComboBox<String> masterListNameField; // Вибіркове поле для вибору індекса майстра
	private JTextField accidentStartDateField; // Поле для введення початкової дати для аварій
	private JTextField accidentEndDateField; // Поле для введення кінцевої дати для аварій
	private JTextField addAccidentDateField; // Поле для введення дати аварії
	private JComboBox<String> addAccidentTypeField; // Вибіркове поле для вибору типу аварії
	private JTextField addAccidentCostField; // Поле для введення вартості аварії
	private JComboBox<String> addAccidentStatusField; // Вибіркове поле для вибору стану аварії
	private JTextField addAccidentAddressField; // Поле для введення адреси аварії
	private JComboBox<String> addAccidentBrigadeField; // Вибіркове поле для вибору номера бригади аварії
	private JComboBox<String> accidentClientPhoneNumberField; // Вибіркове поле для вибору номера телефону клієнта
	private JComboBox<String> accidentIndexField; // Вибіркове поле для вибору індексу аварії
	private JTextField addClientNameField; // Поле для введення імені та прізвища клієнта
	private JTextField addClientAddressField; // Поле для введення адреси клієнта
	private JTextField addClientPhoneField; // Поле для введення номеру телефону клієнта
	private JComboBox<String> addClientDepartmentField; // Вибіркове поле для вибору номера відділення клієнта
	private JComboBox<String> clientListNameField; // Вибіркове поле для вибору ім'я клієнта
	private JTextField reportClientNameField; // Поле для введення кінцевої дати для аварій
	private JTextField reportStartDateField; // Поле для введення початкової дати для звітів
	private JTextField reportEndDateField; // Поле для введення кінцевої дати для звітів

	private JButton countMastersButton; // Кнопка для підрахунку кількості майстрів
	private JButton masterSalaryButton; // Кнопка для розрахунку зарплати майстра
	private JButton countBrigadesButton; // Кнопка для підрахунку кількості бригад
	private JButton addMasterButton; // Кнопка для додавання майстра
	private JButton deleteMasterButton; // Кнопка для видалення майстра
	private JButton updateMasterButton; // Кнопка для оновлення даних про майстра
	private JButton listMastersButton; // Кнопка для відображення списку майстрів
	private JButton exitButton; // Кнопка виходу
	private JButton countAccidentsButton; // Кнопка для підрахунку кількості аварій
	private JButton averageCostButton; // Кнопка для розрахунку середньої вартості аварії
	private JButton accidentTypesButton; // Кнопка для відображення типів аварій
	private JButton addAccidentButton; // Кнопка для додавання аварії
	private JButton deleteAccidentButton; // Кнопка для видалення аварії
	private JButton listAccidentsButton; // Кнопка для відображення списку аварій
	private JButton updateAccidentButton; // Кнопка для оновлення даних про аварію
	private JButton countClientsButton; // Кнопка для підрахунку кількості клієнтів
	private JButton addClientButton; // Кнопка для додавання клієнта
	private JButton deleteClientButton; // Кнопка для видалення клієнта
	private JButton updateClientButton; // Кнопка для оновлення даних про клієнта
	private JButton listClientsButton; // Кнопка для відображення списку клієнтів
	private JButton mastersWithBrigadeInfoButton; // Кнопка для відображення майстрів з інформацією про бригади
	private JButton findClientsByNameButton; // Кнопка для пошуку клієнтів за ім'ям
	private JButton accidentsBetweenDatesButton; // Кнопка для відображення аварій в інтервалі дат
	private JButton accidentCountLastWeekButton; // Кнопка для відображення кількості аварій за минулий тиждень
	private JButton accidentCountPerBrigadeButton; // Кнопка для відображення кількості аварій на бригаду
	private JButton maxAccidentBrigadesButton; // Кнопка для відображення бригад з максимальною кількістю аварій
	private JButton topBrigadePerTypeButton; // Кнопка для відображення найактивніших бригад за типами аварій
	private JButton inactiveBrigadesLeftJoinButton; // Кнопка для відображення неактивних бригад (LEFT JOIN)
	private JButton inactiveBrigadesInButton; // Кнопка для відображення неактивних бригад (NOT IN)
	private JButton inactiveBrigadesExistsButton; // Кнопка для відображення неактивних бригад (EXISTS)
	private JButton brigadeActivityStatusButton; // Кнопка для відображення статусу активності бригад

	private enum FIELD_TYPES {
		MASTER_FIELD, CLIENT_FIELD, ACCIDENT_FIELD
	}

	public UserInterface(Main main) {
		this.main = main;
		this.resultArea = createResultsTextArea(); // Створення текстової області для результатів
		createAndShowGUI(); // Створення та відображення графічного інтерфейсу
		initComponentNames();
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
		tabbedPane.addTab("Звіти", createReportsTab());

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, createResultsPanel());
		splitPane.setResizeWeight(0.5);
		frame.add(splitPane, BorderLayout.CENTER);

		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		connectionStatusLabel = new JLabel("Connecting...");
		statusPanel.add(connectionStatusLabel);
		frame.add(statusPanel, BorderLayout.SOUTH);

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

		updateConnectionStatus();
		populateAllComboBoxes();
	}

	private JPanel createMastersTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(8, 1));

		countMastersButton = new JButton("Кількість майстрів");
		masterSalaryButton = new JButton("Заробіток майстра з... по...");
		countBrigadesButton = new JButton("Кількість бригад");
		addMasterButton = new JButton("Додати майстра");
		deleteMasterButton = new JButton("Видалити майстра");
		updateMasterButton = new JButton("Оновити майстра");
		listMastersButton = new JButton("Список майстрів");
		exitButton = new JButton("Вихід");

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
		addField(fieldsPanel, gbc, "Стан:", addMasterStatusField = new JComboBox<>(Main.getMasterStatuses().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер бригади:", addMasterBrigadeField = new JComboBox<>(Main.getBrigadeAndDepartmentNumbers().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер відділення:", addMasterDepartmentField = new JComboBox<>(Main.getBrigadeAndDepartmentNumbers().toArray(new String[0])));

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Майстер:", masterListNameField = new JComboBox<>());

		masterListNameField.addActionListener(e -> {
			String selectedMasterName = (String) masterListNameField.getSelectedItem();
			if (selectedMasterName != null) {
				loadMasterData(selectedMasterName);
			}
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Початкова дата (dd/MM/yyyy):", masterStartDateField = new JTextField(30));
		addField(fieldsPanel, gbc, "Кінцева дата (dd/MM/yyyy):", masterEndDateField = new JTextField(30));


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

	private JPanel createClientsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(6, 1));

		countClientsButton = new JButton("Кількість клієнтів");
		addClientButton = new JButton("Додати клієнта");
		deleteClientButton = new JButton("Видалити клієнта");
		updateClientButton = new JButton("Оновити клієнта");
		listClientsButton = new JButton("Список клієнтів");
		exitButton = new JButton("Вихід");

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

		addField(fieldsPanel, gbc, "Клієнт:", clientListNameField = new JComboBox<>());

		clientListNameField.addActionListener(e -> {
			String selectedId = (String) clientListNameField.getSelectedItem();
			if (selectedId != null) {
				loadClientData(selectedId); // Завантаження даних про вибраного клієнта
			}
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);


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

	private JPanel createAccidentsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(8, 1));

		countAccidentsButton = new JButton("Кількість аварій з... по...");
		averageCostButton = new JButton("Середня вартість усунення аварії");
		accidentTypesButton = new JButton("Типи аварій у записах");
		addAccidentButton = new JButton("Додати аварію");
		deleteAccidentButton = new JButton("Видалити аварію");
		listAccidentsButton = new JButton("Список аварій");
		updateAccidentButton = new JButton("Оновити аварію");
		exitButton = new JButton("Вихід");

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
		addField(fieldsPanel, gbc, "Тип аварії:", addAccidentTypeField = new JComboBox<>(Main.getAccidentTypes().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Вартість:", addAccidentCostField = new JTextField(30));
		addField(fieldsPanel, gbc, "Стан:", addAccidentStatusField = new JComboBox<>(Main.getAccidentStatuses().toArray(new String[0])));
		addField(fieldsPanel, gbc, "Номер бригади:", addAccidentBrigadeField = new JComboBox<>(main.getUsedDepartmentNumbers().toArray(new String[0])));

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Номер телефону клієнта:", accidentClientPhoneNumberField = new JComboBox<>());
		accidentClientPhoneNumberField.addActionListener(e -> {
			String selectedPhoneNumber = (String) accidentClientPhoneNumberField.getSelectedItem();
			populateClientAccidentIndexComboBox(selectedPhoneNumber);
		});

		addField(fieldsPanel, gbc, "ID аварії:", accidentIndexField = new JComboBox<>());
		accidentIndexField.addActionListener(e -> {
			String selectedPhoneNumber = (String) accidentClientPhoneNumberField.getSelectedItem();
			String selectedId = (String) accidentIndexField.getSelectedItem();
			loadAccidentData(selectedPhoneNumber, Integer.parseInt(selectedId)); // Завантаження даних про вибрану аварію
		});

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fieldsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

		addField(fieldsPanel, gbc, "Початкова дата (dd/MM/yyyy):", accidentStartDateField = new JTextField(30));
		addField(fieldsPanel, gbc, "Кінцева дата (dd/MM/yyyy):", accidentEndDateField = new JTextField(30));


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

	private JPanel createReportsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(11, 1));

		mastersWithBrigadeInfoButton = new JButton("Майстри з інфо про бригади");
		findClientsByNameButton = new JButton("Пошук клієнтів за іменем");
		accidentsBetweenDatesButton = new JButton("Аварії за період");
		accidentCountLastWeekButton = new JButton("Кількість аварій за тиждень");
		accidentCountPerBrigadeButton = new JButton("Аварії по бригадах");
		maxAccidentBrigadesButton = new JButton("Бригади з найбільше аварій");
		topBrigadePerTypeButton = new JButton("Найактивніші бригади по типах");
		inactiveBrigadesLeftJoinButton = new JButton("Неактивні бригади (LEFT JOIN)");
		inactiveBrigadesInButton = new JButton("Неактивні бригади (NOT IN)");
		inactiveBrigadesExistsButton = new JButton("Неактивні бригади (EXISTS)");
		brigadeActivityStatusButton = new JButton("Статус активності бригад");

		Dimension buttonSize = new Dimension(250, 20);
		mastersWithBrigadeInfoButton.setPreferredSize(buttonSize);
		findClientsByNameButton.setPreferredSize(buttonSize);
		accidentsBetweenDatesButton.setPreferredSize(buttonSize);
		accidentCountLastWeekButton.setPreferredSize(buttonSize);
		accidentCountPerBrigadeButton.setPreferredSize(buttonSize);
		maxAccidentBrigadesButton.setPreferredSize(buttonSize);
		topBrigadePerTypeButton.setPreferredSize(buttonSize);
		inactiveBrigadesLeftJoinButton.setPreferredSize(buttonSize);
		inactiveBrigadesInButton.setPreferredSize(buttonSize);
		inactiveBrigadesExistsButton.setPreferredSize(buttonSize);
		brigadeActivityStatusButton.setPreferredSize(buttonSize);

		buttonPanel.add(mastersWithBrigadeInfoButton);
		buttonPanel.add(findClientsByNameButton);
		buttonPanel.add(accidentsBetweenDatesButton);
		buttonPanel.add(accidentCountLastWeekButton);
		buttonPanel.add(accidentCountPerBrigadeButton);
		buttonPanel.add(maxAccidentBrigadesButton);
		buttonPanel.add(topBrigadePerTypeButton);
		buttonPanel.add(inactiveBrigadesLeftJoinButton);
		buttonPanel.add(inactiveBrigadesInButton);
		buttonPanel.add(inactiveBrigadesExistsButton);
		buttonPanel.add(brigadeActivityStatusButton);

		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		addField(fieldsPanel, gbc, "Ім'я клієнта:", reportClientNameField = new JTextField(20));
		addField(fieldsPanel, gbc, "Початкова дата (dd/MM/yyyy):", reportStartDateField = new JTextField(20));
		addField(fieldsPanel, gbc, "Кінцева дата (dd/MM/yyyy):", reportEndDateField = new JTextField(20));

		panel.add(buttonPanel, BorderLayout.EAST);
		panel.add(fieldsPanel, BorderLayout.CENTER);

		mastersWithBrigadeInfoButton.addActionListener(e -> {
			String result = main.getMastersWithBrigadeInfo();
			if (result != null && !result.isEmpty()) {
				resultArea.setText("Майстри з інформацією про бригади:\n" + result);
			} else {
				resultArea.setText("Не знайдено майстрів з інформацією про бригади або виникла помилка.");
			}
		});

		findClientsByNameButton.addActionListener(e -> {
			String namePattern = ((JTextField) fieldsPanel.getComponent(1)).getText();
			if (namePattern.isEmpty()) {
				showErrorOptionPane("Введіть ім'я для пошуку");
				return;
			}
			String result = main.findClientsByNamePattern(namePattern);
			if (result != null && !result.isEmpty()) {
				resultArea.setText("Клієнти за іменем '" + namePattern + "':\n" + result);
			} else {
				resultArea.setText("Не знайдено клієнтів за іменем '" + namePattern + "' або виникла помилка.");
			}
		});

		accidentsBetweenDatesButton.addActionListener(e -> {
			String startDateStr = ((JTextField) fieldsPanel.getComponent(3)).getText();
			String endDateStr = ((JTextField) fieldsPanel.getComponent(5)).getText();
			try {
				Date startDate = main.getDateFormat().parse(startDateStr);
				Date endDate = main.getDateFormat().parse(endDateStr);
				String result = main.getAccidentsBetweenDates(startDate, endDate);
				if (result != null && !result.isEmpty()) {
					resultArea.setText("Аварії з " + startDateStr + " по " + endDateStr + ":\n" + result);
				} else {
					resultArea.setText("Не знайдено аварій за вказаний період або виникла помилка.");
				}
			} catch (ParseException ex) {
				showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy");
			}
		});

		accidentCountLastWeekButton.addActionListener(e -> {
			int count = main.getAccidentCountLastWeek();
			if (count >= 0) {
				resultArea.setText("Кількість аварій за минулий тиждень: " + count);
			} else {
				resultArea.setText("Не вдалося отримати кількість аварій за минулий тиждень.");
			}
		});

		accidentCountPerBrigadeButton.addActionListener(e -> {
			Map<Integer, Integer> counts = main.getAccidentCountPerBrigade();
			StringBuilder sb = new StringBuilder("Кількість аварій по бригадах:\n");
			if (counts != null && !counts.isEmpty()) {
				counts.forEach((brigade, count) ->
						sb.append("Бригада ").append(brigade).append(": ").append(count).append(" аварій\n"));
				resultArea.setText(sb.toString());
			} else {
				resultArea.setText("Не знайдено даних про кількість аварій по бригадах або виникла помилка.");
			}
		});

		maxAccidentBrigadesButton.addActionListener(e -> {
			List<Integer> brigades = main.getBrigadesWithMaxAccidents();
			StringBuilder sb = new StringBuilder("Бригади з максимальною кількістю аварій:\n");
			if (brigades != null && !brigades.isEmpty()) {
				brigades.forEach(brigade -> sb.append("Бригада ").append(brigade).append("\n"));
				resultArea.setText(sb.toString());
			} else {
				resultArea.setText("Не знайдено бригад з максимальною кількістю аварій або виникла помилка.");
			}
		});

		topBrigadePerTypeButton.addActionListener(e -> {
			Map<String, Integer> topBrigades = main.getTopBrigadePerAccidentType();
			StringBuilder sb = new StringBuilder("Найактивніші бригади по типах аварій:\n");
			if (topBrigades != null && !topBrigades.isEmpty()) {
				topBrigades.forEach((type, brigade) ->
						sb.append(type).append(": Бригада ").append(brigade).append("\n"));
				resultArea.setText(sb.toString());
			} else {
				resultArea.setText("Не знайдено найактивніших бригад по типах аварій або виникла помилка.");
			}
		});

		inactiveBrigadesLeftJoinButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesLeftJoin();
			if (brigades != null) {
				showInactiveBrigades("LEFT JOIN", brigades);
			} else {
				resultArea.setText("Не вдалося отримати неактивні бригади (LEFT JOIN) або виникла помилка.");
			}
		});

		inactiveBrigadesInButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesIn();
			if (brigades != null) {
				showInactiveBrigades("NOT IN", brigades);
			} else {
				resultArea.setText("Не вдалося отримати неактивні бригади (NOT IN) або виникла помилка.");
			}
		});

		inactiveBrigadesExistsButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesExists();
			if (brigades != null) {
				showInactiveBrigades("NOT EXISTS", brigades);
			} else {
				resultArea.setText("Не вдалося отримати неактивні бригади (NOT EXISTS) або виникла помилка.");
			}
		});

		brigadeActivityStatusButton.addActionListener(e -> {
			List<String> statuses = main.getBrigadesWithActivityStatus();
			StringBuilder sb = new StringBuilder("Статус активності бригад:\n");
			if (statuses != null && !statuses.isEmpty()) {
				statuses.forEach(status -> sb.append(status).append("\n"));
				resultArea.setText(sb.toString());
			} else {
				resultArea.setText("Не знайдено статусів активності бригад або виникла помилка.");
			}
		});

		return panel;
	}

	public void updateConnectionStatus() {
		SwingUtilities.invokeLater(() -> {
			if (main.dbManager.isConnectionValid()) {
				connectionStatusLabel.setText("Приєднано до БД");
				connectionStatusLabel.setForeground(Color.BLACK);
			} else {
				connectionStatusLabel.setText("Від'єднано від БД");
				connectionStatusLabel.setForeground(Color.RED);
			}
		});
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
				masterListNameField.setSelectedIndex(-1);
				break;
			case ACCIDENT_FIELD:
				accidentClientPhoneNumberField.setSelectedIndex(-1);
				accidentIndexField.setSelectedIndex(-1);
				break;
			case CLIENT_FIELD:
				clientListNameField.setSelectedIndex(-1);
				break;
		}
	}

	private void initComponentNames() {
		setButtonNames();
		setComboBoxNames();
		setTextFieldNames();
		setLabelNames();
		setMiscComponentNames();
	}

	private void setButtonNames() {
		countMastersButton.setName("countMastersButton");
		masterSalaryButton.setName("masterSalaryButton");
		countBrigadesButton.setName("countBrigadesButton");
		addMasterButton.setName("addMasterButton");
		deleteMasterButton.setName("deleteMasterButton");
		updateMasterButton.setName("updateMasterButton");
		listMastersButton.setName("listMastersButton");

		countAccidentsButton.setName("countAccidentsButton");
		averageCostButton.setName("averageCostButton");
		accidentTypesButton.setName("accidentTypesButton");
		addAccidentButton.setName("addAccidentButton");
		deleteAccidentButton.setName("deleteAccidentButton");
		updateAccidentButton.setName("updateAccidentButton");
		listAccidentsButton.setName("listAccidentsButton");

		countClientsButton.setName("countClientsButton");
		addClientButton.setName("addClientButton");
		deleteClientButton.setName("deleteClientButton");
		updateClientButton.setName("updateClientButton");
		listClientsButton.setName("listClientsButton");

		mastersWithBrigadeInfoButton.setName("mastersWithBrigadeInfoButton");
		findClientsByNameButton.setName("findClientsByNameButton");
		accidentsBetweenDatesButton.setName("accidentsBetweenDatesButton");
		accidentCountLastWeekButton.setName("accidentCountLastWeekButton");
		accidentCountPerBrigadeButton.setName("accidentCountPerBrigadeButton");
		maxAccidentBrigadesButton.setName("maxAccidentBrigadesButton");
		topBrigadePerTypeButton.setName("topBrigadePerTypeButton");
		inactiveBrigadesLeftJoinButton.setName("inactiveBrigadesLeftJoinButton");
		inactiveBrigadesInButton.setName("inactiveBrigadesInButton");
		inactiveBrigadesExistsButton.setName("inactiveBrigadesExistsButton");
		brigadeActivityStatusButton.setName("brigadeActivityStatusButton");

		exitButton.setName("exitButton");
	}

	private void setComboBoxNames() {
		addMasterStatusField.setName("addMasterStatusField");
		addMasterBrigadeField.setName("addMasterBrigadeField");
		addMasterDepartmentField.setName("addMasterDepartmentField");
		masterListNameField.setName("masterListNameField");

		addAccidentTypeField.setName("addAccidentTypeField");
		addAccidentStatusField.setName("addAccidentStatusField");
		addAccidentBrigadeField.setName("addAccidentBrigadeField");
		accidentClientPhoneNumberField.setName("accidentClientPhoneNumberField");
		accidentIndexField.setName("accidentIndexField");

		addClientDepartmentField.setName("addClientDepartmentField");
		clientListNameField.setName("clientListNameField");
	}

	private void setTextFieldNames() {
		addMasterNameField.setName("addMasterNameField");
		addMasterAddressField.setName("addMasterAddressField");
		addMasterPhoneField.setName("addMasterPhoneField");
		addMasterExperienceField.setName("addMasterExperienceField");
		masterStartDateField.setName("masterStartDateField");
		masterEndDateField.setName("masterEndDateField");

		addAccidentDateField.setName("addAccidentDateField");
		addAccidentAddressField.setName("addAccidentAddressField");
		addAccidentCostField.setName("addAccidentCostField");
		accidentStartDateField.setName("accidentStartDateField");
		accidentEndDateField.setName("accidentEndDateField");

		addClientNameField.setName("addClientNameField");
		addClientAddressField.setName("addClientAddressField");
		addClientPhoneField.setName("addClientPhoneField");

		reportClientNameField.setName("reportClientNameField");
		reportStartDateField.setName("reportStartDateField");
		reportEndDateField.setName("reportEndDateField");
	}

	private void setLabelNames() {
		connectionStatusLabel.setName("connectionStatusLabel");
	}

	private void setMiscComponentNames() {
		resultArea.setName("resultArea");
	}

	private Date parseDate(String dateStr) {
		try {
			return main.getDateFormat().parse(dateStr);
		} catch (ParseException ex) {
			if (dateStr.isEmpty()) {
				showErrorOptionPane("Поле дати не може бути пустим.");
				return null;
			}
			showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
			return null;
		}
	}

	private void countMasters() {
		try {
			resultArea.setText("Кількість майстрів: " + main.getMasters().size());
		} catch (SQLException e) {
			showErrorOptionPane("Помилка підрахунку майстрів: " + e.getMessage());
		}
	}

	private void calculateMasterSalary() {
		try {
			validateField(masterListNameField, "Майстер");

			String masterName = masterListNameField.getSelectedItem().toString();
			Master selectedMaster = main.getMasterByName(masterName);
			Date startDate = parseDate(masterStartDateField.getText());
			Date endDate = parseDate(masterEndDateField.getText());

			if (selectedMaster != null && startDate != null && endDate != null) {
				double salary = main.getMasterSalaryBetweenDates(selectedMaster.getIndex(), startDate, endDate);
				resultArea.setText("Заробіток майстра " + masterName + " за період з " + main.getDateFormat().format(startDate) + " по " + main.getDateFormat().format(endDate) + " : " + salary);
			} else {
				if(selectedMaster == null) showErrorOptionPane("Майстра не знайдено.");
			}
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка розрахунку зарплати: " + ex.getMessage());
		}
	}

	private void countBrigades() {
		resultArea.setText("Кількість бригад: " + main.getBrigadeCount());
	}

	private void showInactiveBrigades(String method, List<Integer> brigades) {
		if (brigades == null || brigades.isEmpty()) {
			resultArea.setText("Неактивних бригад не знайдено (" + method + ")");
		} else {
			StringBuilder sb = new StringBuilder("Неактивні бригади (" + method + "):\n");
			brigades.forEach(brigade -> sb.append("Бригада ").append(brigade).append("\n"));
			resultArea.setText(sb.toString());
		}
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

			main.addMaster(master);

			showSuccessOptionPane("Майстра додано успішно!");
			clearAddFields(FIELD_TYPES.MASTER_FIELD);
			populateAllComboBoxes();
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка додавання майстра: " + ex.getMessage());
		}
	}

	private void deleteMaster() {
		try {
			validateField(masterListNameField, "Майстер");

			String masterName = (String) masterListNameField.getSelectedItem();
			Master masterToRemove = main.getMasterByName(masterName);
			if (masterToRemove != null) {
				main.deleteMaster(masterToRemove.getIndex());
				showSuccessOptionPane("Майстра \"" + masterName + "\" видалено успішно!");
				clearAddFields(FIELD_TYPES.MASTER_FIELD);
				clearDeleteFields(FIELD_TYPES.MASTER_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Майстра \"" + masterName + "\" не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення майстра: " + ex.getMessage());
		}
	}

	private void updateMaster() {
		try {
			validateField(masterListNameField, "Майстер");
			validateField(addMasterNameField, "Ім'я та прізвище");

			String masterName = (String) masterListNameField.getSelectedItem();
			Master masterToUpdate = main.getMasterByName(masterName);
			if (masterToUpdate != null) {
				masterToUpdate.setName(addMasterNameField.getText());
				masterToUpdate.setAddress(addMasterAddressField.getText());
				masterToUpdate.setPhoneNumber(addMasterPhoneField.getText());
				masterToUpdate.setExperience(Integer.parseInt(addMasterExperienceField.getText()));
				masterToUpdate.setStatus((String) addMasterStatusField.getSelectedItem());
				masterToUpdate.setBrigade(new Brigade(Integer.parseInt((String) addMasterBrigadeField.getSelectedItem())));
				masterToUpdate.setDepartmentNumber(Integer.parseInt((String) addMasterDepartmentField.getSelectedItem()));

				main.updateMaster(masterToUpdate);
				showSuccessOptionPane("Майстра \"" + masterName + "\" оновлено успішно!");
				clearAddFields(FIELD_TYPES.MASTER_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Майстра з іменем " + masterName + " не знайдено.");
			}
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка оновлення майстра: " + ex.getMessage());
		}
	}

	private void listMasters() {
		try {
			List<Master> masters = main.getMasters();
			StringBuilder sb = new StringBuilder("Список майстрів\n");

			if (masters.isEmpty()) {
				sb.append("Немає майстрів у базі даних.");
				resultArea.setText(sb.toString());
				return;
			}
			Map<Integer, Master> uniqueMasters = new LinkedHashMap<>();
			for (Master master : masters) {
				uniqueMasters.put(master.getIndex(), master);
			}

			int nameWidth = "Ім'я та прізвище".length();
			int addressWidth = "Адреса".length();
			int phoneWidth = "Номер телефону".length();
			int experienceWidth = "Стаж".length();
			int statusWidth = "Стан".length();
			int brigadeWidth = "Номер бригади".length();
			int departmentWidth = "Номер відділення".length();

			for (Master master : uniqueMasters.values()) {
				nameWidth = Math.max(nameWidth, master.getName().length());
				addressWidth = Math.max(addressWidth, master.getAddress().length());
				phoneWidth = Math.max(phoneWidth, master.getPhoneNumber().length());
				experienceWidth = Math.max(experienceWidth, (master.getExperience() + " років").length());
				statusWidth = Math.max(statusWidth, master.getStatus().length());
				brigadeWidth = Math.max(brigadeWidth, String.valueOf(master.getBrigade().getNumber()).length());
				departmentWidth = Math.max(departmentWidth, String.valueOf(master.getDepartmentNumber()).length());
			}

			String format = String.format("%%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds\n",
					nameWidth, addressWidth, phoneWidth, experienceWidth, statusWidth, brigadeWidth, departmentWidth);

			sb.append(String.format(format, "Ім'я та прізвище", "Адреса", "Номер телефону", "Стаж", "Стан", "Номер бригади", "Номер відділення"));
			sb.append(String.format(format, "-".repeat(nameWidth), "-".repeat(addressWidth),
					"-".repeat(phoneWidth), "-".repeat(experienceWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth),
					"-".repeat(departmentWidth)));

			for (Master master : uniqueMasters.values()) {
				sb.append(String.format(format,
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

		} catch (SQLException ex) {
			showErrorOptionPane("Помилка завантаження списку майстрів: " + ex.getMessage());
		}
	}

	private void countClients() {
		try {
			resultArea.setText("Кількість клієнтів: " + main.getClients().size());
		} catch (SQLException e) {
			showErrorOptionPane("Помилка підрахунку клієнтів: " + e.getMessage());
		}
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

			int nextClientIndex = main.getNextClientIndex();

			Client client = new Client(nextClientIndex, name, address, phoneNumber, departmentNumber);
			main.addClient(client);

			showSuccessOptionPane("Клієнта додано успішно!");
			clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			populateAllComboBoxes();
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка додавання клієнта: " + ex.getMessage());
		}
	}

	private void deleteClient() {
		try {
			validateField(clientListNameField, "Клієнт");

			String clientName = (String) clientListNameField.getSelectedItem();
			Client clientToRemove = main.getClientByName(clientName);
			if (clientToRemove != null) {
				main.deleteClient(clientToRemove.getIndex());
				showSuccessOptionPane("Клієнта \"" + clientName + "\" та пов'язані аварії видалено успішно!");
				clearAddFields(FIELD_TYPES.CLIENT_FIELD);
				clearDeleteFields(FIELD_TYPES.CLIENT_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Клієнта \"" + clientName + "\" не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення клієнта: " + ex.getMessage());
		}
	}

	private void updateClient() {
		try {
			validateField(clientListNameField, "Клієнт");

			String clientName = (String) clientListNameField.getSelectedItem();
			Client clientToUpdate = main.getClientByName(clientName);

			if (clientToUpdate != null) {
				clientToUpdate.setName(addClientNameField.getText());
				clientToUpdate.setAddress(addClientAddressField.getText());
				clientToUpdate.setPhoneNumber(addClientPhoneField.getText());
				clientToUpdate.setDepartmentNumber(Integer.parseInt((String) addClientDepartmentField.getSelectedItem()));
				main.updateClient(clientToUpdate);
				showSuccessOptionPane("Клієнта \"" + clientName + "\" оновлено успішно!");
				clearAddFields(FIELD_TYPES.CLIENT_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Клієнта не знайдено.");
			}
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка оновлення клієнта: " + ex.getMessage());
		}
	}

	private void listClients() {
		try {
			List<Client> clients = main.getClients();
			StringBuilder sb = new StringBuilder("Список клієнтів\n");
			if (clients.isEmpty()) {
				sb.append("Немає клієнтів у базі даних.");
				resultArea.setText(sb.toString());
				return;
			}
			Map<Integer, Client> uniqueClients = new LinkedHashMap<>();
			for (Client client : clients) {
				uniqueClients.put(client.getIndex(), client);
			}

			int nameWidth = "Ім'я та прізвище".length();
			int addressWidth = "Адреса".length();
			int phoneWidth = "Номер телефону".length();
			int departmentWidth = "Номер відділення".length();

			for (Client client : uniqueClients.values()) {
				nameWidth = Math.max(nameWidth, client.getName().length());
				addressWidth = Math.max(addressWidth, client.getAddress().length());
				phoneWidth = Math.max(phoneWidth, client.getPhoneNumber().length());
				departmentWidth = Math.max(departmentWidth, String.valueOf(client.getDepartmentNumber()).length());
			}

			String format = String.format("%%-%ds  %%-%ds  %%-%ds  %%-%ds\n",
					nameWidth, addressWidth, phoneWidth, departmentWidth);

			sb.append(String.format(format, "Ім'я та прізвище", "Адреса", "Номер телефону", "Номер відділення"));
			sb.append(String.format(format, "-".repeat(nameWidth),
					"-".repeat(addressWidth), "-".repeat(phoneWidth), "-".repeat(departmentWidth)));

			for (Client client : uniqueClients.values()) {
				sb.append(String.format(format,
						client.getName(),
						client.getAddress(),
						client.getPhoneNumber(),
						client.getDepartmentNumber()
				));
			}
			resultArea.setText(sb.toString());

		} catch (SQLException ex) {
			showErrorOptionPane("Помилка завантаження списку клієнтів: " + ex.getMessage());
		}
	}

	private void countAccidents() {
		Date startDate = parseDate(accidentStartDateField.getText());
		Date endDate = parseDate(accidentEndDateField.getText());
		if (startDate != null && endDate != null) {
			int count;
			try {
				count = main.getAccidentCountBetweenDates(startDate, endDate);
				resultArea.setText("Кількість аварій з " + main.getDateFormat().format(startDate) + " по " + main.getDateFormat().format(endDate) + ": " + count);
			} catch (IllegalArgumentException ex) {
				showErrorOptionPane(ex.getMessage());
				resultArea.setText(ex.getMessage());
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

		if (types.isEmpty()) {
			sb.append("Немає");
		} else {
			for (String type : types) {
				sb.append(type).append("\n");
			}
		}

		resultArea.setText(sb.toString());
	}

	private void addAccident() {
		try {
			validateField(addAccidentDateField, "Дата");
			validateField(accidentClientPhoneNumberField, "Номер телефону клієнта");

			Date date = parseDate(addAccidentDateField.getText());
			String address = addAccidentAddressField.getText();
			String type = (String) addAccidentTypeField.getSelectedItem();
			double cost = Double.parseDouble(addAccidentCostField.getText());
			String status = (String) addAccidentStatusField.getSelectedItem();
			int brigadeNumber = Integer.parseInt((String) addAccidentBrigadeField.getSelectedItem());

			String accidentClientPhoneNumber = (String) accidentClientPhoneNumberField.getSelectedItem();
			int newIndex = main.getNextAccidentIndex();
			int newClientAccidentIndex = main.getNextAccidentIndexForClient(accidentClientPhoneNumber);

			Brigade brigade = new Brigade(brigadeNumber);
			Accident accident = new Accident(newIndex, address, type, cost, date, brigade, status, accidentClientPhoneNumber, newClientAccidentIndex);
			main.addAccident(accident);
			showSuccessOptionPane("Аварію додано успішно!");
			clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
			populateAllComboBoxes();
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка додавання аварії: " + ex.getMessage());
		}
	}

	private void deleteAccident() {
		try {
			validateField(accidentClientPhoneNumberField, "Номер телефону клієнта");
			validateField(accidentIndexField, "Iндекс аварії");

			String accidentClientPhoneNumber = (String) accidentClientPhoneNumberField.getSelectedItem();
			String accidentIndexStr = (String) accidentIndexField.getSelectedItem();
			int accidentIndex = Integer.parseInt(accidentIndexStr);
			Accident accidentToRemove = main.getAccidentByPhoneNumAccIndex(accidentClientPhoneNumber, accidentIndex);

			if (accidentToRemove != null) {
				main.deleteAccident(accidentToRemove.getIndex());
				showSuccessOptionPane("Аварію з ID " + accidentToRemove.getIndex() + " видалено успішно!");
				clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
				clearDeleteFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Аварію не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення аварії: " + ex.getMessage());
		}
	}

	private void updateAccident() {
		try {
			validateField(accidentClientPhoneNumberField, "Номер телефону клієнта");
			validateField(accidentIndexField, "Індекс аварії");

			String accidentClientPhoneNumber = (String) accidentClientPhoneNumberField.getSelectedItem();
			String accidentIdStr = (String) accidentIndexField.getSelectedItem();
			int accidentId = Integer.parseInt(accidentIdStr);
			Accident accidentToUpdate = main.getAccidentByPhoneNumAccIndex(accidentClientPhoneNumber, accidentId);

			if (accidentToUpdate != null) {
				accidentToUpdate.setDate(parseDate(addAccidentDateField.getText()));
				accidentToUpdate.setAddress(addAccidentAddressField.getText());
				accidentToUpdate.setType((String) addAccidentTypeField.getSelectedItem());
				accidentToUpdate.setCost(Double.parseDouble(addAccidentCostField.getText()));
				accidentToUpdate.setStatus((String) addAccidentStatusField.getSelectedItem());
				accidentToUpdate.setBrigade(new Brigade(Integer.parseInt((String) addAccidentBrigadeField.getSelectedItem())));
				accidentToUpdate.setClientPhoneNumber(accidentClientPhoneNumber);

				main.updateAccident(accidentToUpdate);
				showSuccessOptionPane("Аварію з ID " + accidentToUpdate.getIndex() + " оновлено успішно!");
				clearAddFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAllComboBoxes();
			} else {
				showErrorOptionPane("Аварію не знайдено.");
			}
		} catch (IllegalArgumentException | SQLException ex) {
			showErrorOptionPane("Помилка оновлення аварії: " + ex.getMessage());
		}
	}

	private void listAccidents() {
		try {
			List<Accident> accidents = main.getAccidents();
			StringBuilder sb = new StringBuilder("Список аварій\n");
			if (accidents.isEmpty()) {
				sb.append("Немає аварій в базі даних.");
				resultArea.setText(sb.toString());
				return;
			}
			Map<Integer, Accident> uniqueAccidents = new LinkedHashMap<>();
			for (Accident accident : accidents) {
				uniqueAccidents.put(accident.getIndex(), accident);
			}

			int phoneNumberWidth = "Номер телефону".length();
			int dateWidth = "Дата".length();
			int addressWidth = "Адреса".length();
			int typeWidth = "Тип аварії".length();
			int costWidth = "Вартість".length();
			int statusWidth = "Стан".length();
			int brigadeWidth = "Номер бригади".length();

			for (Accident accident : uniqueAccidents.values()) {
				phoneNumberWidth = Math.max(phoneNumberWidth, (accident.getClientPhoneNumber() + " (" + accident.getAccidentIndex() + ")").length());
				dateWidth = Math.max(dateWidth, main.getDateFormat().format(accident.getDate()).length());
				addressWidth = Math.max(addressWidth, accident.getAddress().length());
				typeWidth = Math.max(typeWidth, accident.getType().length());
				costWidth = Math.max(costWidth, (accident.getCost() + " грн").length());
				statusWidth = Math.max(statusWidth, accident.getStatus().length());
				brigadeWidth = Math.max(brigadeWidth, String.valueOf(accident.getBrigade().getNumber()).length());
			}

			String format = String.format("%%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds  %%-%ds\n",
					phoneNumberWidth, dateWidth, addressWidth, typeWidth, costWidth, statusWidth, brigadeWidth);

			sb.append(String.format(format, "Номер телефону", "Дата", "Адреса", "Тип аварії", "Вартість", "Стан", "Номер бригади"));
			sb.append(String.format(format, "-".repeat(phoneNumberWidth), "-".repeat(dateWidth), "-".repeat(addressWidth),
					"-".repeat(typeWidth), "-".repeat(costWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth)));

			for (Accident accident : uniqueAccidents.values()) {
				sb.append(String.format(format,
						accident.getClientPhoneNumber() + " (" + accident.getAccidentIndex() + ")",
						main.getDateFormat().format(accident.getDate()),
						accident.getAddress(),
						accident.getType(),
						accident.getCost() + " грн",
						accident.getStatus(),
						accident.getBrigade().getNumber()
				));
			}
			resultArea.setText(sb.toString());

		} catch (SQLException ex) {
			showErrorOptionPane("Помилка завантаження списку аварій: " + ex.getMessage());
		}
	}

	private void exitApplication() {
		int option = JOptionPane.showConfirmDialog(null,
				"Ви впевнені, що хочете вийти?",
				"Підтвердження",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (option == JOptionPane.YES_OPTION) {
			main.dbManager.close();
			frame.dispose();
		}
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
		} else if (field instanceof JComboBox<?> comboBox) {
			if (comboBox.getSelectedIndex() == -1) {
				throw new IllegalArgumentException("Поле " + fieldName + " не може бути порожнім.");
			}
		} else {
			throw new IllegalArgumentException("Невідомий тип поля: " + fieldName);
		}
	}

	private void validateNameField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		for (char c : text.toCharArray()) {
			if (!Character.isLetter(c) && !Character.isSpaceChar(c) && c != '\'' && c != '-') {
				throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки літери, пробіли, апострофи та дефіси.");
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
		if (!text.matches("[\\p{L}0-9\\s.,'-]+")) {
			throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки літери, цифри, пробіли, коми, крапки та тире.");
		}
	}

	private void validateNumberField(JTextField field, String fieldName) {
		String text = field.getText().trim();
		try {
			Double.parseDouble(text);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Поле " + fieldName + " може містити тільки цифри.");
		}
	}

	private void showSuccessOptionPane(String message) {
		JOptionPane.showMessageDialog(frame, message, "Успіх", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showErrorOptionPane(String message) {
		JOptionPane.showMessageDialog(frame, message, "Помилка", JOptionPane.ERROR_MESSAGE);
	}

	public void populateAllComboBoxes() {
		populateMasterComboBoxes();
		populateClientComboBoxes();
		populateAccidentComboBoxes();
	}

	public void populateMasterComboBoxes() {
		try {
			String[] masterNames = main.getMasters().stream()
					.map(Master::getName)
					.sorted()
					.toArray(String[]::new);
			masterListNameField.setModel(new DefaultComboBoxModel<>(masterNames));

			String[] brigadeNumbers = Main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
			addMasterBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));
			String[] departmentNumbers = Main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
			addMasterDepartmentField.setModel(new DefaultComboBoxModel<>(departmentNumbers));

		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження даних для майстрів: " + e.getMessage());
		}
	}

	public void populateAccidentComboBoxes() {
		try {
			String[] brigadeNumbers = main.getUsedBrigadeNumbers().toArray(new String[0]);
			String[] accidentTypes = Main.getAccidentTypes().toArray(new String[0]);

			addAccidentTypeField.setModel(new DefaultComboBoxModel<>(accidentTypes));
			addAccidentBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));

			populateAccidentClientPhoneNumberComboBox();
		} catch (Exception e) {
			showErrorOptionPane("Помилка завантаження даних для аварій: " + e.getMessage());
		}
	}

	public void populateClientComboBoxes() {
		try {
			String[] clientNames = main.getClients().stream()
					.map(Client::getName)
					.sorted()
					.toArray(String[]::new);
			clientListNameField.setModel(new DefaultComboBoxModel<>(clientNames));

			String[] departmentNumbers = main.getUsedDepartmentNumbers().toArray(new String[0]);
			addClientDepartmentField.setModel(new DefaultComboBoxModel<>(departmentNumbers));
		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження даних для клієнтів: " + e.getMessage());
		}
	}

	public void populateAccidentClientPhoneNumberComboBox() {
		try {
			String[] phoneNumbers = main.getClients().stream()
					.map(Client::getPhoneNumber)
					.sorted()
					.toArray(String[]::new);
			accidentClientPhoneNumberField.setModel(new DefaultComboBoxModel<>(phoneNumbers));
		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження номерів телефонів: " + e.getMessage());
		}
	}

	public void populateClientAccidentIndexComboBox(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			accidentIndexField.setModel(new DefaultComboBoxModel<>());
			return;
		}
		List<Integer> indices = main.getAccidentIndicesForClient(phoneNumber);
		String[] indexArray = indices.stream()
				.map(String::valueOf)
				.toArray(String[]::new);
		accidentIndexField.setModel(new DefaultComboBoxModel<>(indexArray));
	}

	public void loadMasterData(String masterName) {
		try {
			Master master = main.getMasterByName(masterName);
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
		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження даних майстра: " + e.getMessage());
		}
	}

	public void loadClientData(String clientName) {
		try {
			Client client = main.getClientByName(clientName);
			if (client != null) {
				addClientNameField.setText(client.getName());
				addClientAddressField.setText(client.getAddress());
				addClientPhoneField.setText(client.getPhoneNumber());
				addClientDepartmentField.setSelectedItem(String.valueOf(client.getDepartmentNumber()));
			} else {
				clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			}
		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження даних клієнта: " + e.getMessage());
		}
	}

	public void loadAccidentData(String clientPhoneNumber, int accidentIndex) {
		try {
			Accident accident = main.getAccidentByPhoneNumAccIndex(clientPhoneNumber, accidentIndex);
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
		} catch (SQLException e) {
			showErrorOptionPane("Помилка завантаження даних аварії: " + e.getMessage());
		}
	}
}