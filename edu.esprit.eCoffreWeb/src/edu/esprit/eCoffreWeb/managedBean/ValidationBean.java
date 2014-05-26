package edu.esprit.eCoffreWeb.managedBean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

@RequestScoped
@ManagedBean
public class ValidationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private IUtiSLocal utiSLocal;

	@ManagedProperty(value = "#{param.id}")
	private Long id;
	private boolean valide = false;

	private static int i = 0;
	
	public ValidationBean() {
		super();
	}
	
	public void validateAccount()
	{
		try {
			if(i==0)
			{
				System.out.println("id : "+id.intValue());
				UTI_S utiS=utiSLocal.getUtiSById(id.intValue());
				utiS.setValide(true);
				System.out.println(1);
				utiSLocal.confirmerSimpleUti(utiS);
				valide=true;
			}
			i++;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("page appellée après inscription");
		}
		
	}

	public boolean isValide() {
		return valide;
	}

	public void setValide(boolean valide) {
		this.valide = valide;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
