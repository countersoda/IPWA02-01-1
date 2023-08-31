package controller;

import java.io.Serializable;
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
import model.Credential;
import model.Emission;
import service.EmissionService;

@Named
@ViewScoped
public class EmissionController implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject Country country;
	private @Inject Credential author;
	private @Inject Emission emission;
	private @Inject EmissionService emissionService;
	private Emission selectedEmission;
	private List<Emission> emissions = new ArrayList<Emission>();
	private LineChartModel model = new LineChartModel();

	public EmissionController() {
	}

	@PostConstruct
	public void init() {
		this.setEmissions();
		this.setEmissionModel();
	}

	public Emission getSelectedEmission() {
		return selectedEmission;
	}

	public void setSelectedEmission(Emission selectedEmission) {
		this.selectedEmission = selectedEmission;
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
		List<Emission> emissions = emissionService.findAllByCountry(country);
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
			emissions.addAll(emissionService.findAllByCountry(country));
		}
	}

	public void add() {
		List<Integer> years = getYears();
		if (!years.contains(Integer.valueOf(emission.getYear()))) {
			Emission newEmission = emissionService.add(emission, country, author);
			emissions.add(newEmission);
			Collections.sort(emissions);
		}
	}

	public void update(RowEditEvent<Emission> event) {
		Emission eventEmission = event.getObject();
		emissionService.update(eventEmission, author);
	}

	public void remove() {
		if (selectedEmission.getId() == null || !selectedEmission.getOwner().equals(this.author.getUsername()))
			return;
		emissionService.removeById(selectedEmission.getId());
		emissions.remove(selectedEmission);
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		if (country.getCode() == null) {
			return years;
		}
		years.addAll(emissionService.findYearsByCountryId(country.getId()));
		return years;
	}

	public void updateAllEditable(boolean editable) {
		emissionService.updateAllEditable(this.country.getId(), this.author.getId(), editable);
		emissions.forEach(
				emission -> emission.setEditable(emission.getOwner().equals(this.author.getUsername()) && editable
						|| (!emission.getOwner().equals(this.author.getUsername()) && emission.isEditable())));
	}

}
