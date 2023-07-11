package service;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import model.Country;

@FacesConverter(value = "countryConverter")
public class CountryConverter implements Converter<Country> {

	private static final JPAService jpaService = JPAService.getInstance();

	@Override
	public Country getAsObject(FacesContext ctx, UIComponent component, String countryCode) {
		Country country = jpaService.runInTransaction(em -> em.createQuery(
				String.format("select distinct country_name, country_code from emission where country_code='%s'",
						countryCode),
				Country.class).getSingleResult());
		return country;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Country country) {
		return country != null ? country.getCode() : "";
	}

}
