package service;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import model.Country;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CountryService {

	private static final JPAService jpaService = JPAService.getInstance();

	public List<Country> findAll() {
		return (List<Country>) jpaService.runInTransaction(em -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Country> cq = cb.createQuery(Country.class);
			Root<Country> root = cq.from(Country.class);
			cq.select(root).distinct(true);
			List<Country> countries = em.createQuery(cq).getResultList();
			return countries;
		});
	}
}