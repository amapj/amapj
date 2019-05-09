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
 package fr.amapj.model.models.fichierbase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;

@Entity
public class Produit  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(length = 255)
	private String nom;
	
	@NotNull
	@Size(min = 1, max = 500)
	@Column(length = 500)
	private String conditionnement;
	

	@NotNull
	@ManyToOne
	private Producteur producteur;
	
	@NotNull
	@Enumerated(EnumType.STRING)
    private TypFacturation typFacturation = TypFacturation.UNITE;
	
	
	
	public enum P implements Mdm
	{
		ID("id") , NOM("nom") , CONDITIONNEMENT("conditionnement") , PRODUCTEUR("producteur") , TYPFACTURATION("typFacturation")  ;
		
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
	
	
	
	// Getters ans setters
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public Producteur getProducteur()
	{
		return producteur;
	}

	public void setProducteur(Producteur producteur)
	{
		this.producteur = producteur;
	}

	public TypFacturation getTypFacturation()
	{
		return typFacturation;
	}

	public void setTypFacturation(TypFacturation typFacturation)
	{
		this.typFacturation = typFacturation;
	}

	public String getConditionnement()
	{
		return conditionnement;
	}

	public void setConditionnement(String conditionnement)
	{
		this.conditionnement = conditionnement;
	}
	
	
	
	
	
}
