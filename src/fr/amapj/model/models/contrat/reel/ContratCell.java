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
 package fr.amapj.model.models.contrat.reel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;

@Entity
public class ContratCell  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@ManyToOne
	private Contrat contrat;
	
	@NotNull
	@ManyToOne
	private ModeleContratProduit modeleContratProduit;
	
	@NotNull
	@ManyToOne
	private ModeleContratDate modeleContratDate;

	@NotNull
	private int qte;
	
	
	
	public enum P implements Mdm
	{
		ID("id") , CONTRAT("contrat") , MODELECONTRATPRODUIT("modeleContratProduit") , MODELECONTRATDATE("modeleContratDate") , QTE("qte") ;
		
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

	public Contrat getContrat()
	{
		return contrat;
	}

	public void setContrat(Contrat contrat)
	{
		this.contrat = contrat;
	}

	public ModeleContratProduit getModeleContratProduit()
	{
		return modeleContratProduit;
	}

	public void setModeleContratProduit(ModeleContratProduit modeleContratProduit)
	{
		this.modeleContratProduit = modeleContratProduit;
	}

	public ModeleContratDate getModeleContratDate()
	{
		return modeleContratDate;
	}

	public void setModeleContratDate(ModeleContratDate modeleContratDate)
	{
		this.modeleContratDate = modeleContratDate;
	}

	public int getQte()
	{
		return qte;
	}

	public void setQte(int qte)
	{
		this.qte = qte;
	}
		

	

	
	
}
