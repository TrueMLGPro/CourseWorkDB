package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
}
