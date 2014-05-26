package edu.esprit.eCoffreEJB.interfaces;

import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;


@Remote
@Path("/on")
public interface IONRemote{

	@GET
	@Path("/test")
	@Produces("text/plain")
	public String test();

	@POST
	@Path("/in")
	@Consumes("multipart/form-data")
	public Response in(MultipartFormDataInput input);
	
	
	@POST
	@Path("upload")
	@Consumes("*/*")
	public String upload(InputStream in);
}
