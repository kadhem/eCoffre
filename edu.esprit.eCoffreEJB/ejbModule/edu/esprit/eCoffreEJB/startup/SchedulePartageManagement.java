package edu.esprit.eCoffreEJB.startup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.Partage;
import edu.esprit.eCoffreEJB.Technique.IEmailLocal;
import edu.esprit.eCoffreEJB.interfaces.IPartageLocal;

public class SchedulePartageManagement implements Runnable {

	InitialContext ctx;
	IPartageLocal partageLocal;
	IEmailLocal emailLocal;

	@Override
	public void run() {
		try {
			ctx = new InitialContext();
			partageLocal = (IPartageLocal) ctx
					.lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/PartageManagement!edu.esprit.eCoffreEJB.interfaces.IPartageLocal");
			emailLocal = (IEmailLocal) ctx
					.lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/Email!edu.esprit.eCoffreEJB.Technique.IEmailLocal");
		
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ejb not yet deployed");
		}
		checkForPartage();
	}

	public void checkForPartage() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<Partage> partages = partageLocal.getAllPartages();
		for (Partage partage : partages) {
			Date today = new Date();
			Date expireDate = partage.getDateExpiration();
			try {
				expireDate = sdf.parse(sdf.format(expireDate));
				today = sdf.parse(sdf.format(today));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calToday = Calendar.getInstance();
			calToday.setTime(today);
			System.out.println("1 : "+calToday.getTime());
			Calendar calExpireDate = Calendar.getInstance();
			calExpireDate.setTime(expireDate);
			System.out.println("2 : "+calExpireDate.getTime());
			if (calExpireDate.compareTo(calToday) <= 0) {
				System.out
						.println("ce partage va être effacé : "+partage.getDescription());
				deletePartage(partage.getIdPartage());
			}
			calExpireDate.add(Calendar.DAY_OF_MONTH, -2);
			System.out.println("Expire date : "+calExpireDate.getTime());

			if (calToday.compareTo(calExpireDate) >= 0) {
				System.out
						.println("il reste 2 jours pour l'expiration du partage: "+partage.getDescription());
				preventInviT(partage.getIdPartage());
			}
		}
	}
	
	public void preventInviT(int idPartage)
	{
		List<Invite> inviT = partageLocal.getInviTByIdPartage(idPartage);
		emailLocal.sendPreventionPartageMail(inviT, idPartage);
		System.out.println("emails sent successfully");
	}
	
	public void deletePartage(int idPartage)
	{
		partageLocal.deletePartage(idPartage);
		System.out.println("partage deleted");
	}
}
