package edu.esprit.eCoffreEJB.interfaces;

import java.util.List;

import javax.ejb.Remote;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;

@Remote
public interface IConteneurRemote {

	public int ajouterConteneur(Conteneur conteneur, CCFN ccfn);
	public boolean supprimerConteneur(Conteneur conteneur);
	public boolean modifierCconteneur(Conteneur conteneur);
	public Conteneur getConteneurByIdConteneur(int idCont);
	public List<Conteneur> getActiveConteneurs();
	public List<Conteneur> getAllConteneurs();
	public Conteneur getDefaultConteneur();
	public Conteneur getConteneurByLibelle(String libelle);
	public boolean activateOrDesactivateConteneur(Conteneur conteneur);
}
