package edu.esprit.eCoffreEJB.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.jcraft.jsch.SftpException;

import edu.esprit.eCoffreEJB.Entities.Conteneur;
import edu.esprit.eCoffreEJB.Entities.Log;
import edu.esprit.eCoffreEJB.Entities.Metadonnees;
import edu.esprit.eCoffreEJB.Entities.ObN;
import edu.esprit.eCoffreEJB.Entities.PartageON;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.HashFile;
import edu.esprit.eCoffreEJB.Technique.SFTPCom;
import edu.esprit.eCoffreEJB.interfaces.ICCFNLocal;
import edu.esprit.eCoffreEJB.interfaces.IConteneurLocal;
import edu.esprit.eCoffreEJB.interfaces.ILogLocal;
import edu.esprit.eCoffreEJB.interfaces.IMetadonneesLocal;
import edu.esprit.eCoffreEJB.interfaces.IONLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

/**
 * Session Bean implementation class ONManagement
 */
@Stateless
@LocalBean
public class ONManagement implements IONLocal {

	@PersistenceContext(unitName = "data")
	private EntityManager entityManager;
	@EJB
	private IConteneurLocal coManagement;
	@EJB
	private ICCFNLocal ccfnManagement;
	@EJB
	private IMetadonneesLocal metaManagement;
	@EJB
	private SFTPCom sftpCom;
	@EJB
	private IUtiSLocal utiSManagement;
	@EJB
	private IPartageLocal partageLocal;
	@EJB
	private IMetadonneesLocal metaLocal;
	@EJB
	private ILogLocal logLocal;

	private InputStream inFromRest;

	/**
	 * Default constructor.
	 */
	public ONManagement() {
	}

	@Override
	public void test(InputStream in) {
		System.out.println("class : " + in.getClass());
		// OutputStream outputStream = null;
		// try {
		// outputStream = new FileOutputStream(new File("d:/testnew1.txt"));
		// int read = 0;
		// byte[] bytes = new byte[1024];
		//
		// while ((read = in.read(bytes)) != -1) {
		// outputStream.write(bytes, 0, read);
		// }
		//
		// System.out.println("Done!");
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// outputStream.close();
		// in.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("fin");
	}

	@Override
	public Map<String, Object> deposerOnAvecControle(final InputStream in,
			String libelle, int idCCFN, int idUti, int idOnUti,
			int idConteneur, String algo, StringBuffer hash) {

		System.out.println("libelle du document : " + libelle);
		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		if (utiS.getProfil().isDeposer()) {
			HashFile hashClass = new HashFile();
			StringBuffer hashfile = null;
			// try {
			// if (in instanceof FileInputStream) {
			// ((FileInputStream) in).getChannel().position(0);
			// } else {
			// in.reset();
			// }
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			if (algo.equals("sha1")) {
				if (inFromRest != null) {
					hashfile = hashClass.getEncryptedFileSHA1(inFromRest);
				} else {
					hashfile = hashClass.getEncryptedFileSHA1(in);
				}

			}

			if (algo.equals("sha-256")) {
				if (inFromRest != null) {
					hashfile = hashClass.getEncryptedFileSHA256(inFromRest);
				} else {
					hashfile = hashClass.getEncryptedFileSHA256(in);
				}
			}

			if (algo.equals("sha-512")) {
				if (inFromRest != null) {
					hashfile = hashClass.getEncryptedFileSHA512(inFromRest);
				} else {
					hashfile = hashClass.getEncryptedFileSHA512(in);
				}
			}

			inFromRest = null;

			if (hash.toString().equals(hashfile.toString())) {
				map.put("match", true);
			} else {
				System.out.println(hash);
				System.out.println(hashfile);
				map.put("status", false);
				map.put("match", false);
				return map;
			}

			try {
				if (in instanceof FileInputStream) {
					((FileInputStream) in).getChannel().position(0);
				} else if (in instanceof SequenceInputStream) {

				} else {
					in.reset();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ObN obN = null;
				DateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				if (sftpCom.uploadFile(in, libelle)) {
					System.out.println("if");
					Conteneur conteneur = coManagement
							.getConteneurByIdConteneur(idConteneur);
					obN = new ObN(idOnUti, libelle);
					obN.linkONToConteneur(conteneur);
					obN.linkONToUtiS(utiS);
					entityManager.persist(obN);
					entityManager.flush();
					long size = sftpCom.getFileSize(libelle);
					System.out.println("sizeeeee : "+size);
					map.put("idu", obN.getIdU());
					map.put("ccfn", idCCFN);
					map.put("cont", idConteneur);
					map.put("uti", idUti);
					map.put("idOnUti", idOnUti);
					map.put("size", size);
					map.put("algo", algo);
					map.put("hash", hashfile);
					map.put("status", true);
					map.put("date", df.format(new Date()));
					return map;
				}
				System.out.println("else");
				map.put("idu", -1);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", idOnUti);
				map.put("size", -1);
				map.put("algo", algo);
				map.put("hash", hashfile);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				System.out.println("catch");
				e.printStackTrace();
				map.put("idu", -1);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", idOnUti);
				map.put("size", -1);
				map.put("algo", algo);
				map.put("hash", hashfile);
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		}
		System.out.println("final");
		map.put("idu", -1);
		map.put("ccfn", idCCFN);
		map.put("cont", idConteneur);
		map.put("uti", idUti);
		map.put("idOnUti", idOnUti);
		map.put("size", -1);
		map.put("algo", algo);
		map.put("hash", hash);
		map.put("status", false);
		map.put("cause", "denied");
		return map;
	}

	@Override
	public Map<String, Object> deposerOnSansControle(final InputStream in,
			String libelle, int idCCFN, int idUti, int idOnUti, int idConteneur) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isDeposer()) {
			try {
				ObN obN = null;
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				if (sftpCom.uploadFile(in, libelle)) {
					Conteneur conteneur = coManagement
							.getConteneurByIdConteneur(idConteneur);
					obN = new ObN(idOnUti, libelle);
					obN.linkONToConteneur(conteneur);
					obN.linkONToUtiS(utiS);
					entityManager.persist(obN);
					entityManager.flush();
					long size = sftpCom.getFileSize(libelle);
					map.put("idu", obN.getIdU());
					map.put("ccfn", idCCFN);
					map.put("cont", idConteneur);
					map.put("uti", idUti);
					map.put("idOnUti", idOnUti);
					map.put("size", size);
					map.put("algo", null);
					map.put("hash", null);
					map.put("status", true);
					map.put("date", df.format(new Date()));
					return map;
				}
				map.put("idu", -1);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", idOnUti);
				map.put("size", -1);
				map.put("algo", null);
				map.put("hash", null);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				map.put("idu", -1);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", idOnUti);
				map.put("size", -1);
				map.put("algo", null);
				map.put("hash", null);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			map.put("idu", -1);
			map.put("ccfn", idCCFN);
			map.put("cont", idConteneur);
			map.put("uti", idUti);
			map.put("idOnUti", idOnUti);
			map.put("size", -1);
			map.put("algo", null);
			map.put("hash", null);
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}

	}

	@Override
	public Map<String, Object> lireON(int idU, int idCCFN, int idUti,
			int idConteneur) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isLire()) {
			try {
				System.out.println("iduuuuuu : " + idU);
				ObN obN = getONByIdu(idU);
				Metadonnees metadonnees = metaManagement.getMetadonnesByON(obN);
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				InputStream in = sftpCom.getInputStreamFile(obN.getLibelle());
				if (in != null) {
					long size = sftpCom.getFileSize(obN.getLibelle());
					map.put("idu", idU);
					map.put("ccfn", idCCFN);
					map.put("cont", idConteneur);
					map.put("uti", idUti);
					map.put("idOnUti", obN.getIdUONUti());
					map.put("size", size);
					map.put("algo", metadonnees.getAlgo());
					map.put("hash", metadonnees.getHash());
					map.put("status", true);
					map.put("data", in);
					map.put("date", df.format(new Date()));
					return map;
				}
				map.put("idu", idU);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", obN.getIdUONUti());
				map.put("size", -1);
				map.put("algo", metadonnees.getAlgo());
				map.put("hash", metadonnees.getHash());
				map.put("status", true);
				map.put("data", in);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				map.put("idu", idU);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", -1);
				map.put("size", -1);
				map.put("algo", "");
				map.put("hash", "");
				map.put("data", null);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			map.put("idu", idU);
			map.put("ccfn", idCCFN);
			map.put("uti", idUti);
			map.put("idOnUti", -1);
			map.put("size", -1);
			map.put("algo", "");
			map.put("hash", "");
			map.put("data", null);
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public Map<String, Object> detruireON(int idU, int idCCFN, int idUti) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isDetruire()) {
			try {
				ObN obN = getONByIdu(idU);
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				if (sftpCom.deleteFile(obN.getLibelle())) {
					System.out.println("obn idu :" + obN.getIdU());
					List<PartageON> pOns = partageLocal.getSharesIdObn(obN
							.getIdU());
					for (PartageON partageON : pOns) {
						System.out.println("d5al lil for : idPartage : "
								+ partageON.getPartage().getIdPartage());
						partageON.setObN(null);
						partageLocal.deleteSharedObn(partageON.getPartage()
								.getIdPartage(), obN.getIdU());
					}
					entityManager.remove(obN);

					map.put("idu", obN.getIdU());
					map.put("ccfn", idCCFN);
					map.put("cont", obN.getConteneur().getIdCont());
					map.put("uti", idUti);
					map.put("idOnUti", obN.getIdUONUti());
					map.put("status", true);
					map.put("date", df.format(new Date()));
					return map;
				}
				map.put("idu", obN.getIdU());
				map.put("ccfn", idCCFN);
				map.put("cont", obN.getConteneur().getIdCont());
				map.put("uti", idUti);
				map.put("idOnUti", obN.getIdUONUti());
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				map.put("idu", idU);
				map.put("ccfn", idCCFN);
				map.put("cont", -1);
				map.put("uti", idUti);
				map.put("idOnUti", -1);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			System.out.println("denied from onmanagement");
			map.put("idu", idU);
			map.put("ccfn", idCCFN);
			map.put("cont", -1);
			map.put("uti", idUti);
			map.put("idOnUti", -1);
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public Map<String, Object> lireMetadonnees(int idU, int idCCFN, int idUti,
			int idConteneur) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isLireMetaDonnees()) {
			try {
				ObN obN = getONByIdu(idU);
				Metadonnees metadonnees = metaManagement.getMetadonnesByON(obN);
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				long size = 0;
				size = sftpCom.getFileSize(obN.getLibelle());
				if (size > 0) {
					map.put("idU", obN.getIdU());
					map.put("ccfn", idCCFN);
					map.put("cont", idConteneur);
					map.put("uti", idUti);
					map.put("idOnUti", obN.getIdUONUti());
					map.put("size", size);
					map.put("algo", metadonnees.getAlgo());
					map.put("hash", metadonnees.getHash());
					map.put("tags", metadonnees.getTags());
					map.put("status", true);
					map.put("date", df.format(new Date()));
					return map;
				}
				map.put("idU", obN.getIdU());
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", obN.getIdUONUti());
				map.put("size", size);
				map.put("algo", metadonnees.getAlgo());
				map.put("hash", metadonnees.getHash());
				map.put("tags", metadonnees.getTags());
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				map.put("idU", idU);
				map.put("ccfn", idCCFN);
				map.put("cont", idConteneur);
				map.put("uti", idUti);
				map.put("idOnUti", -1);
				map.put("size", -1);
				map.put("algo", "");
				map.put("hash", "");
				map.put("tags", "");
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			map.put("idU", idU);
			map.put("ccfn", idCCFN);
			map.put("cont", idConteneur);
			map.put("uti", idUti);
			map.put("idOnUti", -1);
			map.put("size", -1);
			map.put("algo", "");
			map.put("hash", "");
			map.put("tags", "");
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public Map<String, Object> controler(int idU, int idCCFN, int idUti) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		HashFile sh = new HashFile();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isControler()) {
			try {
				ObN obN = getONByIdu(idU);
				Metadonnees metadonnees = metaManagement.getMetadonnesByON(obN);
				sftpCom.createContext();
				sftpCom.changeDir(utiS.getUserName());
				System.out.println("0");
				if (sftpCom.fileExist(obN.getLibelle())) {
					System.out.println("1");
					System.out.println("------1" + metadonnees.getHash());
					System.out.println("------2"
							+ sh.getEncryptedFileSHA512(sftpCom
									.getInputStreamFile(obN.getLibelle())));
					if (metadonnees.getHash()
							.equals(sh
									.getEncryptedFileSHA512(
											sftpCom.getInputStreamFile(obN
													.getLibelle())).toString())) {
						System.out.println("seu pon");
						map.put("idu", obN.getIdU());
						map.put("ccfn", idCCFN);
						map.put("cont", obN.getConteneur().getIdCont());
						map.put("uti", idUti);
						map.put("idOnUti", obN.getIdUONUti());
						map.put("cont",obN.getConteneur().getIdCont());
						map.put("status", true);
						map.put("dateDepot", metadonnees.getDate_fin_depot());
						map.put("match", true);
						map.put("date", df.format(new Date()));
						return map;
					}
					map.put("idu", obN.getIdU());
					map.put("ccfn", idCCFN);
					map.put("cont", obN.getConteneur().getIdCont());
					map.put("uti", idUti);
					map.put("idOnUti", obN.getIdUONUti());
					map.put("cont",obN.getConteneur().getIdCont());
					map.put("dateDepot", metadonnees.getDate_fin_depot());
					map.put("date", df.format(new Date()));
					map.put("status", true);
					map.put("match", false);
					return map;
				}
				map.put("idu", obN.getIdU());
				map.put("ccfn", idCCFN);
				map.put("cont", obN.getConteneur().getIdCont());
				map.put("uti", idUti);
				map.put("idOnUti", obN.getIdUONUti());
				map.put("cont",obN.getConteneur().getIdCont());
				map.put("dateDepot", metadonnees.getDate_fin_depot());
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				map.put("idu", idU);
				map.put("ccfn", idCCFN);
				map.put("cont", -1);
				map.put("uti", idUti);
				map.put("idOnUti", -1);
				map.put("cont",-1);
				map.put("dateDepot", "");
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			map.put("idu", idU);
			map.put("ccfn", idCCFN);
			map.put("cont", -1);
			map.put("uti", idUti);
			map.put("idOnUti", -1);
			map.put("cont",-1);
			map.put("dateDepot", "");
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public Map<String, Object> Lister(int idU, int idCCFN, int idUti,
			String[] date, int[] idOnUti, int idCont) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isLister()) {
			System.out.println("access");
			List<ObN> obNs = getIdus(idU, idCCFN, idUti, date, idOnUti, idCont);
			try {
				if (idU > 0) {
					ObN obN = obNs.get(0);
					map.put("cont", obN.getConteneur().getIdCont());
				} else if (idCont > 0) {
					map.put("cont",idCont);
				} else {
					map.put("cont", -1);
				}
				map.put("ccfn", idCCFN);
				map.put("uti", idUti);
				map.put("status", true);
				map.put("data", obNs);
				map.put("date", df.format(new Date()));
				return map;
			} catch (Exception e) {
				e.printStackTrace();
				if (idU > 0) {
					ObN obN = obNs.get(0);
					map.put("cont", obN.getConteneur().getIdCont());
				} else if (idCont > 0) {
					map.put("cont",idCont);
				} else {
					map.put("cont", -1);
				}
				map.put("ccfn", idCCFN);
				map.put("uti", idUti);
				map.put("data", obNs);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			if (idCont > 0) {
				map.put("cont",idCont);
			} else {
				map.put("cont", -1);
			}
			System.out.println("denied");
			map.put("ccfn", idCCFN);
			map.put("uti", idUti);
			map.put("data", null);
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public Map<String, Object> compter(int idCCFN, int idUti, Date[] date,
			int[] idOnUti) {

		UTI_S utiS = utiSManagement.getUtiSById(idUti);
		Map<String, Object> map = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if (utiS.getProfil().isCompter()) {
			sftpCom.createContext();
			sftpCom.changeDir(utiS.getUserName());
			try {
				int count = sftpCom.countFile();
				List<Integer> idus = getCountON(idCCFN, idUti, date, idOnUti);
				map.put("ccfn", idCCFN);
				map.put("uti", idUti);
				map.put("status", true);
				map.put("data", idus);
				map.put("count", count);
				map.put("date", df.format(new Date()));
				return map;
			} catch (SftpException e) {
				e.printStackTrace();
				map.put("ccfn", idCCFN);
				map.put("uti", idUti);
				map.put("data", null);
				map.put("count", -1);
				map.put("date", df.format(new Date()));
				map.put("cause", "error");
				map.put("status", false);
				return map;
			}
		} else {
			map.put("ccfn", idCCFN);
			map.put("uti", idUti);
			map.put("data", null);
			map.put("count", -1);
			map.put("date", df.format(new Date()));
			map.put("status", false);
			map.put("cause", "denied");
			return map;
		}
	}

	@Override
	public InputStream getInputStreamON(String fileName, String userName) {
		sftpCom.createContext();
		sftpCom.changeDir(userName);
		return sftpCom.getInputStreamFile(fileName);
	}

	@Override
	public void deleteDir(String dir) {
		sftpCom.createContext();
		sftpCom.removeDir(dir);
	}

	@Override
	public void getOnByUser() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObN> getONBbyConteneur(int idConteneur) {
		Query query = entityManager
				.createQuery("select o from ObN o where o.conteneur.idCont=:idConteneur");
		query.setParameter("idConteneur", idConteneur);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObN> getONBbyConteneurIdAndUser(int idConteneur, int idUtiS) {
		Query query = entityManager
				.createQuery("select o from ObN o where o.utiS.idUti=:idUtiS and o.conteneur.idCont=:idConteneur");
		query.setParameter("idConteneur", idConteneur);
		query.setParameter("idUtiS", idUtiS);
		return query.getResultList();
	}

	@Override
	public ObN getONByIdu(int idU) {
		return entityManager.find(ObN.class, idU);
	}

	@Override
	public boolean modifierON(ObN obN) {
		try {
			entityManager.merge(obN);
			return true;
		} catch (Exception e) {

			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public List<Integer> getCountON(int idCCFN, int idUti, Date[] date,
			int[] idOnUti) {
		Query query2 = null;
		String query = "select o.IDU from ObN o "
				+ "join o.metadonnees m "
				+ "join o.conteneur c where m.idUti=:idUti and c.ccfn.idCCFN=:idCCFN and o.conteneur.idCont=c.idCont";
		query2 = entityManager.createQuery(query);
		if (date.length != 0) {
			query.concat(" and m.date_fin_depot>=:date1 and m.date_fin_depot<=:date2");
			query2.setParameter("date1", date[0]);
			query2.setParameter("date2", date[1]);
		}
		if (idOnUti.length != 0) {
			query.concat(" and m.idONUti>=idOnUti1 and m.idONUti<=idOnUti2");
			query2.setParameter("idOnUti1", idOnUti[0]);
			query2.setParameter("idOnUti2", idOnUti[1]);
		}
		System.out.println("query : " + query);
		query2 = entityManager.createQuery(query);
		query2.setParameter("idCCFN", idCCFN);
		query2.setParameter("idUti", idUti);

		return query2.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ObN> getIdus(int idU, int idCCFN, int idUti, String[] date,
			int[] idOnUti, int idCont) {

		Boolean cont = false, idu = false, dat = false, idonuti = false;
		String q = "select o from ObN o join o.metadonnees m join o.conteneur c where m.idUti=:idUti and c.ccfn.idCCFN=:idCCFN";
		Query query = null;

		if (idU != 0) {
			idu = true;
		}
		if (idCont != 0) {
			cont = true;
		}
		if (date != null) {
			dat = true;
		}
		if (idOnUti != null) {
			idonuti = true;
		}
		
		if(cont){ q = q + " and o.conteneur.idCont=c.idCont and c.idCont=:idCont"; }
		if(idu) { q += " and o.idU=:idU and o.idU=m.obN.idU"; } else { q = q + " and o.idU=m.obN.idU"; }
		if(dat) { q+=" and m.date_fin_depot>=:date1 and m.date_fin_depot<=:date2"; }
		if(idonuti) { q+=" and m.idONUti>=:idOnUti1 and m.idONUti<=:idOnUti2"; }
		q += " order by m.date_fin_depot desc";
		
		query = entityManager.createQuery(q);
		
		if(cont){ query.setParameter("idCont", idCont); }
		if(idu) { query = query.setParameter("idU", "%" + String.valueOf(idU) + "%"); }
		if(dat) { query.setParameter("date1", date[0]);	query.setParameter("date2", date[1]); }
		if(idonuti) { query.setParameter("idOnUti1", idOnUti[0]);query.setParameter("idOnUti2", idOnUti[1]); }
		
		System.out.println("query : " + q);
		query.setParameter("idCCFN", idCCFN);
		query.setParameter("idUti", idUti);
		return query.getResultList();
	}

	@Override
	public Long getCountAllObn() {
		return (Long) entityManager.createQuery("select count(idU) from ObN o")
				.getSingleResult();
	}

	@Override
	public String test() {
		return "test test 1 2 1 2";
	}

	@Override
	public String uploadRest(MultipartFormDataInput input, int idUti,
			int idCont, String algo, StringBuffer hash) {

		String fileName = "";
		int idCCFN = ccfnManagement.getCCFN().getIdCCFN();
		Log log;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("fileUpload");
		InputStream inputStream;

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				System.out.println("header = " + header.toString());
				fileName = parseFileName(header);

				System.out.println("filename is " + fileName + " idUti : "
						+ idUti + " idCont" + idCont + " algo : " + algo
						+ " hash " + hash);

				// convert the uploaded file to inputstream
				inputStream = inputPart.getBody(InputStream.class, null);
				inFromRest = inputPart.getBody(InputStream.class, null);
				// System.out.println("hash from input " +new
				// HashFile().getEncryptedFileSHA512(inputStream));
				Map<String, Object> map = deposerOnAvecControle(inputStream,
						fileName, idCCFN, idUti, 0, idCont, algo, hash);
				if ((Boolean) map.get("status")) {
					Metadonnees metadonnees = new Metadonnees(
							(Integer) map.get("idu"), (Integer) map.get("uti"),
							(Integer) map.get("cont"),
							(Integer) map.get("idOnUti"), map.get("size")
									.toString(), map.get("date").toString(),
							map.get("algo").toString(), map.get("hash")
									.toString(), "");
					metaLocal.ajouterMetadonnees(metadonnees);
					
					System.out.println("sizeeeee 2 : "+map.get("size"));
					
					log = new Log(idCCFN, (Integer) map.get("cont"),
							(Integer) map.get("uti"),
							map.get("idu").toString(), "deposer", map.get(
									"date").toString(), "Succès", map.get(
									"algo").toString(), map.get("hash")
									.toString(), map.get("size").toString(),
							null);
					logLocal.addLog(log);
				} else if (map.get("cause").equals("denied")) {
					log = new Log(idCCFN, (Integer) map.get("cont"),
							(Integer) map.get("uti"),
							map.get("idu").toString(), "deposer", map.get(
									"date").toString(), "Erreur inconnu", map
									.get("algo").toString(), map.get("hash")
									.toString());
					logLocal.addLog(log);
				} else if (!(Boolean) map.get("match")) {
					log = new Log(idCCFN, (Integer) map.get("cont"),
							(Integer) map.get("uti"),
							map.get("idu").toString(), "deposer", map.get(
									"date").toString(),
							"incorrespondence d'empreintes", map.get("algo")
									.toString(), map.get("hash").toString());
					logLocal.addLog(log);
				} else {
					log = new Log(idCCFN, (Integer) map.get("cont"),
							(Integer) map.get("uti"),
							map.get("idu").toString(), "deposer", map.get(
									"date").toString(), "Erreur inconnu", map
									.get("algo").toString(), map.get("hash")
									.toString());
					logLocal.addLog(log);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	// Parse Content-Disposition header to get the original file name
	private String parseFileName(MultivaluedMap<String, String> headers) {

		String[] contentDispositionHeader = headers.getFirst(
				"Content-Disposition").split(";");
		for (String name : contentDispositionHeader) {
			if ((name.trim().startsWith("filename"))) {
				String[] tmp = name.split("=");
				String fileName = tmp[1].trim().replaceAll("\"", "");
				return fileName;
			}
		}
		return "randomName";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ObN> listDocs(int idU, int idUti, String minDate,
			String maxDate, int minId, int maxId, int idCont) {
		Map<String, Object> map = new HashMap<String, Object>();
		int idCCFN = ccfnManagement.getCCFN().getIdCCFN();
		Log log;
		// System.out.println("min date + m+ax date : "+minDate+maxDate+" minId + maxId : "+minId+maxId);
		String[] date = null;
		if (minDate != null && maxDate != null) {
			date = new String[2];
			date[0] = minDate;
			date[1] = maxDate;
		}

		int[] idOnUti = null;
		if (minId != Integer.valueOf("0") && maxId != Integer.valueOf("0")) {
			idOnUti = new int[2];
			idOnUti[0] = minId;
			idOnUti[1] = maxId;
		}

		map = Lister(idU, idCCFN, idUti, date, idOnUti, idCont);
		if((Boolean) map.get("status"))
		{
			List<ObN> obNs = (List<ObN>) map.get("data");
			String idUs = "";
			for (ObN o : obNs) {
				idUs += o.getIdU()+",";
//				System.out.println("*" + o.getLibelle() + "*");
			}
			log = new Log(idCCFN, (Integer) map.get("cont"), (Integer) map.get("uti"), idUs
					, "lister", map.get("date").toString(), "Succès");
			logLocal.addLog(log);
			System.out.println("log added");
		} else if(map.get("cause").equals("denied")){
			log = new Log(idCCFN, (Integer) map.get("cont"), (Integer) map.get("uti"), "vide"
					, "lister", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
		} else {
			log = new Log(idCCFN, (Integer) map.get("cont"), (Integer) map.get("uti"), "vide"
					, "lister", map.get("date").toString(), "Erreur inconnue");
			logLocal.addLog(log);
		}
		return (List<ObN>) map.get("data");
	}

	@Override
	public InputStream downloadRest(@QueryParam("idUti") int idUti,
			@QueryParam("idU") int idU) {

		Log log;
		System.out.println("idUti :" + idUti + " idU : " + idU);
		Map<String, Object> map = new HashMap<String, Object>();
		int idCCFN = ccfnManagement.getCCFN().getIdCCFN();
		map = lireON(idU, idCCFN, idUti, 0);
		if ((Boolean) map.get("status")) {
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "Succès");
			logLocal.addLog(log);
			
		}
		else if(map.get("cause").equals("denied")) {
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
		}
		else {
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "telecharger", map.get("date").toString(), "Erreur inconnu");
			logLocal.addLog(log);
		}
		return (InputStream) map.get("data");
	}

	@Override
	public InputStream showRest(@QueryParam("idUti") int idUti,
			@QueryParam("idU") int idU) {

		Log log;
		System.out.println("idUti :" + idUti + " idU : " + idU);
		Map<String, Object> map = new HashMap<String, Object>();
		int idCCFN = ccfnManagement.getCCFN().getIdCCFN();
		map = lireON(idU, idCCFN, idUti, 0);
		if ((Boolean) map.get("status")) {
			InputStream stream = (InputStream) map.get("data");
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "Succès");
			logLocal.addLog(log);
		}
		else if(map.get("cause").equals("denied")) {
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "Accès refusé");
			logLocal.addLog(log);
		}
		else {
			log = new Log(idCCFN, -1, (Integer) map.get("uti"), map.get("idu").toString()
					, "visualiser", map.get("date").toString(), "Erreur inconnu");
			logLocal.addLog(log);
		}
		return (InputStream) map.get("data");
	}
}
