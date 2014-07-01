package edu.esprit.eCoffreWeb.managedBean;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.CloseEvent;

import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.Log;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesLocal;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

@ManagedBean
@SessionScoped
public class PartageBean implements Serializable{

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
	private IPartageLocal partageLocal;

	private UTI_S utiS;
	private UserBean userBean;
	private List<Partage> partages;
	private List<Partage> allPartages;

	private List<Invite> invites = new ArrayList<Invite>();
	private List<Invite> invitesToDelete = new ArrayList<Invite>();

	private Partage selectedPartage;
	private Partage partageForInviT;

	private List<ObN> obNsForInviT;
	private List<ObN> obNsPartages;
	private List<ObN> obNsNonPartages;
	private List<ObN> ObnsToAdd;
	private List<ObN> ObnsToDelete;

	private String mail;
	private String nom;
	private String description;
	private Date datePartage;
	private Date dateExpiration;

	private String idPartage;
	private String inviT;
	private String statusOp;
	private boolean success;
	private boolean fail;

	private int i = 1;

	public PartageBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String encrypt(String string)
    {
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
	        md.update(string.getBytes("UTF-8"));
	 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	 
	        System.out.println("Hex format : " + sb.toString());
	 
	        //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<byteData.length;i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	System.out.println("Hex format : " + hexString.toString());
	    	return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }

	@PostConstruct
	public void initPartage() {
		try {
			System.out.println("init");
			userBean = (UserBean) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userBean");
			utiS = utiSLocal
					.getUtiSByUserName(userBean.getUser().getUserName());
			partages = partageLocal.getPartagesByIdUti(utiS.getIdUti());
			for (Partage p : partages) {
				System.out.println(p.getDescription() + "**");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String redirectToPartages() {
		userBean = (UserBean) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("userBean");
		utiS = utiSLocal.getUtiSByUserName(userBean.getUser().getUserName());
		partages = partageLocal.getPartagesByIdUti(utiS.getIdUti());
		for (Partage p : partages) {
			System.out.println(p.getDescription() + "**");
		}
		return "mespartages?faces-redirect=true";
	}
	
	public String fileName(String fileName)
	{
				if(fileName.length()>17)
				{
					return fileName.substring(0, 16)+"...";
				}
				return fileName;
	}

	public void getSharedObN() {
		obNsPartages = partageLocal.getSharedObNByIdPartage(selectedPartage
				.getIdPartage());
	}

	public void getNotSharedObN() {
		System.out.println("nonprtg" + selectedPartage.getIdPartage());
		obNsNonPartages = partageLocal
				.getNotSharedObNByIdPartage(selectedPartage.getIdPartage());

	}

	public void getSharedObnForInviT() {
		allPartages = partageLocal.getAllPartages();
		for (Partage p : allPartages) {
			if (encrypt(String.valueOf(p.getIdPartage())).equals(idPartage)) {
				System.out.println("partage trouvé");
				System.out.println(encrypt(String.valueOf(p.getIdPartage())));
				System.out.println(idPartage);
				List<Invite> invites = partageLocal.getInviTByIdPartage(p
						.getIdPartage());
				for (Invite i : invites) {
					System.out.println(i.getEmail());
					System.out.println(encrypt(i.getEmail()));
					System.out.println(inviT);
					if (encrypt(i.getEmail()).equals(inviT)) {
						System.out.println("email trouvé");
						obNsForInviT = partageLocal.getSharedObNByIdPartage(p
								.getIdPartage());
						partageForInviT = p;
						success = true;
						return;
					}
				}
				fail = true;
				statusOp = "Vous n'avez pas le droit de voir le contenu de ce partage";
				return;
			}
		}
		fail = true;
		statusOp = "Partage inexistant ou expiré";
		selectedPartage = null;
		invites = null;
	}

	public void addPartage() {
		try {

			Partage partage = new Partage(nom, description, datePartage,
					dateExpiration);
			partageLocal.addPartage(partage, utiS);
			for (Invite i : invites) {
				i.linkInviteToPartage(partage);
			}
			partage.setInvites(invites);
			partageLocal.editPartage(partage);
			if(partages.add(partage)) {
				Collections.sort(partages, Partage.partageComparator);
				for (Partage p : partages) {
					System.out.println(p.getDescription() + "**");
				}
				if(invites.size()>0)
				{
					if(partageLocal.sendMailToInviT(utiS, invites, partage.getIdPartage())) {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation ont été envoyée avec succès ",  null);
				        FacesContext.getCurrentInstance().addMessage(null, message);
					} else {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation n'ont pas été envoyée. "
								+ "Une erreur est survenue",  null);
				        FacesContext.getCurrentInstance().addMessage(null, message);
					}
				}
				statusOp = "Partage ajouté avec succès";
				success = true;
				description = nom = "";
				datePartage = dateExpiration = null;
				invites = new ArrayList<Invite>();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			fail = true;
			statusOp = "Une erreur est survenue, réessayez plus tard";
		}
	}

	public void selectPartage(Partage partage) {
		System.out.println("selectedPartage");
		System.out.println("*"+partage.getNom());
		statusOp = "";
		fail = false;
		success = false;
		partage.setInvites(partageLocal.getInviTByIdPartage(partage
				.getIdPartage()));
		selectedPartage = partage;
		System.out.println(selectedPartage.getNom()+"*");
	}

	public void editPartage() {
		try {
			for (Invite i : invitesToDelete) {
				partageLocal.deleteInvite(i.getIdInvite());
			}
			for (Invite i : selectedPartage.getInvites()) {
				System.out.println(i.getEmail() + "**");
				i.linkInviteToPartage(selectedPartage);
				mail = i.getEmail();
			}
			
			if(partageLocal.editPartage(selectedPartage))
			{
				Partage ptd = null;
				for (Partage p : partages) {
					if(p==selectedPartage)
					{
						ptd=p;
					}
				}
				if(ptd!=null) {
					partages.set(partages.indexOf(ptd), selectedPartage);
				}
				statusOp = "Partage modifié avec succès";
				success = true;
				if(invites.size()>0)
				{
					if(partageLocal.sendMailToInviT(utiS, invites, selectedPartage.getIdPartage())) {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation ont été envoyée avec succès ",  null);
				        FacesContext.getCurrentInstance().addMessage(null, message);
					} else {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation n'ont pas été envoyée. "
								+ "Une erreur est survenue",  null);
				        FacesContext.getCurrentInstance().addMessage(null, message);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			statusOp = "Une erreur est survenue, réessayez plus tard";
			success = false;
			fail = true;
		}
	}

	public void deletePartage() {
		if(selectedPartage!=null)
		{
			if (partageLocal.deletePartage(selectedPartage.getIdPartage())) {
				partages.remove(selectedPartage);
				success = true;
				statusOp = "Partage supprimé.";
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Le partage a été supprimé ",  null);
		        FacesContext.getCurrentInstance().addMessage(null, message);
			}
			else {
				fail = true;
				statusOp = "Une erreur est survenue";
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Une erreur est survenue, le partage n'a pas été supprimé ",  null);
		        FacesContext.getCurrentInstance().addMessage(null, message);
			}
		}
		else {
			System.out.println("selectedpartage null");
		}
	}

	public void addFileToPartage() {
		for (ObN o : ObnsToAdd) {
			if (partageLocal.shareObN(o.getIdU(),
					selectedPartage.getIdPartage(), new Date())) {
				obNsPartages.add(o);
				obNsNonPartages.remove(o);
			}
		}
		partages = partageLocal.getPartagesByIdUti(utiS.getIdUti());
		System.out.println("fin add file");
	}

	public void deleteFilesFromPartage() {
		for (ObN o : ObnsToDelete) {
			System.out.println("delete obn : " + selectedPartage.getIdPartage()
					+ "**" + o.getIdU());
			if (partageLocal.deleteSharedObn(selectedPartage.getIdPartage(),
					o.getIdU())) {
				obNsNonPartages.add(o);
				obNsPartages.remove(o);
			}
		}
		partages = partageLocal.getPartagesByIdUti(utiS.getIdUti());
	}

	public void resendInvitation(Invite invite) {
		List<Invite> invites = new ArrayList<Invite>();
		invites.add(invite);
		if(partageLocal.sendMailToInviT(utiS, invites,selectedPartage.getIdPartage()))
		{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation ont été envoyée avec succès ",  null);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"Les invitation n'ont pas été envoyée. "
					+ "Une erreur est survenue",  null);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void addInvite() {
		if (!mail.equals("") && mail != null) {
			if (selectedPartage != null) {
				selectedPartage.getInvites().add(new Invite(mail));
				invites.add(new Invite(mail));
			} else {
				invites.add(new Invite(mail));
			}
			mail = "";
		}

	}

	public void deleteInvite(Invite invite) {
		if (selectedPartage != null) {
			invitesToDelete.add(invite);
			selectedPartage.getInvites().remove(invite);
		}
		if (invites.size() > 0) {
			invites.remove(invite);
		}
	}

	public void resetAjoutDialog(CloseEvent event) {
		description = "";
		nom = "";
		statusOp = "";
		datePartage = null;
		dateExpiration = null;
		success = false;
		fail = false;
		invites = new ArrayList<Invite>();

	}

	public void resetEditDialog(CloseEvent event) {
		success = false;
		fail = false;
		statusOp = "";
		selectedPartage = null;
		invitesToDelete = new ArrayList<Invite>();
		invites = new ArrayList<Invite>();
	}

	public void resetDeleteDialog(CloseEvent event) {
		System.out.println("reset delete dialog");
		success = false;
		fail = false;
		statusOp = "";
		selectedPartage = null;
	}

	public void resetAddFileDIalog(CloseEvent event) {
		partages = partageLocal.getPartagesByIdUti(utiS.getIdUti());
		obNsNonPartages = new ArrayList<ObN>();
		obNsPartages = new ArrayList<ObN>();
		ObnsToAdd = new ArrayList<ObN>();
		ObnsToDelete = new ArrayList<ObN>();
	}

	public List<Partage> getPartages() {
		return partages;
	}

	public void setPartages(List<Partage> partages) {
		this.partages = partages;
	}

	public List<Invite> getInvites() {
		return invites;
	}

	public void setInvites(List<Invite> invites) {
		this.invites = invites;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDatePartage() {
		return datePartage;
	}

	public void setDatePartage(Date datePartage) {
		this.datePartage = datePartage;
	}

	public Date getDateExpiration() {
		return dateExpiration;
	}

	public void setDateExpiration(Date dateExpiration) {
		this.dateExpiration = dateExpiration;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getStatusOp() {
		return statusOp;
	}

	public void setStatusOp(String statusOp) {
		this.statusOp = statusOp;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isFail() {
		return fail;
	}

	public void setFail(boolean fail) {
		this.fail = fail;
	}

	public Partage getSelectedPartage() {
		return selectedPartage;
	}

	public void setSelectedPartage(Partage selectedPartage) {
		this.selectedPartage = selectedPartage;
	}

	public List<Invite> getInvitesToDelete() {
		return invitesToDelete;
	}

	public void setInvitesToDelete(List<Invite> invitesToDelete) {
		this.invitesToDelete = invitesToDelete;
	}

	public List<ObN> getObNsPartages() {
		return obNsPartages;
	}

	public void setObNsPartages(List<ObN> obNsPartages) {
		this.obNsPartages = obNsPartages;
	}

	public List<ObN> getObNsNonPartages() {
		return obNsNonPartages;
	}

	public void setObNsNonPartages(List<ObN> obNsNonPartages) {
		this.obNsNonPartages = obNsNonPartages;
	}

	public List<ObN> getObnsToAdd() {
		return ObnsToAdd;
	}

	public void setObnsToAdd(List<ObN> obnsToAdd) {
		ObnsToAdd = obnsToAdd;
	}

	public List<ObN> getObnsToDelete() {
		return ObnsToDelete;
	}

	public void setObnsToDelete(List<ObN> obnsToDelete) {
		ObnsToDelete = obnsToDelete;
	}
	
	public String getIdPartage() {
		return idPartage;
	}

	public void setIdPartage(String idPartage) {
		this.idPartage = idPartage;
	}

	public String getInviT() {
		return inviT;
	}

	public void setInviT(String inviT) {
		this.inviT = inviT;
	}

	public List<Partage> getAllPartages() {
		return allPartages;
	}

	public void setAllPartages(List<Partage> allPartages) {
		this.allPartages = allPartages;
	}

	public List<ObN> getObNsForInviT() {
		return obNsForInviT;
	}

	public void setObNsForInviT(List<ObN> obNsForInviT) {
		this.obNsForInviT = obNsForInviT;
	}

	public Partage getPartageForInviT() {
		return partageForInviT;
	}

	public void setPartageForInviT(Partage partageForInviT) {
		this.partageForInviT = partageForInviT;
	}

}
