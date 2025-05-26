package com.vitaliyrev.courseworkdb.app;

import java.util.ArrayList;
import java.util.List;

// Клас "Бригада"
public class Brigade {
	private int number; // Унікальний номер бригади
	private final List<Master> masters; // Список майстрів, що належать до бригади

	// Конструктор класу з параметром number для ініціалізації номера бригади
	public Brigade(int number) {
		this.number = number;
		this.masters = new ArrayList<>();
	}

	// Гетери та сетери для доступу до приватних полів класу
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	// Метод для додавання майстра до бригади
	public void addMaster(Master master) {
		masters.add(master);
	}

	// Метод для видалення майстра з бригади
	public void removeMaster(Master master) {
		masters.remove(master);
	}

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		return number + ";" + masters.stream()
			.map(Entity::getIndex) // Отримання індексів майстрів
			.map(String::valueOf) // Перетворення індексів у рядки
			.reduce("", (a, b) -> a + ";" + b) // Об'єднання рядків з індексами
			.substring(1); // Видалення початкового символу ";"
	}
}