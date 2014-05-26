package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Evenement implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idEvenement;
	private String titre;
	private String description;
	private Date debutDate;
	private Date finDate;
	
	private UTI_S utiS;
	
	public Evenement() {
		super();
	}

	public Evenement(String titre, String description, Date debutDate,
			Date finDate) {
		super();
		this.titre = titre;
		this.description = description;
		this.debutDate = debutDate;
		this.finDate = finDate;
	}
	
	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdEvenement() {
		return idEvenement;
	}

	public void setIdEvenement(int idEvenement) {
		this.idEvenement = idEvenement;
	}
	
	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDebutDate() {
		return debutDate;
	}

	public void setDebutDate(Date debutDate) {
		this.debutDate = debutDate;
	}

	public Date getFinDate() {
		return finDate;
	}

	public void setFinDate(Date finDate) {
		this.finDate = finDate;
	}

	@ManyToOne
	@JoinColumn(name="idUti", referencedColumnName="idUti")
	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}

	public void linkEventToUtis(UTI_S utiS)
	{
		setUtiS(utiS);
	}
}