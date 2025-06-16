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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

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

	protected void prepareTestData() throws SQLException {
		Brigade brigade1 = new Brigade(1);
		Master master1 = new Master(1, "Валерій Іваненко", "вул. Молодіжна 12", "0987654320", 5, "Вільний", brigade1, 1);
		Client client1 = new Client(1, "Василь Стасенко", "вул. Приморська 43", "0987654321", 1);
		Accident accident1 = new Accident(1, "вул. Приморська 43", "Крах крана", 150.0, new Date(125, Calendar.JUNE, 10), brigade1, "У процесі", "0987654321", 1);

		List<Master> masters = List.of(master1);
		List<Client> clients = List.of(client1);
		List<Accident> accidents = List.of(accident1);
		List<String> usedBrigadeNumbers = List.of("1");
		List<String> usedDepartmentNumbers = List.of("1");

		Mockito.when(mainMock.getMasters()).thenReturn(masters);
		Mockito.when(mainMock.getClients()).thenReturn(clients);
		Mockito.when(mainMock.getAccidents()).thenReturn(accidents);
		Mockito.when(mainMock.getMasterByName("Валерій Іваненко")).thenReturn(master1);
		Mockito.when(mainMock.getClientByName("Василь Стасенко")).thenReturn(client1);
		Mockito.when(mainMock.getClientPhoneNumbers()).thenReturn(List.of("0987654321"));
		Mockito.when(mainMock.getAccidentByPhoneNumAccIndex("0987654321", 1)).thenReturn(accident1);
		Mockito.when(mainMock.getUsedBrigadeNumbers()).thenReturn(usedBrigadeNumbers);
		Mockito.when(mainMock.getUsedDepartmentNumbers()).thenReturn(usedDepartmentNumbers);
		Mockito.when(mainMock.getAccidentIndicesForClient("0987654321")).thenReturn(List.of(1));

		Mockito.doNothing().when(mainMock).addMaster(any(Master.class));
		Mockito.doNothing().when(mainMock).updateMaster(any(Master.class));
		Mockito.doNothing().when(mainMock).deleteMaster(any(Integer.class));
		Mockito.doNothing().when(mainMock).addClient(any(Client.class));
		Mockito.doNothing().when(mainMock).updateClient(any(Client.class));
		Mockito.doNothing().when(mainMock).deleteClient(any(Integer.class));
		Mockito.doNothing().when(mainMock).addAccident(any(Accident.class));
		Mockito.doNothing().when(mainMock).updateAccident(any(Accident.class));
		Mockito.doNothing().when(mainMock).deleteAccident(any(Integer.class));

		Mockito.when(mainMock.getNextMasterIndex()).thenReturn(2);
		Mockito.when(mainMock.getNextClientIndex()).thenReturn(2);
		Mockito.when(mainMock.getNextAccidentIndex()).thenReturn(2);
		Mockito.when(mainMock.getNextAccidentIndexForClient(anyString())).thenReturn(2);

		ui.populateAllComboBoxes();
	}

	protected int getComboboxItemsCount(String comboName) {
		return window.comboBox(comboName).contents().length;
	}
}