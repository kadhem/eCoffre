package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

import edu.esprit.eCoffreEJB.Entities.Metadonnees;
import edu.esprit.eCoffreEJB.Entities.ObN;

@Remote
public interface IMetadonneesRemote {

	public boolean ajouterMetadonnees(Metadonnees metadonnees);

	public Metadonnees getMetadonnesByON(ObN obN);
}
