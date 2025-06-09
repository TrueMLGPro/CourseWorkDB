package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MasterTest {
	@Test
	void masterShouldStoreAllProperties() {
		Brigade brigade = new Brigade(1);
		Master master = new Master(1, "Валерій Іваненко", "вул. Молодіжна 12", "0987654321", 5, "Зайнятий", brigade, 1);

		assertEquals("Валерій Іваненко", master.getName());
		assertEquals("вул. Молодіжна 12", master.getAddress());
		assertEquals("0987654321", master.getPhoneNumber());
		assertEquals(5, master.getExperience());
		assertEquals("Зайнятий", master.getStatus());
		assertEquals(brigade, master.getBrigade());
		assertEquals(1, master.getDepartmentNumber());
	}
}
