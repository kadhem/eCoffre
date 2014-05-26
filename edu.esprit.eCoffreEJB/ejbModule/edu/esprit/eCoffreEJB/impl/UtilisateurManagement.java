package edu.esprit.eCoffreEJB.impl;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
			ldapCom.createContext();
			Map<String, Object> map = ldapCom.Login(userName, passwd);
			if (map.get("message").equals("found")) {
				System.out.println("trouV");
				return (Utilisateur) map.get("user");
				// Query query;
				// String
				// req="select u from Utilisateur u where u.userName=:userName and u.password=:passwd";
				// query=entityManager.createQuery(req);
				// query.setParameter("userName", userName);
				// query.setParameter("passwd", passwd);
				// return (Utilisateur)query.getSingleResult();
			} else {
				System.out.println("non trouV");
				return null;
			}
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean sinscrire(String userName, String firstName,
			String lastName, String passwd, String tel) {

		// if(ldapCom.addUser(userName, firstName, lastName, passwd, tel))
		// {
		// SFTPCom sftpCom=new SFTPCom();
		// sftpCom.createPersonalDir(userName);
		// UTI_S utiS=new UTI_S(userName, firstName, lastName, passwd, tel);
		// utiSManagement.ajouterSimpleUti(utiS);
		// return true;
		// }
		return false;

	}

	/**
	 * 
	 * @return
	 */
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
}
