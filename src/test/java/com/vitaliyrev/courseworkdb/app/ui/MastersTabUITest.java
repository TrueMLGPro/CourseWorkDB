package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GUITestExtension.class)
class MastersTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testListMasters() {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		window.button("listMastersButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Валерій Іваненко"));
		assertTrue(result.contains("0987654321"));
		assertTrue(result.contains("вул. Молодіжна 12"));
	}

	@GUITest
	@Test
	void testAddNewMaster() {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		window.textBox("addMasterNameField").enterText("Новий Майстер");
		window.textBox("addMasterAddressField").enterText("вул. Майстерська 7");
		window.textBox("addMasterPhoneField").enterText("0678889900");
		window.textBox("addMasterExperienceField").enterText("5");
		window.comboBox("addMasterStatusField").selectItem("Призначений");
		window.comboBox("addMasterBrigadeField").selectItem("2");
		window.comboBox("addMasterDepartmentField").selectItem("3");

		window.button("addMasterButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		window.button("listMastersButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Новий Майстер"));
		assertTrue(result.contains("Призначений"));
	}

	@GUITest
	@Test
	void testUpdateMasterStatus() {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		JComboBoxFixture masterCombo = window.comboBox("masterIndexField");
		masterCombo.selectItem(0);

		String masterIndex = (String) masterCombo.target().getSelectedItem();
		ui.loadMasterData(Integer.parseInt(masterIndex));
		window.comboBox("addMasterStatusField").selectItem("Зайнятий");
		window.button("updateMasterButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		window.button("listMastersButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Зайнятий"));
	}

	@GUITest
	@Test
	void testDeleteMaster() {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		int initialCount = getComboboxItemsCount("masterIndexField");
		JComboBoxFixture masterCombo = window.comboBox("masterIndexField");
		masterCombo.selectItem(0);

		String masterIndex = (String) masterCombo.target().getSelectedItem();
		ui.loadMasterData(Integer.parseInt(masterIndex));
		window.button("deleteMasterButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		assertEquals(initialCount - 1, getComboboxItemsCount("masterIndexField"));
	}

	@GUITest
	@Test
	void testMasterValidation_InvalidExperience() {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		window.textBox("addMasterExperienceField").enterText("-5");
		window.button("addMasterButton").click();

		window.optionPane().requireErrorMessage().okButton().click();
		assertTrue(window.textBox("addMasterExperienceField").text().contains("-"));
	}
}
