package model;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Named
@ViewScoped
@Entity
public class Emission implements Serializable, Comparable<Emission> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "emission_id")
	private Integer id;

	@Column(name = "emission_year", nullable = false)
	private int emission_year;

	@Column(name = "emission_amount", nullable = false)
	private float emission_amount;

	@Column(name = "emission_published", nullable = false)
	private boolean emission_editable;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "owner_id")
	private Credential owner;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "country_id")
	private Country country;

	public Emission() {
	}

	public Emission(int year, float amount, boolean editable, Country country, Credential owner) {
		this.emission_year = year;
		this.emission_amount = amount;
		this.emission_editable = editable;
		this.country = country;
		this.owner = owner;
	}

	public Integer getId() {
		return this.id;
	}

	public String getOwner() {
		return this.owner.getUsername();
	}

	@Override
	public String toString() {
		return String.format("%d; %f; %b", this.emission_year, this.emission_amount, this.emission_editable);
	}

	public float getAmount() {
		return emission_amount;
	}

	public void setAmount(float amount) {
		this.emission_amount = amount;
	}

	public int getYear() {
		return emission_year;
	}

	public void setYear(int year) {
		this.emission_year = year;
	}

	public boolean isEditable() {
		return emission_editable;
	}

	public void setEditable(boolean editable) {
		this.emission_editable = editable;
	}

	@Override
	public int compareTo(Emission o) {
		return this.emission_year - o.emission_year;
	}
}