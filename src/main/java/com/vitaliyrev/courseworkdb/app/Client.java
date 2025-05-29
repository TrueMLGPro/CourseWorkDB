package com.vitaliyrev.courseworkdb.app;

// Клас "Клієнт"
public class Client extends Entity {
	// Персональні дані клієнта
	private String accidentId; // Унікальний ідентифікатор аварії, пов'язаної з клієнтом
	private String name; // Ім'я клієнта
	private String address; // Адреса проживання клієнта
	private String phoneNumber; // Номер телефону клієнта
	private int departmentNumber; // Номер відділу, з яким пов'язан клієнт

	// Конструктор класу з параметрами для ініціалізації всіх полів
	public Client(int index, String accidentId, String name, String address, String phoneNumber, int departmentNumber) {
		super(index); // Виклик конструктора батьківського класу Entity з передачею індексу
		this.accidentId = accidentId;
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.departmentNumber = departmentNumber;
	}

	// Гетери та сетери для доступу до приватних полів класу
	public String getAccidentId() {
		return accidentId;
	}

	public void setAccidentId(String accidentId) {
		this.accidentId = accidentId;
	}

	public void setAccident(Accident accident) {
		if (accident != null) {
			this.address = accident.getAddress();
			accident.setAddress(this.address);
		}
	}

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

	public int getDepartmentNumber() {
		return departmentNumber;
	}

	public void setDepartmentNumber(int departmentNumber) {
		this.departmentNumber = departmentNumber;
	}

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		return getIndex() + ";" + accidentId + ";" + name + ";" + address + ";" + phoneNumber + ";" + departmentNumber;
	}
}