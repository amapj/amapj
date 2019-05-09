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
 package fr.amapj.model.models.saas;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;


/**
 * Instance de l'application,
 *
 */
@Entity
public class AppInstance  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 0, max = 255)
	@Column(length = 255) 
	private String nomInstance;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreation;
	
	@NotNull
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String dbms;
	
	// Utilisé uniquement pour les base de données externe
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String dbUserName;
	
	// Utilisé uniquement pour les base de données externe
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String dbPassword;
	

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getNomInstance()
	{
		return nomInstance;
	}

	public void setNomInstance(String nomInstance)
	{
		this.nomInstance = nomInstance;
	}

	public Date getDateCreation()
	{
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation)
	{
		this.dateCreation = dateCreation;
	}

	public String getDbUserName()
	{
		return dbUserName;
	}

	public void setDbUserName(String dbUserName)
	{
		this.dbUserName = dbUserName;
	}

	public String getDbPassword()
	{
		return dbPassword;
	}

	public void setDbPassword(String dbPassword)
	{
		this.dbPassword = dbPassword;
	}

	public String getDbms()
	{
		return dbms;
	}

	public void setDbms(String dbms)
	{
		this.dbms = dbms;
	}

	

	
}
