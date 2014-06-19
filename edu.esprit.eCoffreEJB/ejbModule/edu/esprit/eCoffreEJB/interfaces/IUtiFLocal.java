package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.UTI_F;

@Local
public interface IUtiFLocal {

	public int createAdminF(UTI_F utiF);
	public boolean deleteAdminF(int idUti);
	public boolean editAdminF(UTI_F utiF);
	public UTI_F getAdminFById(int idUti);
}
