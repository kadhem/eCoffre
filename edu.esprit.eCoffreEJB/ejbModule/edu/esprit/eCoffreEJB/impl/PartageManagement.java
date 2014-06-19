package edu.esprit.eCoffreEJB.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Entities.PartageON;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.Email;
import edu.esprit.eCoffreEJB.Technique.SFTPCom;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageRemote;

/**
 * Session Bean implementation class IPartageManagement
 */
@Stateless
@LocalBean
public class PartageManagement implements IPartageRemote, IPartageLocal {

	@PersistenceContext(unitName="data")
	private EntityManager entityManager;
	@EJB
	private UtiSManagement utiSManagement;
	@EJB
	private ONManagement onManagement;
	@EJB
	private Email email;
    /**
     * Default constructor. 
     */
    public PartageManagement() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Partage getPartageById(int idPartage)
    {
    	return entityManager.find(Partage.class, idPartage);
    }
    
    @Override
    public List<Partage> getAllPartages()
    {
    	Query query2 = null;
    	String query="select p from Partage p";
    	query2 = entityManager.createQuery(query);
    	return query2.getResultList();
    }
    
    @Override
    public boolean deletePartage(int idPartage)
    {
    	try {
    		System.out.println("id delete avant :"+idPartage);
    		entityManager.remove(getPartageById(idPartage));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public boolean editPartage(Partage partage)
    {
    	try {
			entityManager.merge(partage);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public int addPartage(Partage partage,UTI_S utiS)
    {
    	try {
    		partage.linkPartageToUtiS(utiS);
    		entityManager.persist(partage);
    		entityManager.flush();
			return partage.getIdPartage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
			
		}
    }
    
    @Override
    public boolean shareObN(int idU, int idPartage, Date dateAjout)
    {
    	try {
    		System.out.println("idPartage : "+idPartage);
    		ObN obN = onManagement.getONByIdu(idU);
    		System.out.println("obn : "+obN.getLibelle());
    		System.out.println("11111111111111");
    		System.out.println("partage : ");
    		PartageON partageON= new PartageON(dateAjout, obN, getPartageById(idPartage) );
    		entityManager.persist(partageON);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
    }
    
    @Override
    public boolean deleteObnFromShare(int idU)
    {
    	try {
    		String q = "delete from PartageON p where p.obN.idU=:idU";
    		Query query = entityManager.createQuery(q);
    		query.setParameter(idU, "idU");
    		query.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public boolean deleteSharedObn(int idPartage, int idU)
    {
    	try {
    		String q = "delete from PartageON p where p.obN.idU="+idU+" and p.partage.idPartage="+idPartage;
    		Query query = entityManager.createQuery(q);
    		query.executeUpdate();
    		
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public boolean deletePartageObN(PartageON partageON)
    {
    	try {
    		entityManager.remove(partageON);
    		return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    @Override
    public boolean editPartageObN(PartageON partageON)
    {
    	try {
    		entityManager.merge(partageON);
    		return true;
		} catch (Exception e) {
			return false;
		}
    	
    }
    
    @Override
    public List<PartageON> getSharesIdObn(int idU)
    {
    	Query query2 = null;
    	String query="select p from PartageON p join p.partageONPK pk " +
    			"where pk.idU=:idU";
    	query2 = entityManager.createQuery(query);
    	query2.setParameter("idU", idU);
		return query2.getResultList();
    }
    
    @Override
    public List<ObN> getAllSharedObN()
    {
    	Query query2 = null;
    	String query="select o from ObN o join o.partageONs op " +
    			"where o.IDU=op.obN.IDU ";
    	query2 = entityManager.createQuery(query);
    	return query2.getResultList();
    }
    
    @Override
    public List<ObN> getSharedObNByIdPartage(int idPartage)
    {
    	Query query2 = null;
    	String query="select o from ObN o join o.partageONs op join op.partageONPK onpk " +
    			"where onpk.idPartage=:idPartage " +
    			"and o.idU=op.obN.idU ";
    	query2 = entityManager.createQuery(query);
    	query2.setParameter("idPartage", idPartage);
		return query2.getResultList();
    }
    
    @Override
    public List<ObN> getNotSharedObNByIdPartage(int idPartage)
    {
    	Query query2 = null;
    	String query="select o from ObN o where o.idU not in (select o1.idU from ObN o1 join o1.partageONs op join op.partageONPK onpk " +
    			"where onpk.idPartage=:idPartage)";
    	query2 = entityManager.createQuery(query);
    	query2.setParameter("idPartage", idPartage);
		return query2.getResultList();
    }
    
    @Override
    public List<ObN> getSharedObNByIdUti(int idUti)
    {
    	Query query2 = null;
    	String query="select o from ObN o join o.utiS u join o.partageONs op join op.partageONPK onpk " +
    			"where u.ID_UTI=:idUti" +
    			"and onpk.idU=o.idU " +
    			"and o.idU=op.obN.idU ";
    	query2 = entityManager.createQuery(query);
    	query2.setParameter("idUti", idUti);
		return query2.getResultList();
    }
    
    @Override
    public List<Partage> getPartagesByIdUti(int idUti)
    {
    	String query="select p from Partage p join p.utiS uti " +
    			"where uti.idUti=:idUti";
    	Query query2 = entityManager.createQuery(query);
    	query2.setParameter("idUti", idUti);
		return query2.getResultList();
    }
    
    @Override
    public boolean deleteInvite(int idInvite)
    {
    	try {
    		entityManager.remove(entityManager.find(Invite.class, idInvite));
    		return true;
		} catch (Exception e) {
			return false;
		}
    	
    }
    
    @Override
    public List<Invite> getInviTByIdPartage(int idPartage)
    {
    	String query="select i from Invite i join i.partage p " +
    			"where p.idPartage=:idPartage";
    	Query query2 = entityManager.createQuery(query);
    	query2.setParameter("idPartage", idPartage);
		return query2.getResultList();
    }

    @Override
    public boolean sendMailToInviT(UTI_S utiS, List<Invite> inviT, int idPartage)
    {
    	try {
			email.sendInvitationMail(utiS, inviT, idPartage);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
}
