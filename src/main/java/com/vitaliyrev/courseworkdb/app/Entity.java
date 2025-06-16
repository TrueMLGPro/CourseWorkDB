package com.vitaliyrev.courseworkdb.app;

// Базовий клас "Сутність"
public abstract class Entity {
	private int index; // Унікальний індекс сутності

	// Конструктор класу з параметром index для ініціалізації індексу
	public Entity(int index) {
		this.index = index;
	}

	// Гетери та сетери для доступу до приватного поля index
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		return String.valueOf(index);
	}
}