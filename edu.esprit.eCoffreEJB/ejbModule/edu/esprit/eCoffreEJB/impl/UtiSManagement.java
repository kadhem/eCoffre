package edu.esprit.eCoffreEJB.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.esprit.eCoffreEJB.Entities.Profil;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.LdapCom;
import edu.esprit.eCoffreEJB.Technique.SFTPCom;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSRemote;

/**
 * Session Bean implementation class Uti_fManagement
 */
@Stateless
@LocalBean
public class UtiSManagement implements IUtiSRemote, IUtiSLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	
	@EJB
	private LdapCom ldapComLocal;
	@EJB
	private SFTPCom comLocal;
    /**
     * Default constructor. 
     */
    public UtiSManagement() {

    }
    
    @Override
	public int ajouterSimpleUti(UTI_S utiS, Profil profil) {
		try {
			utiS.linkProfilToUti(profil);
			profil.linkProfilToUti(utiS);
			entityManager.persist(utiS);
			entityManager.flush();
			ldapComLocal.createContext();
			ldapComLocal.addUser(utiS);
			comLocal.createContext();
			comLocal.createPersonalDir(utiS.getUserName());
			return utiS.getIdUti();
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
    
    @Override
	public int ajouterSimpleUtiSansConfirmation(UTI_S utiS) {
		try {
			entityManager.persist(utiS);
			entityManager.flush();
			return utiS.getIdUti();
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
    
    @Override
	public int confirmerSimpleUti(UTI_S utiS) {
		try {
			Profil profil = new Profil();
			profil.linkProfilToUti(utiS);
			utiS.linkProfilToUti(profil);
			entityManager.merge(utiS);
			if(ldapComLocal.createContext()!=null)
			{
				System.out.println("after create ldap context");
				if(ldapComLocal.addUser(utiS))
				{
					System.out.println("after create ldap user");
					if(comLocal.createContext())
					{
						comLocal.createPersonalDir(utiS.getUserName());
						return utiS.getIdUti();
					}
					else
					{
						entityManager.getTransaction().rollback();
					}
				}
				else
				{
					entityManager.getTransaction().rollback();
				}
			}
			else
			{
				entityManager.getTransaction().rollback();
			}
			return 0;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("catch confirmerSimpleUti");
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return 0;
		}
	}
	
	@Override
	public boolean supprimerSimpleUti(UTI_S utiS) {
		try {
			entityManager.remove(getUtiSByUserName(utiS.getUserName()));
			if(ldapComLocal.createContext()!=null)
			{
				ldapComLocal.deleteUser(utiS.getIdUti());
				if(comLocal.createContext())
				{
					comLocal.removeDir(utiS.getUserName());
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return false;
		}
		
	}
	
	@Override
	public boolean modifierSimpleUti(UTI_S utiS) {
		try {
			if(ldapComLocal.createContext()!=null)
			{
				ldapComLocal.editUser(utiS);
				entityManager.merge(utiS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return false;
		}
		
	}
	
	@Override
	public UTI_S getUtiSByUserName(String userName)
	{
		try {
			System.out.println("username from utismanagement : "+userName);
			return (UTI_S)entityManager.createQuery("select u from Utilisateur u where u.userName=:userName")
					.setParameter("userName", userName).getSingleResult();
		} catch (Exception e) {
			return null;
		}
		
	}

	@Override
	public UTI_S getUtiSByUserNameAndPasswd(String userName, String passwd)
	{
		String queryString="select us from UTI_S us where us.userName=:userName and us.password=:passwd";
		Query query=entityManager.createQuery(queryString);
		return (UTI_S)query.getSingleResult();
	}
	
	@Override
	public UTI_S getUtiSById(int idUti)
	{
		return entityManager.find(UTI_S.class, idUti);
//		System.out.println("id from ejb : "+idUti);
//		String queryString = "select us from UTI_S us where us.idUti=:idUti";
//		Query query = entityManager.createQuery(queryString);
//		query.setParameter("idUti", idUti);
//		return (UTI_S) query.getSingleResult();
	}
	
	@Override
	public long getUsedSpace(String userName)
	{
		UTI_S utiS=getUtiSByUserName(userName);
		comLocal.createContext();
		comLocal.changeDir(utiS.getUserName());
		return comLocal.getUsedSpace();
	}

	@Override
	public List<UTI_S> getAllUtiS() {
		// TODO Auto-generated method stub
		return entityManager.createQuery("select u from UTI_S u").getResultList();
	}
	
}
