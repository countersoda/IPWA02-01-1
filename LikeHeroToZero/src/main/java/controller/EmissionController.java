package controller;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import model.Country;
import model.Credential;
import model.Emission;
import service.JPAService;

@Named
@ViewScoped
public class EmissionController implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject Country country;
	private @Inject Credential owner;
	private @Inject Emission emission;
	private Emission selectedEmission;
	private List<Emission> emissions = new ArrayList<Emission>();
	private LineChartModel model = new LineChartModel();
	private static final JPAService jpaService = JPAService.getInstance();

	public EmissionController() throws SQLException {
	}

	@PostConstruct
	public void init() {
		this.setEmissions();
		this.setEmissionModel();
	}

	public LineChartModel setEmissionModel() {
		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();
		LineChartOptions options = new LineChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Emission");
		options.setTitle(title);
		model.setOptions(options);

		List<String> years = new ArrayList<String>();
		List<Object> amounts = new ArrayList<Object>();
		model = new LineChartModel();
		if (country.getCode() == null || country.getId() == null) {
			return model;
		}
		List<Emission> emissions = jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, this.country.getId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Emission> cr = cb.createQuery(Emission.class);
			Root<Emission> root = cr.from(Emission.class);
			cr.select(root).where(cb.equal(root.get("country"), country));
			List<Emission> result = em.createQuery(cr).getResultList();
			return result;
		});

		years = emissions.stream().map(e -> String.valueOf(e.getYear())).collect(Collectors.toList());
		amounts = emissions.stream().map(e -> e.getAmount()).collect(Collectors.toList());
		dataSet.setData(amounts);
		dataSet.setFill(false);
		dataSet.setLabel("CO₂ in kt");
		dataSet.setBorderColor("rgb(75, 192, 192)");
		dataSet.setTension(0.1);
		data.addChartDataSet(dataSet);
		data.setLabels(years);
		model.setData(data);
		return model;
	}

	public LineChartModel getEmissionModel() {
		return model;
	}

	public List<Emission> getEmissions() {
		return emissions;
	}

	public void setEmissions() {
		if (country.getCode() != null && country.getId() != null) {
			emissions.clear();
			emissions.addAll(jpaService.runInTransaction(em -> {
				Country country = em.find(Country.class, this.country.getId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Emission> cq = cb.createQuery(Emission.class);
				Root<Emission> root = cq.from(Emission.class);
				cq.select(root).where(cb.equal(root.get("country"), country))
						.orderBy(cb.asc(root.get("emission_year")));
				List<Emission> result = em.createQuery(cq).getResultList();
				return result;
			}));
		}
	}

	public void add() {
		List<Integer> years = getYears();
		if (!years.contains(Integer.valueOf(emission.getYear()))) {
			jpaService.runInTransaction(em -> {
				Country country = em.find(Country.class, this.country.getId());
				Credential owner = em.find(Credential.class, this.owner.getId());
				Emission emission = new Emission(this.emission.getYear(), this.emission.getAmount(),
						this.emission.isEditable(), country, owner);
				em.persist(emission);
				emissions.add(emission);
				return null;
			});
			Collections.sort(emissions);
		}
	}

	public void update(RowEditEvent<Emission> event) {
		Emission eventEmission = event.getObject();
		jpaService.runInTransaction(em -> {
			Emission emission = em.find(Emission.class, eventEmission.getId());
			if (emission.isEditable()) {
				emission.setAmount(eventEmission.getAmount());
			} else if (emission.getOwner().equals(owner.getUsername())) {
				emission.setAmount(eventEmission.getAmount());
				emission.setEditable(eventEmission.isEditable());
			}
			em.persist(emission);
			return null;
		});
	}
	
	public void remove() {
		if (selectedEmission.getId() == null || !selectedEmission.getOwner().equals(this.owner.getUsername()))
			return;
		jpaService.runInTransaction(em -> {
			Emission emission = em.find(Emission.class, selectedEmission.getId());
			em.remove(emission);
			return null;
		});
		emissions.remove(selectedEmission);
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		if (country.getCode() == null) {
			return years;
		}
		years.addAll(jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, this.country.getId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Emission> cq = cb.createQuery(Emission.class);
			Root<Emission> root = cq.from(Emission.class);
			cq.select(root).where(cb.equal(root.get("country"), country)).orderBy(cb.asc(root.get("emission_year")));
			List<Integer> result = em.createQuery(cq).getResultStream().map(e -> e.getYear())
					.collect(Collectors.toList());
			return result;
		}));
		return years;
	}

	public void setEditable(boolean editable) {
		jpaService.runInTransaction(em -> {
			Country country = em.find(Country.class, this.country.getId());
			Credential owner = em.find(Credential.class, this.owner.getId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<Emission> cu = cb.createCriteriaUpdate(Emission.class);
			Root<Emission> root = cu.from(Emission.class);
			cu.set("emission_editable", editable);
			cu.where(cb.and(cb.equal(root.get("country"), country), cb.equal(root.get("owner"), owner)));
			em.createQuery(cu).executeUpdate();
			return null;
		});
		emissions.forEach(
				emission -> emission.setEditable(emission.getOwner().equals(this.owner.getUsername()) && editable
						|| (!emission.getOwner().equals(this.owner.getUsername()) && emission.isEditable())));
	}

	public Emission getSelectedEmission() {
		return selectedEmission;
	}

	public void setSelectedEmission(Emission selectedEmission) {
		this.selectedEmission = selectedEmission;
	}

}
