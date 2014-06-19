package edu.esprit.eCoffreEJB.interfaces;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.Log;

@Local
public interface ILogLocal {

	boolean addLog(Log log);

	boolean deleteLog(int idLog);

	Log findLogById(int idLog);

	List<Log> findLogByON(int idU);

	List<Log> findLogByUti(int idUti);

	Map<String, Object> LireJournal(int idU, int idCCFN, int idUti,
			String[] date, int[] idOnUti, int idCont);

}
