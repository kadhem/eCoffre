package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity implementation class for Entity: Invite
 *
 */
@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class Invite implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int idInvite;
	private String email;
	
	private Partage partage;

	public Invite() {
		super();
	}   
	
	public Invite(String email) {
		super();
		this.email = email;
	}

	@Id@GeneratedValue(strategy=GenerationType.AUTO)
	public int getIdInvite() {
		return this.idInvite;
	}

	public void setIdInvite(int idInvite) {
		this.idInvite = idInvite;
	}   
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="idPartage", referencedColumnName="idPartage")
	public Partage getPartage() {
		return partage;
	}
	public void setPartage(Partage partage) {
		this.partage = partage;
	}
	
	public void linkInviteToPartage(Partage partage)
	{
		setPartage(partage);
	}
}
