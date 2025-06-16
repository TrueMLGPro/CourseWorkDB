package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(GUITestExtension.class)
class AccidentsTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testAccidentReport() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		Mockito.when(mainMock.getAccidentCountBetweenDates(any(Date.class), any(Date.class))).thenReturn(1);

		window.textBox("accidentStartDateField").enterText("01/01/2025");
		window.textBox("accidentEndDateField").enterText("01/07/2025");
		window.button("countAccidentsButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Кількість аварій"));
		assertTrue(result.contains(": 1"));
	}

	@GUITest
	@Test
	void testAccidentReport_NoResults() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.textBox("accidentStartDateField").enterText("01/01/2030");
		window.textBox("accidentEndDateField").enterText("01/07/2030");
		window.button("countAccidentsButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains(": 0"));
	}

	@GUITest
	@Test
	void testAddNewAccident() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.textBox("addAccidentDateField").enterText("15/06/2025");
		window.textBox("addAccidentAddressField").enterText("вул. Виноградна 15");
		window.comboBox("addAccidentTypeField").selectItem("Крах водонапорної системи");
		window.textBox("addAccidentCostField").enterText("150.50");
		window.comboBox("addAccidentStatusField").selectItem("Вирішено");
		window.comboBox("addAccidentBrigadeField").selectItem("1");
		window.comboBox("accidentClientPhoneNumberField").selectItem("0987654321");

		window.button("addAccidentButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).addAccident(any());
	}

	@GUITest
	@Test
	void testUpdateAccident() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		JComboBoxFixture accidentClientPhoneNumberCombo = window.comboBox("accidentClientPhoneNumberField");
		accidentClientPhoneNumberCombo.selectItem(0);
		String accidentClientPhoneNumber = (String) accidentClientPhoneNumberCombo.target().getSelectedItem();

		ui.populateClientAccidentIndexComboBox("0987654321");

		JComboBoxFixture accidentIndexCombo = window.comboBox("accidentIndexField");
		accidentIndexCombo.selectItem(0);
		String accidentIndex = (String) accidentIndexCombo.target().getSelectedItem();

		ui.loadAccidentData(accidentClientPhoneNumber, Integer.parseInt(accidentIndex));

		window.textBox("addAccidentAddressField").deleteText().enterText("вул. Оновлена 99");
		window.button("updateAccidentButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).updateAccident(any());
	}

	@GUITest
	@Test
	void testDeleteAccident() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.comboBox("accidentClientPhoneNumberField").selectItem("0987654321");
		ui.populateClientAccidentIndexComboBox("0987654321");
 		window.comboBox("accidentIndexField").selectItem("1");

		window.button("deleteAccidentButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).deleteAccident(1);
	}
}