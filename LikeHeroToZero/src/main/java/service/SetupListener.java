package service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
			try {
				String data = readFile("co2_emission.csv");
				for (String line : data.split("\n")) {
					String[] values = line.split(";");
					String name = values[0];
					String code = values[1];
					String year = values[4];
					String amount = values[5];
					System.out.println(name + "," + code + "," + year + "," + amount);
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
