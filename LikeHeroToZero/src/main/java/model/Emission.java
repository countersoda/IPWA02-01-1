package model;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Named
@ViewScoped
@Entity(name = "emission")
public class Emission implements Serializable, Comparable<Emission> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "emission_id")
	private Integer id;

	@Column(name = "emission_year", nullable = false)
	private int year;

	@Column(name = "emission_amount", nullable = false)
	private float amount;

	@Column(name = "emission_published", nullable = false)
	private boolean published;

	@OneToOne
	@JoinColumn(name = "country", referencedColumnName = "country_id")
	private Country country;

	public Emission() {
	}

	public Emission(int year, float amount, boolean published, Country country) {
		this.year = year;
		this.amount = amount;
		this.published = published;
		this.country = country;
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

	@Override
	public int compareTo(Emission o) {
		return this.year - o.year;
	}
}