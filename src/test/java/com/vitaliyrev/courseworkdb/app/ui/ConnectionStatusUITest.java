package com.vitaliyrev.courseworkdb.app.ui;

import com.vitaliyrev.courseworkdb.app.ui.abc.BaseUITest;
import com.vitaliyrev.courseworkdb.app.ui.abc.GUITestExtension;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.fixture.JLabelFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.Color;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(GUITestExtension.class)
class ConnectionStatusUITest extends BaseUITest {

	@GUITest
	@Test
	void testConnectionStatusIndicator() {
		when(mockDbManager.isConnectionValid()).thenReturn(true);
		ui.updateConnectionStatus();

		JLabelFixture statusLabel = window.label("connectionStatusLabel");
		assertEquals("Приєднано до БД", statusLabel.text());
		assertEquals(Color.BLACK, statusLabel.foreground().target());

		when(mockDbManager.isConnectionValid()).thenReturn(false);
		ui.updateConnectionStatus();

		assertEquals("Від'єднано від БД", statusLabel.text());
		assertEquals(Color.RED, statusLabel.foreground().target());
	}

	@GUITest
	@Test
	void testReconnectFlow() throws SQLException {
		doNothing().when(mainMock).reconnect();
		when(mockDbManager.isConnectionValid()).thenReturn(false);
		ui.updateConnectionStatus();

		window.tabbedPane().selectTab("Звіти");
		window.button("accidentCountPerBrigadeButton").click();

		verify(mainMock, times(1)).reconnect();
	}
}
