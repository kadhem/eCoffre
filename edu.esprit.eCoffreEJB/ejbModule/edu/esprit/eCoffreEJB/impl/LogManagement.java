package edu.esprit.eCoffreEJB.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.esprit.eCoffreEJB.Entities.Log;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.ILogLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

/**
 * Session Bean implementation class ILog
 */
@Stateless
@LocalBean
public class LogManagement implements ILogLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	
	@EJB
	private IUtiSLocal utiSManagement;
    /**
     * Default constructor. 
     */
    public LogManagement() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean addLog(Log log)
    {
    	try {
    		entityManager.persist(log);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    @Override
    public boolean deleteLog(int idLog)
    {
    	try {
    		entityManager.remove(findLogById(idLog));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }

    @Override
    public Log findLogById(int idLog)
    {
    	try {
    		return entityManager.find(Log.class, idLog);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public List<Log> findLogByON(int idU)
    {
    	String q = "select l from Log l where l.idU=:idU";
    	Query query = entityManager.createQuery(q);
    	query.setParameter("idU", idU);
    	return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public List<Log> findLogByUti(int idUti)
    {
    	String q = "select l from Log l where l.idUti=:idUti";
    	Query query = entityManager.createQuery(q);
    	query.setParameter("idUti", idUti);
    	return query.getResultList();
    }
    
    @Override
    public Map<String, Object> LireJournal(int idU, int idCCFN, int idUti,
			String[] date, int[] idOnUti, int idCont) {
    	
    	UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		if (utiS.getProfil().isLireJournal()) {
			System.out.println("access");
			List<Object[]> logs = getLog(idU, idCCFN, idUti, date, idOnUti, idCont);
			map.put("ccfn", idCCFN);
			map.put("uti", idUti);
			map.put("status", true);
			map.put("data", logs);
			DateFormat df = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			map.put("date", df.format(new Date()));
			return map;
		}
		else {
				System.out.println("denied");
				map.put("status", false);
				map.put("cause", "denied");
				return map;
		}
	}
    
    public List<Object[]> getLog(int idU, int idCCFN, int idUti,
    		String[] date, int[] idOnUti, int idCont)
    {
    	Boolean cont = false, idu = false, dat = false, idonuti = false;
    	
		String q = "select l.idCCFN, l.idCont, l.idUti, l.idU, l.function, l.date, l.params, l.status, m"+
				" from Log l, Metadonnees m" +
				" where l.idCCFN=:idCCFN and l.idUti=:idUti and LOCATE(m.obN.idU, l.idU)=1";

		Query query = null;
		
		if(idCont != 0 )
		{
			cont = true;
		}
		
		if (idU != 0) {
			idu = true;
			
		} else {
			if(date!=null)
			{
				System.out.println("date : "+date[0]+"*"+date[1]);
				if (date.length != 0) {
					dat = true;
				}
			}
			if(idOnUti!=null)
			{
				if (idOnUti.length != 0) {
					idonuti = true;
				}
			}
		}
		
//		if(cont){ q += " and l.idCont=:idCont"; }
//		if(idu) { q += " and l.idU LIKE :idU"; }
		if(dat) { q+=" and m.date_fin_depot>=:date1 and m.date_fin_depot<=:date2"; }
//		if(idonuti) { q+=" and m.idONUti>=:idOnUti1 and m.idONUti<=:idOnUti2"; }
		query = entityManager.createQuery(q);
//		if(cont){ query.setParameter("idCont", idCont); }
//		if(idu) { query = query.setParameter("idU", "%" + String.valueOf(idU) + "%"); }
		if(dat) { query.setParameter("date1", date[0]);	query.setParameter("date2", date[1]); }
//		if(idonuti) { query = query.setParameter("idOnUti1", idOnUti[0]);query.setParameter("idOnUti2", idOnUti[1]); }
		
		System.out.println("query : " + q);
		
		query.setParameter("idCCFN", idCCFN);
		query.setParameter("idUti", idUti);
		return query.getResultList();
    }
}
