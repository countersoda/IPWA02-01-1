package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteService {
	private Connection connection;
	private static SqliteService instance = new SqliteService();

	public SqliteService() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite::memory:");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static Connection getConnection() {
		return instance.connection;
	}

}
