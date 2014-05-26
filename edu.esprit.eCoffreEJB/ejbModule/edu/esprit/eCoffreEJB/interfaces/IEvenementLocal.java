package edu.esprit.eCoffreEJB.interfaces;

import java.util.List;

import javax.ejb.Local;

import edu.esprit.eCoffreEJB.Entities.Evenement;
import edu.esprit.eCoffreEJB.Entities.UTI_S;

@Local
public interface IEvenementLocal {

	public boolean addEvent(Evenement evenement, UTI_S utiS);

	public boolean editEvent(Evenement evenement);

	public boolean deleteEvent(int idEvenement);

	public Evenement getEventById(int idEvenement);

	public List<Evenement> getEventsByIdUtis(int idUti);

	public List<Evenement> getEvents();
}
