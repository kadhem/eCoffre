package edu.esprit.eCoffreWeb.managedBean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;

import edu.esprit.eCoffreEJB.Entities.CCFN;
import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.Log;
import edu.esprit.eCoffreEJB.Entities.Metadonnees;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.HashFile;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.ILogLocal;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesLocal;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;
import edu.esprit.eCoffreWeb.converter.ObnDataModel;

@SessionScoped
@ManagedBean
public class ObnBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private IONLocal onLocal;
	@EJB
	private ICCFNLocal ccfnLocal;
	@EJB
	private IConteneurLocal conteneurLocal;
	@EJB
	private IUtiSLocal utiSLocal;
	@EJB
	private IMetadonneesLocal metaLocal;
	@EJB
	private ILogLocal logLocal;

	private Map<String, Object> map;
	private List<ObN> obNs;
	private List<ObN> filtredObNs;
	private List<Conteneur> conteneurs;
	private DonutChartModel donutModel;
	private PieChartModel pieModel;
	private UploadedFile fileToUpload;
	private List<UploadedFile> filesToUpload = new ArrayList<UploadedFile>();
	private List<InputStream> inputStreams = new ArrayList<InputStream>();
	private DefaultStreamedContent fileToShow;
	private List<Log> logs;
	
	private CCFN ccfn;
	private Conteneur selectedConteneur;
	private ObN selectedObN;
	private UserBean userBean;
	private UTI_S utiS;
	private ObN obnToShow;
	private Metadonnees metadonnees;
	private Conteneur conteneur;
	private String[] tag;
	private ObnDataModel obnModel;
	private List<Log> selectedLogs;
	
	
	private String autre[] = { "xls", "xlsx", "doc", "docx", "rtf", "txt" };
	private String img[] = { "jpeg", "jpg", "gif", "png" };
	private String tags;
	private String statusOp;
	private static int i = 0;
	private boolean fail;
	private boolean success;
	private boolean denied;
	private long usedSpace;
	private int idToDelete;
	private String typeFileToShow;
	private String contentType;
	private Date date1;
	private Date date2;

	public ObnBean() {
		super();
	}

	public void valueChange(AjaxBehaviorEvent event) {
		System.out.println("////////////" + selectedConteneur.getLibelle()
				+ "////////");
	}
	
	@PostConstruct
	public void initON() {
		
		userBean = (UserBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userBean");
		if(userBean!=null)
		{
			System.out.println("username : "+userBean.getUser().getUserName());
			ccfn = ccfnLocal.getCCFN();
			utiS = (UTI_S) userBean.getUser();
			if(utiS!=null)
			{
				listLogs();
				if(listFiles())
				{
					conteneurs = conteneurLocal.getActiveConteneurs();
					createDonutModel();
					createPieModel();
				}
				else
				{
					System.out.println("erreur");
				}
			}
			else
			{
				System.out.println("utilisateur introuvable");
			}
		}
	}

	public long calculUsedSpace() {
		UserBean userBean = (UserBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userBean");
		usedSpace = utiSLocal.getUsedSpace(userBean.getUser().getUserName());
		long x = (usedSpace / 1073741824);
		return x;
	}

	public String calculUsedSpace1() {
		long ko = usedSpace / 1024;
		long mo = usedSpace / 1048576;
		if (mo >= 1) {
			String sko = String.valueOf(ko);
			String smo = String.valueOf(mo);
			String sub = sko.substring(smo.length(), sko.length());
			ko = Integer.parseInt(sub);

		}
		if (Long.toString(ko).length() < 3) {
			return mo + "," + Long.toString(ko).substring(0, 1) + " mo";
		} else {
			return mo + "," + Long.toString(ko).substring(0, 2) + " mo";
		}
	}

	public long getUsedSpace() {
		return utiSLocal.getUsedSpace(utiS.getUserName());
	}

	public String redirectToFiles() {
		return "moncoffre?faces-redirect=true";
	}
	
	public String redirectToLogs() {
		for (ObN o : obNs) {
			System.out.println("*"+o.getLibelle()+"*");
		}
		obnModel = new ObnDataModel(obNs);
		return "monhistorique?faces-redirect=true";
	}

	public String getIconObN(String libelle) {
		int indice = libelle.lastIndexOf(".");
		String extension = libelle.substring(indice + 1);
		return "/resources/templateA/img/" + extension.toLowerCase() + ".png";
	}
	
	public String getSizeObN(String size) {
		if (!size.equals("")) {
			int size1 = Integer.parseInt(size);
			long ko = size1 / 1024;
			long mo = size1 / 1048576;
			if (mo >= 1) {
				String sko = String.valueOf(ko);
				String smo = String.valueOf(mo);
				String sub = sko.substring(smo.length(), sko.length());
				ko = Integer.parseInt(sub);
				return mo + "," + Long.toString(ko).substring(0, 2) + " mo";
			}
			return ko + " ko";
		}
		return "";
	}
	
	public void listLogs(){
		System.out.println("debut liste log");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.add(Calendar.HOUR, 1);
		DateFormat df = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String[] dates = {df.format(cal.getTime()),df.format(new Date())};
		Map<String, Object> mapLog = logLocal.LireJournal(0, ccfn.getIdCCFN(), utiS.getIdUti(), dates, null, 0);
		List<Object[]> list = (List<Object[]>) mapLog.get("data");
		logs = new ArrayList<Log>();
		System.out.println("size logs : "+list.size());
		int i =0;
		for (i=0;i<list.size(); i++)
	    {
	        //r = (Roleuser) persons.get(i);
			int idCCFN = (Integer) list.get(i)[0];
			int idCont = (Integer) list.get(i)[1];
			int idUti = (Integer) list.get(i)[2];
			String idU = (String) list.get(i)[3];
			String function = (String) list.get(i)[4];
			String date = (String) list.get(i)[5];
			String status = (String) list.get(i)[7];
			String params = (String) list.get(i)[6];
			String algo = ((Metadonnees)list.get(i)[8]).getAlgo();
			String hash = ((Metadonnees)list.get(i)[8]).getHash();
			String size = ((Metadonnees)list.get(i)[8]).getSize();
			Log log = new Log(idCCFN, idCont, idUti, idU, function, date, status, algo, hash, size, params);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
	        System.out.println("Testing n "+ i +" " + list.get(i)[3] + "*" + list.get(i)[6]+"*"+list.get(i)[4]);
	    }
	}
	
	public void listLogsByObn(SelectEvent event) {
		System.out.println("***///****"+((ObN) event.getObject()).getLibelle()+"***///***");
		System.out.println("****////****"+selectedObN.getLibelle());
		selectedLogs = new ArrayList<Log>();
		for (Log l : logs) {
			if(l.getIdU().equals(String.valueOf(selectedObN.getIdU())))
			{
				selectedLogs.add(l);
			}
		}
	}
	
	public void listLogsByDateAndIdOnUti()
	{
		DateFormat df = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dates[] = {df.format(date1),df.format(date2)};
		Map<String, Object> mapLog = logLocal.LireJournal(0, ccfn.getIdCCFN(), utiS.getIdUti(), dates, null, 0);
		List<Object[]> list = (List<Object[]>) mapLog.get("data");
		logs = new ArrayList<Log>();
		System.out.println("size logs : "+list.size());
		int i =0;
		for (i=0;i<list.size(); i++)
	    {
	        //r = (Roleuser) persons.get(i);
			int idCCFN = (Integer) list.get(i)[0];
			int idCont = (Integer) list.get(i)[1];
			int idUti = (Integer) list.get(i)[2];
			String idU = (String) list.get(i)[3];
			String function = (String) list.get(i)[4];
			String date = (String) list.get(i)[5];
			String status = (String) list.get(i)[7];
			String params = (String) list.get(i)[6];
			String algo = ((Metadonnees)list.get(i)[8]).getAlgo();
			String hash = ((Metadonnees)list.get(i)[8]).getHash();
			String size = ((Metadonnees)list.get(i)[8]).getSize();
			Log log = new Log(idCCFN, idCont, idUti, idU, function, date, status, algo, hash, size, params);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
	        System.out.println("Testing n "+ i +" " + list.get(i)[3] + "*" + list.get(i)[6]+"*"+list.get(i)[4]);
	    }
	}
	
	public void unselectObn()
	{
		selectedObN = null;
		selectedLogs = new ArrayList<Log>();
		System.out.println("unselect");
	}
	
	public boolean listFiles(){
		i++;
		System.out.println("appel n°: "+i);
		Log log;
		map = onLocal
				.Lister(0, ccfn.getIdCCFN(), 
						utiS.getIdUti(), null, null,0);
		if((Boolean) map.get("status"))
		{
			obNs = (List<ObN>) map.get("data");
			String idUs = "";
			for (ObN o : obNs) {
				idUs += o.getIdU()+",";
//				System.out.println("*" + o.getLibelle() + "*");
			}
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), idUs
					, "lister", map.get("date").toString(), "succès");
			logLocal.addLog(log);
			return true;
		} else if(map.get("cause").equals("denied")){
			denied = true;
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), "vide"
					, "lister", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
			return false;
		} else {
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), "vide"
					, "lister", map.get("date").toString(), "Erreur inconnue");
			logLocal.addLog(log);
			return false;
		}
		
		
	}

	public void handle(FileUploadEvent event) {
		System.out.println(" i = "+i);
		i++;
		System.out.println("handle");
		filesToUpload.add(event.getFile());
		InputStream in;
		try {
			in = event.getFile().getInputstream();
			inputStreams.add(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void uploadFile() {
		int j = -1;
		Log log;
		loops:
		for (InputStream in : inputStreams) {
			// onLocal.test(in);
			j++;
			HashFile hash = new HashFile();
			StringBuffer hashfile = hash.getEncryptedFileSHA512(in);

			try {
				map = onLocal.deposerOnAvecControle(filesToUpload.get(j)
						.getInputstream(), filesToUpload.get(j).getFileName(),
						ccfn.getIdCCFN(), utiS.getIdUti(), 0,
						selectedConteneur.getIdCont(), "sha-512", hashfile);
				System.out.println(map.get("status")+"*****---"+map.get("idu"));
				if ((Boolean) map.get("status")) {
					success = true;
					if (i < 2) {
						statusOp = "Votre document a été bien déposé";
					} else {
						statusOp = "Vos " + i
								+ " documents ont été bien déposé";
					}
					Metadonnees metadonnees = new Metadonnees(
							(Integer) map.get("idu"), (Integer) map.get("uti"),
							(Integer) map.get("cont"),
							(Integer) map.get("idOnUti"), map.get("size")
									.toString(), map.get("date").toString(),
							map.get("algo").toString(), map.get("hash")
									.toString(), tags);
					metaLocal.ajouterMetadonnees(metadonnees);
					
					log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
							, "deposer", map.get("date").toString(), "succès", map.get("algo").toString()
							, map.get("hash").toString(), map.get("size").toString(), null);
					logLocal.addLog(log);
					logs.add(log);
					Collections.sort(logs,Log.logComparator);
				} else if(map.get("cause").equals("denied")){
					denied = true;
					log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
							, "deposer", map.get("date").toString(), "Erreur inconnu", map.get("algo").toString()
							, map.get("hash").toString());
					logLocal.addLog(log);
					logs.add(log);
					Collections.sort(logs,Log.logComparator);
					RequestContext.getCurrentInstance().execute("deniedDialog.show();");
					return;
				} else if (!(Boolean)map.get("match")) {
					fail = true;
					statusOp = "L'empreinte ne correspond pas au fichier "+filesToUpload.get(j).getFileName();
					log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
							, "deposer", map.get("date").toString(), "incorrespondence d'empreintes", map.get("algo").toString()
							, map.get("hash").toString());
					logLocal.addLog(log);
					logs.add(log);
					Collections.sort(logs,Log.logComparator);
					break loops;
				} else {
					fail = true;
					statusOp = "Une erreur est survenue, réessayez plus tard";
					log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
							, "deposer", map.get("date").toString(), "Erreur inconnu", map.get("algo").toString()
							, map.get("hash").toString());
					logLocal.addLog(log);
					logs.add(log);
					Collections.sort(logs,Log.logComparator);
					break loops;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail = true;
				statusOp = "Une erreur est survenue, réessayez plus tard";
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
							, "deposer", map.get("date").toString(), "Erreur inconnu", map.get("algo").toString()
							, map.get("hash").toString());
				logLocal.addLog(log);
				break loops;
			}
		}
		if(success)
		{
			map = onLocal
					.Lister(0, ccfn.getIdCCFN(), utiS.getIdUti(), null, null,0);
			obNs = (List<ObN>) map.get("data");
			for (ObN o : obNs) {
				System.out.println(o.getLibelle()+"*");
			}
			System.out.println("if success");
		}
		i = 0;
		j = -1;
		selectedConteneur = new Conteneur();
		tags = "";
		inputStreams = new ArrayList<InputStream>();
		filesToUpload = new ArrayList<UploadedFile>();
	}

	public StreamedContent downloadFile(int idU, String fileName) {

		System.out.println("idu obnbean : "+idU);
		map = onLocal.lireON(idU, ccfn.getIdCCFN(), utiS.getIdUti(), 0);
		Log log;
		if ((Boolean) map.get("status")) {
			InputStream stream = (InputStream) map.get("data");
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			return new DefaultStreamedContent(stream,
					"application/octet-stream", fileName);
			
		}
		else if(map.get("cause").equals("denied")) {
			denied = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			RequestContext.getCurrentInstance().execute("deniedDialog.show()");
		}
		else {
			fail = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "Erreur inconnu");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
		return new DefaultStreamedContent();
	}

	public void showFile(int idU) {

		System.out.println("idU to show "+idU);
		setTypeFileToShow("");
		obnToShow = onLocal.getONByIdu(idU);
		int i = obnToShow.getLibelle().lastIndexOf(".");
		String l = obnToShow.getLibelle().substring(i + 1,
				obnToShow.getLibelle().length());
		pdf: if (l.toLowerCase().equals("pdf")) {
			contentType = "application/pdf";
			setTypeFileToShow("pdf");
			break pdf;
		}

		autre: for (int j = 0; j < autre.length; j++) {
			if (l.toLowerCase().equals(autre[j])) {
				// contentType = "application/pdf";
				setTypeFileToShow("autre");
				break autre;
			}
		}
		img: for (int j = 0; j < img.length; j++) {
			if (l.toLowerCase().equals(img[j])) {
				contentType = "image/" + l;
				setTypeFileToShow("img");
				break img;
			}
		}
		System.out.println("filename : " + obnToShow.getLibelle());
		Log log;
		map = onLocal.lireON(obnToShow.getIdU(), ccfn.getIdCCFN(), utiS.getIdUti(), 0);
		if ((Boolean) map.get("status")) {
			InputStream stream = (InputStream) map.get("data");
			setFileToShow(new DefaultStreamedContent(stream, contentType,
					obnToShow.getLibelle()));
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			RequestContext.getCurrentInstance().execute("PF('showDialog').show();");
		}
		else if(map.get("cause").equals("denied")) {
			denied = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			RequestContext.getCurrentInstance().execute("deniedDialog.show();");
		}
		else {
			fail = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "Erreur inconnu");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}

	public void deleteFile() {
		success = true;
		statusOp = "ça va";
		System.out.println("idU  to" + idToDelete);
		try {
			ObN obN = onLocal.getONByIdu(idToDelete);
			Log log;
			map = onLocal.detruireON(obN.getIdU(), ccfn.getIdCCFN(),
					utiS.getIdUti());
			if ((Boolean) map.get("status")) {
				System.out.println("iff");
				obNs.remove(obN);
				success = true;
				statusOp = obN.getLibelle() + " a été supprimé avec succès.";
				log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
						, "detruire", map.get("date").toString(), "succès");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs,Log.logComparator);
			}
			else if(map.get("cause").equals("denied")) {
				System.out.println("denied from obnbean");
				denied = true;
				log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map.get("idu").toString()
						, "detruire", map.get("date").toString(), "Accès refusé");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs,Log.logComparator);
				RequestContext.getCurrentInstance().execute("PF('deniedDialog').show();PF('deleteDialog').hide()");
				
			}
			else {
				System.out.println("else else");
				fail = true;
				statusOp = "Une erreur est survenue, réessayez plus tard";
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
						, "detruire", map.get("date").toString(), "Erreur inconnue");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs,Log.logComparator);
			}
		} catch (Exception e) {
			System.out.println("catch");
			e.printStackTrace();
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}
	
	public DefaultStreamedContent showSharedFile() {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		}
		// if(idToShow==0)
		// {
		// return new DefaultStreamedContent();
		// }
		else {
			System.out.println("filename : " + obnToShow.getLibelle());
			
			InputStream stream = onLocal.getInputStreamON(
					obnToShow.getLibelle(), obnToShow.getUtiS().getUserName());
			return new DefaultStreamedContent(stream, contentType,
					obnToShow.getLibelle());
		}
	}
	
	public void showMeta(ObN obN)
	{
		map = onLocal.lireMetadonnees(obN.getIdU(), ccfn.getIdCCFN(), utiS.getIdUti(), obN.getConteneur().getIdCont());
		System.out.println("showMeta");
		metadonnees = new Metadonnees((Integer)map.get("idU"), (Integer)map.get("uti"),
				(Integer)map.get("cont"), (Integer)map.get("idOnUti"), map.get("size").toString()
				, map.get("date").toString(), map.get("algo").toString(), map.get("hash").toString(), map.get("tags").toString());
		metadonnees.setObN(obN);
		conteneur = conteneurLocal.getConteneurByIdConteneur(metadonnees.getIdCont());
		tag=metadonnees.getTags().split(",");
		Log log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idU").toString()
				, "lire metadonnees", map.get("date").toString(), "succès");
		logLocal.addLog(log);
	}
	
	public void controler(ObN obN)
	{
		map = onLocal.controler(obN.getIdU(), ccfn.getIdCCFN(), utiS.getIdUti());
		Log log;
		if( ((Boolean) map.get("status")) && ( (Boolean) map.get("match")) )
		{
			success = true;
			statusOp = "Votre document est 100% intègre";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
					, "controler", map.get("date").toString(), "succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
		}
		else if(!(Boolean) map.get("match"))
		{
			fail = true;
			statusOp = "Votre document a été altéré, veuillez contacter l'administrateur";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
					, "controler", map.get("date").toString(), "incorrespondence d'empreintes");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
		}
		else if(map.get("cause").equals("denied")) {
			denied = true;
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
					, "controler", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
			RequestContext.getCurrentInstance().execute("deniedDialog.show()");
		}
		else {
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"), (Integer) map.get("uti"), map.get("idu").toString()
					, "controler", map.get("date").toString(), "Erreur inconnue");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs,Log.logComparator);
		}
	}
	
	public String findFileNameByIdu(int idU)
	{
		for (ObN o : obNs) {
			if(o.getIdU()==idU)
			{
//				if(o.getLibelle().length()>10)
//				{
//					return o.getLibelle().substring(0, 9)+"...";
//				}
				return o.getLibelle();
			}
		}
		return "";
	}

	public DonutChartModel createDonutModel() {

		donutModel = new DonutChartModel();
		if(conteneurs!=null)
		{
			Map<String, Number> circle1 = new HashMap<String, Number>();
			for (Conteneur c : conteneurs) {
				circle1.put(
						c.getLibelle(),
						onLocal.getONBbyConteneurIdAndUser(c.getIdCont(),
								utiS.getIdUti()).size());
			}
			donutModel.addCircle(circle1);
		}
		return donutModel;
	}

	public PieChartModel createPieModel() {

		pieModel = new PieChartModel();
		if(conteneurs!=null)
		{
			for (Conteneur c : conteneurs) {
				pieModel.set(
						c.getLibelle(),
						onLocal.getONBbyConteneurIdAndUser(c.getIdCont(),
								utiS.getIdUti()).size());
			}
		}
		
		return pieModel;
	}

	public String getLibelle(String libelle) {
		int i = libelle.lastIndexOf(".");
		String l = libelle.substring(0, i);
		return l;
	}

	public void resetDepotDialog() {
		System.out.println("reset depot dialog");
		selectedConteneur = new Conteneur();
		tags = "";
		inputStreams = new ArrayList<InputStream>();
		filesToUpload = new ArrayList<UploadedFile>();
		fail = false;
		success = false;
		statusOp = "";
	}

	public void resetDeleteDialog() {
		System.out.println("reset delete dialog");
		fail = false;
		success = false;
		statusOp = "";
		System.out.println("*"+fail + "*"+success);
//		RequestContext.getCurrentInstance().update("formDelete");
	}
	
	public void resetMetaDialog() {
		System.out.println("reset meta dialog");
		fail = false;
		success = false;
		denied = false;
		statusOp = "";
	}

	public void selectIdToDelete(int idU) {
		System.out.println("idU " + idU);
		setIdToDelete(idU);
	}

	public List<ObN> getObNs() {
		return obNs;
	}

	public void setObNs(List<ObN> obNs) {
		this.obNs = obNs;
	}

	public CCFN getCcfn() {
		return ccfn;
	}

	public void setCcfn(CCFN ccfn) {
		this.ccfn = ccfn;
	}

	public DonutChartModel getDonutModel() {
		return donutModel;
	}

	public void setDonutModel(DonutChartModel donutModel) {
		this.donutModel = donutModel;
	}

	public List<Conteneur> getConteneurs() {
		return conteneurs;
	}

	public void setConteneurs(List<Conteneur> conteneurs) {
		this.conteneurs = conteneurs;
	}

	public PieChartModel getPieModel() {
		return pieModel;
	}

	public void setPieModel(PieChartModel pieModel) {
		this.pieModel = pieModel;
	}

	public List<ObN> getFiltredObNs() {
		return filtredObNs;
	}

	public void setFiltredObNs(List<ObN> filtredObNs) {
		this.filtredObNs = filtredObNs;
	}

	public Conteneur getSelectedConteneur() {
		return selectedConteneur;
	}

	public void setSelectedConteneur(Conteneur selectedConteneur) {
		this.selectedConteneur = selectedConteneur;
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

	public UploadedFile getFileToUpload() {
		return fileToUpload;
	}

	public void setFileToUpload(UploadedFile fileToUpload) {
		this.fileToUpload = fileToUpload;
	}

	public List<UploadedFile> getFilesToUpload() {
		return filesToUpload;
	}

	public void setFilesToUpload(List<UploadedFile> filesToUpload) {
		this.filesToUpload = filesToUpload;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public int getIdToDelete() {
		return idToDelete;
	}

	public void setIdToDelete(int idToDelete) {
		this.idToDelete = idToDelete;
	}

	public String getTypeFileToShow() {
		return typeFileToShow;
	}

	public void setTypeFileToShow(String typeFileToShow) {
		this.typeFileToShow = typeFileToShow;
	}

	public ObN getObnToShow() {
		return obnToShow;
	}

	public void setObnToShow(ObN obnToShow) {
		this.obnToShow = obnToShow;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isDenied() {
		return denied;
	}

	public void setDenied(boolean denied) {
		this.denied = denied;
	}

	public DefaultStreamedContent getFileToShow() {
		return fileToShow;
	}

	public void setFileToShow(DefaultStreamedContent fileToShow) {
		this.fileToShow = fileToShow;
	}

	public UTI_S getUtiS() {
		return utiS;
	}

	public void setUtiS(UTI_S utiS) {
		this.utiS = utiS;
	}

	public Metadonnees getMetadonnees() {
		return metadonnees;
	}

	public void setMetadonnees(Metadonnees metadonnees) {
		this.metadonnees = metadonnees;
	}

	public Conteneur getConteneur() {
		return conteneur;
	}

	public void setConteneur(Conteneur conteneur) {
		this.conteneur = conteneur;
	}

	public String[] getTag() {
		return tag;
	}

	public void setTag(String[] tag) {
		this.tag = tag;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public ObN getSelectedObN() {
		return selectedObN;
	}

	public void setSelectedObN(ObN selectedObN) {
		this.selectedObN = selectedObN;
	}

	public ObnDataModel getObnModel() {
		return obnModel;
	}

	public void setObnModel(ObnDataModel obnModel) {
		this.obnModel = obnModel;
	}

	public List<Log> getSelectedLogs() {
		return selectedLogs;
	}

	public void setSelectedLogs(List<Log> selectedLogs) {
		this.selectedLogs = selectedLogs;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}
	
}
