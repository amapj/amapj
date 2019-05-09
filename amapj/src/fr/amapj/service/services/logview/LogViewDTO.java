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
 package fr.amapj.service.services.logview;

import java.util.Date;

import fr.amapj.model.models.param.ChoixOnOff;
import fr.amapj.model.models.saas.TypLog;

/**
 * Contient les parametres pour la requete a effectuer 
 */
public class LogViewDTO
{
	
	public String nom;
		
	public String dbName;
	
	public ChoixOnOff status;
	
	// type du log (user ou demon)
	public TypLog typLog;
	
	public String ip;
	
	public Date dateMin;
	
	public Date dateMax;
	
	// nb d'erreur 
	public int nbError;
	
	
	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public Date getDateMin()
	{
		return dateMin;
	}

	public void setDateMin(Date dateMin)
	{
		this.dateMin = dateMin;
	}

	public Date getDateMax()
	{
		return dateMax;
	}

	public void setDateMax(Date dateMax)
	{
		this.dateMax = dateMax;
	}

	public String getDbName()
	{
		return dbName;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
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

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public ChoixOnOff getStatus()
	{
		return status;
	}

	public void setStatus(ChoixOnOff status)
	{
		this.status = status;
	}
	
	
}
