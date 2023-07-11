package service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Country;
import model.Emission;

@WebListener
public class SetupListener implements ServletContextListener {

	private static final JPAService jpaService = JPAService.getInstance();

	public String readFile(String path) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("co2_emissions.csv").getFile());
		String data = FileUtils.readFileToString(file, "UTF-8");
		return data;
	}

	public void contextInitialized(ServletContextEvent sce) {
		jpaService.runInTransaction(em -> {
			try {
				String data = readFile("co2_emission.csv");
				for (String line : data.split("\n")) {
					String[] values = line.split(";");
					String name = values[0].trim();
					String code = values[1].trim();
					String year = values[4].trim();
					String amount = values[5].trim();
					if (name.equals("country_name") || amount.isBlank())
						continue;
					Country country = new Country(name, code);
					em.persist(country);
					em.persist(new Emission(Integer.parseInt(year), Float.parseFloat(amount), false, country));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});

	}

	public void contextDestroyed(ServletContextEvent sce) {
		jpaService.shutdown();
	}
}
