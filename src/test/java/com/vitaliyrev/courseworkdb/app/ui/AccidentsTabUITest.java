package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GUITestExtension.class)
class AccidentsTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testAccidentReport() {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.textBox("accidentStartDateField").enterText("01/01/2025");
		window.textBox("accidentEndDateField").enterText("01/07/2025");

		window.button("countAccidentsButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Кількість"));
		assertTrue(result.contains(": 1"));
	}

	@GUITest
	@Test
	void testAccidentReport_NoResults() {
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
	void testAddNewAccident() {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.textBox("addAccidentDateField").enterText("15/06/2025");
		window.textBox("addAccidentAddressField").enterText("вул. Виноградна 15");
		window.comboBox("addAccidentTypeField").selectItem("Крах водонапорної системи");
		window.textBox("addAccidentCostField").enterText("150.50");
		window.comboBox("addAccidentBrigadeField").selectItem("1");
		window.comboBox("addAccidentStatusField").selectItem("Вирішено");

		window.button("addAccidentButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		window.button("listAccidentsButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("вул. Виноградна 15"));
		assertTrue(result.contains("150.5"));
	}

	@GUITest
	@Test
	void testUpdateAccident() {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		JComboBoxFixture accidentIndexCombo = window.comboBox("accidentIndexField");
		accidentIndexCombo.selectItem(0);
		String accidentIndex = (String) accidentIndexCombo.target().getSelectedItem();

		JComboBoxFixture accidentClientPhoneNumberCombo = window.comboBox("accidentClientPhoneNumberField");
		accidentClientPhoneNumberCombo.selectItem(0);
		String accidentClientPhoneNumber = (String) accidentClientPhoneNumberCombo.target().getSelectedItem();

		ui.loadAccidentData(accidentClientPhoneNumber, Integer.parseInt(accidentIndex));

		window.textBox("addAccidentAddressField").deleteText().enterText("вул. Оновлена 99");
		window.button("updateAccidentButton").click();

		window.optionPane().requireInformationMessage().okButton().click();

		window.button("listAccidentsButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("вул. Оновлена 99"));
	}

	@GUITest
	@Test
	void testDeleteAccident() {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		int initialCount = getComboboxItemsCount("accidentIndexField");
		JComboBoxFixture accidentIndexCombo = window.comboBox("accidentIndexField");
		accidentIndexCombo.selectItem(0);

		JComboBoxFixture accidentClientPhoneNumberCombo = window.comboBox("accidentClientPhoneNumberField");
		accidentClientPhoneNumberCombo.selectItem(0);

		String accidentClientPhoneNumber = (String) accidentClientPhoneNumberCombo.target().getSelectedItem();
		String accidentIndex = (String) accidentIndexCombo.target().getSelectedItem();

		ui.loadAccidentData(accidentClientPhoneNumber, Integer.parseInt(accidentIndex));
		window.button("deleteAccidentButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		assertEquals(initialCount - 1, getComboboxItemsCount("accidentIndexField"));
	}

	@GUITest
	@Test
	void testAccidentValidation_InvalidCost() {
		prepareTestData();
		window.tabbedPane().selectTab("Аварії");

		window.textBox("addAccidentCostField").enterText("invalid");
		window.button("addAccidentButton").click();

		window.optionPane().requireErrorMessage().okButton().click();
		assertTrue(window.textBox("addAccidentCostField").text().contains("invalid"));
	}
}
