package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;

@Local
@Path("/uti")
public interface IUtilisateurLocal {

	@GET
	@Path("/connexion")
	@Produces("application/json")
	public Utilisateur seConnecter(@QueryParam("username")String userName, @QueryParam("passwd")String passwd) throws NamingException;
	public boolean sinscrire(String userName, String firstName, String lastName, String passwd, String tel);
	public UTI_S getUtiByUserName(String userName);
	public boolean seDesinscrire(UTI_S utiS);
	public boolean activerOuDesactiverUtilisateur(Utilisateur utilisateur);
	public boolean modifierUtilisateur(Utilisateur utilisateur);
}
