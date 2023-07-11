package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Country;
import service.JPAService;

@Named
@ViewScoped
public class CountryController implements Serializable {

	private @Inject Country country;
	private static final JPAService jpaService = JPAService.getInstance();
	private List<Country> countries = new ArrayList<Country>();

	public CountryController() {
		countries = jpaService.runInTransaction(em -> {
			List<Country> countries = em
					.createQuery("select distinct country_name, country_code from country", Country.class)
					.getResultList();
			return countries;
		});
		Collections.sort(countries);
	}

	@PostConstruct
	public void init() {
		country.setCode(countries.get(0).getCode());
		country.setName(countries.get(0).getName());
	}

	public List<Country> getCountries() {
		return countries;
	}

}
