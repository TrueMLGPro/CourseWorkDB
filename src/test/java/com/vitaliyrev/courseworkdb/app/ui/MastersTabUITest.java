package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(GUITestExtension.class)
class MastersTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testListMasters() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");
		window.button("listMastersButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Валерій Іваненко"));
		assertTrue(result.contains("0987654320"));
		assertTrue(result.contains("вул. Молодіжна 12"));
	}

	@GUITest
	@Test
	void testAddNewMaster() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		window.textBox("addMasterNameField").enterText("Новий Майстер");
		window.textBox("addMasterAddressField").enterText("вул. Майстерська 7");
		window.textBox("addMasterPhoneField").enterText("0678889900");
		window.textBox("addMasterExperienceField").enterText("5");
		window.comboBox("addMasterStatusField").selectItem("Зайнятий");
		window.comboBox("addMasterBrigadeField").selectItem("1");
		window.comboBox("addMasterDepartmentField").selectItem("1");

		window.button("addMasterButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).addMaster(any());
	}

	@GUITest
	@Test
	void testUpdateMasterStatus() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		JComboBoxFixture masterCombo = window.comboBox("masterListNameField").selectItem("Валерій Іваненко");
		String masterName = (String) masterCombo.target().getSelectedItem();
		ui.loadMasterData(masterName);
		window.comboBox("addMasterStatusField").selectItem("Зайнятий");
		window.button("updateMasterButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).updateMaster(any());
	}

	@GUITest
	@Test
	void testDeleteMaster() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Майстри");

		JComboBoxFixture masterCombo = window.comboBox("masterListNameField").selectItem("Валерій Іваненко");
		String masterName = (String) masterCombo.target().getSelectedItem();
		ui.loadMasterData(masterName);
		window.button("deleteMasterButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).deleteMaster(1);
	}
}