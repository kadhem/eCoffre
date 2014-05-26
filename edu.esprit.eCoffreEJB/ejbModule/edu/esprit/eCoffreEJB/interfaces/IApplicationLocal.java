package edu.esprit.eCoffreEJB.interfaces;

import java.text.ParseException;

import javax.ejb.Local;

import org.jboss.netty.util.TimerTask;

@Local
public interface IApplicationLocal {

	public void notify_user();
	public void save_log();
	public void getLog_by_user();
	public void getLog_by_ON();
	public void checkForPartage();
	public void deletePartag(int idPartage);
}
