package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
