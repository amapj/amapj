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
 package fr.amapj.model.models.contrat.modele;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;


/**
 * Cette classe permet de mémoriser les elements qui sont 
 * exclus de ce contrat.
 * Il est possible d'exclure une date complète, ou bien un produit à une date donnée
 *
 */
@Entity
public class ModeleContratExclude implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private ModeleContrat modeleContrat;
	
	@ManyToOne
	private ModeleContratProduit produit;
	
	@NotNull
	@ManyToOne
	private ModeleContratDate date;
	
	
	
	public enum P implements Mdm
	{
		ID("id") , MODELECONTRAT("modeleContrat") , PRODUIT("produit") , DATE("date") ;
		
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



	public ModeleContratProduit getProduit()
	{
		return produit;
	}



	public void setProduit(ModeleContratProduit produit)
	{
		this.produit = produit;
	}



	public ModeleContratDate getDate()
	{
		return date;
	}



	public void setDate(ModeleContratDate date)
	{
		this.date = date;
	} ;
}
