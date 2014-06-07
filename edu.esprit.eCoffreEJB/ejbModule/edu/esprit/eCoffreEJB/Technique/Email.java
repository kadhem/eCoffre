package edu.esprit.eCoffreEJB.Technique;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import sun.misc.BASE64Encoder;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;

/**
 * Session Bean implementation class Email
 */
@Stateless
@LocalBean
public class Email implements IEmailLocal {

	@EJB
	IPartageLocal partageLocal;
	private String urlConfirmation = "http://localhost:8383/edu.esprit.eCoffreWeb/confirmation.jsf?id=";
	private String inviT;
	private String partage; 
	private String urlInvitation = "http://localhost:8383/edu.esprit.eCoffreWeb/partages/partage.jsf?i=";
	
	public Email() {
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

	@Override
	public void sendConfirmationMail(UTI_S utiS) {
		// Sender's email ID needs to be mentioned
		String from = "kadhemk@gmail.com";

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		Authenticator authenticator = new Authenticator() {
				private PasswordAuthentication authentication;
					{
						authentication = new PasswordAuthentication(
								"kadhemk@gmail.com", "03071981.");
					}

					protected PasswordAuthentication getPasswordAuthentication() {
						return authentication;
					}
				};
				// Get the default Session object.
				Session session = Session.getInstance(properties, authenticator);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					utiS.getUserName()));

			// Set Subject: header field
			message.setSubject("Confirmation du compte");

			// Now set the actual message
			message.setText("Bonjour <strong>"+utiS.getFirstName()+" "+utiS.getLastName()+"</strong> <br /><br />"
					+ "Merci d'avoir choisi EspritBox. "
					+ "Pour commencer à utiliser EspritBox veuillez cliquer <a href=\""+ urlConfirmation + utiS.getIdUti() + "\"> ici </a>"
					+"<br /><br /> Cordialement", "UTF-8", "html");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

	@Override
	public void sendInvitationMail(UTI_S utiS, List<Invite> invites, int idPartage) {
		
		// Sender's email ID needs to be mentioned
		String from = "kadhemk@gmail.com";

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		Authenticator authenticator = new Authenticator() {
			private PasswordAuthentication authentication;

			{
				authentication = new PasswordAuthentication(
						"kadhemk@gmail.com", "03071981.");
			}

			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		};
		// Get the default Session object.
		Session session = Session.getInstance(properties, authenticator);
		
		for (Invite invite : invites) {
			this.partage = encrypt(String.valueOf(idPartage));
			inviT = encrypt(invite.getEmail());
			
			try {
				// Create a default MimeMessage object.
				MimeMessage message = new MimeMessage(session);

				// Set From: header field of the header.
				message.setFrom(new InternetAddress(from));

				// Set To: header field of the header.
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(
						invite.getEmail()));

				// Set Subject: header field
				message.setSubject("Invitation à un espace de partage");

				// Now set the actual message
				message.setText("<strong>Bonjour</strong> <br /> <br /><strong>"
						+ utiS.getFirstName() + " " + utiS.getLastName() + "</strong> a partagé des fichiers avec vous <br />"
						+ "Pour accèder au partage veuillez cliquer <a href=\""+urlInvitation + inviT + "&p="+this.partage+"\"> ici </a>" +
								"<br /> <br />Cordialement", "UTF-8", "html");

				// Send message
				Transport.send(message);
				System.out.println("Sent message successfully....");

			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
		}
	}

	@Override
	public void sendPreventionPartageMail(List<Invite> invites, int idPartage) {
		
		Partage o = partageLocal.getPartageById(idPartage);
		
		// Sender's email ID needs to be mentioned
		String from = "kadhemk@gmail.com";

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		Authenticator authenticator = new Authenticator() {
			private PasswordAuthentication authentication;

			{
				authentication = new PasswordAuthentication(
						"kadhemk@gmail.com", "03071981.");
			}

			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		};
		// Get the default Session object.
		Session session = Session.getInstance(properties, authenticator);
		
		//send email to inviT
		for (Invite invite : invites) {
			this.partage = encrypt(String.valueOf(idPartage));
			inviT = encrypt(invite.getEmail());
			
			try {
				// Create a default MimeMessage object.
				MimeMessage message = new MimeMessage(session);

				// Set From: header field of the header.
				message.setFrom(new InternetAddress(from));

				// Set To: header field of the header.
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(
						invite.getEmail()));

				// Set Subject: header field
				message.setSubject("Rappel concernant le partage : "+o.getNom());

				// Now set the actual message
				message.setText("<strong>Bonjour</strong> <br /> <br /><strong>EspritBox </strong>veut vous rappeler que le partage " +
						"<strong>"+ o.getNom() +"</strong> partagé par <strong>"+o.getUtiS().getFirstName()+" "+ o.getUtiS().getLastName()
						+"</strong> va bientôt expirer <br />"
						+ "Pour accèder au partage veuillez cliquer <a href=\""+urlInvitation + inviT + "&p="+this.partage+"\"> ici </a>" +
								"<br /> <br />Cordialement", "UTF-8", "html");
				// Send message
				Transport.send(message);
				System.out.println("Sent message successfully....");

			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
		}
		
		//send email to share Owner
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			System.out.println("+++"+o.getUtiS().getUserName());
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(o.getUtiS().getUserName()));

			// Set Subject: header field
			message.setSubject("Rappel concernant le partage : "+o.getNom());
			// Now set the actual message
			message.setText("<strong>Bonjour "+ o.getUtiS().getFirstName()+" "+ o.getUtiS().getLastName()+"</strong> <br /> <br />" +
					"<strong>EspritBox </strong>veut vous rappeler que le partage " +
					"<strong>"+ o.getNom() +"</strong> que vous avez partagé va bientôt expirer <br /><br />Cordialement", "UTF-8", "html");
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	
	@Override
	public void sendPreventionEventnMail(UTI_S utiS, Evenement evenement) {
		// Sender's email ID needs to be mentioned
		String from = "kadhemk@gmail.com";

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.setProperty("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.port", "465");
		Authenticator authenticator = new Authenticator() {
				private PasswordAuthentication authentication;
					{
						authentication = new PasswordAuthentication(
								"kadhemk@gmail.com", "03071981.");
					}

					protected PasswordAuthentication getPasswordAuthentication() {
						return authentication;
					}
				};
				// Get the default Session object.
				Session session = Session.getInstance(properties, authenticator);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					utiS.getUserName()));

			// Set Subject: header field
			message.setSubject("Evénement proche");

			// Now set the actual message
			message.setText("<strong>Bonjour "+utiS.getFirstName()+" "+utiS.getLastName()+"</strong> <br />"
					+ "EspritBox vous rappelle que votre évenement intitulé : "+evenement.getTitre()+" qui a pour description : "+evenement.getDescription()+" <br />"
					+ "commençe à la date suivante : "+evenement.getDebutDate()+"<br /> Cordialement", "UTF-8", "html");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");

		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}
	
}
