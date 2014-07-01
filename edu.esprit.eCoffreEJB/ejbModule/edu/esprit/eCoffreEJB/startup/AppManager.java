package edu.esprit.eCoffreEJB.startup;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.Profil;
import edu.esprit.eCoffreEJB.Entities.UTI_F;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesLocal;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageRemote;
import edu.esprit.eCoffreEJB.interfaces.IUtiFLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

/**
 * Session Bean implementation class ApplicationManagement
 */
@Singleton
@Startup
public class AppManager {

	private ScheduledExecutorService scheduler;

	Context context;
	
	@EJB
	IONLocal proxy;
	@EJB
	IMetadonneesLocal proxy1;
	@EJB
	ICCFNLocal proxy2;
	@EJB
	IConteneurLocal proxy3;
	@EJB
	IUtiSLocal proxy4;
	@EJB
	IPartageRemote proxy5;
	@EJB
	IUtiFLocal proxy6;
	

	/**
	 * Default constructor.
	 */
	public AppManager() {
		// TODO Auto-generated constructor stub

	}

	@PostConstruct
	public void startup() {

		System.out.println("start");

//		ajouterCCFN();
//		ajouterConteneur();
//		ajouterAdminF();
//		ajouterUtiEtPartage();

		scheduler = Executors.newSingleThreadScheduledExecutor();
		long initialDelay = (getMidnight().getTime() - System
				.currentTimeMillis()) / 1000;
		long period = (getMidnight().getTime()) / 1000;

		scheduler.scheduleAtFixedRate(new SchedulePartageManagement(),
				initialDelay, period, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(new ScheduleEventManagement(), 0, 2,
				TimeUnit.HOURS);
	}

	@PreDestroy
	public void destroy() {
		System.out.println("finish");
		scheduler.shutdown();
	}

	private static Date getMidnight() {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	public void ajouterCCFN() {
		proxy2.ajouterCCFN(new CCFN(1919, "first", "192.168.1.250", 22,
				"upload", "upload", "/mnt/hda/uploads/", "192.168.1.251",
				"389", "dc=esprit,dc=com", "Manager", "ldap"));
		CCFN ccfn = proxy2.getCCFN();
		System.out.println("ccfn : " + ccfn.getDnLdapServer());

	}

	public void ajouterConteneur() {
		/* ajout de conteneurs */
		CCFN ccfn = proxy2.getCCFN();
		UTI_F utiF = new UTI_F();utiF.setIdUti(6);
		Conteneur conteneur = new Conteneur("Documents", true, true);
		proxy3.ajouterConteneur(conteneur, ccfn,utiF);
		conteneur = new Conteneur("Factures", true, false);
		proxy3.ajouterConteneur(conteneur, ccfn,utiF);
		conteneur = new Conteneur("Assurances", true, false);
		proxy3.ajouterConteneur(conteneur, ccfn,utiF);
		conteneur = new Conteneur("Banque", true, false);
		proxy3.ajouterConteneur(conteneur, ccfn,utiF);
		conteneur = new Conteneur("Diplômes", true, false);
		proxy3.ajouterConteneur(conteneur, ccfn,utiF);
	}

	public void supprimerConteneur() {
		/* ajout de conteneurs */
		Conteneur conteneur = proxy3.getConteneurByIdConteneur(2);
		proxy3.supprimerConteneur(conteneur);
	}

	public void supprimerUti() {
		/* suppression d'utilisateurs */
		UTI_S utiS = new UTI_S("kadhemk@gmail.com", "kadhem", "kacem",
				"kadhem", "22936773", "16 rue de la mecque", null);
		utiS.setValide(true);
		proxy4.supprimerSimpleUti(utiS);
		// utiS = new
		// UTI_S("sinda.lehlib@hotmail.fr","sinda","lehlib","sinda","58379640",
		// "19 rue de l'ariana",null);
		// proxy4.supprimerSimpleUti(utiS);
		// utiS = new
		// UTI_S("sara.torkhani@esprit.tn","sara","torkhani","sara","21112112",
		// "11 rue de bousalem",null);
		// proxy4.supprimerSimpleUti(utiS);
	}

	public void ajouterUtiEtPartage() {
		/* ajout d'utilisateurs */
		UTI_S utiS = new UTI_S("kadhemk@gmail.com", "kadhem", "kacem","031981", "22936773", "16 rue de la mecque", null);
		Profil profil = new Profil();
		int idUti = proxy4.ajouterSimpleUti(utiS, profil);
		System.out.println("idUti : " + idUti);
		// utiS = new
		// UTI_S("sinda.lehlib@hotmail.fr","sinda","lehlib","sinda","58379640",
		// "19 rue de l'ariana",null);
		// profil = new Profil(utiS);
		// proxy4.ajouterSimpleUti(utiS);
		// utiS = new
		// UTI_S("sara.torkhani@esprit.tn","sara","torkhani","sara","21112112",
		// "11 rue de bousalem",null);
		// profil = new Profil(utiS);
		// proxy4.ajouterSimpleUti(utiS);
		// Partage partage=new Partage("partage1","partage de factures",new
		// Date(), new Date());
		// proxy5.addPartage(partage, proxy4.getUtiSById(idUti));
	}
	
	public void ajouterAdminF()
	{
		UTI_F utiF = new UTI_F("admin@espritbox.tn", "Lehlib", "sinda", "sinda");
		proxy6.createAdminF(utiF);
	}

	public static void main(String[] args) {
		AppManager manager = new AppManager();
		manager.destroy();
	}
}
