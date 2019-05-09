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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.Mdm;

/**
 * Suivi des accès à l'application, au niveau du master
 * 
 */
@Entity
public class LogAccess  implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String ip;
	
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String browser;
	
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String nom;
	
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String prenom;
	
	private Long idUtilisateur;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateIn;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateOut;
	
	// Nombre de secondes pendant lequel l'utilisateur est actif
	private int activityTime;
	
	// Nom de la base de données associée
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String dbName;
	
	// Nom du fichier de log associé
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String logFileName;
	
	// Type du log : user ou deamon
	@NotNull
	@Enumerated(EnumType.STRING)
    private TypLog typLog = TypLog.USER;
	
	// nb d'erreur
	@NotNull
	private int nbError=0;
	
	// 0 si cas classique, 1 si sudo
	private int sudo=0;
	
	
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



	public String getIp()
	{
		return ip;
	}



	public void setIp(String ip)
	{
		this.ip = ip;
	}



	public String getBrowser()
	{
		return browser;
	}



	public void setBrowser(String browser)
	{
		this.browser = browser;
	}

	public Date getDateIn()
	{
		return dateIn;
	}



	public void setDateIn(Date dateIn)
	{
		this.dateIn = dateIn;
	}



	public Date getDateOut()
	{
		return dateOut;
	}



	public void setDateOut(Date dateOut)
	{
		this.dateOut = dateOut;
	}



	public String getNom()
	{
		return nom;
	}



	public void setNom(String nom)
	{
		this.nom = nom;
	}



	public String getPrenom()
	{
		return prenom;
	}



	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}



	public Long getIdUtilisateur()
	{
		return idUtilisateur;
	}



	public void setIdUtilisateur(Long idUtilisateur)
	{
		this.idUtilisateur = idUtilisateur;
	}



	public String getDbName()
	{
		return dbName;
	}



	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}



	public String getLogFileName()
	{
		return logFileName;
	}



	public void setLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
	}



	public int getActivityTime()
	{
		return activityTime;
	}



	public void setActivityTime(int activityTime)
	{
		this.activityTime = activityTime;
	}



	public TypLog getTypLog()
	{
		return typLog;
	}



	public void setTypLog(TypLog typLog)
	{
		this.typLog = typLog;
	}



	public int getNbError()
	{
		return nbError;
	}

	public void setNbError(int nbError)
	{
		this.nbError = nbError;
	}



	public int getSudo()
	{
		return sudo;
	}


	public void setSudo(int sudo)
	{
		this.sudo = sudo;
	}
	
}
