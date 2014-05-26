package edu.esprit.eCoffreEJB.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.IEvenementLocal;
import edu.esprit.eCoffreEJB.interfaces.IEvenementRemote;

/**
 * Session Bean implementation class EvenementManagement
 */
@Stateless
@LocalBean
public class EvenementManagement implements IEvenementLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
    /**
     * Default constructor. 
     */
    public EvenementManagement() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean addEvent(Evenement evenement, UTI_S utiS)
    {
    	try {
    		evenement.linkEventToUtis(utiS);
    		entityManager.persist(evenement);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
			return false;
		}
    }
    
    @Override
    public boolean editEvent(Evenement evenement)
    {
    	try {
			entityManager.merge(evenement);
			return true;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			return false;
		}
    }
    
    @Override
    public boolean deleteEvent(int idEvenement)
    {
    	try {
			entityManager.remove(getEventById(idEvenement));
			return true;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			return false;
		}
    }
    
    @Override
    public Evenement getEventById(int idEvenement)
    {
    	return entityManager.find(Evenement.class, idEvenement);
    }
    
    @Override
    public List<Evenement> getEventsByIdUtis(int idUti)
    {
    	String q = "select e from Evenement e join e.utiS u where u.idUti=:idUti";
    	Query query = entityManager.createQuery(q);
    	query.setParameter("idUti", idUti);
    	return query.getResultList();
    }
    
    @Override
    public List<Evenement> getEvents()
    {
    	String q = "select e from Evenement e";
    	Query query = entityManager.createQuery(q);
    	return query.getResultList();
    }
}
