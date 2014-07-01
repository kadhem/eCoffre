package edu.esprit.eCoffreEJB.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurRemote;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;

/**
 * Session Bean implementation class ConteneurManagement
 */
@Stateless
@LocalBean
public class ConteneurManagement implements IConteneurLocal {

	@PersistenceContext(unitName="data")
	private EntityManager entityManager;
	@EJB
	private IONLocal onLocal;
	
	@Override
	public int ajouterConteneur(Conteneur conteneur, CCFN ccfn, UTI_F utiF) {
		// TODO Auto-generated method stub
		try {
			conteneur.linkConteneurToCCFN(ccfn);
			conteneur.linkConteneurToAdmin(utiF);
			Conteneur conteneur2 = getDefaultConteneur();
			if(conteneur2!=null)
			{
				conteneur2.setParDefaut(false);
				modifierCconteneur(conteneur2);
			}
			entityManager.persist(conteneur);
			entityManager.flush();
			return conteneur.getIdCont();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
		
	}

	@Override
	public boolean supprimerConteneur(Conteneur conteneur) {
		
		try{
			List<ObN> obNs=onLocal.getONBbyConteneur(conteneur.getIdCont());
			Conteneur aux = getConteneurByLibelle("Documents");
			for (ObN obN : obNs) {
				obN.linkONToConteneur(aux);
				entityManager.merge(obN);
			}
			entityManager.remove(getConteneurByIdConteneur(conteneur.getIdCont()));
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean modifierCconteneur(Conteneur conteneur) {
		try {
			entityManager.merge(conteneur);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}

	@Override
	public Conteneur getConteneurByIdConteneur(int idCont) {
		// TODO Auto-generated method stub
		return entityManager.find(Conteneur.class, idCont);
	}
	
	@Override
	public Conteneur getConteneurByLibelle(String libelle) {
		// TODO Auto-generated method stub
		return (Conteneur) entityManager.createQuery("select c from Conteneur c where c.libelle=:libelle")
				.setParameter("libelle", libelle).getSingleResult();
	}
	
	@Override
	public Conteneur getDefaultConteneur()
	{
		try {
			return (Conteneur) entityManager.createQuery("select c from Conteneur c where c.parDefaut=true").getSingleResult();
		} catch (Exception e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Conteneur> getActiveConteneurs()
	{
		return entityManager.createQuery("select c from Conteneur c where c.actif=true").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Conteneur> getAllConteneurs()
	{
		return entityManager.createQuery("select c from Conteneur c").getResultList();
	}
	
	@Override
	public boolean activateOrDesactivateConteneur(Conteneur conteneur)
	{
		try {
			if(!conteneur.isActif())
			{
				List<ObN> obNs = onLocal.getONBbyConteneur(conteneur.getIdCont());
				for (ObN obN : obNs) {
					obN.setConteneur(getDefaultConteneur());
					onLocal.modifierON(obN);
				}
			}
			entityManager.merge(conteneur);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
    /**
     * Default constructor. 
     */
    

    
}
