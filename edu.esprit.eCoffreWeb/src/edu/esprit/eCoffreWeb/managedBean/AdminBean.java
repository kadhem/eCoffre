package edu.esprit.eCoffreWeb.managedBean;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Entities.Utilisateur;
import edu.esprit.eCoffreEJB.Technique.LdapCom;
import edu.esprit.eCoffreEJB.Technique.SFTPCom;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtilisateurLocal;

@SessionScoped
@ManagedBean
public class AdminBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	ICCFNLocal ccfnLocal;
	@EJB
	IUtilisateurLocal utilisateurLocal;
	@EJB
	IUtiSLocal utiSLocal;
	@EJB
	IPartageLocal partageLocal;
	@EJB
	IONLocal onLocal;
	@EJB
	IConteneurLocal conteneurLocal;
	@EJB
	SFTPCom sftpCom;
	@EJB
	LdapCom ldapCom;

	private List<UTI_S> users;
	private List<Partage> shares;
	private List<Conteneur> conteneurs;
	private Long countObn;
	private CCFN ccfn;
	
	
	private CartesianChartModel linearModel;
	
	private String libelle;
	private boolean actif;
	private boolean parDefaut;
	
	private boolean exist;
	private String lastName;
	private String firstName;
	private String userName;
	private String password;
	private Date dateNaissance;
	private String tel = "";
	private String adresse = "";
	private boolean coche;
	
	private boolean fail;
	private boolean success;
	private String statusOp;

	private UserBean userBean;
	
	public AdminBean() {
		super();
	}

	@PostConstruct
	public void init() {
		userBean = (UserBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userBean");
		ccfn = ccfnLocal.getCCFN();
		users = utiSLocal.getAllUtiS();
		shares = partageLocal.getAllPartages();
		countObn = onLocal.getCountAllObn();
		conteneurs = conteneurLocal.getAllConteneurs();
		for (Conteneur c : conteneurs) {
			System.out.println("admin : "+c.getUtiF().getFirstName());
		}
		createLinearModel();
	}

	private void createLinearModel() {
		linearModel = new CartesianChartModel();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date today = new Date();
		try {
			today = sdf.parse(sdf.format(today));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calToday = Calendar.getInstance();
		calToday.setTime(today);
		Calendar cal;
		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel("Inscrits");
		int i = 4;
		while(i>=0)
		{
			
			int nb = 0;
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -i);
			for (UTI_S u : users) {
				Date dateAdd = u.getDateAdd();
				try {
					dateAdd = sdf.parse(sdf.format(dateAdd));
					Calendar calAdd = Calendar.getInstance();
					calAdd.setTime(dateAdd);
					int yearCal = cal.get(Calendar.YEAR);
					int monthCal = cal.get(Calendar.MONTH);
					int yearAdd = calAdd.get(Calendar.YEAR);
					int monthAdd = calAdd.get(Calendar.MONTH);
					if(yearCal==yearAdd && monthCal == monthAdd)
					{
						nb++;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(nb);
			series1.set(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), nb);
			i--;
		}
		linearModel.addSeries(series1);
	}
	
	public long calculUsedSpace(String userName) {
		UTI_S utiS = null;
		for (UTI_S u : users) {
			if(u.getUserName().equals(userName))
			{
				utiS = u;
				break;
			}
		}
		long usedSpace = utiSLocal.getUsedSpace(userName);
		System.out.println("used space : " + usedSpace + "quota : "
				+ utiS.getQuota());
		long x = (usedSpace * 100) / utiS.getQuota();
		return x;
	}
	
	public void enableConteneur(Conteneur conteneur)
	{
		conteneur.setActif(true);
		conteneurLocal.activateOrDesactivateConteneur(conteneur);
		conteneurs.get(conteneurs.indexOf(conteneur)).setActif(true);
	}

	public void disableConteneur(Conteneur conteneur)
	{
		if(conteneur.isParDefaut())
		{
			RequestContext.getCurrentInstance().execute("dialog.show();");
			return;
		}
		conteneur.setActif(false);
		conteneurLocal.activateOrDesactivateConteneur(conteneur);
		conteneurs.get(conteneurs.indexOf(conteneur)).setActif(false);
	}
	
	public void createConteneur()
	{
		Conteneur conteneur = new Conteneur(libelle, actif, parDefaut);
		conteneur.linkConteneurToAdmin((UTI_F) userBean.getUser());
		if(conteneurLocal.ajouterConteneur(new Conteneur(libelle, actif, parDefaut), ccfn,(UTI_F) userBean.getUser())!=0)
		{
			if(parDefaut)
			{
				for (Conteneur c : conteneurs) {
					if(c.isParDefaut())
					{
						c.setParDefaut(false);
					}
				}
			}
			conteneurs.add(conteneur);
			success = true;
			statusOp = "Conteneur ajouté avec succès";
		}
		else
		{
			fail = true;
			statusOp = "Une erreur est survenue";
		}
	}
	
	public boolean checkUsername() {
		if (utiSLocal.getUtiSByUserName(userName) == null) {
			exist = false;
		} else {
			exist = true;
		}
		return exist;
	}
	
	public void createUser() {

		if (checkUsername()) {
			password = "";
			userName = "";
		}
		
		password = encryptPassword(password);
		UTI_S utiS = new UTI_S(userName, firstName, lastName, password, tel, adresse,
				dateNaissance);
		if (utiSLocal.ajouterSimpleUtiSansConfirmation(utiS) != 0) {
			utiSLocal.confirmerSimpleUti(utiS);
			userName = null;
			firstName = null;
			lastName = null;
			password = null;
			tel = null;
			adresse = null;
			dateNaissance = null;
			setCoche(false);
			users.add(utiS);
		} else {
			userName = null;
			firstName = null;
			lastName = null;
			password = null;
			tel = null;
			adresse = null;
			dateNaissance = null;
			setCoche(false);
		}
	}
	
	public void ableOrDisableUser(UTI_S utilisateur)
	{
		System.out.println("utilisateur valide : "+utilisateur.isValide());
		for (Utilisateur u : users) {
			if(utilisateur.equals(u))
			{
				if(u.isValide())
				{
					u.setValide(false);
				}
				else
				{
					u.setValide(true);
				}
				
			}
		}
		
		System.out.println("utilisateur valide : "+utilisateur.isValide());
		utilisateurLocal.activerOuDesactiverUtilisateur(utilisateur);
		
	}
	
	public void resetAddDialog(CloseEvent event)
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
	    	System.out.println("userpassword in LDAP:" + result);
	    	return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
	
	public List<UTI_S> getUsers() {
		return users;
	}

	public void setUsers(List<UTI_S> users) {
		this.users = users;
	}

	public CartesianChartModel getLinearModel() {
		return linearModel;
	}

	public void setLinearModel(CartesianChartModel linearModel) {
		this.linearModel = linearModel;
	}

	public List<Partage> getShares() {
		return shares;
	}

	public void setShares(List<Partage> shares) {
		this.shares = shares;
	}

	public Long getCountObn() {
		return countObn;
	}

	public void setCountObn(Long countObn) {
		this.countObn = countObn;
	}

	public List<Conteneur> getConteneurs() {
		return conteneurs;
	}

	public void setConteneurs(List<Conteneur> conteneurs) {
		this.conteneurs = conteneurs;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public boolean isParDefaut() {
		return parDefaut;
	}

	public void setParDefaut(boolean parDefaut) {
		this.parDefaut = parDefaut;
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

	public String getStatusOp() {
		return statusOp;
	}

	public void setStatusOp(String statusOp) {
		this.statusOp = statusOp;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
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

	
}
