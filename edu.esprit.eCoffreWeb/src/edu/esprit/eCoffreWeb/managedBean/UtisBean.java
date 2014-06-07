package edu.esprit.eCoffreWeb.managedBean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.IEmailLocal;
import edu.esprit.eCoffreEJB.interfaces.IEvenementLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

@SessionScoped
@ManagedBean
public class UtisBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private IUtiSLocal utiSLocal;
	@EJB
	private IEmailLocal emailLocal;
	@EJB
	private IEvenementLocal evenementLocal;

	private boolean exist;
	private UTI_S utiS;
	private String lastName;
	private String firstName;
	private String userName;
	private String password;
	private Date dateNaissance;
	private String tel = "";
	private String adresse = "";
	private boolean coche;

	private String statusOp;
	private boolean fail;
	private boolean success;

	private Date today = new Date();

	// Begin Event attributes
	private String titre;
	private String description;
	private Evenement evenement;
	private List<Evenement> evenements;

	private ScheduleModel eventModel;
	private ScheduleEvent selectedEvent = new DefaultScheduleEvent();
	// End Event attributes

	private ObnBean obnBean;

	public UtisBean() {
		super();
	}

	public boolean checkUsername() {
		if (utiSLocal.getUtiSByUserName(userName) == null) {
			exist = false;
		} else {
			exist = true;
		}
		return exist;
	}

	public String doCreate() {

		if (checkUsername()) {
			password = "";
			userName = "";
			return null;
		}
		utiS = new UTI_S(userName, firstName, lastName, password, tel, adresse,
				dateNaissance);
		if (utiSLocal.ajouterSimpleUtiSansConfirmation(utiS) != 0) {
			emailLocal.sendConfirmationMail(utiS);
			userName = null;
			firstName = null;
			lastName = null;
			password = null;
			tel = null;
			adresse = null;
			dateNaissance = null;
			coche = false;
			return "succes?faces-redirect=true&includeViewParams=true";
		} else {
			userName = null;
			firstName = null;
			lastName = null;
			password = null;
			tel = null;
			adresse = null;
			dateNaissance = null;
			coche = false;
			return "#";
		}
	}

	public String doUpdate() {
		utiSLocal.modifierSimpleUti(utiS);
		return "#";
	}

	public String doDelete() {
		utiSLocal.supprimerSimpleUti(utiS);
		return "#";
	}
	
	public void doReset()
	{
		userName = null;
		firstName = null;
		lastName = null;
		password = null;
		tel = null;
		adresse = null;
		dateNaissance = null;
		coche = false;
		RequestContext.getCurrentInstance().reset("formAjout"); 
	}

	public String getCurrentUtiS() {
		UserBean userBean = (UserBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userBean");
		utiS = utiSLocal.getUtiSByUserName(userBean.getUser().getUserName());
		return "moncompte?faces-redirect=true";
	}

	public String redirectToEvents() {
		obnBean = (ObnBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("obnBean");
		utiS = obnBean.getUtiS();
		evenements = evenementLocal.getEventsByIdUtis(utiS.getIdUti());
		eventModel = new DefaultScheduleModel();
		for (Evenement e : evenements) {
			eventModel.addEvent(new DefaultScheduleEvent(e.getTitre(), e
					.getDebutDate(), e.getFinDate(), (Object) e
					.getDescription().toString()));
		}
		return "monagenda?faces-redirect=true";
	}

	public void onDateSelect(SelectEvent selectEvent) throws ParseException {
		selectedEvent = new DefaultScheduleEvent("",
				(Date) selectEvent.getObject(), (Date) selectEvent.getObject(),
				"");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if( df.parse(df.format((Date) selectEvent.getObject())).before(df.parse(df.format(new Date()))) )
		{
			RequestContext.getCurrentInstance().execute("PF('erreurDialog').show();");
		}
		else
		{
			RequestContext.getCurrentInstance().execute("PF('addEvent').show();");
		}
				
	}

	public void onEventSelect(SelectEvent selectEvent) {
		selectedEvent = (ScheduleEvent) selectEvent.getObject();
	}

	public void doAddEvent() {
		try {
			System.out.println("addd");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = df.parse(df.format(selectedEvent.getStartDate()));
			Date end = df.parse(df.format(selectedEvent.getEndDate()));
			Date now = df.parse(df.format(new Date()));
			System.out.println("now :"+now.toString()+" \n start : "+start.toString()+" \n end :"+end.toString());
			if(start.before(now))
			{
				fail = true;
				statusOp = "La date de début doit être supérieur à la date actuelle";
				return;
			}
			if(start.after(end))
			{
				fail = true;
				statusOp = "La date de fin doit être supérieur à la date de début";
				return;
			}
			evenement = new Evenement(titre, description,
					selectedEvent.getStartDate(), selectedEvent.getEndDate());
			evenementLocal.addEvent(evenement, utiS);
			eventModel.addEvent(new DefaultScheduleEvent(evenement.getTitre(),
					evenement.getDebutDate(), evenement.getFinDate(), evenement
							.getDescription()));
			success = true;
			statusOp = "L'événement a été bien ajouté";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}

	public void doEditEvent() {
		try {
			List<ScheduleEvent> events = eventModel.getEvents();
			int i = 0;
			for (ScheduleEvent e : events) {
				i++;
				if (e.equals(selectedEvent)) {
					System.out.println("i : " + i + " id : " + e.getId());
					break;
				}
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = df.parse(df.format(selectedEvent.getStartDate()));
			Date end = df.parse(df.format(selectedEvent.getEndDate()));
			Date now = df.parse(df.format(new Date()));
			System.out.println("now :"+now.toString()+" \n start : "+start.toString()+" \n end :"+end.toString());
			if(start.before(now))
			{
				fail = true;
				statusOp = "La date de début doit être supérieur à la date actuelle";
				return;
			}
			if(start.after(end))
			{
				fail = true;
				statusOp = "La date de fin doit être supérieur à la date de début";
				return;
			}
			Evenement evenement = evenements.get(i - 1);
			evenement.setDebutDate(selectedEvent.getStartDate());
			evenement.setFinDate(selectedEvent.getEndDate());
			evenement.setTitre(selectedEvent.getTitle());
			evenement.setDescription((String) selectedEvent.getData());
			evenementLocal.editEvent(evenement);
			System.out.println("fin edit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetAddEventDialog() {
		System.out.println("reset");
		titre = "";
		description = "";
		statusOp = "";
		fail = false;
		success = false;
	}

	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public boolean isCoche() {
		return coche;
	}

	public void setCoche(boolean coche) {
		this.coche = coche;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public List<Evenement> getEvenements() {
		return evenements;
	}

	public void setEvenements(List<Evenement> evenements) {
		this.evenements = evenements;
	}

	public Evenement getEvenement() {
		return evenement;
	}

	public void setEvenement(Evenement evenement) {
		this.evenement = evenement;
	}

	public ScheduleEvent getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(ScheduleEvent selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public String getStatusOp() {
		return statusOp;
	}

	public void setStatusOp(String statusOp) {
		this.statusOp = statusOp;
	}

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getToday() {
		return today;
	}

	public void setToday(Date today) {
		this.today = today;
	}

}
