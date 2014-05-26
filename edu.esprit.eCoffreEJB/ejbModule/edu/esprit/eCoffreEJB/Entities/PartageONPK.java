package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.PersistenceContext;

/**
 * ID class for entity: PartageON
 *
 */ 
@Embeddable
@PersistenceContext(name="data")
public class PartageONPK  implements Serializable {   
   
	         
	private int idU;         
	private int idPartage;
	private static final long serialVersionUID = 1L;

	public PartageONPK() {}

	public PartageONPK(int idU, int idPartage) {
		super();
		this.idU = idU;
		this.idPartage = idPartage;
	}

	public int getIdU() {
		return this.idU;
	}

	public void setIdU(int idU) {
		this.idU = idU;
	}
	

	public int getIdPartage() {
		return this.idPartage;
	}

	public void setIdPartage(int idPartage) {
		this.idPartage = idPartage;
	}
	
   
	/*
	 * @see java.lang.Object#equals(Object)
	 */	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof PartageONPK)) {
			return false;
		}
		PartageONPK other = (PartageONPK) o;
		return true
			&& getIdU() == other.getIdU()
			&& getIdPartage() == other.getIdPartage();
	}
	
	/*	 
	 * @see java.lang.Object#hashCode()
	 */	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getIdU();
		result = prime * result + getIdPartage();
		return result;
	}
   
   
}
