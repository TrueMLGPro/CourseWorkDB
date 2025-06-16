package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(GUITestExtension.class)
public class ClientsTabUITest extends BaseUITest {

	@GUITest
	@Test
	void testListClients() throws SQLException {
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
	void testAddNewClient() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		window.textBox("addClientNameField").enterText("Новий Клієнт");
		window.textBox("addClientAddressField").enterText("вул. Нова 25");
		window.textBox("addClientPhoneField").enterText("0991112233");
		window.comboBox("addClientDepartmentField").selectItem("1");

		window.button("addClientButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).addClient(any());
	}

	@GUITest
	@Test
	void testUpdateClientPhone() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		window.comboBox("clientListNameField").selectItem("Василь Стасенко");
		window.textBox("addClientPhoneField").deleteText().enterText("0505556677");
		window.button("updateClientButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).updateClient(any());
	}

	@GUITest
	@Test
	void testDeleteClient() throws SQLException {
		prepareTestData();
		window.tabbedPane().selectTab("Клієнти");

		window.comboBox("clientListNameField").selectItem("Василь Стасенко");
		window.button("deleteClientButton").click();
		window.optionPane().requireInformationMessage().okButton().click();

		Mockito.verify(mainMock, Mockito.times(1)).deleteClient(1);
	}
}