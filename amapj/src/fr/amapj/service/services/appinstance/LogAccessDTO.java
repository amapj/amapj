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
 package fr.amapj.service.services.appinstance;

import java.util.Date;

import fr.amapj.model.models.saas.TypLog;
import fr.amapj.view.engine.tools.TableItem;


/**
 * 
 */
public class LogAccessDTO implements TableItem
{
	public Long id;

	public String ip;
	
	public String browser;
	
	public String nom;
	
	public String prenom;
	
	public Long idUtilisateur;
	
	public Date dateIn;
	
	public Date dateOut;
	
	// Nom de la base de données associée
	public String dbName;
	
	// Nom du fichier de log associé
	public String logFileName;
	
	// type du log (user ou demon)
	public TypLog typLog;
	
	// nb d'erreur 
	public int nbError;
	
	// Vide si pas de sudo, contient le mot SUDO sinon
	public String sudo;
	
	
	
	public void setStatus(String status)
	{
		
	}
	
	public String getStatus()
	{
		if (dateOut==null)
		{
			return "On";
		}
		return "Off";
	}
	
	

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

	public String getSudo()
	{
		return sudo;
	}

	public void setSudo(String sudo)
	{
		this.sudo = sudo;
	}
	
}
