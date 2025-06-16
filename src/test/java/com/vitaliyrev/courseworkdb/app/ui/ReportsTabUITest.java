package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(GUITestExtension.class)
public class ReportsTabUITest extends BaseUITest {

	@BeforeEach
	void setUp() throws SQLException {
		prepareTestData();
	}

	@GUITest
	@Test
	void testMastersWithBrigadeInfoButton() {
		window.tabbedPane().selectTab("Звіти");
		window.button("mastersWithBrigadeInfoButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getMastersWithBrigadeInfo();
	}

	@GUITest
	@Test
	void testFindClientsByNamePattern() {
		window.tabbedPane().selectTab("Звіти");
		String namePattern = "Василь";
		window.textBox("reportClientNameField").enterText(namePattern);

		window.button("findClientsByNameButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).findClientsByNamePattern(anyString());
	}

	@GUITest
	@Test
	void testAccidentsBetweenDates() {
		window.tabbedPane().selectTab("Звіти");
		String startDate = "01/01/2025";
		String endDate = "01/07/2025";
		window.textBox("reportStartDateField").enterText(startDate);
		window.textBox("reportEndDateField").enterText(endDate);

		window.button("accidentsBetweenDatesButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getAccidentsBetweenDates(any(), any());
	}

	@GUITest
	@Test
	void testAccidentCountLastWeek() {
		window.tabbedPane().selectTab("Звіти");
		window.button("accidentCountLastWeekButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getAccidentCountLastWeek();
	}

	@GUITest
	@Test
	void testAccidentCountPerBrigade() {
		window.tabbedPane().selectTab("Звіти");
		window.button("accidentCountPerBrigadeButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getAccidentCountPerBrigade();
	}

	@GUITest
	@Test
	void testBrigadesWithMaxAccidents() {
		window.tabbedPane().selectTab("Звіти");
		window.button("maxAccidentBrigadesButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getBrigadesWithMaxAccidents();
	}

	@GUITest
	@Test
	void testTopBrigadePerAccidentType() {
		window.tabbedPane().selectTab("Звіти");
		window.button("topBrigadePerTypeButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getTopBrigadePerAccidentType();
	}

	@GUITest
	@Test
	void testInactiveBrigadesLeftJoin() {
		window.tabbedPane().selectTab("Звіти");
		window.button("inactiveBrigadesLeftJoinButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getInactiveBrigadesLeftJoin();
	}

	@GUITest
	@Test
	void testInactiveBrigadesIn() {
		window.tabbedPane().selectTab("Звіти");
		window.button("inactiveBrigadesInButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getInactiveBrigadesIn();
	}

	@GUITest
	@Test
	void testInactiveBrigadesExists() {
		window.tabbedPane().selectTab("Звіти");
		window.button("inactiveBrigadesExistsButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getInactiveBrigadesExists();
	}

	@GUITest
	@Test
	void testBrigadeActivityStatus() {
		window.tabbedPane().selectTab("Звіти");
		window.button("brigadeActivityStatusButton").click();
		Mockito.verify(mainMock, Mockito.times(1)).getBrigadesWithActivityStatus();
	}
}