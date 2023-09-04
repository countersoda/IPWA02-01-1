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

import beans.CountryBean;
import beans.CredentialBean;
import beans.EmissionBean;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import model.Emission;
import service.EmissionService;
import types.Role;

@Named
@ViewScoped
public class EmissionController implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject CountryBean country;
	private @Inject CredentialBean user;
	private @Inject EmissionBean emission;
	private @Inject EmissionService emissionService;
	private List<Emission> emissions = new ArrayList<Emission>();
	private LineChartModel model = new LineChartModel();

	public EmissionController() {
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
		List<Emission> emissions = emissionService.findAllByCountry(country, false);
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
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) context.getRequest();
		String path = request.getRequestURI();
		boolean draft = false;
		if (path.contains("dashboard") && this.user != null && this.user.getRole() == Role.Publisher) {
			draft = true;
		}
		if (country.getCode() != null && country.getId() != null) {
			emissions.clear();
			emissions.addAll(emissionService.findAllByCountry(country, draft));
		}
	}

	public void add() {
		this.emission.setCountry(this.country);
		Emission newEmission = emissionService.add(this.emission);
		this.emissions.add(newEmission);
		Collections.sort(this.emissions);
	}

	public void add(Emission emission) {
		emission.setDraft(false);
		boolean updated = emissionService.update(emission);
		if (updated) {
			emissionService.removeById(emission.getId());
			this.emissions.remove(emission);
		}
		this.setEmissions();
	}

	public void update(RowEditEvent<Emission> event) {
		Emission emission = event.getObject().clone();
		Emission newEmission = emissionService.add(emission);
		emissions.add(newEmission);
		Collections.sort(this.emissions);
	}

	public void remove(Emission emission) {
		emissionService.removeById(emission.getId());
		emissions.remove(emission);
	}

}
