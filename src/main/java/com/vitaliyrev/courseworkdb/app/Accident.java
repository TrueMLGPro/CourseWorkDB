package com.vitaliyrev.courseworkdb.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Клас "Аварія"
public class Accident extends Entity {
	private Client client;
	// Дані аварії
	private String address; // Адреса, де відбулася аварія
	private String type; // Тип аварії
	private double cost; // Вартість ремонту або вирішення аварії
	private Date date; // Дата виникнення аварії
	private Brigade brigade; // Бригада, яка бере участь у вирішенні аварії
	private String status; // Стан аварії
	private String clientPhoneNumber; // Номер телефону клієнта
	private int accidentIndex;

	// Конструктор класу з параметрами для ініціалізації всіх полів
	public Accident(int index, String address, String type, double cost, Date date, Brigade brigade, String status, String clientPhoneNumber, int accidentIndex) {
		super(index); // Виклик конструктора батьківського класу Entity з передачею індексу
		this.address = address;
		this.type = type;
		this.cost = cost;
		this.date = date;
		this.brigade = brigade;
		this.status = status;
		this.clientPhoneNumber = clientPhoneNumber;
		this.accidentIndex = accidentIndex;
	}

	// Гетери та сетери для доступу до приватних полів класу
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Brigade getBrigade() {
		return brigade;
	}

	public void setBrigade(Brigade brigade) {
		this.brigade = brigade;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClientPhoneNumber() {
		return clientPhoneNumber;
	}

	public void setClientPhoneNumber(String clientPhoneNumber) {
		this.clientPhoneNumber = clientPhoneNumber;
	}

	public int getAccidentIndex() {
		return accidentIndex;
	}

	public void setAccidentIndex(int accidentIndex) {
		this.accidentIndex = accidentIndex;
	}

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		return getIndex() + ";" + address + ";" + type + ";" + cost + ";" + dateFormat.format(date) + ";" + (brigade != null ? brigade.getNumber() : "") + ";" + status + ";" + clientPhoneNumber + ";" + accidentIndex;
	}
}