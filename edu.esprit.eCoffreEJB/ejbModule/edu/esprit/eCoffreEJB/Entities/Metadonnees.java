package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Metadonnees implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idMeta;
	private int idUti;
	private int idCont;
	private int idUONUti;
	private String size;
	private String date_fin_depot;
	private String algo;
	private String hash;
	private String tags;
	
	private ObN obN;
	
	/*-------------------*/
	
	public Metadonnees() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	public Metadonnees(int idU, int idUti, int idCont, int idUONUti, String size,
			String date_fin_depot, String algorithme, String empreinte,
			String tags) {
		super();
		
		ObN obN = new ObN();obN.setIdU(idU);
		this.obN=obN;
		this.idUti = idUti;
		this.idCont = idCont;
		this.idUONUti = idUONUti;
		this.size = size;
		this.date_fin_depot = date_fin_depot;
		this.algo = algorithme;
		this.hash = empreinte;
		this.tags = tags;
	}



	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdMeta() {
		return idMeta;
	}

	public void setIdMeta(int idMeta) {
		this.idMeta = idMeta;
	}

	public int getIdUti() {
		return idUti;
	}

	public void setIdUti(int idUti) {
		this.idUti = idUti;
	}

	public int getIdCont() {
		return idCont;
	}

	public void setIdCont(int idCont) {
		this.idCont = idCont;
	}

	public int getIdUONUti() {
		return idUONUti;
	}

	public void setIdUONUti(int idUONUti) {
		this.idUONUti = idUONUti;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDate_fin_depot() {
		return date_fin_depot;
	}

	public void setDate_fin_depot(String date_fin_depot) {
		this.date_fin_depot = date_fin_depot;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@OneToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="idU",referencedColumnName="idU")
	@JsonIgnore
	@XmlTransient
	public ObN getObN() {
		return obN;
	}

	public void setObN(ObN obN) {
		this.obN = obN;
	}

	

}