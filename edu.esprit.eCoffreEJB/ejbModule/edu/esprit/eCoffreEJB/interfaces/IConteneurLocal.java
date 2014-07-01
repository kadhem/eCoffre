package edu.esprit.eCoffreEJB.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.UTI_F;

@Local
@Path("/ws/cont")
public interface IConteneurLocal {

	public int ajouterConteneur(Conteneur conteneur, CCFN ccfn, UTI_F utiF);
	public boolean supprimerConteneur(Conteneur conteneur);
	public boolean modifierCconteneur(Conteneur conteneur);
	public Conteneur getConteneurByIdConteneur(int idCont);
	public List<Conteneur> getActiveConteneurs();
	
	@GET
	@Produces("application/json")
	@Path("/all")
	public List<Conteneur> getAllConteneurs();
	public Conteneur getDefaultConteneur();
	public Conteneur getConteneurByLibelle(String libelle);
	public boolean activateOrDesactivateConteneur(Conteneur conteneur);
}
