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

import fr.amapj.model.models.param.SmtpType;
import fr.amapj.model.models.saas.TypDbExemple;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Permet la gestion des instances
 * 
 */
public class AppInstanceDTO implements TableItem , Comparable<AppInstanceDTO>
{
	public Long id;
	
	public String nomInstance;
	
	public Date dateCreation;
	
	public AppState state;
	
	public int nbUtilisateurs;
	
	public String dbms;
	
	public String dbUserName;
	
	public String dbPassword;
	
	public int nbMails;
	
	// Partie création de la base de démo
	public TypDbExemple typDbExemple;
	
	public String nomAmap;
	
	public String villeAmap;
	
	public SmtpType smtpType;
	
	public String adrMailSrc;
	
	public int nbMailMax;
	
	public String url;
	
	public Date dateDebut;
	
	public Date dateFin;
	
	public Date dateFinInscription;
	
	public String password;
	
	public String user1Nom;
	
	public String user1Prenom;
	
	public String user1Email;
	
	
		

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDateCreation()
	{
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation)
	{
		this.dateCreation = dateCreation;
	}

	public String getNomInstance()
	{
		return nomInstance;
	}

	public void setNomInstance(String nomInstance)
	{
		this.nomInstance = nomInstance;
	}

	public AppState getState()
	{
		return state;
	}

	public void setState(AppState state)
	{
		this.state = state;
	}

	public int getNbUtilisateurs()
	{
		return nbUtilisateurs;
	}

	public void setNbUtilisateurs(int nbUtilisateurs)
	{
		this.nbUtilisateurs = nbUtilisateurs;
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

	public TypDbExemple getTypDbExemple()
	{
		return typDbExemple;
	}

	public void setTypDbExemple(TypDbExemple typDbExemple)
	{
		this.typDbExemple = typDbExemple;
	}

	public String getNomAmap()
	{
		return nomAmap;
	}

	public void setNomAmap(String nomAmap)
	{
		this.nomAmap = nomAmap;
	}

	public String getVilleAmap()
	{
		return villeAmap;
	}

	public void setVilleAmap(String villeAmap)
	{
		this.villeAmap = villeAmap;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Date getDateDebut()
	{
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut)
	{
		this.dateDebut = dateDebut;
	}

	public Date getDateFin()
	{
		return dateFin;
	}

	public void setDateFin(Date dateFin)
	{
		this.dateFin = dateFin;
	}

	public Date getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(Date dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUser1Nom()
	{
		return user1Nom;
	}

	public void setUser1Nom(String user1Nom)
	{
		this.user1Nom = user1Nom;
	}

	public String getUser1Prenom()
	{
		return user1Prenom;
	}

	public void setUser1Prenom(String user1Prenom)
	{
		this.user1Prenom = user1Prenom;
	}

	public String getUser1Email()
	{
		return user1Email;
	}

	public void setUser1Email(String user1Email)
	{
		this.user1Email = user1Email;
	}

	@Override
	public int compareTo(AppInstanceDTO o)
	{
		return id.compareTo(o.id);
	}

	public int getNbMails()
	{
		return nbMails;
	}

	public void setNbMails(int nbMails)
	{
		this.nbMails = nbMails;
	}

	public SmtpType getSmtpType()
	{
		return smtpType;
	}

	public void setSmtpType(SmtpType smtpType)
	{
		this.smtpType = smtpType;
	}

	public String getAdrMailSrc()
	{
		return adrMailSrc;
	}

	public void setAdrMailSrc(String adrMailSrc)
	{
		this.adrMailSrc = adrMailSrc;
	}

	public int getNbMailMax()
	{
		return nbMailMax;
	}

	public void setNbMailMax(int nbMailMax)
	{
		this.nbMailMax = nbMailMax;
	}
	
	
	
	
}
