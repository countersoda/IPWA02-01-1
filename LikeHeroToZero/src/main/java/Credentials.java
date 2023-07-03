import java.io.Serializable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class Credentials implements Serializable{
	
	private static Credentials instance = new Credentials();

	private String username;
	private String password;

	public Credentials() {
	}
	
	public static Credentials getInstance() {
		return instance;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}