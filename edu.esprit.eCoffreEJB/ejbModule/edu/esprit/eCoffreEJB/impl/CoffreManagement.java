package edu.esprit.eCoffreEJB.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import edu.esprit.eCoffreEJB.interfaces.ICoffreLocal;
import edu.esprit.eCoffreEJB.interfaces.ICoffreRemote;

/**
 * Session Bean implementation class ICoffreManagement
 */
@Stateless
@LocalBean
public class CoffreManagement implements ICoffreRemote, ICoffreLocal {

    /**
     * Default constructor. 
     */
    public CoffreManagement() {
        // TODO Auto-generated constructor stub
    }
    
    /**
	 * 
	 * @return 
	 */
	public void getCoffre_by_user() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return 
	 */
	public void ajouterCoffre() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return 
	 */
	public void supprimerCoffre() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return 
	 */
	public void modifierCoffre() {
		throw new UnsupportedOperationException();
	}

}
