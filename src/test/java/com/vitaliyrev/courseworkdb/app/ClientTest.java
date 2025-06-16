package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Date;

class ClientTest {

	@Test
	void clientShouldStoreAllProperties() {
		Client client = new Client(1, "Василь Стасенко", "вул. Приморська 43", "0987654321", 1);

		assertEquals(1, client.getIndex());
		assertEquals("Василь Стасенко", client.getName());
		assertEquals("вул. Приморська 43", client.getAddress());
		assertEquals("0987654321", client.getPhoneNumber());
		assertEquals(1, client.getDepartmentNumber());
	}

	@Test
	void clientShouldSetAccidentAssociation() {
		Client client = new Client(1, "Василь Стасенко", "вул. Приморська 43", "0987654321", 1);
		Accident accident = new Accident(1, "вул. Приморська 43", "Крах крана", 150.0, new Date(), new Brigade(1), "У процесі", "0987654321", 1);

		client.setAccident(accident);

		assertEquals(accident.getAddress(), client.getAddress());
		assertEquals(accident.getClientPhoneNumber(), client.getPhoneNumber());
	}
}
