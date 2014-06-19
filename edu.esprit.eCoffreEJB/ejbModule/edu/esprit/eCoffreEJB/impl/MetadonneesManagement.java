package edu.esprit.eCoffreEJB.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.Metadonnees;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesLocal;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesRemote;

/**
 * Session Bean implementation class MetadonneesManagement
 */
@Stateless
@LocalBean
public class MetadonneesManagement implements IMetadonneesRemote, IMetadonneesLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	
    /**
     * Default constructor. 
     */
    public MetadonneesManagement() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean ajouterMetadonnees(Metadonnees metadonnees)
    {
    	try {
			entityManager.persist(metadonnees);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public Metadonnees getMetadonnesByON(ObN obN)
    {
    	return (Metadonnees)entityManager.createQuery(
				"select m from Metadonnees m where m.obN.idU=:idu").setParameter("idu", obN.getIdU())
				.getSingleResult();
    }

}
