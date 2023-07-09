package controller;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.primefaces.event.SelectEvent;

import jakarta.enterprise.context.ApplicationScoped;
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

	public List<Emission> getEmissions() {
		List<Emission> emissions = new ArrayList<Emission>();
		if (country.getCode() == null) {
			return emissions;
		}

		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(String.format(
					"select country_code, year, amount from emission where country_code=\"%s\"", country.getCode()));
			while (result.next()) {
				int year = result.getInt("year");
				float amount = result.getFloat("amount");
				emissions.add(new Emission(year, amount));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emissions;
	}

	public void update() {
		System.out.println(emission.getYear() + ", " + emission.getAmount());
		System.out.println(country.getCode() + ", " + country.getName());
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			statement.execute(String.format("update emission set amount=%s where country_code=\"%s\" and year=%s",
					emission.getAmount(), country.getCode(), emission.getYear()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void add() {
		System.out.println(emission.getYear() + ", " + emission.getAmount());
		System.out.println(country.getCode() + ", " + country.getName());
	}

	public List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>();
		if (country.getCode() == null) {
			return years;
		}

		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement.executeQuery(String.format(
					"select country_code, year, amount from emission where country_code=\"%s\"", country.getCode()));
			while (result.next()) {
				int year = result.getInt("year");
				years.add(year);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return years;
	}

	public void setAmount() {
		try (Statement statement = SqliteService.getConnection().createStatement()) {
			ResultSet result = statement
					.executeQuery(String.format("select amount from emission where country_code=\"%s\" and year=%d",
							country.getCode(), emission.getYear()));
			long amount = result.getInt("amount");
			emission.setAmount(amount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
