package listeners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Country;
import model.Credential;
import model.Emission;
import service.JPAService;
import types.Role;

@WebListener
public class SetupListener implements ServletContextListener {

	private static final JPAService jpaService = JPAService.getInstance();

	public String readFile(String path) {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("co2_emissions.csv").getFile());
		try {
			return FileUtils.readFileToString(file, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void contextInitialized(ServletContextEvent sce) {
		jpaService.runInTransaction(em -> {
			Credential user = new Credential();
			user.setUsername("test");
			user.setPassword("test");
			user.setRole(Role.Publisher);
			em.persist(user);

			Credential user2 = new Credential();
			user2.setUsername("test2");
			user2.setPassword("test");
			user2.setRole(Role.Researcher);
			em.persist(user2);

			String data = readFile("co2_emission.csv");
			if (data == null) {
				return null;
			}
			Map<String, Country> countryMap = new HashMap<String, Country>();
			for (String line : data.split("\n")) {
				String[] values = line.split(";");
				String name = values[0].trim();
				String code = values[1].trim();
				String year = values[4].trim();
				String amount = values[5].trim();
				if (name.equals("country_name") || amount.isBlank())
					continue;
				Country country;
				if (!countryMap.containsKey(code)) {
					country = new Country(name, code);
					countryMap.put(code, country);
				} else {
					country = countryMap.get(code);
				}
				em.persist(new Emission(Integer.parseInt(year), Float.parseFloat(amount), false, country));
			}
			return null;
		});

	}

	public void contextDestroyed(ServletContextEvent sce) {
		jpaService.shutdown();
	}
}
