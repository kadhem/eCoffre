package edu.esprit.eCoffreEJB.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.ICCFNRemote;

/**
 * Session Bean implementation class CCFNManagement
 */
@Stateless
@LocalBean
public class CCFNManagement implements ICCFNRemote, ICCFNLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	
    /**
     * Default constructor. 
     */
    public CCFNManagement() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public int ajouterCCFN(CCFN ccfn) {
		// TODO Auto-generated method stub
		try {
			entityManager.remove(ccfn);
			entityManager.persist(ccfn);
			entityManager.flush();
			System.out.println("id ccfn : "+ccfn.getIdCCFN());
			return ccfn.getIdCCFN();
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
	
	@Override
	public CCFN getCCFNById(int idCCFN)
	{
		return entityManager.find(CCFN.class, idCCFN);
	}
	
	@Override
	public CCFN getCCFN()
	{
		return (CCFN)entityManager.createQuery("select c from CCFN c").getSingleResult();
	}

    
}
