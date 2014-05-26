package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity implementation class for Entity: PartageON
 *
 */
@Entity
@PersistenceContext(name="data")
@XmlRootElement
public class PartageON implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Date dateAjout;

	private PartageONPK partageONPK;
	private ObN obN;
	private Partage partage;
	
	public PartageON() {
		super();
	}   
	
	public PartageON(Date dateAjout, ObN obN, Partage partage) {
		super();
		this.partageONPK=new PartageONPK(obN.getIdU(), partage.getIdPartage());
		this.dateAjout = dateAjout;
		this.obN = obN;
		this.partage = partage;
	}

	public Date getDateAjout() {
		return this.dateAjout;
	}

	public void setDateAjout(Date dateAjout) {
		this.dateAjout = dateAjout;
	}
	
	@EmbeddedId
	public PartageONPK getPartageONPK() {
		return partageONPK;
	}
	public void setPartageONPK(PartageONPK partageONPK) {
		this.partageONPK = partageONPK;
	}

	@ManyToOne
	@JoinColumn(name = "idU", referencedColumnName = "IDU", insertable = false, updatable = false)
	public ObN getObN() {
		return obN;
	}

	public void setObN(ObN obN) {
		this.obN = obN;
	}
	
	@ManyToOne
	@JoinColumn(name = "idPartage", referencedColumnName = "idPartage", insertable = false, updatable = false)
	public Partage getPartage() {
		return partage;
	}

	public void setPartage(Partage partage) {
		this.partage = partage;
	}
   
	
}
