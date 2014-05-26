package edu.esprit.eCoffreEJB.startup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreEJB.Technique.IEmailLocal;
import edu.esprit.eCoffreEJB.interfaces.IEvenementLocal;
import edu.esprit.eCoffreEJB.interfaces.IUtiSLocal;

public class ScheduleUserManagement implements Runnable {

	InitialContext ctx;
	IUtiSLocal utiSLocal;
	IEmailLocal emailLocal;

	@Override
	public void run() {
		try {
			ctx = new InitialContext();
			utiSLocal = (IUtiSLocal) ctx
					.lookup("java:global/edu.esprit.eCoffre/edu.esprit.eCoffreEJB/UtiSManagement!edu.esprit.eCoffreEJB.interfaces.IUtiSLocal");
		
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ejb not yet deployed");
		}
		checkForInvalidUser();
	}

	public void checkForInvalidUser() {
		List<UTI_S> utS = utiSLocal.getAllUtiS();
		for (UTI_S u : utS) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			Date today = new Date();
			Date addDate = u.getDateAdd();
			try {
				addDate = sdf.parse(sdf.format(addDate));
				today = sdf.parse(sdf.format(today));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calToday = Calendar.getInstance();
			calToday.setTime(today);
			Calendar calAddDate = Calendar.getInstance();
			calAddDate.setTime(addDate);
			calAddDate.add(Calendar.DAY_OF_MONTH, +1);
			if(!u.isValide())
			{
				if( (calToday.compareTo(calAddDate)==0) || (calToday.compareTo(calAddDate)>0) ) {
					deleteUser(u);
				}
			}
			
		}
	}
	
	public void deleteUser(UTI_S utiS)
	{
		utiSLocal.supprimerSimpleUti(utiS);
	}
}
