package edu.esprit.eCoffreEJB.startup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Technique.IEmailLocal;
import edu.esprit.eCoffreEJB.interfaces.IEvenementLocal;

public class ScheduleEventManagement implements Runnable {

	InitialContext ctx;
	IEvenementLocal evenementLocal;
	IEmailLocal emailLocal;

	@Override
	public void run() {
		try {
			ctx = new InitialContext();
			evenementLocal = (IEvenementLocal) ctx
					.lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/EvenementManagement!edu.esprit.eCoffreEJB.interfaces.IEvenementLocal");
			emailLocal = (IEmailLocal) ctx
					.lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/Email!edu.esprit.eCoffreEJB.Technique.IEmailLocal");
		
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ejb not yet deployed");
		}
		checkForEvent();
	}

	public void checkForEvent() {
		List<Evenement> evenements = evenementLocal.getEvents();
		for (Evenement evenement : evenements) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				Date today = new Date();
				Date startDate = evenement.getDebutDate();
				Date endDate = evenement.getFinDate();
				try {
					startDate = sdf.parse(sdf.format(startDate));
					endDate = sdf.parse(sdf.format(endDate));
					today = sdf.parse(sdf.format(today));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar calToday = Calendar.getInstance();
				calToday.setTime(today);
				Calendar calStartDate = Calendar.getInstance();
				calStartDate.setTime(startDate);
				Calendar calEndDate = Calendar.getInstance();
				calEndDate.setTime(endDate);
				calStartDate.add(Calendar.HOUR, -2);
				
				if((calToday.compareTo(calStartDate)>=0) && (calEndDate.compareTo(calToday)>0))
				{
					System.out.println(evenement.getTitre());
					System.out.println("today : "+calToday.getTime() + " start : "+calStartDate.getTime() + " end : "+calEndDate.getTime());
					preventUtiForEvent(evenement);
				}
		}
	}
	
	public void preventUtiForEvent(Evenement evenement)
	{
		emailLocal.sendPreventionEventnMail(evenement.getUtiS(), evenement);
		System.out.println("event : email sent successfully");
	}
	
	public void deletePartage(int idPartage)
	{
		System.out.println("partage deleted");
	}
}
