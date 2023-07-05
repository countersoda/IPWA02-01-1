package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Country;
import model.Emission;
import service.SqliteService;

@Named
@ApplicationScoped
public class EmissionController {
	private List<Emission> emissions;
	private List<Country> countries = new ArrayList<Country>();

	public EmissionController() {
		try {
			Statement statement = SqliteService.getConnection().createStatement();
			ResultSet result = statement.executeQuery("select distinct country_name, country_code from emission");
			while (result.next()) {
				String name = result.getString("country_name");
				String code = result.getString("country_code");
				countries.add(new Country(name, code));
			}
			Collections.sort(countries);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Country> getCountries() {
		return countries;
	}

}
