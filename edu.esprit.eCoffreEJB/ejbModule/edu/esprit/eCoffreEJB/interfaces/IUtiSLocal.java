package edu.esprit.eCoffreEJB.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.esprit.eCoffreEJB.Entities.Profil;
import edu.esprit.eCoffreEJB.Entities.UTI_S;

@Local
@Path("/utis")
public interface IUtiSLocal {
	
	public int ajouterSimpleUti(UTI_S utiS, Profil profil);
	public boolean supprimerSimpleUti(UTI_S utiS);
	public boolean modifierSimpleUti(UTI_S utiS);
	public UTI_S getUtiSByUserName(String userName);
	public UTI_S getUtiSByUserNameAndPasswd(String userName, String passwd);
	public UTI_S getUtiSById(int idUti);
	public List<UTI_S> getAllUtiS();
	
	@GET
	@Produces
	@Path("/space")
	public long getUsedSpace(@QueryParam("uti") String userName);
	public int ajouterSimpleUtiSansConfirmation(UTI_S utiS);
	public int confirmerSimpleUti(UTI_S utiS);
}
