package controller;

import java.io.IOException;
import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpSession;
import model.Country;
import model.Credentials;
import service.JPAService;

@Named
@SessionScoped
public class CredentialsController implements Serializable {

	private @Inject Credentials credentials;
	private static final JPAService jpaService = JPAService.getInstance();

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

	public void login() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		Credentials user = jpaService.runInTransaction(em -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Credentials> cr = cb.createQuery(Credentials.class);
			Root<Credentials> root = cr.from(Credentials.class);
			cr.select(root);
			cr.where(cb.equal(root.get("username"), credentials.getUsername()));
			return em.createQuery(cr).getSingleResult();
		});
		if (user.getUsername() != null && user.getPassword() != null
				&& user.getUsername().equals(credentials.getUsername())
				&& user.getPassword().equals(credentials.getPassword())) {
			HttpSession session = (HttpSession) context.getSession(false);
			session.setMaxInactiveInterval(300);
			session.setAttribute("username", user.getUsername());
			credentials.setId(user.getId());
			context.redirect("dashboard.xhtml");
		} else {
			context.redirect("login.xhtml");
		}

	}
}
