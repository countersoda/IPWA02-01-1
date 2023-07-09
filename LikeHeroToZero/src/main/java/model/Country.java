package model;

import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class Country implements Serializable, Comparable<Country> {
	private String name;
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
