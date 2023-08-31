package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import beans.CountryBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Country;
import service.CountryService;

@Named
@ViewScoped
public class CountryController implements Serializable {

	private static final long serialVersionUID = 1L;

	private @Inject CountryBean country;
	private @Inject CountryService countryService;
	private List<Country> countries = new ArrayList<Country>();

	public CountryController() {
	}

	@PostConstruct
	public void init() {
		countries = countryService.findAll();
		Collections.sort(countries);
		country.setCode(countries.get(0).getCode());
		country.setName(countries.get(0).getName());
		country.setId(countries.get(0).getId());
	}

	public List<Country> getCountries() {
		return countries;
	}

}
