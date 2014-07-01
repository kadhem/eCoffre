package edu.esprit.eCoffreEJB.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.ejb3.annotation.Clustered;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;
import edu.esprit.eCoffreEJB.Technique.LdapCom;
import edu.esprit.eCoffreEJB.Technique.SFTPCom;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtilisateurLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtilisateurRemote;

/**
 * Session Bean implementation class UtilisateurManagement
 */
@Stateless
@LocalBean
public class UtilisateurManagement implements IUtilisateurLocal {

	@PersistenceContext(unitName="data")
	EntityManager entityManager;
	@EJB
	private LdapCom ldapCom;
	@EJB
	private IUtiSLocal utiSManagement;

	/**
	 * Default constructor.
	 */
	public UtilisateurManagement() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return
	 * @throws NamingException 
	 */
	@Override
	public Utilisateur seConnecter(String userName, String passwd) throws NamingException {
			System.out.println("username : "+userName + " password : "+passwd);
			if(ldapCom.createContext())
			{
				Map<String, Object> map = ldapCom.Login(userName, passwd);
				if (map.get("message").equals("found")) {
					System.out.println("trouV");
					return (Utilisateur) map.get("user");
				} else {
					System.out.println("non trouV");
					return null;
				}
			}
			else {
				 Query query;
				 String req="select u from Utilisateur u";
				 query=entityManager.createQuery(req);
				 List<Utilisateur> utilisateurs = query.getResultList();
				 for (Utilisateur u : utilisateurs) {
					if(u.getUserName().equals(userName))
					{
						System.out.println(encryptPassword(u.getPassword()));
						if(u.getPassword().equals(passwd))
						{
							return u;
						}
					}
				}
				 return null;
			}
	}
	
	@Override
	public Utilisateur seConnecterWS(String userName, String passwd) throws NamingException {
			System.out.println("username : "+userName + " password : "+passwd);
			passwd = "{SHA512}"+passwd;
			if(ldapCom.createContext())
			{
				Map<String, Object> map = ldapCom.Login(userName, passwd);
				if (map.get("message").equals("found")) {
					System.out.println("trouV");
					return (Utilisateur) map.get("user");
				} else {
					System.out.println("non trouV");
					return null;
				}
			}
			else {
				 Query query;
				 String req="select u from Utilisateur u";
				 query=entityManager.createQuery(req);
				 List<Utilisateur> utilisateurs = query.getResultList();
				 for (Utilisateur u : utilisateurs) {
					if(u.getUserName().equals(userName))
					{
						System.out.println(encryptPassword(u.getPassword()));
						if(u.getPassword().equals(passwd))
						{
							return u;
						}
					}
				}
				 return null;
			}
	}

	@Override
	public boolean seDesinscrire(UTI_S utiS) {
		
		ldapCom.createContext();
		if (ldapCom.deleteUser(utiS.getIdUti())) {
			entityManager.remove(getUtiByUserName(utiS.getUserName()));
			SFTPCom sftpCom = new SFTPCom();
			sftpCom.removeDir(utiS.getUserName());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean activerOuDesactiverUtilisateur(Utilisateur utilisateur)
	{
		ldapCom.createContext();
		if (ldapCom.enableOrDisableUser(utilisateur))
		{
			modifierUtilisateur(utilisateur);
			return true;
		}
		return false;
	}

	@Override
	public UTI_S getUtiByUserName(String userName) {
		return (UTI_S) entityManager
				.createQuery(
						"select u from Utilisateur u where u.userName=:userName")
				.setParameter("userName", userName).getSingleResult();
	}

	@Override
	public boolean modifierUtilisateur(Utilisateur utilisateur)
	{
		try {
			entityManager.merge(utilisateur);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String encryptPassword(String passwd)
    {
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(passwd.getBytes());
			 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	String result = "{SHA512}" + hexString;
	    	System.out.println("userpassword from database:" + result);
	    	return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
}
