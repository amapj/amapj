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
 package fr.amapj.model.models.editionspe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;

@Entity
@Table( uniqueConstraints=
		{
		   @UniqueConstraint(columnNames={"nom"})
		})
public class EditionSpecifique implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 1, max = 100)
	@Column(length = 100)
	private String nom;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	// Permet de savoir l'état du modele de contrat
    private TypEditionSpecifique typEditionSpecifique;
	
	@NotNull
	@Size(min = 1, max = 20480)
	@Column(length = 20480)
	private String content;
	
	
	

	public enum P implements Mdm
	{ 
		ID("id") ,  NOM("nom") ; 
	
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




	public String getNom()
	{
		return nom;
	}




	public void setNom(String nom)
	{
		this.nom = nom;
	}




	public TypEditionSpecifique getTypEditionSpecifique()
	{
		return typEditionSpecifique;
	}




	public void setTypEditionSpecifique(TypEditionSpecifique typEditionSpecifique)
	{
		this.typEditionSpecifique = typEditionSpecifique;
	}




	public String getContent()
	{
		return content;
	}




	public void setContent(String content)
	{
		this.content = content;
	}


	


	
}