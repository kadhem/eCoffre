package edu.esprit.eCoffreWeb.managedBean;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
			user = userLocal.seConnecter(userName, encryptPassword(passwd));
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

	public String doLogOut() {
		
		System.out.println("logout");
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.clear();

		return "/accueil?faces-redirect=true";

	}
	
	public String encryptPassword(String passwd)
    {
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(passwd.getBytes());
			 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	String result = "{SHA512}" + hexString;
	    	System.out.println("userpassword userBean:" + result);
	    	return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
