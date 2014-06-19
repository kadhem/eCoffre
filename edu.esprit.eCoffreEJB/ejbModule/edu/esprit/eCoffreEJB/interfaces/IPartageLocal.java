package edu.esprit.eCoffreEJB.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Entities.PartageON;
import edu.esprit.eCoffreEJB.Entities.UTI_S;

@Local
public interface IPartageLocal {

	public Partage getPartageById(int idPartage);

	public boolean deletePartage(int idPartage);

	public boolean editPartage(Partage partage);

	public int addPartage(Partage partage, UTI_S utiS);

	public boolean shareObN(int idU, int idPartage, Date dateAjout);

	public List<ObN> getSharedObNByIdPartage(int idPartage);

	public List<ObN> getSharedObNByIdUti(int idUti);
	
	public List<Partage> getPartagesByIdUti(int idUti);
	
	public List<Invite> getInviTByIdPartage(int idPartage);
	
	public boolean deleteInvite(int idInvite);
	
	public List<ObN> getNotSharedObNByIdPartage(int idPartage);
	
	public boolean deleteSharedObn(int idPartage, int idU);
	
	public boolean sendMailToInviT(UTI_S utiS, List<Invite> inviT, int idPartage);

	public List<ObN> getAllSharedObN();
	
	public List<Partage> getAllPartages();
	
	public boolean deleteObnFromShare(int idU);
	
	public List<PartageON> getSharesIdObn(int idU);
	
	public boolean editPartageObN(PartageON partageON);
	
	public boolean deletePartageObN(PartageON partageON);
}
