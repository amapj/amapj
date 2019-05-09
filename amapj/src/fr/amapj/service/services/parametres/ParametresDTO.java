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
 package fr.amapj.service.services.parametres;

import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.EtatModule;
import fr.amapj.model.models.param.SmtpType;


/**
 * Permet la gestion des utilisateurs en masse
 * ou du changement de son état
 * 
 */
public class ParametresDTO 
{
	
	public String nomAmap;
	
	public String villeAmap;
	
	public SmtpType smtpType;
	
	public String sendingMailUsername;
	
	public String sendingMailPassword;
	
	public int sendingMailNbMax;
	
	public String mailCopyTo;
	
	public String url;
	
	public String backupReceiver;
	
	public EtatModule etatPlanningDistribution;
	
	public EtatModule etatGestionCotisation;
	
	public ChoixOuiNon envoiMailRappelPermanence;
	
	public int delaiMailRappelPermanence;
	
	public String titreMailRappelPermanence;
	
	public String contenuMailRappelPermanence;
	
	public ChoixOuiNon envoiMailPeriodique;
	
	public int numJourDansMois;
	
	public String titreMailPeriodique;
	
	public String contenuMailPeriodique;
	
	
	// Champs calculés
	public boolean serviceMailActif;
	

	public String getNomAmap()
	{
		return nomAmap;
	}

	public void setNomAmap(String nomAmap)
	{
		this.nomAmap = nomAmap;
	}

	public String getSendingMailUsername()
	{
		return sendingMailUsername;
	}

	public void setSendingMailUsername(String sendingMailUsername)
	{
		this.sendingMailUsername = sendingMailUsername;
	}

	public String getSendingMailPassword()
	{
		return sendingMailPassword;
	}

	public void setSendingMailPassword(String sendingMailPassword)
	{
		this.sendingMailPassword = sendingMailPassword;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getBackupReceiver()
	{
		return backupReceiver;
	}

	public void setBackupReceiver(String backupReceiver)
	{
		this.backupReceiver = backupReceiver;
	}

	public String getVilleAmap()
	{
		return villeAmap;
	}

	public void setVilleAmap(String villeAmap)
	{
		this.villeAmap = villeAmap;
	}

	public EtatModule getEtatPlanningDistribution()
	{
		return etatPlanningDistribution;
	}

	public void setEtatPlanningDistribution(EtatModule etatPlanningDistribution)
	{
		this.etatPlanningDistribution = etatPlanningDistribution;
	}

	public ChoixOuiNon getEnvoiMailRappelPermanence()
	{
		return envoiMailRappelPermanence;
	}

	public void setEnvoiMailRappelPermanence(ChoixOuiNon envoiMailRappelPermanence)
	{
		this.envoiMailRappelPermanence = envoiMailRappelPermanence;
	}

	public String getContenuMailRappelPermanence()
	{
		return contenuMailRappelPermanence;
	}

	public void setContenuMailRappelPermanence(String contenuMailRappelPermanence)
	{
		this.contenuMailRappelPermanence = contenuMailRappelPermanence;
	}

	public String getTitreMailRappelPermanence()
	{
		return titreMailRappelPermanence;
	}

	public void setTitreMailRappelPermanence(String titreMailRappelPermanence)
	{
		this.titreMailRappelPermanence = titreMailRappelPermanence;
	}

	public int getDelaiMailRappelPermanence()
	{
		return delaiMailRappelPermanence;
	}

	public void setDelaiMailRappelPermanence(int delaiMailRappelPermanence)
	{
		this.delaiMailRappelPermanence = delaiMailRappelPermanence;
	}

	public ChoixOuiNon getEnvoiMailPeriodique()
	{
		return envoiMailPeriodique;
	}

	public void setEnvoiMailPeriodique(ChoixOuiNon envoiMailPeriodique)
	{
		this.envoiMailPeriodique = envoiMailPeriodique;
	}

	public int getNumJourDansMois()
	{
		return numJourDansMois;
	}

	public void setNumJourDansMois(int numJourDansMois)
	{
		this.numJourDansMois = numJourDansMois;
	}

	public String getTitreMailPeriodique()
	{
		return titreMailPeriodique;
	}

	public void setTitreMailPeriodique(String titreMailPeriodique)
	{
		this.titreMailPeriodique = titreMailPeriodique;
	}

	public String getContenuMailPeriodique()
	{
		return contenuMailPeriodique;
	}

	public void setContenuMailPeriodique(String contenuMailPeriodique)
	{
		this.contenuMailPeriodique = contenuMailPeriodique;
	}

	public EtatModule getEtatGestionCotisation()
	{
		return etatGestionCotisation;
	}

	public void setEtatGestionCotisation(EtatModule etatGestionCotisation)
	{
		this.etatGestionCotisation = etatGestionCotisation;
	}

	public SmtpType getSmtpType()
	{
		return smtpType;
	}

	public void setSmtpType(SmtpType smtpType)
	{
		this.smtpType = smtpType;
	}

	public boolean isServiceMailActif()
	{
		return serviceMailActif;
	}

	public void setServiceMailActif(boolean serviceMailActif)
	{
		this.serviceMailActif = serviceMailActif;
	}

	public int getSendingMailNbMax()
	{
		return sendingMailNbMax;
	}

	public void setSendingMailNbMax(int sendingMailNbMax)
	{
		this.sendingMailNbMax = sendingMailNbMax;
	}

	public String getMailCopyTo()
	{
		return mailCopyTo;
	}

	public void setMailCopyTo(String mailCopyTo)
	{
		this.mailCopyTo = mailCopyTo;
	}

	
}
