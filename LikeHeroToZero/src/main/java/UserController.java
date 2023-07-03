import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class UserController implements Serializable {

	
	public Credentials getCredentials() {
		return Credentials.getInstance();
	}

	private Connection connection;

	public UserController() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite::memory:");
			Statement statement = connection.createStatement();
			statement.execute("drop table if exists user");
			statement.execute("create table user(user_id integer, username string, password string)");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public void login() {
		Credentials credentials = getCredentials();
		System.out.println("Username = " + credentials.getUsername());
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery(String.format("select * from user where username='%s'", credentials.getUsername()));
			String username = rs.getString("username");
			if (username != credentials.getUsername() || username == null) {
				System.out.println("USER NOT FOUND!");
			} else {
				System.out.println("USER FOUND!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
