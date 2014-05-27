package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class ObN implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idU;
	private int idUONUti;
	private String libelle;
	
	private Conteneur conteneur;
	private Metadonnees metadonnees;
	private UTI_S utiS;
	private List<PartageON> partageONs;

	public ObN() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ObN(int idUONUti, String libelle) {
		super();
		this.idUONUti = idUONUti;
		this.libelle = libelle;
	}
	
	public static Comparator<ObN> obnComparator = new Comparator<ObN>() {

		@Override
		public int compare(ObN o1, ObN o2) {
			// TODO Auto-generated method stub
			String date1 = o1.getMetadonnees().getDate_fin_depot();
			String date2 = o2.getMetadonnees().getDate_fin_depot();
			return date2.compareTo(date1);
		}

		

		};



	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdU() {
		return idU;
	}

	public void setIdU(int idU) {
		this.idU = idU;
	}

	public int getIdUONUti() {
		return idUONUti;
	}

	public void setIdUONUti(int idUONUti) {
		this.idUONUti = idUONUti;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="idCont", referencedColumnName="idCont")
	public Conteneur getConteneur() {
		return conteneur;
	}

	public void setConteneur(Conteneur conteneur) {
		this.conteneur = conteneur;
	}

	@OneToOne(mappedBy="obN",cascade=CascadeType.ALL)
	public Metadonnees getMetadonnees() {
		return metadonnees;
	}

	public void setMetadonnees(Metadonnees metadonnees) {
		this.metadonnees = metadonnees;
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="idUtiS",referencedColumnName="idUti")
	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}
	
	@OneToMany(mappedBy="obN",cascade=CascadeType.MERGE)
	@JsonIgnore
	@XmlTransient
	public List<PartageON> getPartageONs() {
		return partageONs;
	}

	public void setPartageONs(List<PartageON> partageONs) {
		this.partageONs = partageONs;
	}

	public void linkONToConteneur(Conteneur conteneur)
	{
		setConteneur(conteneur);
	}
	
	public void linkONToUtiS(UTI_S utiS)
	{
		setUtiS(utiS);
	}
}