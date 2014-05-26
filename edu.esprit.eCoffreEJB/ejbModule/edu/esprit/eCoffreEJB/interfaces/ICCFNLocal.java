package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;

import edu.esprit.eCoffreEJB.Entities.CCFN;

@Local
public interface ICCFNLocal {

	public int ajouterCCFN(CCFN ccfn);
	
	public CCFN getCCFN();
}
