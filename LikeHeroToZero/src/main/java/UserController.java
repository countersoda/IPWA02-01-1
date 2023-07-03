import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

@Named
@SessionScoped
public class UserController implements Serializable {

	private Connection connection;

	private @Inject Credentials credentials;

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
			statement.execute(
					"create table if not exists user(user_id integer primary key, username string, password string)");
			statement.executeUpdate("insert into user(username,password) values('test','test')");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	@PreDestroy
	private void destroy() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isLoggedin() {
		return credentials != null;
	}

	public void login() {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext context = facesContext.getExternalContext();
			
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from user");
			String username = rs.getString("username");
			String password = rs.getString("password");
			
			if (username.equals(credentials.getUsername()) && password.equals(credentials.getPassword())) {
				HttpSession session = (HttpSession) context.getSession(false);
				session.setAttribute("username", username);
				context.redirect("dashboard.xhtml");
				System.out.println("USER FOUND!");
			} else {
				context.redirect("login.xhtml");
				System.out.println("USER NOT FOUND!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
