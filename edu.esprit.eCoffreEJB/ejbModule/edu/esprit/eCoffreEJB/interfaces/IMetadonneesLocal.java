package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;

import edu.esprit.eCoffreEJB.Entities.Metadonnees;
import edu.esprit.eCoffreEJB.Entities.ObN;

@Local
public interface IMetadonneesLocal {

	boolean ajouterMetadonnees(Metadonnees metadonnees);

	Metadonnees getMetadonnesByON(ObN obN);

}
