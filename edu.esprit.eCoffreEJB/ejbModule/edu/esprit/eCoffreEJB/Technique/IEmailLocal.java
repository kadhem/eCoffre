package edu.esprit.eCoffreEJB.Technique;

import java.util.List;

import javax.ejb.Local;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.Invite;
import edu.esprit.eCoffreEJB.Entities.UTI_S;

@Local
public interface IEmailLocal {

	public void sendConfirmationMail(UTI_S utiS);

	public Boolean sendInvitationMail(UTI_S utiS, List<Invite> invites, int idPartage);

	public void sendPreventionPartageMail(List<Invite> invites, int idPartage);

	public void sendPreventionEventnMail(UTI_S utiS, Evenement evenement);

}
