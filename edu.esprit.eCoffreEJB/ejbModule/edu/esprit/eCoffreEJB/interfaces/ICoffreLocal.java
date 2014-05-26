package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;

@Local
public interface ICoffreLocal {

	public void getCoffre_by_user();
	public void ajouterCoffre();
	public void supprimerCoffre();
	public void modifierCoffre();
}
