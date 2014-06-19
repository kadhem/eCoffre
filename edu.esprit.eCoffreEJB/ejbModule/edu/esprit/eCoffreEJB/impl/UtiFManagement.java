package edu.esprit.eCoffreEJB.impl;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.Technique.LdapCom;
import edu.esprit.eCoffreEJB.interfaces.IUtiFLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiFRemote;

/**
 * Session Bean implementation class Uti_fManagement
 */
@Stateless
@LocalBean
public class UtiFManagement implements IUtiFRemote, IUtiFLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	
	@EJB
	private LdapCom ldapComLocal;
	
    /**
     * Default constructor. 
     */
    public UtiFManagement() {
    	
    }
    
    /**
	 * 
	 * @return 
	 */
    @Override
	public int createAdminF(UTI_F utiF) {
		try {
			System.out.println("createAdminF");
			entityManager.persist(utiF);
			System.out.println("after adding");
			entityManager.flush();
			
			if(ldapComLocal.createContext()!=null)
			{
				System.out.println("after create ldap context");
				if(ldapComLocal.addUser(utiF))
				{
					
				}
			}
			return utiF.getIdUti();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 
	 * @return 
	 */
    @Override
	public boolean deleteAdminF(int idUti) {
		try {
			entityManager.merge(getAdminFById(idUti));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @return 
	 */
    @Override
	public boolean editAdminF(UTI_F utiF) {
		try {
			entityManager.merge(utiF);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
    @Override
	public UTI_F getAdminFById(int idUti)
	{
		try {
			return entityManager.find(UTI_F.class, idUti);
		} catch (Exception e) {
			return null;
		}
		
	}

}
