package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import model.Country;

@FacesConverter(forClass = Country.class, value = "countryConverter")
public class CountryConverter implements Converter<Country> {

	@Override
	public Country getAsObject(FacesContext ctx, UIComponent component, String countryCode) {
		Statement statement;
		try {
			statement = SqliteService.getConnection().createStatement();
			ResultSet result = statement.executeQuery(String.format(
					"select distinct country_name, country_code from emission where country_code='%s'", countryCode));
			String name = result.getString("country_name");
			String code = result.getString("country_code");
			return new Country(name, code);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Country country) {
		return country != null ? country.getCode() : "";
	}

}
