package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

import edu.esprit.eCoffreEJB.Entities.UTI_F;

@Remote
public interface IUtiFRemote {

	public int createAdminF(UTI_F utiF);
	public boolean deleteAdminF(int idUti);
	public boolean editAdminF(UTI_F utiF);
	public UTI_F getAdminFById(int idUti);
}
