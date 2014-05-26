package edu.esprit.eCoffreWeb.converter;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;

@ManagedBean
@SessionScoped
public class ConteneurConverter implements Converter {

	@EJB
	IConteneurLocal conteneurLocal;

	@PersistenceContext
	EntityManager entityManager;
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		try {
			conteneurLocal = (IConteneurLocal) new InitialContext().lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/" +
					"ConteneurManagement!edu.esprit.eCoffreEJB.impl.ConteneurManagement");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (arg2.equals("")) {
			return null;
		} else {
			
			try {
				int id = Integer.parseInt(arg2);
				Conteneur c= conteneurLocal.getConteneurByIdConteneur(id);
				return c;

			} catch (NumberFormatException exception) {
				throw new ConverterException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Conversion Error",
						"Not a valid conteneur"));
			}
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		if (arg2 == null || arg2.equals("")) {
			return "";
		} else {
			return String.valueOf(((Conteneur) arg2).getIdCont());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
