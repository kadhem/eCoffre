package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;
import javax.naming.NamingException;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;

@Remote
public interface IUtilisateurRemote {

	public Utilisateur seConnecter(String userName, String passwd) throws NamingException;
	public boolean sinscrire(String userName, String firstName, String lastName, String passwd, String tel);
	public UTI_S getUtiByUserName(String userName);
	public boolean seDesinscrire(UTI_S utiS);
	public boolean activerOuDesactiverUtilisateur(Utilisateur utilisateur);
	public boolean modifierUtilisateur(Utilisateur utilisateur);
}
