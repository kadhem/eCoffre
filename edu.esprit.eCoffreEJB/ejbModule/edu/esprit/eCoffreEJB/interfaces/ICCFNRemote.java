package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

import edu.esprit.eCoffreEJB.Entities.CCFN;

@Remote
public interface ICCFNRemote {
	
	public int ajouterCCFN(CCFN ccfn);

	public CCFN getCCFNById(int idCCFN);

	public CCFN getCCFN();
}
