package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Conteneur implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idCont;
	private String libelle;
	private boolean actif;
	private boolean parDefaut;
	
	private CCFN ccfn;
	private List<ObN> obNs;

	/*---------------------*/
	
	public Conteneur() {
		super();
	}
	
	public Conteneur(String libelle) {
		super();
		this.libelle = libelle;
	}
	
	public Conteneur(String libelle, boolean actif, boolean parDefaut) {
		super();
		this.libelle = libelle;
		this.actif = actif;
		this.parDefaut = parDefaut;
	}

	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdCont() {
		return idCont;
	}

	public void setIdCont(int idCont) {
		this.idCont = idCont;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="idCCFN", referencedColumnName="idCCFN")
	public CCFN getCcfn() {
		return ccfn;
	}

	public void setCcfn(CCFN ccfn) {
		this.ccfn = ccfn;
	}
	
	@OneToMany(mappedBy="conteneur")
	@XmlTransient
	@JsonIgnore
	public List<ObN> getObNs() {
		return obNs;
	}

	public void setObNs(List<ObN> obNs) {
		this.obNs = obNs;
	}

	public void linkConteneurToCCFN(CCFN ccfn)
	{
		setCcfn(ccfn);
	}

	@Override
	public String toString() {
		return getLibelle();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idCont;
		result = prime * result + ((libelle == null) ? 0 : libelle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conteneur other = (Conteneur) obj;
		if (idCont != other.idCont)
			return false;
		if (libelle == null) {
			if (other.libelle != null)
				return false;
		} else if (!libelle.equals(other.libelle))
			return false;
		return true;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public boolean isParDefaut() {
		return parDefaut;
	}

	public void setParDefaut(boolean parDefaut) {
		this.parDefaut = parDefaut;
	}
	
	
	
}