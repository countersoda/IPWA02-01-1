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
import model.Country;
import model.Emission;
import service.JPAService;

@Named
@ViewScoped
public class EmissionController implements Serializable {

	private @Inject Country country;
	private @Inject Emission emission;
	private List<Emission> emissions = new ArrayList<Emission>();
	private Emission selectedEmission;
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
		List<String> years = new ArrayList<String>();
		List<Object> amounts = new ArrayList<Object>();
		model = new LineChartModel();
		if (country.getCode() == null) {
			return model;
		}
		List<Emission> emissions = jpaService.runInTransaction(em -> em
				.createQuery(String.format("select country_id, year, amount from emission where published=true"),
						Emission.class)
				.getResultList());
		years = emissions.stream().map(e -> String.valueOf(e.getYear())).collect(Collectors.toList());
		amounts = emissions.stream().map(e -> e.getAmount()).collect(Collectors.toList());
		dataSet.setData(amounts);
		dataSet.setFill(false);
		dataSet.setLabel("CO₂ in kt");
		dataSet.setBorderColor("rgb(75, 192, 192)");
		dataSet.setTension(0.1);
		data.addChartDataSet(dataSet);
		data.setLabels(years);
		LineChartOptions options = new LineChartOptions();
		Title title = new Title();
		title.setDisplay(true);
		title.setText("Emission");
		options.setTitle(title);
		model.setOptions(options);
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
		if (country.getCode() != null) {
			emissions.clear();
			emissions.addAll(jpaService.runInTransaction(em -> em.createQuery(
					String.format("select year, amount, published from emission where country_code=\"%s\" order by year",
							country.getCode()),
					Emission.class).getResultList()));
		}
	}

	public void add() {
		List<Integer> years = getYears();
		if (!years.contains(Integer.valueOf(emission.getYear()))) {
			jpaService.runInTransaction(em -> em
					.merge(new Emission(emission.getYear(), emission.getAmount(), emission.isPublished(), country)));
			emissions.add(emission);
			Collections.sort(emissions);
		}
	}

	public void remove() {
		jpaService.runInTransaction(em -> {
			em.remove(selectedEmission);
			return null;
		});
		emissions.remove(selectedEmission);
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		if (country.getCode() == null) {
			return years;
		}
		years.addAll(jpaService.runInTransaction(em -> em
				.createQuery(String.format("select year from emission where country_code=\"%s\" order by year",
						country.getCode()), Emission.class)
				.getResultStream().map(e -> e.getYear()).collect(Collectors.toList())));
		return years;
	}

	public void update(RowEditEvent<Emission> event) {
		Emission emission = event.getObject();
		jpaService.runInTransaction(em -> {
			em.persist(emission);
			return null;
		});
	}

	public void setPublished(boolean published) {
		jpaService.runInTransaction(
				em -> em.createQuery(String.format("update emission set published=%s where country_code=\"%s\"",
						published, country.getCode()), Emission.class).getResultList());
		emissions.forEach(emission -> emission.setPublished(published));
	}

	public Emission getSelectedEmission() {
		return selectedEmission;
	}

	public void setSelectedEmission(Emission selectedEmission) {
		this.selectedEmission = selectedEmission;
	}

}
