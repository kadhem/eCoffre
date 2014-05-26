package edu.esprit.eCoffreEJB.interfaces;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import edu.esprit.eCoffreEJB.Entities.ObN;

@Local
@Path("/on")
public interface IONLocal {

	public Map<String, Object> deposerOnAvecControle(InputStream in,
			String libelle, int idCCFN, int idUti, int idOnUti,
			int idConteneur, String algo, StringBuffer empreinte);

	public Map<String, Object> deposerOnSansControle(InputStream in,
			String libelle, int idCCFN, int idUti, int idOnUti, int idConteneur);

	public Map<String, Object> lireON(int idU, int idCCFN, int idUti,
			int idConteneur);

	public Map<String, Object> detruireON(int idU, int idCCFN, int idUti);

	public Map<String, Object> lireMetadonnees(int idU, int idCCFN, int idUti,
			int idConteneur);

	public Map<String, Object> Lister(int idU, int idCCFN, int idUti,
			String[] date, int[] idOnUti, int idCont);

	public Map<String, Object> controler(int idU, int idCCFN, int idUti);

	public Map<String, Object> compter(int idCCFN, int idUti, Date[] date,
			int[] idOnUti);

	public void getOnByUser();

	public List<ObN> getONBbyConteneurIdAndUser(int idConteneur, int idUtiS);

	public boolean modifierON(ObN obN);

	public ObN getONByIdu(int idU);

	public InputStream getInputStreamON(String fileName, String userName);

	public void deleteDir(String dir);

	void test(InputStream in);

	public List<ObN> getONBbyConteneur(int idConteneur);
	
	public Long getCountAllObn();
	
	@GET
	@Path("/test")
	@Produces("text/plain")
	public String test();

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public String uploadRest(MultipartFormDataInput input, @QueryParam("idUti") int idUti, @QueryParam("idCont") int idCont, 
			@QueryParam("algo") String algo, @QueryParam("hash") StringBuffer hash);
	
	@GET
	@Path("/docs")
	@Produces("application/json")
	public List<ObN> listDocs(@QueryParam("idU")int idU, @QueryParam("idUti")int idUti,
			@QueryParam("minD")String minDate,  @QueryParam("maxD")String maxDate, 
			@QueryParam("minId")int minId, @QueryParam("maxId")int maxId, @QueryParam("idCont")int idCont);

	@GET
	@Path("download")
	@Produces("*/*")
	public InputStream downloadRest(@QueryParam("idUti") int idUti, @QueryParam("idU") int idU);
	
	@GET
	@Path("show")
	@Produces("*/*")
	public InputStream showRest(@QueryParam("idUti") int idUti, @QueryParam("idU") int idU);
	
}
