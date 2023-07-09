package model;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class Emission implements Serializable {

	private int year;
	private float amount;

	public Emission() {
	}

	public Emission(int year, float amount) {
		this.year = year;
		this.amount = amount;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}