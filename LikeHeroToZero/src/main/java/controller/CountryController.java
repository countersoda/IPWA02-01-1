package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import model.Country;
import service.JPAService;

@Named
@ViewScoped
public class CountryController implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject Country country;
	private static final JPAService jpaService = JPAService.getInstance();
	private List<Country> countries = new ArrayList<Country>();

	public CountryController() {
		countries.addAll(jpaService.runInTransaction(em -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Country> cq = cb.createQuery(Country.class);
			Root<Country> root = cq.from(Country.class);
			cq.select(root).distinct(true);
			List<Country> countries = em.createQuery(cq).getResultList();
			return countries;
		}));
		Collections.sort(countries);
	}

	@PostConstruct
	public void init() {
		country.setCode(countries.get(0).getCode());
		country.setName(countries.get(0).getName());
		country.setId(countries.get(0).getId());
	}

	public List<Country> getCountries() {
		return countries;
	}

}
