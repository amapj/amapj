/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
 * 
 *  This file is part of AmapJ.
 *  
 *  AmapJ is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  AmapJ is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with AmapJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
 package fr.amapj.service.services.saisiepermanence.planif;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.amapj.view.views.gestioncontrat.editorpart.FrequenceLivraison;


/**
 * Permet la planification des permanences
 * 
 */
public class PlanifDTO 
{
	public Date dateDebut;

	public Date dateFin;
	
	public FrequenceLivraison frequence;
	
	// Nb de personnes par permanence
	public int nbPersonne;
	
	// Peut être null
	public Long idPeriodeCotisation;
	
	//
	public List<PlanifDateDTO> dates = new ArrayList<>();
	
	public List<PlanifUtilisateurDTO> utilisateurs = new ArrayList<>();

	public Date getDateDebut()
	{
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut)
	{
		this.dateDebut = dateDebut;
	}

	public Date getDateFin()
	{
		return dateFin;
	}

	public void setDateFin(Date dateFin)
	{
		this.dateFin = dateFin;
	}

	public int getNbPersonne()
	{
		return nbPersonne;
	}

	public void setNbPersonne(int nbPersonne)
	{
		this.nbPersonne = nbPersonne;
	}

	public List<PlanifUtilisateurDTO> getUtilisateurs()
	{
		return utilisateurs;
	}

	public void setUtilisateurs(List<PlanifUtilisateurDTO> utilisateurs)
	{
		this.utilisateurs = utilisateurs;
	}

	public FrequenceLivraison getFrequence()
	{
		return frequence;
	}

	public void setFrequence(FrequenceLivraison frequence)
	{
		this.frequence = frequence;
	}

	public List<PlanifDateDTO> getDates()
	{
		return dates;
	}

	public void setDates(List<PlanifDateDTO> dates)
	{
		this.dates = dates;
	}

	public Long getIdPeriodeCotisation()
	{
		return idPeriodeCotisation;
	}

	public void setIdPeriodeCotisation(Long idPeriodeCotisation)
	{
		this.idPeriodeCotisation = idPeriodeCotisation;
	}
	
	

}
