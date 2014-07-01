package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@PersistenceContext(name="data")
public class UTI_F extends Utilisateur implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Conteneur> conteneurs;

	public UTI_F() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UTI_F(String userName, String firstName, String lastName,
			String password) {
		super(userName, firstName, lastName, password);
		// TODO Auto-generated constructor stub
	}

	public UTI_F(int idUti, String lastName, String firstName, String userName,
			String password, boolean valide) {
		super(idUti, lastName, firstName, userName, password, valide);
		// TODO Auto-generated constructor stub
	}

	@OneToMany(mappedBy="utiF")
	@XmlTransient
	@JsonIgnore
	public List<Conteneur> getConteneurs() {
		return conteneurs;
	}

	public void setConteneurs(List<Conteneur> conteneurs) {
		this.conteneurs = conteneurs;
	}

}