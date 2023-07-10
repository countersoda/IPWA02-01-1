package controller;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Country;
import model.Emission;
import service.SqliteService;

@Named
@ViewScoped
public class EmissionController implements Serializable {

	private @Inject Country country;
	private @Inject Emission emission;

	public EmissionController() throws SQLException {
	}

	public LineChartModel getEmissionModel() {
		LineChartModel model = new LineChartModel();
		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();
		List<String> years = new ArrayList<String>();
		List<Object> amounts = new ArrayList<Object>();
		if (country.getCode() == null) {
			return model;
		}

		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(String.format(
					"select country_code, year, amount from emission where country_code=\"%s\" and published=true",
					country.getCode()));
			while (result.next()) {
				int year = result.getInt("year");
				float amount = result.getFloat("amount");
				years.add(String.valueOf(year));
				amounts.add(amount);
			}
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return model;
	}

	public List<Emission> getEmissions() {
		List<Emission> emissions = new ArrayList<Emission>();
		if (country.getCode() == null) {
			return emissions;
		}

		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(
					String.format("select year, amount,published from emission where country_code=\"%s\" order by year",
							country.getCode()));
			while (result.next()) {
				int year = result.getInt("year");
				float amount = result.getFloat("amount");
				boolean published = result.getBoolean("published");
				emissions.add(new Emission(year, amount, published));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return emissions;
	}

	public void update() {
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			statement.execute(
					String.format("update emission set amount=%s, published=%s where country_code=\"%s\" and year=%s",
							emission.getAmount(), emission.isPublished(), country.getCode(), emission.getYear()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(Emission emission) {
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			statement.execute(
					String.format("update emission set amount=%s, published=%s where country_code=\"%s\" and year=%s",
							emission.getAmount(), !emission.isPublished(), country.getCode(), emission.getYear()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void add() {
		List<Integer> years = getYears();
		if (!years.contains(Integer.valueOf(emission.getYear()))) {
			try (Statement statement = SqliteService.getConnection().createStatement()) {
				statement.executeUpdate(String.format(
						"insert into emission(country_name,country_code,year,amount,published) values(\"%s\",\"%s\",%s,%s,%s)",
						country.getName(), country.getCode(), emission.getYear(), emission.getAmount(),
						emission.isPublished()));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void remove() {
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			statement.executeUpdate(String.format("delete from emission where country_code=\"%s\" and year=%s",
					country.getCode(), emission.getYear()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		if (country.getCode() == null) {
			return years;
		}

		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(String
					.format("select year from emission where country_code=\"%s\" order by year", country.getCode()));
			while (result.next()) {
				int year = result.getInt("year");
				years.add(year);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return years;
	}

	public void setEmission() {
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(
					String.format("select amount,published from emission where country_code=\"%s\" and year=%d",
							country.getCode(), emission.getYear()));
			float amount = result.getFloat("amount");
			boolean published = result.getBoolean("published");
			emission.setAmount(amount);
			emission.setPublished(published);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
