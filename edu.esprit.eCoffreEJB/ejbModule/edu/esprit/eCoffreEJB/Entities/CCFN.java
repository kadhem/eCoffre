package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class CCFN implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idCCFN;
	private String libelle;
	private String urlFileServer;
	private int portFileServer;
	private String loginFileServer;
	private String passwdFileServer;
	private String dirFileServer;
	private String urlLdapServer;
	private String portLdapServer;
	private String dnLdapServer;
	private String loginManager;
	private String passwdManager;
	
	private List<Conteneur> conteneurs;

	/*--------------------*/
	
	public CCFN() {
		super();
	}

	public CCFN(String libelle, String urlFileServer, int portFileServer,
			String loginFileServer, String passwdFileServer,
			String dirFileServer, String urlLdapServer, String portLdapServer,
			String dnLdapServer, String loginManager, String passwdManager) {
		super();
		this.libelle = libelle;
		this.urlFileServer = urlFileServer;
		this.portFileServer = portFileServer;
		this.loginFileServer = loginFileServer;
		this.passwdFileServer = passwdFileServer;
		this.dirFileServer = dirFileServer;
		this.urlLdapServer = urlLdapServer;
		this.portLdapServer = portLdapServer;
		this.dnLdapServer = dnLdapServer;
		this.loginManager = loginManager;
		this.passwdManager = passwdManager;
	}

	public CCFN(int iD_CCFN, String libelle, String urlFileServer,
			int portFileServer, String loginFileServer,
			String passwdFileServer, String dirFileServer,
			String urlLdapServer, String portLdapServer, String dnLdapServer,
			String loginManager, String passwdManager) {
		super();
		idCCFN = iD_CCFN;
		this.libelle = libelle;
		this.urlFileServer = urlFileServer;
		this.portFileServer = portFileServer;
		this.loginFileServer = loginFileServer;
		this.passwdFileServer = passwdFileServer;
		this.dirFileServer = dirFileServer;
		this.urlLdapServer = urlLdapServer;
		this.portLdapServer = portLdapServer;
		this.dnLdapServer = dnLdapServer;
		this.loginManager = loginManager;
		this.passwdManager = passwdManager;
	}

	public CCFN(String libelle) {
		super();
		this.libelle = libelle;
	}
	
	@Id
	public int getIdCCFN() {
		return idCCFN;
	}

	public void setIdCCFN(int idCCFN) {
		this.idCCFN = idCCFN;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	

	public String getUrlFileServer() {
		return urlFileServer;
	}

	public void setUrlFileServer(String urlFileServer) {
		this.urlFileServer = urlFileServer;
	}

	public int getPortFileServer() {
		return portFileServer;
	}

	public void setPortFileServer(int portFileServer) {
		this.portFileServer = portFileServer;
	}

	public String getLoginFileServer() {
		return loginFileServer;
	}

	public void setLoginFileServer(String loginFileServer) {
		this.loginFileServer = loginFileServer;
	}

	public String getPasswdFileServer() {
		return passwdFileServer;
	}

	public void setPasswdFileServer(String passwdFileServer) {
		this.passwdFileServer = passwdFileServer;
	}

	public String getDirFileServer() {
		return dirFileServer;
	}

	public void setDirFileServer(String dirFileServer) {
		this.dirFileServer = dirFileServer;
	}

	public String getUrlLdapServer() {
		return urlLdapServer;
	}


	public void setUrlLdapServer(String urlLdapServer) {
		this.urlLdapServer = urlLdapServer;
	}


	public String getPortLdapServer() {
		return portLdapServer;
	}


	public void setPortLdapServer(String portLdapServer) {
		this.portLdapServer = portLdapServer;
	}


	public String getDnLdapServer() {
		return dnLdapServer;
	}


	public void setDnLdapServer(String dnLdapServer) {
		this.dnLdapServer = dnLdapServer;
	}


	public String getLoginManager() {
		return loginManager;
	}


	public void setLoginManager(String loginManager) {
		this.loginManager = loginManager;
	}


	public String getPasswdManager() {
		return passwdManager;
	}

	public void setPasswdManager(String passwdManager) {
		this.passwdManager = passwdManager;
	}
	
	@OneToMany(mappedBy="ccfn",cascade=CascadeType.ALL)
	@XmlTransient
	@JsonIgnore
	public List<Conteneur> getConteneurs() {
		return conteneurs;
	}

	public void setConteneurs(List<Conteneur> conteneurs) {
		this.conteneurs = conteneurs;
	}
	
}