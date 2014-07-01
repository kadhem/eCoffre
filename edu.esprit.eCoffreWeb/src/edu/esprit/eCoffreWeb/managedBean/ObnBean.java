package edu.esprit.eCoffreWeb.managedBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
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
import edu.esprit.eCoffreWeb.converter.Document;
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
	private ObnDataModel obnModel;
	private List<Log> selectedLogs;

	private long usedSpace;

	private int idSharedObn;
	private static int i = 0;
	private int idToDelete;

	private String[] tag;
	private String autre[] = { "xls", "xlsx", "doc", "docx", "rtf", "txt" };
	private String img[] = { "jpeg", "jpg", "gif", "png" };
	private String tags;
	private String typeFileToShow;
	private String contentType;
	private String statusOp;
	private String statusS = "";
	private String statusF = "";
	private String deniedMessage;

	private Date date1;
	private Date date2;

	private boolean fail;
	private boolean success;
	private boolean denied;
	
	private TreeNode root;
	private TreeNode selectedNode;

	public ObnBean() {
		super();
	}

	public void valueChange(AjaxBehaviorEvent event) {
		System.out.println("////////////" + selectedConteneur.getLibelle()
				+ "////////");
	}

	@PostConstruct
	public void initON() {
		String name = FacesContext.getCurrentInstance().getViewRoot()
				.getViewId();
		System.out.println("*************page : " + name);
		try {

			if (!name.contains("partage.xhtml")) {
				userBean = (UserBean) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("userBean");
				if (userBean != null) {
					System.out.println("username : "
							+ userBean.getUser().getUserName());
					ccfn = ccfnLocal.getCCFN();
					utiS = (UTI_S) userBean.getUser();
					if (utiS != null) {
						System.out.println("utis : " + utiS.getFirstName());
						listLogs();
						if (listFilesByConteneur(0)) {
							conteneurs = conteneurLocal.getActiveConteneurs();
							root = createDocuments();
							createDonutModel();
							createPieModel();
						} else {
							System.out.println("erreur");
						}
					} else {
						System.out.println("utilisateur introuvable");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String calculateUserQuota() {
		long go = utiS.getQuota() / 1073741824;
		long mo = utiS.getQuota() / 1048576;
		long ko = utiS.getQuota() / 1024;
		if (go >= 1) {
			if (utiS.getQuota() % 1073741824 == 0) {
				System.out.println(utiS.getQuota() % 1073741824);
				return go + "Go";
			}
			String smo = String.valueOf(ko);
			String sgo = String.valueOf(mo);
			String sub = smo.substring(sgo.length(), smo.length());
			mo = Integer.parseInt(sub);
			return go + "," + Long.toString(mo).substring(0, 1) + " Go";
		} else {
			if (utiS.getQuota() % 1048576 == 0) {
				System.out.println(utiS.getQuota() % 1048576);
				return mo + "Mo";
			}
			String sko = String.valueOf(ko);
			String smo = String.valueOf(mo);
			String sub = sko.substring(smo.length(), sko.length());
			ko = Integer.parseInt(sub);
			return mo + "," + Long.toString(ko).substring(0, 1) + " Mo";
		}
	}

	public long calculUsedSpace() {
		try {
			usedSpace = utiSLocal.getUsedSpace(utiS.getUserName());
			System.out.println("used space : " + usedSpace + "quota : "
					+ utiS.getQuota());
			long x = (usedSpace * 100) / utiS.getQuota();
			System.out.println("used space = " + x);
			return x;
		} catch (Exception e) {
			return 0;
		}

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
		listFilesWithLog();
		return "moncoffre?faces-redirect=true";
	}

	public String redirectToLogs() {
		for (ObN o : obNs) {
			System.out.println("*" + o.getLibelle() + "*");
		}
		listFilesByConteneur(0);
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

	public void listLogs() {
		System.out.println("debut liste log");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.add(Calendar.HOUR, 1);
		Map<String, Object> mapLog = logLocal.LireJournal(0, ccfn.getIdCCFN(),
				utiS.getIdUti(), null, null, 0);
		Log log;
		if ((Boolean) mapLog.get("status")) {
			List<Object[]> list = (List<Object[]>) mapLog.get("data");
			logs = new ArrayList<Log>();
			System.out.println("size logs : " + list.size());
			int i = 0;
			for (i = 0; i < list.size(); i++) {
				// r = (Roleuser) persons.get(i);
				int idCCFN = (Integer) list.get(i)[0];
				int idCont = (Integer) list.get(i)[1];
				int idUti = (Integer) list.get(i)[2];
				String idU = (String) list.get(i)[3];
				String function = (String) list.get(i)[4];
				String date = (String) list.get(i)[5];
				String status = (String) list.get(i)[7];
				String params = (String) list.get(i)[6];
				String algo = ((Metadonnees) list.get(i)[8]).getAlgo();
				String hash = ((Metadonnees) list.get(i)[8]).getHash();
				String size = ((Metadonnees) list.get(i)[8]).getSize();
				log = new Log(idCCFN, idCont, idUti, idU, function, date,
						status, algo, hash, size, params);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
				System.out.println("Testing n " + i + " " + list.get(i)[3]
						+ "*" + list.get(i)[6] + "*" + list.get(i)[4]);
			}
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Succès");
			// logLocal.addLog(log);
		} else if (mapLog.get("cause").equals("denied")) {
			// denied = true;
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Accès refusé");
			// logLocal.addLog(log);
			// deniedMessage =
			// "Vous n'avez pas le droit de lister votre historique. Contactez l'administrateur";
			// RequestContext.getCurrentInstance().execute("PF('deniedDialog').show();");
		} else {
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Erreur inconnue");
			// logLocal.addLog(log);
		}

	}

	public void listLogsByObn(SelectEvent event) {
		System.out.println("***///****"
				+ ((ObN) event.getObject()).getLibelle() + "***///***");
		System.out.println("****////****" + selectedObN.getLibelle());

		// for (Log l : logs) {
		// if(l.getIdU().equals(String.valueOf(selectedObN.getIdU())))
		// {
		// selectedLogs.add(l);
		// }
		// }
		Map<String, Object> mapLog = logLocal.LireJournal(selectedObN.getIdU(),
				ccfn.getIdCCFN(), utiS.getIdUti(), null, null, 0);
		Log log;
		if ((Boolean) mapLog.get("status")) {
			List<Object[]> list = (List<Object[]>) mapLog.get("data");
			selectedLogs = new ArrayList<Log>();
			System.out.println("size logs : " + list.size());
			int i = 0;
			for (i = 0; i < list.size(); i++) {
				// r = (Roleuser) persons.get(i);
				int idCCFN = (Integer) list.get(i)[0];
				int idCont = (Integer) list.get(i)[1];
				int idUti = (Integer) list.get(i)[2];
				String idU = (String) list.get(i)[3];
				String function = (String) list.get(i)[4];
				String date = (String) list.get(i)[5];
				String status = (String) list.get(i)[7];
				String params = (String) list.get(i)[6];
				String algo = ((Metadonnees) list.get(i)[8]).getAlgo();
				String hash = ((Metadonnees) list.get(i)[8]).getHash();
				String size = ((Metadonnees) list.get(i)[8]).getSize();
				log = new Log(idCCFN, idCont, idUti, idU, function, date,
						status, algo, hash, size, params);
				selectedLogs.add(log);
				Collections.sort(logs, Log.logComparator);
				System.out.println("Testing n " + i + " " + list.get(i)[3]
						+ "*" + list.get(i)[6] + "*" + list.get(i)[4]);
			}
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Succès");
			// logLocal.addLog(log);
		} else if (mapLog.get("cause").equals("denied")) {
			// denied = true;
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Accès refusé");
			// logLocal.addLog(log);
			// deniedMessage =
			// "Vous n'avez pas le droit de lister votre historique. Contactez l'administrateur";
			// RequestContext.getCurrentInstance().execute("PF('deniedDialog').show();");
		} else {
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Erreur inconnue");
			// logLocal.addLog(log);
		}
	}

	public void listLogsByDateAndIdOnUti() {
		unselectObn();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String dates[] = { df.format(date1), df.format(date2) };
		Map<String, Object> mapLog = logLocal.LireJournal(0, ccfn.getIdCCFN(),
				utiS.getIdUti(), dates, null, 0);

		Log log;
		if ((Boolean) mapLog.get("status")) {
			List<Object[]> list = (List<Object[]>) mapLog.get("data");
			logs = new ArrayList<Log>();
			System.out.println("size logs : " + list.size());
			int i = 0;
			for (i = 0; i < list.size(); i++) {
				int idCCFN = (Integer) list.get(i)[0];
				int idCont = (Integer) list.get(i)[1];
				int idUti = (Integer) list.get(i)[2];
				String idU = (String) list.get(i)[3];
				String function = (String) list.get(i)[4];
				String date = (String) list.get(i)[5];
				String status = (String) list.get(i)[7];
				String params = (String) list.get(i)[6];
				String algo = ((Metadonnees) list.get(i)[8]).getAlgo();
				String hash = ((Metadonnees) list.get(i)[8]).getHash();
				String size = ((Metadonnees) list.get(i)[8]).getSize();
				log = new Log(idCCFN, idCont, idUti, idU, function, date,
						status, algo, hash, size, params);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
				System.out.println("Testing n " + i + " " + list.get(i)[3]
						+ "*" + list.get(i)[6] + "*" + list.get(i)[4]);
			}
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Succès");
			// logLocal.addLog(log);
		} else if (map.get("cause").equals("denied")) {
			// denied = true;
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Accès refusé");
			// logLocal.addLog(log);
			// deniedMessage =
			// "Vous n'avez pas le droit de lister votre historique. Contactez l'administrateur";
			// RequestContext.getCurrentInstance().execute("PF('deniedDialog').show();");
		} else {
			// log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
			// map.get("idu").toString()
			// , "Lire journal", map.get("date").toString(), "Erreur inconnue");
			// logLocal.addLog(log);
		}
	}

	public void unselectObn() {
		selectedObN = null;
		selectedLogs = new ArrayList<Log>();
		System.out.println("unselect");
	}
	
	public boolean listFilesByConteneur(int idConteneur) {
		i++;
		System.out.println("appel n°: " + i);
		Log log;
		map = onLocal.Lister(0, ccfn.getIdCCFN(), utiS.getIdUti(), null, null,
				idConteneur);
		if ((Boolean) map.get("status")) {
			obNs = (List<ObN>) map.get("data");
			if ((obNs.size() != 0) && (obNs != null)) {
				String idUs = "";
				for (ObN o : obNs) {
					idUs += o.getIdU() + ",";
					// System.out.println("*" + o.getLibelle() + "*");
				}
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
						(Integer) map.get("uti"), idUs, "lister", map.get(
								"date").toString(), "Succès");
			}
			return true;
		} else if (map.get("cause").equals("denied")) {
			if (obNs != null) {
				obNs = new ArrayList<ObN>();
			}
			denied = true;
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), "vide", "lister", map.get("date")
							.toString(), "Accès refusé");
			deniedMessage = "Vous n'avez pas le droit de lister vos documents. Contactez l'administrateur";
			RequestContext.getCurrentInstance().execute(
					"PF('deniedDialog').show();");
			return false;
		} else {
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), "vide", "lister", map.get("date")
							.toString(), "Erreur inconnue");
			return false;
		}

	}

	public boolean listFilesWithLog() {
		i++;
		System.out.println("appel n°: " + i);
		Log log;
		map = onLocal.Lister(0, ccfn.getIdCCFN(), utiS.getIdUti(), null, null,
				0);
		if ((Boolean) map.get("status")) {
			obNs = (List<ObN>) map.get("data");
			if ((obNs.size() != 0) && (obNs != null)) {
				String idUs = "";
				for (ObN o : obNs) {
					idUs += o.getIdU() + ",";
					// System.out.println("*" + o.getLibelle() + "*");
				}
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
						(Integer) map.get("uti"), idUs, "lister", map.get(
								"date").toString(), "Succès");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
			}
			return true;
		} else if (map.get("cause").equals("denied")) {
			if (obNs != null) {
				obNs = new ArrayList<ObN>();
			}
			denied = true;
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), "vide", "lister", map.get("date")
							.toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			deniedMessage = "Vous n'avez pas le droit de lister vos documents. Contactez l'administrateur";
			RequestContext.getCurrentInstance().execute(
					"PF('deniedDialog').show();");
			return false;
		} else {
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), "vide", "lister", map.get("date")
							.toString(), "Erreur inconnue");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			return false;
		}

	}

	public void handle(FileUploadEvent event) {
		System.out.println(" i = " + i);
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
		System.out.println("before loops  size : "+inputStreams.size()+"*"+filesToUpload.size());
		for (InputStream in : inputStreams) {
			// onLocal.test(in);
			System.out.println("j courant : "+j);
			j++;
			if (getUsedSpace() + filesToUpload.get(j).getSize() > utiS.getQuota()) {
				System.out.println("espace insuffisant");
				fail = true;
				setStatusF(getStatusF()
						+ "Espace insuffisant pour le document "
						+ filesToUpload.get(j).getFileName() + "\n");
			} else {

				HashFile hash = new HashFile();
				StringBuffer hashfile = hash.getEncryptedFileSHA512(in);
				try {
					String fileName = renameDoc(filesToUpload.get(j)
							.getFileName());
					map = onLocal.deposerOnAvecControle(filesToUpload.get(j)
							.getInputstream(), fileName, ccfn.getIdCCFN(), utiS
							.getIdUti(), 0, selectedConteneur.getIdCont(),
							"sha-512", hashfile);
					System.out.println(map.get("status") + "*****---"
							+ map.get("idu"));
					if ((Boolean) map.get("status")) {
						success = true;
						if (!fileName
								.equals(filesToUpload.get(j).getFileName())) {
							statusS += filesToUpload.get(j).getFileName()
									+ " a été bien déposé avec le nom '"
									+ fileName + "\n";
						} else {
							statusS += fileName + " a été bien déposé \n";
						}
						Metadonnees metadonnees = new Metadonnees(
								(Integer) map.get("idu"),
								(Integer) map.get("uti"),
								(Integer) map.get("cont"),
								(Integer) map.get("idOnUti"), map.get("size")
										.toString(),
								map.get("date").toString(), map.get("algo")
										.toString(),
								map.get("hash").toString(), tags);
						metaLocal.ajouterMetadonnees(metadonnees);

						log = new Log(ccfn.getIdCCFN(),
								(Integer) map.get("cont"),
								(Integer) map.get("uti"), map.get("idu")
										.toString(), "deposer", map.get("date")
										.toString(), "Succès", map.get("algo")
										.toString(),
								map.get("hash").toString(), map.get("size")
										.toString(), null);
						logLocal.addLog(log);
						logs.add(log);
						Collections.sort(logs, Log.logComparator);
					} else if (map.get("cause").equals("denied")) {
						System.out.println("else if 2 depot");
						denied = true;
						log = new Log(ccfn.getIdCCFN(),
								(Integer) map.get("cont"),
								(Integer) map.get("uti"), map.get("idu")
										.toString(), "deposer", map.get("date")
										.toString(), "Accès refusé", map.get(
										"algo").toString(), map.get("hash")
										.toString());
						logLocal.addLog(log);
						logs.add(log);
						Collections.sort(logs, Log.logComparator);
						deniedMessage = "Vous n'avez pas le droit de déposer des documents. Contactez l'administrateur";
						RequestContext.getCurrentInstance().execute(
								"PF('deniedDialog').show();");
						break;
					} else if (!(Boolean) map.get("match")) {
						System.out.println("else if 1 depot");
						fail = true;
						setStatusF(getStatusF()
								+ "L'empreinte ne correspond pas au fichier "
								+ filesToUpload.get(j).getFileName() + " \n");
						log = new Log(ccfn.getIdCCFN(),
								(Integer) map.get("cont"),
								(Integer) map.get("uti"), map.get("idu")
										.toString(), "deposer", map.get("date")
										.toString(),
								"incorrespondence d'empreintes", map
										.get("algo").toString(), map
										.get("hash").toString());
						logLocal.addLog(log);
						logs.add(log);
						Collections.sort(logs, Log.logComparator);
					} else {
						System.out.println("else depot");
						fail = true;
						setStatusF(getStatusF()
								+ "Une erreur est survenue lors du depot du document "
								+ filesToUpload.get(j).getFileName() + " \n");
						log = new Log(ccfn.getIdCCFN(),
								(Integer) map.get("cont"),
								(Integer) map.get("uti"), map.get("idu")
										.toString(), "deposer", map.get("date")
										.toString(), "Erreur inconnu", map.get(
										"algo").toString(), map.get("hash")
										.toString());
						logLocal.addLog(log);
						logs.add(log);
						Collections.sort(logs, Log.logComparator);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fail = true;
					setStatusF("Une erreur est survenue, réessayez plus tard");
					log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
							(Integer) map.get("uti"),
							map.get("idu").toString(), "deposer", map.get(
									"date").toString(), "Erreur inconnu", map
									.get("algo").toString(), map.get("hash")
									.toString());
					logLocal.addLog(log);
					logs.add(log);
					Collections.sort(logs, Log.logComparator);
					break;
				}
			}
		}
		if (success) {
			listFilesByConteneur(0);
			System.out.println("if success");
		}
		System.out.println("after loops");
		i = 0;
		j = -1;
		selectedConteneur = new Conteneur();
		tags = "";
		inputStreams = new ArrayList<InputStream>();
		filesToUpload = new ArrayList<UploadedFile>();
	}

	public StreamedContent downloadFile(int idU, String fileName) {

		System.out.println("idu obnbean : " + idU);
		map = onLocal.lireON(idU, ccfn.getIdCCFN(), utiS.getIdUti(), 0);
		Log log;
		if ((Boolean) map.get("status")) {
			InputStream stream = (InputStream) map.get("data");
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "telecharger", map.get("date")
					.toString(), "Succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			return new DefaultStreamedContent(stream,
					"application/octet-stream", fileName);

		} else if (map.get("cause").equals("denied")) {
			System.out.println("denied download");
			denied = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "telecharger", map.get("date")
					.toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			deniedMessage = "Vous n'avez pas le droit de télécharger des documents. Contactez l'administrateur";
		} else {
			fail = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "telecharger", map.get("date")
					.toString(), "Erreur inconnu");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
		System.out.println("return new default");
		return null;
	}

	public void downloadAllFiles() {
		try {
			String dir = System.getProperty("user.home") + File.separator
					+ "Downloads";
			System.out.println("directory  :" + dir);
			FileOutputStream fos = new FileOutputStream(dir + File.separator
					+ "documents.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			listFilesByConteneur(0);
			for (ObN o : obNs) {
				System.out.println("o courant : " + o.getLibelle());
				map = onLocal.lireON(o.getIdU(), ccfn.getIdCCFN(),
						utiS.getIdUti(), 0);
				InputStream in = (InputStream) map.get("data");
				ZipEntry ze = new ZipEntry(o.getLibelle());
				zos.putNextEntry(ze);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				in.close();
			}
			zos.close();
			fos.close();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Téléchargement terminé à l'emplacement " + dir
							+ File.separator + "documents.zip", null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Une erreur est survenue, réessayez plus tard", null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Une erreur est survenue, réessayez plus tard", null);
			FacesContext.getCurrentInstance().addMessage(null, message);
		}

	}

	public void showFile(int idU) {

		System.out.println("idU to show " + idU);
		setTypeFileToShow("autre");
		obnToShow = onLocal.getONByIdu(idU);
		int i = obnToShow.getLibelle().lastIndexOf(".");
		String l = obnToShow.getLibelle().substring(i + 1,
				obnToShow.getLibelle().length());
		pdf: if (l.toLowerCase().equals("pdf")) {
			contentType = "application/pdf";
			setTypeFileToShow("pdf");
			break pdf;
		}

//		autre: for (int j = 0; j < autre.length; j++) {
//			if (l.toLowerCase().equals(autre[j])) {
//				setTypeFileToShow("autre");
//				break autre;
//			}
//		}
		
		img: for (int j = 0; j < img.length; j++) {
			if (l.toLowerCase().equals(img[j])) {
				contentType = "image/" + l;
				setTypeFileToShow("img");
				break img;
			}
		}
		System.out.println("filename : " + obnToShow.getLibelle());
		Log log;
		map = onLocal.lireON(obnToShow.getIdU(), ccfn.getIdCCFN(),
				utiS.getIdUti(), 0);
		if ((Boolean) map.get("status")) {
			InputStream stream = (InputStream) map.get("data");
			setFileToShow(new DefaultStreamedContent(stream, contentType,
					obnToShow.getLibelle()));
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "visualiser", map.get("date")
					.toString(), "Succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			RequestContext.getCurrentInstance().execute(
					"PF('showDialog').show();");
		} else if (map.get("cause").equals("denied")) {
			denied = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "visualiser", map.get("date")
					.toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			deniedMessage = "Vous n'avez pas le droit de visualiser des documents. Contactez l'administrateur";
			RequestContext.getCurrentInstance().execute(
					"PF('deniedDialog').show();");
		} else {
			fail = true;
			log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"), map
					.get("idu").toString(), "visualiser", map.get("date")
					.toString(), "Erreur inconnu");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}

	public void deleteFile() {
		System.out.println("idU  to" + idToDelete);
		try {
			ObN obN = onLocal.getONByIdu(idToDelete);
			Log log;
			map = onLocal.detruireON(obN.getIdU(), ccfn.getIdCCFN(),
					utiS.getIdUti());
			if ((Boolean) map.get("status")) {
				System.out.println("iff");
				success = true;
				statusOp = obN.getLibelle() + " a été supprimé avec succès.";
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
						(Integer) map.get("uti"), map.get("idu").toString(),
						"detruire", map.get("date").toString(), "Succès");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
				map = onLocal.Lister(0, ccfn.getIdCCFN(), utiS.getIdUti(),
						null, null, 0);
				obNs = (List<ObN>) map.get("data");

			} else if (map.get("cause").equals("denied")) {
				System.out.println("denied from obnbean");
				denied = true;
				log = new Log(ccfn.getIdCCFN(), -1, (Integer) map.get("uti"),
						map.get("idu").toString(), "detruire", map.get("date")
								.toString(), "Accès refusé");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
				deniedMessage = "Vous n'avez pas le droit de supprimer des documents. Contactez l'administrateur";
				RequestContext.getCurrentInstance().execute(
						"PF('deniedDialog').show();PF('deleteDialog').hide()");

			} else {
				System.out.println("else else");
				fail = true;
				statusOp = "Une erreur est survenue, réessayez plus tard";
				log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
						(Integer) map.get("uti"), map.get("idu").toString(),
						"detruire", map.get("date").toString(),
						"Erreur inconnue");
				logLocal.addLog(log);
				logs.add(log);
				Collections.sort(logs, Log.logComparator);
			}
		} catch (Exception e) {
			System.out.println("catch");
			e.printStackTrace();
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}

	public StreamedContent downloadSharedFile(int idU) {
		obnToShow = onLocal.getONByIdu(idU);
		System.out.println("filename : " + obnToShow.getLibelle());

		InputStream stream = onLocal.getInputStreamON(obnToShow.getLibelle(),
				obnToShow.getUtiS().getUserName());
		return new DefaultStreamedContent(stream, "application/octet-stream",
				obnToShow.getLibelle());
	}

	public void showSharedFile(int idU) {
		System.out.println("selected shared obn : " + idU);
		setIdSharedObn(idU);
		setTypeFileToShow("autre");
		obnToShow = onLocal.getONByIdu(idU);
		System.out.println("obntoshow id : " + obnToShow.getIdU());
		int i = obnToShow.getLibelle().lastIndexOf(".");
		String l = obnToShow.getLibelle().substring(i + 1,
				obnToShow.getLibelle().length());
		pdf: if (l.toLowerCase().equals("pdf")) {
			contentType = "application/pdf";
			setTypeFileToShow("pdf");
			break pdf;
		}

//		autre: for (int j = 0; j < autre.length; j++) {
//			if (l.toLowerCase().equals(autre[j])) {
//				setTypeFileToShow("autre");
//				break autre;
//			}
//		}
		
		img: for (int j = 0; j < img.length; j++) {
			if (l.toLowerCase().equals(img[j])) {
				contentType = "image/" + l;
				setTypeFileToShow("img");
				break img;
			}
		}
		System.out.println("filename : " + obnToShow.getLibelle());

		InputStream stream = onLocal.getInputStreamON(obnToShow.getLibelle(),
				obnToShow.getUtiS().getUserName());
		fileToShow = new DefaultStreamedContent(stream, contentType,
				obnToShow.getLibelle());
	}

	public void showMeta(ObN obN) {
		map = onLocal.lireMetadonnees(obN.getIdU(), ccfn.getIdCCFN(),
				utiS.getIdUti(), obN.getConteneur().getIdCont());
		if ((Boolean) map.get("status")) {
			System.out.println("showMeta");
			metadonnees = new Metadonnees((Integer) map.get("idU"),
					(Integer) map.get("uti"), (Integer) map.get("cont"),
					(Integer) map.get("idOnUti"), map.get("size").toString(),
					map.get("date").toString(), map.get("algo").toString(), map
							.get("hash").toString(), map.get("tags").toString());
			metadonnees.setObN(obN);
			conteneur = conteneurLocal.getConteneurByIdConteneur(metadonnees
					.getIdCont());
			tag = metadonnees.getTags().split(",");
			Log log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idU").toString(),
					"lire metadonnees", map.get("date").toString(), "Succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			RequestContext.getCurrentInstance().execute(
					"PF('metaDialog').show();");

		} else if (map.get("cause").equals("denied")) {
			Log log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idU").toString(),
					"lire metadonnees", map.get("date").toString(),
					"Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			deniedMessage = "Vous n'avez pas le droit de visualiser les métadonnées. Contactez l'administrateur";
			RequestContext.getCurrentInstance().execute(
					"PF('deniedDialog').show();");
		} else {
			Log log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idU").toString(),
					"lire metadonnees", map.get("date").toString(),
					"Erreur inconnue");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
		}

	}

	public void controler(ObN obN) {
		map = onLocal
				.controler(obN.getIdU(), ccfn.getIdCCFN(), utiS.getIdUti());
		Log log;
		if (((Boolean) map.get("status")) && ((Boolean) map.get("match"))) {
			success = true;
			statusOp = "Votre document est 100% intègre";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idu").toString(),
					"controler", map.get("date").toString(), "Succès");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
		} else if (!(Boolean) map.get("match")) {
			fail = true;
			statusOp = "Votre document a été altéré, veuillez contacter l'administrateur";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idu").toString(),
					"controler", map.get("date").toString(),
					"incorrespondence d'empreintes");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
		} else if (map.get("cause").equals("denied")) {
			denied = true;
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idu").toString(),
					"controler", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
			deniedMessage = "Vous n'avez pas le droit de contrôler des documents. Contactez l'administrateur";
			RequestContext.getCurrentInstance().execute(
					"PF('deniedDialog').show();");
		} else {
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
			log = new Log(ccfn.getIdCCFN(), (Integer) map.get("cont"),
					(Integer) map.get("uti"), map.get("idu").toString(),
					"controler", map.get("date").toString(), "Erreur inconnue");
			logLocal.addLog(log);
			logs.add(log);
			Collections.sort(logs, Log.logComparator);
		}
	}

	public String findFileNameByIdu(int idU) {
		for (ObN o : obNs) {
			if (o.getIdU() == idU) {
				// if(o.getLibelle().length()>10)
				// {
				// return o.getLibelle().substring(0, 9)+"...";
				// }
				return o.getLibelle();
			}
		}
		return "";
	}

	public DonutChartModel createDonutModel() {

		donutModel = new DonutChartModel();
		Map<String, Number> circle1 = new HashMap<String, Number>();
		if (conteneurs != null) {
			for (Conteneur c : conteneurs) {
				circle1.put(
						c.getLibelle(),
						onLocal.getONBbyConteneurIdAndUser(c.getIdCont(),
								utiS.getIdUti()).size());
			}
		} else {
			circle1.put("Vide", 0);
		}
		donutModel.addCircle(circle1);
		return donutModel;
	}

	public PieChartModel createPieModel() {

		pieModel = new PieChartModel();
		if (conteneurs != null) {
			for (Conteneur c : conteneurs) {
				pieModel.set(
						c.getLibelle(),
						onLocal.getONBbyConteneurIdAndUser(c.getIdCont(),
								utiS.getIdUti()).size());
			}
		} else {
			pieModel.set("Vide", 0);
		}
		return pieModel;
	}

	public String getLibelle(String libelle) {
		int i = libelle.lastIndexOf(".");
		String l = libelle.substring(0, i);
		return l;
	}

	public void resetDeniedDialog(CloseEvent event) {
		System.out.println("reset denied");
		denied = false;
	}

	public void resetDepotDialog(CloseEvent event) {
		System.out.println("reset depot dialog");
		selectedConteneur = new Conteneur();
		tags = "";
		inputStreams = new ArrayList<InputStream>();
		filesToUpload = new ArrayList<UploadedFile>();
		fail = false;
		success = false;
		statusOp = "";
		statusS = "";
		statusF = "";
	}

	public void resetDeleteDialog(CloseEvent event) {
		System.out.println("reset delete dialog");
		fail = false;
		success = false;
		statusOp = "";
		System.out.println("*" + fail + "*" + success);
		// RequestContext.getCurrentInstance().update("formDelete");
	}

	public void resetMetaDialog(CloseEvent event) {
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

	public String renameDoc(String libelle) {
		int i = 1;
		for (ObN o : obNs) {
			if (o.getLibelle().equals(libelle)) {
				int index = libelle.lastIndexOf(".");
				String fileName = libelle.substring(0, index);
				String extension = libelle.substring(index, libelle.length());
				libelle = fileName + "(" + i + ")" + extension;
				renameDoc(libelle);
				i++;
			}
		}
		return libelle;
	}
	
	public TreeNode createDocuments() {
        TreeNode root = new DefaultTreeNode(new Document("Files", "-", "Folder"), null);
         
        for (Conteneur c : conteneurs) {
        	new DefaultTreeNode(new Document(c.getLibelle(), "-", "Folder"), root);
		}
         
        return root;
    }
	
	public void onNodeSelect(NodeSelectEvent event) {
		System.out.println("node selectionné : "+event.getTreeNode().toString());
		int idConteneur = 0;
		for (Conteneur c : conteneurs) {
			if(c.getLibelle().equals(event.getTreeNode().toString())) {
				
				System.out.println("id du conteneur selectionné : "+c.getIdCont());
				idConteneur = c.getIdCont();
				break;
			}
		}
		listFilesByConteneur(idConteneur);
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

	public String getStatusS() {
		return statusS;
	}

	public void setStatusS(String statusS) {
		this.statusS = statusS;
	}

	public String getStatusF() {
		return statusF;
	}

	public void setStatusF(String statusF) {
		this.statusF = statusF;
	}

	public String getDeniedMessage() {
		return deniedMessage;
	}

	public void setDeniedMessage(String deniedMessage) {
		this.deniedMessage = deniedMessage;
	}

	public int getIdSharedObn() {
		return idSharedObn;
	}

	public void setIdSharedObn(int idSharedObn) {
		this.idSharedObn = idSharedObn;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

}
