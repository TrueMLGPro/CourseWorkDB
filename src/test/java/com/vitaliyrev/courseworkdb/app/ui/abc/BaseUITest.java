package com.vitaliyrev.courseworkdb.app.ui.abc;

import com.vitaliyrev.courseworkdb.app.*;
import com.vitaliyrev.courseworkdb.app.db.DatabaseManager;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(GUITestExtension.class)
public abstract class BaseUITest {
	protected FrameFixture window;
	protected Robot robot;

	protected UserInterface ui;
	protected DatabaseManager mockDbManager;
	protected Main mainMock;

	@BeforeEach
	public void onSetUp() {
		mockDbManager = Mockito.mock(DatabaseManager.class);
		mainMock = Mockito.spy(new Main(mockDbManager));
		ui = GuiActionRunner.execute(() -> new UserInterface(mainMock));

		robot = BasicRobot.robotWithCurrentAwtHierarchy();
		window = new FrameFixture(robot, ui.frame);
		window.show();
	}

	@AfterEach
	public void onTearDown() {
		if (window != null) {
			window.cleanUp();
		}
		if (robot != null) {
			robot.cleanUpWithoutDisposingWindows();
		}
	}

	protected void prepareTestData() {
		List<Brigade> brigades = new ArrayList<>(List.of(
				new Brigade(1)
		));
		List<Master> masters = new ArrayList<>(List.of(
				new Master(1, "Валерій Іваненко", "вул. Молодіжна 12", "0987654320", 5, "Зайнятий", new Brigade(1), 1)
		));
		List<Client> clients = new ArrayList<>(List.of(
				new Client(1, "Василь Стасенко", "вул. Приморська 43", "0987654321", 1)
		));
		List<Accident> accidents = new ArrayList<>(List.of(
				new Accident(1, "вул. Приморська 43", "Крах крана", 150.0, new Date(125, Calendar.JUNE, 10), new Brigade(1), "У процесі", "0987654321", 1)
		));

		mainMock.setMasters(masters);
		mainMock.setClients(clients);
		mainMock.setAccidents(accidents);

		try {
			Mockito.when(mockDbManager.loadExistingBrigades()).thenReturn(brigades);
			Mockito.when(mockDbManager.loadMasters(any())).thenReturn(masters);
			Mockito.when(mockDbManager.loadClients()).thenReturn(clients);
			Mockito.when(mockDbManager.loadAccidents(any())).thenReturn(accidents);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		mainMock.loadAllData();
		ui.populateMasterComboBoxes();
		ui.populateClientComboBoxes();
		ui.populateAccidentComboBoxes();
	}

	protected int getComboboxItemsCount(String comboName) {
		return window.comboBox(comboName).contents().length;
	}
}