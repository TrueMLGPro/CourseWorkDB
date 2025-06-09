package com.vitaliyrev.courseworkdb.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EntityTest {

	@Test
	void entityShouldStoreIndex() {
		Entity entity = new TestEntity(5);
		assertEquals(5, entity.getIndex());

		entity.setIndex(10);
		assertEquals(10, entity.getIndex());
	}

	private static class TestEntity extends Entity {
		public TestEntity(int index) {
			super(index);
		}
	}
}
