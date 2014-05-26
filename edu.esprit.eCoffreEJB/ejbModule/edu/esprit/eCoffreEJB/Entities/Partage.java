package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Entity implementation class for Entity: Partage
 *
 */
@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Partage implements Serializable {

	private int idPartage;
	private String nom;
	private String description;
	private Date datePartage;
	private Date dateExpiration;
	
	private List<PartageON> partageONs;
	private UTI_S utiS;
	private List<Invite> invites;
	
	private static final long serialVersionUID = 1L;

	public Partage(String nom, String descprition, Date datePartage, Date dateExpiration) {
		super();
		this.nom = nom;
		this.description=descprition;
		this.datePartage = datePartage;
		this.dateExpiration = dateExpiration;
	}

	public Partage() {
		super();
	}   
	
	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdPartage() {
		return idPartage;
	}
	
	public void setIdPartage(int idPartage) {
		this.idPartage = idPartage;
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDatePartage() {
		return datePartage;
	}

	public void setDatePartage(Date datePartage) {
		this.datePartage = datePartage;
	}

	public Date getDateExpiration() {
		return dateExpiration;
	}

	public void setDateExpiration(Date dateExpiration) {
		this.dateExpiration = dateExpiration;
	}

	@OneToMany(mappedBy="partage",fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JsonIgnore
	@XmlTransient
	public List<PartageON> getPartageONs() {
		return partageONs;
	}

	public void setPartageONs(List<PartageON> partageONs) {
		this.partageONs = partageONs;
	}

	@ManyToOne
	@JoinColumn(name = "idUtiS", referencedColumnName = "idUti")
	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}

	@OneToMany(mappedBy="partage",cascade=CascadeType.ALL)
	@JsonIgnore
	@XmlTransient
	public List<Invite> getInvites() {
		return invites;
	}

	public void setInvites(List<Invite> invites) {
		this.invites = invites;
	}
	
	public void linkPartageToUtiS(UTI_S utiS)
	{
		setUtiS(utiS);
	}
}
