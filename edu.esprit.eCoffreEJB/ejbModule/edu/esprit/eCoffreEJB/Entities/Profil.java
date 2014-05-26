package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Profil implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idProfil;
	private boolean deposer;
	private boolean lire;
	private boolean lireMetaDonnees;
	private boolean controler;
	private boolean detruire;
	private boolean lireJournal;
	private boolean lister;
	private boolean compter;

	private UTI_S utiS;
	
	public Profil() {
		super();
		this.deposer = true;
		this.lire = true;
		this.lireMetaDonnees = true;
		this.controler = true;
		this.detruire = true;
		this.lireJournal = true;
		this.lister = true;
		this.compter = true;
	}

	public Profil(boolean deposer, boolean lire, boolean lireMetaDonnees,
			boolean controler, boolean detruire, boolean lireJournal,
			boolean lister, boolean compter, UTI_S utiS) {
		super();
		this.deposer = deposer;
		this.lire = lire;
		this.lireMetaDonnees = lireMetaDonnees;
		this.controler = controler;
		this.detruire = detruire;
		this.lireJournal = lireJournal;
		this.lister = lister;
		this.compter = compter;
		this.utiS = utiS;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdProfil() {
		return idProfil;
	}

	public void setIdProfil(int idProfil) {
		this.idProfil = idProfil;
	}

	public boolean isDeposer() {
		return this.deposer;
	}

	public void setDeposer(boolean deposer) {
		this.deposer = deposer;
	}

	public boolean isLire() {
		return this.lire;
	}

	public void setLire(boolean lire) {
		this.lire = lire;
	}

	public boolean isLireMetaDonnees() {
		return this.lireMetaDonnees;
	}

	public void setLireMetaDonnees(boolean lireMetaDonnees) {
		this.lireMetaDonnees = lireMetaDonnees;
	}

	public boolean isControler() {
		return this.controler;
	}

	public void setControler(boolean controler) {
		this.controler = controler;
	}

	public boolean isDetruire() {
		return detruire;
	}

	public void setDetruire(boolean detruire) {
		this.detruire = detruire;
	}

	public boolean isLireJournal() {
		return this.lireJournal;
	}

	public void setLireJournal(boolean lireJournal) {
		this.lireJournal = lireJournal;
	}

	public boolean isLister() {
		return this.lister;
	}

	public void setLister(boolean lister) {
		this.lister = lister;
	}

	public boolean isCompter() {
		return this.compter;
	}

	public void setCompter(boolean compter) {
		this.compter = compter;
	}

	@OneToOne
	@JoinColumn(name="idUti")
	@XmlTransient
	@JsonIgnore
	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}

	public void linkProfilToUti(UTI_S utiS)
	{
		setUtiS(utiS);
		System.out.println(utiS.getFirstName());
	}

}