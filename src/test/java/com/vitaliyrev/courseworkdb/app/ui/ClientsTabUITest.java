package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
