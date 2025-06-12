package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;

class AccidentTest {
	@Test
	void accidentShouldStoreAllProperties() {
		Date now = new Date();
		Brigade brigade = new Brigade(1);
		Accident accident = new Accident(1, "вул. Приморська 43", "Крах крана", 150.0, now, brigade, "У процесі", "0987654321", 1);

		assertEquals(1, accident.getIndex());
		assertEquals("вул. Приморська 43", accident.getAddress());
		assertEquals("Крах крана", accident.getType());
		assertEquals(150.0, accident.getCost(), 0.001);
		assertEquals(now, accident.getDate());
		assertEquals(brigade, accident.getBrigade());
		assertEquals("У процесі", accident.getStatus());
		assertEquals("0987654321", accident.getClientPhoneNumber());
	}
}
