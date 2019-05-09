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
 package fr.amapj.model.models.contrat.modele;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;

/**
 * Date de paiement pour un modele de contrat
 *
 */
@Entity
public class ModeleContratDatePaiement implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private ModeleContrat modeleContrat;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date datePaiement;
	
	public enum P implements Mdm
	{
		ID("id") ,  MODELECONTRAT("modeleContrat") , DATEPAIEMENT("datePaiement") ;
		
		private String propertyId;   
		   
	    P(String propertyId) 
	    {
	        this.propertyId = propertyId;
	    }
	    public String prop() 
	    { 
	    	return propertyId; 
	    }
		
	} ;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public ModeleContrat getModeleContrat()
	{
		return modeleContrat;
	}

	public void setModeleContrat(ModeleContrat modeleContrat)
	{
		this.modeleContrat = modeleContrat;
	}

	public Date getDatePaiement()
	{
		return datePaiement;
	}

	public void setDatePaiement(Date datePaiement)
	{
		this.datePaiement = datePaiement;
	}

	
	
}
