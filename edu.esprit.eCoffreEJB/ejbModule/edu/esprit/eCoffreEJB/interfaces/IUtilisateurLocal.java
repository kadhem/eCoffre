package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;

@Local
@Path("/ws/uti")
public interface IUtilisateurLocal {

	public Utilisateur seConnecter(String userName, String passwd) throws NamingException;
	@GET
	@Path("/connexion")
	@Produces("application/json")
	public Utilisateur seConnecterWS(@QueryParam("username")String userName, @QueryParam("passwd")String passwd) throws NamingException;
	public UTI_S getUtiByUserName(String userName);
	public boolean seDesinscrire(UTI_S utiS);
	public boolean activerOuDesactiverUtilisateur(Utilisateur utilisateur);
	public boolean modifierUtilisateur(Utilisateur utilisateur);
}
