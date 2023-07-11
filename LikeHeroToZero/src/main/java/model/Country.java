package model;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Named
@ViewScoped
@Entity
@Table(name = "country")
public class Country implements Serializable, Comparable<Country> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id")
	private Integer id;

	@Column(name = "country_name")
	private String name;

	@Column(name = "country_code")
	private String code;

	public Country() {
	}

	public Country(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int compareTo(Country o) {
		if (this.name == null) {
			return -1;
		} else {
			return this.name.compareTo(o.getName());
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Country)) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (this.name == null || this.code == null) {
			return false;
		}
		return this.name.equals(((Country) other).getName()) && this.code.equals(((Country) other).getCode());
	}

}
