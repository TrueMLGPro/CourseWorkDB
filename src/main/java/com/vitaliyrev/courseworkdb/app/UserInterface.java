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
	private JButton deleteAccidentAndClientButton; // Кнопка для видалення аварії та клієнта
	private JButton listAccidentsButton; // Кнопка для відображення списку аварій
	private JButton updateAccidentButton; // Кнопка для оновлення даних про аварію
	private JButton countClientsButton; // Кнопка для підрахунку кількості клієнтів
	private JButton addClientButton; // Кнопка для додавання клієнта
	private JButton deleteClientButton; // Кнопка для видалення клієнта
	private JButton deleteClientAndAccidentButton; // Кнопка для видалення клієнта та аварії
	private JButton updateClientButton; // Кнопка для оновлення даних про клієнта
	private JButton listClientsButton; // Кнопка для відображення списку клієнтів
	private JButton mastersWithBrigadeInfoButton; // Кнопка для відображення майстрів з інформацією про бригади
	private JButton findClientsByNameButton; // Кнопка для пошуку клієнтів за ім'ям
	private JButton accidentsBetweenDatesButton; // Кнопка для відображення аварій в інтервалі дат
	private JButton accidentCountLastWeekButton; // Кнопка для відображення кількості аварій за минулий тиждень
	private JButton accidentCountPerBrigadeButton; // Кнопка для відображення кількості аварій на бригаду
	private JButton maxAccidentBrigadesButton; // Кнопка для відображення бригад з максимальnoю кількістю аварій
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
		buttonPanel.setLayout(new GridLayout(9, 1));

		countAccidentsButton = new JButton("Кількість аварій з... по...");
		averageCostButton = new JButton("Середня вартість усунення аварії");
		accidentTypesButton = new JButton("Типи аварій у записах");
		addAccidentButton = new JButton("Додати аварію");
		deleteAccidentButton = new JButton("Видалити аварію");
		deleteAccidentAndClientButton = new JButton("Видалити аварію та клієнта");
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
		buttonPanel.add(deleteAccidentAndClientButton);
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
		deleteAccidentAndClientButton.addActionListener(e -> deleteAccidentAndClient());
		listAccidentsButton.addActionListener(e -> listAccidents()); // Обробка натискання кнопки "Список аварій"
		updateAccidentButton.addActionListener(e -> updateAccident()); // Обробка натискання кнопки "Оновити аварію"
		exitButton.addActionListener(e -> exitApplication()); // Обробка натискання кнопки "Вихід"

		return panel;
	}

	private JPanel createClientsTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(7, 1));

		countClientsButton = new JButton("Кількість клієнтів");
		addClientButton = new JButton("Додати клієнта");
		deleteClientButton = new JButton("Видалити клієнта");
		deleteClientAndAccidentButton = new JButton("Видалити клієнта та аварію");
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
		buttonPanel.add(deleteClientAndAccidentButton);
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
		deleteClientAndAccidentButton.addActionListener(e -> deleteClientAndAccident()); // Обробка натискання кнопки "Видалити клієнта та аварію"
		updateClientButton.addActionListener(e -> updateClient()); // Обробка натискання кнопки "Оновити клієнта"
		listClientsButton.addActionListener(e -> listClients()); // Обробка натискання кнопки "Список клієнтів"
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

		// Add action listeners
		mastersWithBrigadeInfoButton.addActionListener(e -> {
			String result = main.getMastersWithBrigadeInfo();
			resultArea.setText("Майстри з інформацією про бригади:\n" + result);
		});

		findClientsByNameButton.addActionListener(e -> {
			String namePattern = ((JTextField) fieldsPanel.getComponent(1)).getText();
			if (namePattern.isEmpty()) {
				showErrorOptionPane("Введіть ім'я для пошуку");
				return;
			}
			String result = main.findClientsByNamePattern(namePattern);
			resultArea.setText("Клієнти за іменем '" + namePattern + "':\n" + result);
		});

		accidentsBetweenDatesButton.addActionListener(e -> {
			String startDateStr = ((JTextField) fieldsPanel.getComponent(3)).getText();
			String endDateStr = ((JTextField) fieldsPanel.getComponent(5)).getText();
			try {
				Date startDate = main.getDateFormat().parse(startDateStr);
				Date endDate = main.getDateFormat().parse(endDateStr);
				String result = main.getAccidentsBetweenDates(startDate, endDate);
				resultArea.setText("Аварії з " + startDateStr + " по " + endDateStr + ":\n" + result);
			} catch (ParseException ex) {
				showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy");
			}
		});

		accidentCountLastWeekButton.addActionListener(e -> {
			int count = main.getAccidentCountLastWeek();
			resultArea.setText("Кількість аварій за минулий тиждень: " + count);
		});

		accidentCountPerBrigadeButton.addActionListener(e -> {
			Map<Integer, Integer> counts = main.getAccidentCountPerBrigade();
			StringBuilder sb = new StringBuilder("Кількість аварій по бригадах:\n");
			counts.forEach((brigade, count) ->
					sb.append("Бригада ").append(brigade).append(": ").append(count).append(" аварій\n"));
			resultArea.setText(sb.toString());
		});

		maxAccidentBrigadesButton.addActionListener(e -> {
			List<Integer> brigades = main.getBrigadesWithMaxAccidents();
			StringBuilder sb = new StringBuilder("Бригади з максимальною кількістю аварій:\n");
			brigades.forEach(brigade -> sb.append("Бригада ").append(brigade).append("\n"));
			resultArea.setText(sb.toString());
		});

		topBrigadePerTypeButton.addActionListener(e -> {
			Map<String, Integer> topBrigades = main.getTopBrigadePerAccidentType();
			StringBuilder sb = new StringBuilder("Найактивніші бригади по типах аварій:\n");
			topBrigades.forEach((type, brigade) ->
					sb.append(type).append(": Бригада ").append(brigade).append("\n"));
			resultArea.setText(sb.toString());
		});

		inactiveBrigadesLeftJoinButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesLeftJoin();
			showInactiveBrigades("LEFT JOIN", brigades);
		});

		inactiveBrigadesInButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesIn();
			showInactiveBrigades("NOT IN", brigades);
		});

		inactiveBrigadesExistsButton.addActionListener(e -> {
			List<Integer> brigades = main.getInactiveBrigadesExists();
			showInactiveBrigades("NOT EXISTS", brigades);
		});

		brigadeActivityStatusButton.addActionListener(e -> {
			List<String> statuses = main.getBrigadesWithActivityStatus();
			StringBuilder sb = new StringBuilder("Статус активності бригад:\n");
			statuses.forEach(status -> sb.append(status).append("\n"));
			resultArea.setText(sb.toString());
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
		deleteAccidentAndClientButton.setName("deleteAccidentAndClientButton");
		updateAccidentButton.setName("updateAccidentButton");
		listAccidentsButton.setName("listAccidentsButton");

		countClientsButton.setName("countClientsButton");
		addClientButton.setName("addClientButton");
		deleteClientButton.setName("deleteClientButton");
		deleteClientAndAccidentButton.setName("deleteClientAndAccidentButton");
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
		masterIndexField.setName("masterIndexField");

		addAccidentTypeField.setName("addAccidentTypeField");
		addAccidentStatusField.setName("addAccidentStatusField");
		addAccidentBrigadeField.setName("addAccidentBrigadeField");
		accidentIdField.setName("accidentIdField");

		addClientDepartmentField.setName("addClientDepartmentField");
		clientAccidentIdField.setName("clientAccidentIdField");
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
			ex.printStackTrace();
			showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
			return null;
		}
	}

	private void countMasters() {
		resultArea.setText("Кількість майстрів: " + main.getMasters().size());
	}

	private void calculateMasterSalary() {
		validateField(masterIndexField, "Індекс майстра");
		int masterIndex = Integer.parseInt(masterIndexField.getSelectedItem().toString());
		Date startDate = parseDate(masterStartDateField.getText());
		Date endDate = parseDate(masterEndDateField.getText());

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
			main.getMasters().add(master);
			brigade.addMaster(master);
			main.saveAllData();
			showSuccessOptionPane("Майстра додано успішно!");
			clearAddFields(FIELD_TYPES.MASTER_FIELD);
			populateMasterComboBoxes();
			populateClientComboBoxes();
			populateAccidentComboBoxes();
		} catch (IllegalArgumentException ex) {
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
				main.dbManager.deleteMaster(masterToRemove.getIndex());
				main.saveAllData();
				showSuccessOptionPane("Майстра з індексом " + masterIndex + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.MASTER_FIELD);
				populateMasterComboBoxes();
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Майстра з індексом " + masterIndex + " не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
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
			showErrorOptionPane("Помилка оновлення майстра: " + ex.getMessage());
		}
	}

	private void listMasters() {
		StringBuilder sb = new StringBuilder("Список майстрів:\n");

		// Create a map to track unique masters by ID
		Map<Integer, Master> uniqueMasters = new LinkedHashMap<>();
		for (Master master : main.getMasters()) {
			uniqueMasters.put(master.getIndex(), master);
		}

		// Calculate column widths using unique masters
		int indexWidth = "Індекс".length();
		int nameWidth = "Ім'я та прізвище".length();
		int addressWidth = "Адреса".length();
		int phoneWidth = "Номер телефону".length();
		int experienceWidth = "Стаж".length();
		int statusWidth = "Стан".length();
		int brigadeWidth = "Номер бригади".length();
		int departmentWidth = "Номер відділення".length();

		for (Master master : uniqueMasters.values()) {
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
		sb.append(String.format(format, "-".repeat(indexWidth), "-".repeat(nameWidth), "-".repeat(addressWidth),
				"-".repeat(phoneWidth), "-".repeat(experienceWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth),
				"-".repeat(departmentWidth)));

		// Display only unique masters
		for (Master master : uniqueMasters.values()) {
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
				count = main.getAccidentCountBetweenDates(startDate, endDate);
				resultArea.setText("Кількість аварій з " + main.getDateFormat().format(startDate) + " по " + main.getDateFormat().format(endDate) + ": " + count);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
				showErrorOptionPane("Неправильний формат дати. Використовуйте dd/MM/yyyy.");
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
			validateField(addAccidentAddressField, "Адреса");
			validateField(addAccidentTypeField, "Тип аварії");
			validateField(addAccidentCostField, "Вартість");
			validateField(addAccidentStatusField, "Стан");
			validateField(addAccidentBrigadeField, "Номер бригади");

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
			showErrorOptionPane("Помилка додавання аварії: " + ex.getMessage());
		}
	}

	private void deleteAccident() {
		try {
			validateField(accidentIdField, "ID аварії");
			String accidentId = (String) accidentIdField.getSelectedItem();
			Accident accidentToRemove = main.getAccidentById(accidentId);
			if (accidentToRemove != null) {
				// Break the association before deletion
				Client associatedClient = main.getClientByAccidentId(accidentId);
				if (associatedClient != null) {
					associatedClient.setAccident(null);
				}

				// Now delete the accident
				main.dbManager.deleteAccident(accidentToRemove.getIndex());
				main.getAccidents().remove(accidentToRemove);

				main.saveAllData();
				showSuccessOptionPane("Аварію з ID " + accidentId + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAccidentComboBoxes();
				populateClientComboBoxes();
			} else {
				showErrorOptionPane("Аварію з ID " + accidentId + " не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення аварії: " + ex.getMessage());
		}
	}

	private void deleteAccidentAndClient() {
		try {
			validateField(accidentIdField, "ID аварії");
			String accidentId = (String) accidentIdField.getSelectedItem();
			Accident accidentToRemove = main.getAccidentById(accidentId);
			Client clientToRemove = main.getClientByAccidentId(accidentId);

			if (accidentToRemove != null && clientToRemove != null) {
				// Delete accident first to trigger cascade
				main.dbManager.deleteAccident(accidentToRemove.getIndex());
				main.getAccidents().remove(accidentToRemove);

				// Client will be automatically deleted by cascade
				main.getClients().remove(clientToRemove);

				main.saveAllData();
				showSuccessOptionPane("Аварію та клієнта видалено успішно!");
				clearDeleteFields(FIELD_TYPES.ACCIDENT_FIELD);
				populateAccidentComboBoxes();
				populateClientComboBoxes();
			} else {
				showErrorOptionPane("Не вдалося знайти пов'язані записи");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення: " + ex.getMessage());
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
			showErrorOptionPane("Помилка оновлення аварії: " + ex.getMessage());
		}
	}

	private void listAccidents() {
		StringBuilder sb = new StringBuilder("Список аварій:\n");

		// Create a map to track unique accidents by ID
		Map<String, Accident> uniqueAccidents = new LinkedHashMap<>();
		for (Accident accident : main.getAccidents()) {
			uniqueAccidents.put(accident.getAccidentId(), accident);
		}

		// Calculate column widths using unique accidents
		int idWidth = "ID аварії".length();
		int dateWidth = "Дата".length();
		int addressWidth = "Адреса".length();
		int typeWidth = "Тип аварії".length();
		int costWidth = "Вартість".length();
		int statusWidth = "Стан".length();
		int brigadeWidth = "Номер бригади".length();

		for (Accident accident : uniqueAccidents.values()) {
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
		sb.append(String.format(format, "-".repeat(idWidth), "-".repeat(dateWidth), "-".repeat(addressWidth),
				"-".repeat(typeWidth), "-".repeat(costWidth), "-".repeat(statusWidth), "-".repeat(brigadeWidth)));

		// Display only unique accidents
		for (Accident accident : uniqueAccidents.values()) {
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

			int nextClientIndex = main.getNextClientIndex();
			int newAccidentIndex = main.getNextAccidentIndex();
			String accidentId = newAccidentIndex + "-" + Main.generateRandomString(8);

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

			Client client = new Client(nextClientIndex, accidentId, name, address, phoneNumber, departmentNumber);
			main.getClients().add(client);

			accident.setClient(client);

			main.saveAllData();
			showSuccessOptionPane("Клієнта додано успішно!");
			clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			populateClientComboBoxes();
			populateAccidentComboBoxes();
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane("Помилка додавання клієнта: " + ex.getMessage());
		}
	}

	private void deleteClient() {
		try {
			validateField(clientAccidentIdField, "ID аварії");
			String accidentId = (String) clientAccidentIdField.getSelectedItem();
			Client clientToRemove = main.getClientByAccidentId(accidentId);
			if (clientToRemove != null) {
				// Break the association before deletion
				Accident associatedAccident = main.getAccidentById(accidentId);
				if (associatedAccident != null) {
					associatedAccident.setClient(null);
				}

				// Now delete the client
				main.dbManager.deleteClient(clientToRemove.getIndex());
				main.getClients().remove(clientToRemove);

				main.saveAllData();
				showSuccessOptionPane("Клієнта з ID аварії " + accidentId + " видалено успішно!");
				clearDeleteFields(FIELD_TYPES.CLIENT_FIELD);
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Клієнта з ID аварії " + accidentId + " не знайдено.");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення клієнта: " + ex.getMessage());
		}
	}

	private void deleteClientAndAccident() {
		try {
			validateField(clientAccidentIdField, "ID аварії");
			String accidentId = (String) clientAccidentIdField.getSelectedItem();
			Client clientToRemove = main.getClientByAccidentId(accidentId);
			Accident accidentToRemove = main.getAccidentById(accidentId);

			if (clientToRemove != null && accidentToRemove != null) {
				// Delete client first to trigger cascade
				main.dbManager.deleteClient(clientToRemove.getIndex());
				main.getClients().remove(clientToRemove);

				// Accident will be automatically deleted by cascade
				main.getAccidents().remove(accidentToRemove);

				main.saveAllData();
				showSuccessOptionPane("Клієнта та аварію видалено успішно!");
				clearDeleteFields(FIELD_TYPES.CLIENT_FIELD);
				populateClientComboBoxes();
				populateAccidentComboBoxes();
			} else {
				showErrorOptionPane("Не вдалося знайти пов'язані записи");
			}
		} catch (SQLException | IllegalArgumentException ex) {
			showErrorOptionPane("Помилка видалення: " + ex.getMessage());
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
			Accident accident = main.getAccidentById(accidentId);

			if (accident == null) {
				showErrorOptionPane("Аварію з ID " + accidentId + " не знайдено.");
				return;
			}

			if (clientToUpdate == null) {
				// Create new client if it doesn't exist
				int nextClientIndex = main.getNextClientIndex();
				clientToUpdate = new Client(
						nextClientIndex,
						accidentId,
						addClientNameField.getText(),
						addClientAddressField.getText(),
						addClientPhoneField.getText(),
						Integer.parseInt((String) addClientDepartmentField.getSelectedItem())
				);
				main.getClients().add(clientToUpdate);
				accident.setClient(clientToUpdate);
			} else {
				// Update existing client
				clientToUpdate.setName(addClientNameField.getText());
				clientToUpdate.setAddress(addClientAddressField.getText());
				clientToUpdate.setPhoneNumber(addClientPhoneField.getText());
				clientToUpdate.setDepartmentNumber(Integer.parseInt((String) addClientDepartmentField.getSelectedItem()));
			}

			// Update accident address
			accident.setAddress(addClientAddressField.getText());

			main.saveAllData();
			showSuccessOptionPane("Клієнта з ID аварії " + accidentId + " оновлено успішно!");
			clearAddFields(FIELD_TYPES.CLIENT_FIELD);
			populateClientComboBoxes();
			populateAccidentComboBoxes();
		} catch (IllegalArgumentException ex) {
			showErrorOptionPane("Помилка оновлення клієнта: " + ex.getMessage());
		}
	}

	private void listClients() {
		StringBuilder sb = new StringBuilder("Список клієнтів:\n");

		// Create a map to track unique clients by accident ID
		Map<String, Client> uniqueClients = new LinkedHashMap<>();
		for (Client client : main.getClients()) {
			uniqueClients.put(client.getAccidentId(), client);
		}

		// Calculate column widths using unique clients
		int accidentIdWidth = "ID аварії".length();
		int nameWidth = "Ім'я та прізвище".length();
		int addressWidth = "Адреса".length();
		int phoneWidth = "Номер телефону".length();
		int departmentWidth = "Номер відділення".length();

		for (Client client : uniqueClients.values()) {
			accidentIdWidth = Math.max(accidentIdWidth, client.getAccidentId().length());
			nameWidth = Math.max(nameWidth, client.getName().length());
			addressWidth = Math.max(addressWidth, client.getAddress().length());
			phoneWidth = Math.max(phoneWidth, client.getPhoneNumber().length());
			departmentWidth = Math.max(departmentWidth, String.valueOf(client.getDepartmentNumber()).length());
		}

		String format = String.format("%%-%ds %%-%ds %%-%ds %%-%ds %%-%ds\n",
				accidentIdWidth, nameWidth, addressWidth, phoneWidth, departmentWidth);

		sb.append(String.format(format, "ID аварії", "Ім'я та прізвище", "Адреса", "Номер телефону", "Номер відділення"));
		sb.append(String.format(format, "-".repeat(accidentIdWidth), "-".repeat(nameWidth),
				"-".repeat(addressWidth), "-".repeat(phoneWidth), "-".repeat(departmentWidth)));

		// Display only unique clients
		for (Client client : uniqueClients.values()) {
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
				"Ви впевнені, що хочете вийти?",
				"Підтвердження",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (option == JOptionPane.YES_OPTION) {
			main.saveAllData();
			showSuccessOptionPane("Дані збережено!");
		} else if (option == JOptionPane.NO_OPTION) {
			return;
		}

		// Закриття зв'язку з БД
		try {
			main.dbManager.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			showErrorOptionPane("Не вдалося закрити зв'язок з БД: " + ex.getMessage());
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

	public void populateMasterComboBoxes() {
		String[] masterIndices = main.getMasters().stream()
				.map(master -> String.valueOf(master.getIndex()))
				.toArray(String[]::new);

		masterIndexField.setModel(new DefaultComboBoxModel<>(masterIndices));

		String[] brigadeNumbers = main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
		addMasterBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));

		String[] departmentNumbers = main.getBrigadeAndDepartmentNumbers().toArray(new String[0]);
		addMasterDepartmentField.setModel(new DefaultComboBoxModel<>(departmentNumbers));
	}

	public void populateAccidentComboBoxes() {
		Set<String> uniqueIds = new HashSet<>();
		List<String> accidentIds = new ArrayList<>();

		for (Accident accident : main.getAccidents()) {
			String id = accident.getAccidentId();
			if (uniqueIds.add(id)) {
				accidentIds.add(id);
			}
		}

		accidentIds.sort((id1, id2) -> {
			int num1 = Integer.parseInt(id1.split("-")[0]);
			int num2 = Integer.parseInt(id2.split("-")[0]);
			return Integer.compare(num1, num2);
		});

		accidentIdField.setModel(new DefaultComboBoxModel<>(accidentIds.toArray(new String[0])));

		String[] brigadeNumbers = main.getUsedBrigadeNumbers().toArray(new String[0]);
		String[] accidentTypes = main.getAccidentTypes().toArray(new String[0]);

		addAccidentTypeField.setModel(new DefaultComboBoxModel<>(accidentTypes));
		addAccidentBrigadeField.setModel(new DefaultComboBoxModel<>(brigadeNumbers));
	}

	public void populateClientComboBoxes() {
		Set<String> uniqueIds = new HashSet<>();

		for (Accident accident : main.getAccidents()) {
			uniqueIds.add(accident.getAccidentId());
		}

		for (Client client : main.getClients()) {
			uniqueIds.add(client.getAccidentId());
		}

		List<String> sortedIds = new ArrayList<>(uniqueIds);
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