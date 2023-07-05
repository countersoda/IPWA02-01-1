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

@WebListener
public class SetupListener implements ServletContextListener {

	public String readFile(String path) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("co2_emissions.csv").getFile());
		String data = FileUtils.readFileToString(file, "UTF-8");
		return data;
	}

	public void contextInitialized(ServletContextEvent sce) {
		Connection connection = SqliteService.getConnection();

		try {
			Statement statement = connection.createStatement();
			statement.execute("drop table if exists user");
			statement.execute(
					"create table if not exists user(user_id integer primary key, username string, password string)");
			statement.executeUpdate("insert into user(username,password) values('test','test')");

			statement.execute("drop table if exists emission");
			statement.execute(
					"create table if not exists emission(country_name string, country_code string, year integer, amount float)");
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
					statement.executeUpdate(String.format(
							"insert into emission(country_name,country_code,year,amount) values(\"%s\",\"%s\",%s,%s)",
							name, code, year, amount));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			SqliteService.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
