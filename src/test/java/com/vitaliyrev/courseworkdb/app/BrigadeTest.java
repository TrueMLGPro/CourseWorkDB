package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BrigadeTest {
	private Brigade brigade;
	private Master master;

	@BeforeEach
	void setUp() {
		brigade = new Brigade(1);
		master = new Master(1, "Валерій Іваненко", "вул. Молодіжна 12", "0987654321", 5, "Зайнятий", brigade, 1);
	}

	@Test
	void brigadeShouldHandleMasters() {
		brigade.addMaster(master);
		assertTrue(brigade.getMasters().contains(master));

		brigade.removeMaster(master);
		assertFalse(brigade.getMasters().contains(master));
	}

	@Test
	void brigadeNumberShouldBeSettable() {
		brigade.setNumber(2);
		assertEquals(2, brigade.getNumber());
	}
}
