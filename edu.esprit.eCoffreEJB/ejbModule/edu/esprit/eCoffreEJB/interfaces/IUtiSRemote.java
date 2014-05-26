package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

import edu.esprit.eCoffreEJB.Entities.Profil;
import edu.esprit.eCoffreEJB.Entities.UTI_S;

@Remote
public interface IUtiSRemote {
	
	public int ajouterSimpleUti(UTI_S utiS, Profil profil);
	public boolean supprimerSimpleUti(UTI_S utiS);
	public boolean modifierSimpleUti(UTI_S utiS);
	public UTI_S getUtiSByUserName(String userName);
	public UTI_S getUtiSByUserNameAndPasswd(String userName, String passwd);
	public UTI_S getUtiSById(int idUti);
	public long getUsedSpace(String userName);
	public int ajouterSimpleUtiSansConfirmation(UTI_S utiS);
	public int confirmerSimpleUti(UTI_S utiS);
}
