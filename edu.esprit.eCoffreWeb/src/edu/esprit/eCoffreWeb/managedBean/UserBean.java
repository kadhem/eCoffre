package edu.esprit.eCoffreWeb.managedBean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.Entities.UTI_G;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;
import edu.esprit.eCoffreEJB.interfaces.IUtilisateurLocal;

@ManagedBean
@SessionScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Utilisateur user;

	private String userName;
	private String passwd;

	@EJB
	private IUtilisateurLocal userLocal;

	private String trouV;

	public UserBean() {

	}

	public String doAuthenticate() {
		String navigateTo = null;
		try {
			user = userLocal.seConnecter(userName, passwd);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			trouV = "Une erreur est survenue, veuillez réessayer plus tard!";
			navigateTo = null;
		}

		if (user != null) {
			System.out.println("user not null");
			if(user.isValide())
			{
				if (user instanceof UTI_S) {

					System.out.println("utis***** : " + user.getFirstName()+"id : "+user.getIdUti());
					navigateTo = "pages/utis/index?faces-redirect=true";
					System.out.println("*"+navigateTo);
				}
				else if (user instanceof UTI_G) {
					System.out.println("adminG");
				}
				else if (user instanceof UTI_F) {
					System.out.println("adminF");
					navigateTo = "pages/adminf/index?faces-redirect=true";
				}
			}
			else {
				trouV = "Votre compte a été desactivé par l'administrateur. Veuillez le contacter";
				navigateTo = null;
			}
		} else {
			trouV = "Paramètres de connexion incorrectes ou compte non validé!";
			navigateTo = null;
		}

		return navigateTo;
	}

	public String logOut() {
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
		//
		// return"../welcome";
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.clear();

		return "../index?faces-redirect=true";

	}

	public Utilisateur getUser() {
		return user;
	}

	public void setUser(Utilisateur user) {
		this.user = user;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getTrouV() {
		return trouV;
	}

	public void setTrouV(String trouV) {
		this.trouV = trouV;
	}

}
