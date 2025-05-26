package app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Клас "Аварія"
public class Accident extends Entity {
	// Дані аварії
	private String accidentId; // Унікальний ідентифікатор аварії
	private String address; // Адреса, де відбулася аварія
	private String type; // Тип аварії
	private double cost; // Вартість ремонту або вирішення аварії
	private Date date; // Дата виникнення аварії
	private Brigade brigade; // Бригада, яка бере участь у вирішенні аварії
	private String status; // Стан аварії

	// Конструктор класу з параметрами для ініціалізації всіх полів
	public Accident(int index, String accidentId, String address, String type, double cost, Date date, Brigade brigade, String status) {
		super(index); // Виклик конструктора батьківського класу Entity з передачею індексу
		this.accidentId = accidentId;
		this.address = address;
		this.type = type;
		this.cost = cost;
		this.date = date;
		this.brigade = brigade;
		this.status = status;
	}

	// Конструктор класу з параметрами типу String[], що використовується для ініціалізації об'єкта з рядка даних
	public Accident(String[] data) {
		super(Integer.parseInt(data[0]));
		this.accidentId = data[1];
		this.address = data[2];
		this.type = data[3];
		this.cost = Double.parseDouble(data[4]);
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
			this.date = dateFormat.parse(data[5]);
		} catch (Exception e) {
			this.date = new Date(); // Якщо парсинг не вдався, встановлюється поточна дата
		}
		this.brigade = new Brigade(Integer.parseInt(data[6]));
		this.status = data[7];
	}

	// Гетери та сетери для доступу до приватних полів класу
	public String getAccidentId() {
		return accidentId;
	}

	public void setAccidentId(String accidentId) {
		this.accidentId = accidentId;
	}

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

	// Перевизначення методу toString() для представлення об'єкта у вигляді рядка
	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
		return getIndex() + ";" + accidentId + ";" + address + ";" + type + ";" + cost + ";" + dateFormat.format(date) + ";" + (brigade != null ? brigade.getNumber() : "") + ";" + status;
	}
}