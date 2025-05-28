package com.vitaliyrev.courseworkdb.app;

// Клас "Майстер"
public class Master extends Entity {
	// Персональні дані майстра
	private String name; // Ім'я майстра
	private String address; // Адреса проживання майстра
	private String phoneNumber; // Номер телефону майстра
	private int experience; // Стаж роботи майстра
	private String status; // Стан майстра
	private Brigade brigade; // Бригада, до якої належить майстер
	private int departmentNumber; // Номер відділу, в якому працює майстр

	// Конструктор класу з параметрами для ініціалізації всіх полів
	public Master(int index, String name, String address, String phoneNumber, int experience, String status, Brigade brigade, int departmentNumber) {
		super(index); // Виклик конструктора батьківського класу Entity з передачею індексу
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.experience = experience;
		this.status = status;
		this.brigade = brigade;
		this.departmentNumber = departmentNumber;
	}

	// Гетери та сетери для доступу до приватних полів класу
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Brigade getBrigade() {
		return brigade;
	}

	public void setBrigade(Brigade brigade) {
		this.brigade = brigade;
	}

	public int getDepartmentNumber() {
		return departmentNumber;
	}

	public void setDepartmentNumber(int departmentNumber) {
		this.departmentNumber = departmentNumber;
	}

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		return getIndex() + ";" + name + ";" + address + ";" + phoneNumber + ";" + experience + ";" + status + ";" + (brigade != null ? brigade.getNumber() : "") + ";" + departmentNumber;
	}
}