package edu.esprit.eCoffreEJB.impl;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import edu.esprit.eCoffreEJB.interfaces.IUtiGLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiGRemote;

/**
 * Session Bean implementation class Uti_fManagement
 */
@Stateless
@LocalBean
public class UtiGManagement implements IUtiGRemote, IUtiGLocal {

    /**
     * Default constructor. 
     */
    public UtiGManagement() {
        // TODO Auto-generated constructor stub
    }

}
