package edu.esprit.eCoffreEJB.Entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.PersistenceContext;

@Entity
@PersistenceContext(name="data")
public class UTI_F extends Utilisateur implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

}