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
 package fr.amapj.model.models.param;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.model.engine.Identifiable;

/**
 * Paramètres généraux de l'application 
 * 
 *
 */
@Entity
public class Parametres implements Identifiable
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Nom de l'AMAP
	 */
	@Size(min = 0, max = 100)
	@Column(length = 100)
	private String nomAmap;
	
	/**
	 * Ville de l'AMAP
	 */
	@Size(min = 0, max = 200)
	@Column(length = 200)
	private String villeAmap;
	
	/**
	 * Envoi de mail
	 */
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String sendingMailUsername;
	
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String sendingMailPassword;
	
	// Nombre maximum de mail qu'il est possible d'envoyer par jour 
	private int sendingMailNbMax;
	
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String mailCopyTo;
	
	/**
	 * Type du serveur pour l'envoi des mails
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
    private SmtpType smtpType;
	
	/**
	 * Url de l'application visible dans les mails
	 */
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String url;
	

	/**
	 * Destinataire de la sauvegarde
	 */
	@Size(min = 0, max = 255)
	@Column(length = 255)
	private String backupReceiver;

	
	// Partie gestion des permanences
	
	/**
	 * Activation ou désactivation du module "Planning de distribution"
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
    private EtatModule etatPlanningDistribution;
	
	/**
	 * Activation ou désactivation du module "Gestion des cotisations"
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
    private EtatModule etatGestionCotisation;
	
	/**
	 * Envoi des mails pour le rappel de permanence
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
    private ChoixOuiNon envoiMailRappelPermanence;
	
	
	private int delaiMailRappelPermanence;
	
	/**
	 * Titre du mail pour le rappel de permanence
	 */
	@Size(min = 0, max = 2048)
	@Column(length = 2048)
	private String titreMailRappelPermanence;
	
	/**
	 * Contenu du mail pour le rappel de permanence
	 */
	@Size(min = 0, max = 20480)
	@Column(length = 20480)
	private String contenuMailRappelPermanence;
	
	
	// Partie envoi des mails périodiques
	
	@NotNull
	@Enumerated(EnumType.STRING)
    private ChoixOuiNon envoiMailPeriodique;
	
	
	/**
	 * Numéro du jour dans le mois 
	 */
	private int numJourDansMois;
	
	/**
	 * Titre du mail pour le mail periodique
	 */
	@Size(min = 0, max = 2048)
	@Column(length = 2048)
	private String titreMailPeriodique;
	
	/**
	 * Contenu du mail pour le mail periodique
	 */
	@Size(min = 0, max = 20480)
	@Column(length = 20480) 
	private String contenuMailPeriodique;
	
	
	
	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}


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
