package controller;

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
import model.Credentials;
import service.SqliteService;

@Named
@SessionScoped
public class CredentialsController implements Serializable {

	private @Inject Credentials credentials;

	public CredentialsController() {
	}

	public boolean isLoggedIn() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);
		String username = (session != null) ? (String) session.getAttribute("username") : null;
		return username != null;
	}

	public void logout() throws IOException {
		credentials = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);
		session.invalidate();
		context.redirect("index.xhtml");
	}

	public void login() throws SQLException, IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();

		Statement statement = SqliteService.getConnection().createStatement();
		ResultSet rs = statement.executeQuery("select * from user");
		String username = rs.getString("username");
		String password = rs.getString("password");

		if (username.equals(credentials.getUsername()) && password.equals(credentials.getPassword())) {
			HttpSession session = (HttpSession) context.getSession(false);
			session.setMaxInactiveInterval(300);
			session.setAttribute("username", username);
			context.redirect("dashboard.xhtml");
		} else {
			context.redirect("login.xhtml");
		}
	}
}
