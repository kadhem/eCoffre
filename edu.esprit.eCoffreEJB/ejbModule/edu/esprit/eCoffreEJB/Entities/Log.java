package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity implementation class for Entity: Log
 * 
 */
@Entity
public class Log implements Serializable {

	private static final long serialVersionUID = 1L;
	private int idLog;
	private int idCCFN;
	private int idCont;
	private int idUti;
	private String idU;
	private String function;
	private String date;
	private String status;
	private String algo;
	private String hash;
	private String size;
	private String params;

	public Log() {
		super();
	} 

	public Log(int idCCFN, int idCont, int idUti, String idU, String function,
			String date, String status) {
		super();
		this.idCCFN = idCCFN;
		this.idCont = idCont;
		this.idUti = idUti;
		this.idU = idU;
		this.function = function;
		this.date = date;
		this.status = status;
	}

	public Log(int idCCFN, int idCont, int idUti, String idU, String function,
			String date, String status, String algo, String hash) {
		super();
		this.idCCFN = idCCFN;
		this.idCont = idCont;
		this.idUti = idUti;
		this.idU = idU;
		this.function = function;
		this.date = date;
		this.status = status;
		this.algo = algo;
		this.hash = hash;
	}



	public Log(int idCCFN, int idCont, int idUti, String idU, String function,
			String date, String status, String algo, String hash, String size) {
		super();
		this.idCCFN = idCCFN;
		this.idCont = idCont;
		this.idUti = idUti;
		this.idU = idU;
		this.function = function;
		this.date = date;
		this.status = status;
		this.algo = algo;
		this.hash = hash;
		this.size = size;
	}



	public Log(int idCCFN, int idCont, int idUti, String idU, String function,
			String date, String status, String algo, String hash, String size,
			String params) {
		super();
		this.idCCFN = idCCFN;
		this.idCont = idCont;
		this.idUti = idUti;
		this.idU = idU;
		this.function = function;
		this.date = date;
		this.status = status;
		this.algo = algo;
		this.hash = hash;
		this.size = size;
		this.params = params;
	}
	
	public static Comparator<Log> logComparator = new Comparator<Log>() {

		@Override
		public int compare(Log o1, Log o2) {
			// TODO Auto-generated method stub
			String date1 = o1.getDate();
			String date2 = o2.getDate();
			return date2.compareTo(date1);
		}

	};



	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdLog() {
		return this.idLog;
	}

	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}

	public int getIdCCFN() {
		return this.idCCFN;
	}

	public void setIdCCFN(int idCCFN) {
		this.idCCFN = idCCFN;
	}
	
	public int getIdCont() {
		return idCont;
	}

	public void setIdCont(int idCont) {
		this.idCont = idCont;
	}

	public int getIdUti() {
		return idUti;
	}

	public void setIdUti(int idUti) {
		this.idUti = idUti;
	}

	public String getIdU() {
		return idU;
	}

	public void setIdU(String idU) {
		this.idU = idU;
	}

	public String getFunction() {
		return this.function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAlgo() {
		return this.algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getParams() {
		return this.params;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
