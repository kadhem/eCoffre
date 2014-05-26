package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@PersistenceContext(name="data")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@XmlRootElement
public class Utilisateur implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idUti;
	private String lastName;
	private String firstName;
	private String userName;
	private String password;
	private Date dateAdd;
	private boolean valide;
	
	public Utilisateur() {
		super();
	}
	

	public Utilisateur(int idUti, String lastName, String firstName,
			String userName, String password, boolean valide) {
		super();
		this.idUti = idUti;
		this.lastName = lastName;
		this.firstName = firstName;
		this.userName = userName;
		this.password = password;
		this.valide = valide;
	}

	public Utilisateur(String userName, String firstName, String lastName,  
			String password) {
		super();
		this.lastName = lastName;
		this.firstName = firstName;
		this.userName = userName;
		this.password = password;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		try {
			this.dateAdd = df.parse(df.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdUti() {
		return idUti;
	}

	public void setIdUti(int idUti) {
		this.idUti = idUti;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public boolean isValide() {
		return valide;
	}

	public void setValide(boolean valide) {
		this.valide = valide;
	}

}