package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

@Remote
public interface ICoffreRemote {

	public void getCoffre_by_user();
	public void ajouterCoffre();
	public void supprimerCoffre();
	public void modifierCoffre();
}
