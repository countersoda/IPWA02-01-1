package model;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class Emission implements Serializable {

	private int year;
	private float amount;
	private boolean published;

	public Emission() {
	}

	public Emission(int year, float amount, boolean published) {
		this.year = year;
		this.amount = amount;
		this.setPublished(published);
	}

	@Override
	public String toString() {
		return String.format("%d; %f; %b", this.year, this.amount, this.published);
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

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}
}