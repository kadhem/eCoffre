package edu.esprit.eCoffreEJB.interfaces;

import javax.ejb.Remote;

@Remote
public interface IApplicationRemote {

	public void notify_user();
	public void save_log();
	public void getLog_by_user();
	public void getLog_by_ON();
	public void checkForPartage();
	public void deletePartag(int idPartage);
	
}
