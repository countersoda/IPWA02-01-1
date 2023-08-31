package service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import model.Country;
import model.Credential;
import model.Emission;

@ApplicationScoped
public class EmissionService {
	private static final JPAService jpaService = JPAService.getInstance();

	public List<Emission> findAllByCountry(Country _country) {
		return jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, _country.getId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Emission> cr = cb.createQuery(Emission.class);
			Root<Emission> root = cr.from(Emission.class);
			cr.select(root).where(cb.equal(root.get("country"), country)).orderBy(cb.asc(root.get("year")));
			List<Emission> result = em.createQuery(cr).getResultList();
			return result;
		});
	}

	public List<Integer> findYearsByCountryId(Integer countryId) {
		return jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, countryId);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Emission> cq = cb.createQuery(Emission.class);
			Root<Emission> root = cq.from(Emission.class);
			cq.select(root).where(cb.equal(root.get("country"), country)).orderBy(cb.asc(root.get("year")));
			List<Integer> result = em.createQuery(cq).getResultStream().map(e -> e.getYear())
					.collect(Collectors.toList());
			return result;
		});
	}

	public Emission add(Emission emission, Country country, Credential author) {
		return jpaService.runInTransaction(em -> {
			Country _country = em.find(Country.class, country.getId());
			Credential _author = em.find(Credential.class, author.getId());
			Emission newEmission = new Emission(emission.getYear(), emission.getAmount(), emission.isEditable(),
					_country, _author);
			em.persist(newEmission);
			return newEmission;
		});
	}

	public void update(Emission emission, Credential author) {
		jpaService.runInTransaction(em -> {
			Emission updateEmission = em.find(Emission.class, emission.getId());
			if (updateEmission.isEditable()) {
				updateEmission.setAmount(emission.getAmount());
			} else if (updateEmission.getOwner().equals(author.getUsername())) {
				updateEmission.setAmount(emission.getAmount());
				updateEmission.setEditable(emission.isEditable());
			}
			em.merge(updateEmission);
			return null;
		});
	}

	public void updateAllEditable(Integer countryId, Integer authorId, boolean editable) {
		jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, countryId);
			Credential author = em.find(Credential.class, authorId);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<Emission> cu = cb.createCriteriaUpdate(Emission.class);
			Root<Emission> root = cu.from(Emission.class);
			cu.set("editable", editable);
			cu.where(cb.and(cb.equal(root.get("country"), country), cb.equal(root.get("author"), author)));
			em.createQuery(cu).executeUpdate();
			return null;
		});
	}

	public void removeById(Integer emissionId) {
		jpaService.runInTransaction(em -> {
			Emission emission = em.find(Emission.class, emissionId);
			em.remove(emission);
			return null;
		});
	}

}