package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GUITestExtension.class)
public class ClientsTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testListClients() {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		window.button("listClientsButton").click();

		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Василь Стасенко"));
		assertTrue(result.contains("вул. Приморська 43"));
		assertTrue(result.contains("0987654321"));
	}

	@GUITest
	@Test
	void testAddNewClient() {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		window.textBox("addClientNameField").enterText("Новий Клієнт");
		window.textBox("addClientAddressField").enterText("вул. Нова 25");
		window.textBox("addClientPhoneField").enterText("0991112233");
		window.comboBox("addClientDepartmentField").selectItem("1");

		window.button("addClientButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		window.button("listClientsButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("Новий Клієнт"));
		assertTrue(result.contains("0991112233"));
	}

	@GUITest
	@Test
	void testUpdateClientPhone() {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		JComboBoxFixture clientCombo = window.comboBox("clientListNameField");
		clientCombo.selectItem(0);

		String clientId = (String) clientCombo.target().getSelectedItem();
		ui.loadClientData(clientId);
		window.textBox("addClientPhoneField").deleteText().enterText("0505556677");
		window.button("updateClientButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		window.button("listClientsButton").click();
		String result = window.textBox("resultArea").text();
		assertTrue(result.contains("0505556677"));
	}

	@GUITest
	@Test
	void testDeleteClient() {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		int initialCount = getComboboxItemsCount("clientListNameField");
		JComboBoxFixture clientCombo = window.comboBox("clientListNameField");
		clientCombo.selectItem(0);

		String clientId = (String) clientCombo.target().getSelectedItem();
		ui.loadClientData(clientId);
		window.button("deleteClientButton").click();

		window.optionPane().requireInformationMessage().okButton().click();
		assertEquals(initialCount - 1, getComboboxItemsCount("clientListNameField"));
	}

	@GUITest
	@Test
	void testClientValidation_MissingName() {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		JComboBoxFixture clientCombo = window.comboBox("clientListNameField");
		clientCombo.selectItem(0);

		String clientId = (String) clientCombo.target().getSelectedItem();
		ui.loadClientData(clientId);
		window.textBox("addClientNameField").deleteText();
		window.button("addClientButton").click();

		window.optionPane().requireErrorMessage().okButton().click();
		assertTrue(window.textBox("addClientNameField").text().isEmpty());
	}
}
