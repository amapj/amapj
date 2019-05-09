/*
 *  Copyright 2013-2018 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.model.models.stats;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.reel.PermanenceCell;

/**
 * Suivi des notifications réalisées par e mail 
 * 
 */
@Entity
public class NotificationDone  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet d'indiquer si cet utilisateur est actif ou inactif
	public TypNotificationDone typNotificationDone;
	
	@ManyToOne
	public ModeleContratDate modeleContratDate;
	
	@Temporal(TemporalType.DATE)
	public Date dateMailPeriodique;
	
	@NotNull
	@ManyToOne
	public Utilisateur utilisateur;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateEnvoi;
	
	
	public enum P implements Mdm
	{
		ID("id");
		
		private String propertyId;   
		   
	    P(String propertyId) 
	    {
	        this.propertyId = propertyId;
	    }
	    public String prop() 
	    { 
	    	return propertyId; 
	    }
		
	}
	

	// Getters and setters

	
	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}	
	
}
