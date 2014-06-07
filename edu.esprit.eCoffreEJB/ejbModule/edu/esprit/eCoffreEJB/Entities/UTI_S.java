package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class UTI_S extends Utilisateur implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long quota=104857600;
	private Date dateNaissance;
	private String tel;
	private String adresse;
	
	private List<Evenement> evenements;
	private Profil profil;
	private List<ObN> obns;
	private List<Partage> partages;
	

	public UTI_S() {
		super();
	}

	public UTI_S(int idUti, String lastName, String firstName, String userName,
			String password, boolean valide, String tel, String adresse) {
		super(idUti, lastName, firstName, userName, password, valide);
		this.tel = tel;
		this.adresse = adresse;
	}

	public UTI_S(String userName, String firstName, String lastName, String passwd, String tel, String adresse, Date dateNaissance) {
		super(userName, firstName, lastName, passwd);
		this.setTel(tel);
		this.dateNaissance=dateNaissance;
		this.adresse=adresse;
		setValide(false);
	}
	
	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	@OneToOne(mappedBy="utiS",cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public Profil getProfil() {
		return profil;
	}

	public void setProfil(Profil profil) {
		this.profil = profil;
	}
	
	@OneToMany(mappedBy="utiS",cascade=CascadeType.ALL)
	@JsonIgnore
	@XmlTransient
	public List<ObN> getObns() {
		return obns;
	}

	public void setObns(List<ObN> obns) {
		this.obns = obns;
	}

	@OneToMany(mappedBy="utiS",cascade=CascadeType.ALL)
	@JsonIgnore
	@XmlTransient
	public List<Partage> getPartages() {
		return partages;
	}

	public void setPartages(List<Partage> partages) {
		this.partages = partages;
	}
	
	@OneToMany(mappedBy="utiS", cascade=CascadeType.ALL)
	@JsonIgnore
	@XmlTransient
	public List<Evenement> getEvenements() {
		return evenements;
	}

	public void setEvenements(List<Evenement> evenements) {
		this.evenements = evenements;
	}

	public void linkProfilToUti(Profil profil)
	{
		setProfil(profil);
	}
}