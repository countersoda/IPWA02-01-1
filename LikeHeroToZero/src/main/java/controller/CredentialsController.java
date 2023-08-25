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
import model.Credential;
import service.JPAService;

@Named
@SessionScoped
public class CredentialsController implements Serializable {

	private static final long serialVersionUID = 1L;
	private @Inject Credential credential;
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
		credential = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);
		session.invalidate();
		context.redirect("index.xhtml");
	}

	public void login() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext context = facesContext.getExternalContext();
		Credential user = jpaService.runInTransaction(em -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Credential> cr = cb.createQuery(Credential.class);
			Root<Credential> root = cr.from(Credential.class);
			cr.select(root);
			cr.where(cb.equal(root.get("username"), credential.getUsername()));
			return em.createQuery(cr).getSingleResult();
		});
		if (user.getUsername() != null && user.getPassword() != null
				&& user.getUsername().equals(credential.getUsername())
				&& user.getPassword().equals(credential.getPassword())) {
			HttpSession session = (HttpSession) context.getSession(false);
			session.setMaxInactiveInterval(300);
			session.setAttribute("username", user.getUsername());
			credential.setId(user.getId());
			context.redirect("dashboard.xhtml");
		} else {
			context.redirect("login.xhtml");
		}

	}
}
