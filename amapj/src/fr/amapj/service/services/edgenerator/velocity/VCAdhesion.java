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
 package fr.amapj.service.services.edgenerator.velocity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.amapj.common.StringUtils;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.cotisation.PeriodeCotisation;
import fr.amapj.model.models.cotisation.PeriodeCotisationUtilisateur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.service.services.mescontrats.InfoPaiementDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.mespaiements.MesPaiementsService;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


public class VCAdhesion
{
	public String nomPeriode;
	
	public String dateDebut;
	
	public String dateFin;
	
	private String montantAdhesion;
	
	private String montantMini;
	
	private String montantConseille;
	
	private String dateDebutInscription;
	
	private String dateFinInscription;
	
	private String textPaiement;
	
	private String libCheque;
	
	private String dateRemiseCheque;
	
	private String dateAdhesion;
	

	
	public void load(PeriodeCotisationUtilisateur pcu,EntityManager em)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		PeriodeCotisation pc = pcu.getPeriodeCotisation();
		CurrencyTextFieldConverter ctc = new CurrencyTextFieldConverter();
	
		nomPeriode = s(pc.getNom());
		dateDebut = df.format(pc.getDateDebut());
		dateFin = df.format(pc.getDateFin());
		montantAdhesion = ctc.convertToString(pcu.getMontantAdhesion());
		montantMini = ctc.convertToString(pc.getMontantMini());
		montantConseille = ctc.convertToString(pc.getMontantConseille());
		dateDebutInscription = df.format(pc.getDateDebutInscription());
		dateFinInscription = df.format(pc.getDateFinInscription());
		textPaiement = s(pc.getTextPaiement());
		libCheque = s(pc.getLibCheque());
		dateRemiseCheque = df.format(pc.getDateRemiseCheque());
		dateAdhesion = df.format(pcu.getDateAdhesion());
	}

	/**
	 * Permet d'escaper les caracteres HTML  
	 */
	private String s(String value)
	{
		return VCBuilder.s(value);
	}

	
	// Getters and setters pour Velocity 
	
	
	public String getNomPeriode()
	{
		return nomPeriode;
	}

	public void setNomPeriode(String nomPeriode)
	{
		this.nomPeriode = nomPeriode;
	}

	public String getDateDebut()
	{
		return dateDebut;
	}

	public void setDateDebut(String dateDebut)
	{
		this.dateDebut = dateDebut;
	}

	public String getDateFin()
	{
		return dateFin;
	}

	public void setDateFin(String dateFin)
	{
		this.dateFin = dateFin;
	}

	public String getMontantAdhesion()
	{
		return montantAdhesion;
	}

	public void setMontantAdhesion(String montantAdhesion)
	{
		this.montantAdhesion = montantAdhesion;
	}

	public String getMontantMini()
	{
		return montantMini;
	}

	public void setMontantMini(String montantMini)
	{
		this.montantMini = montantMini;
	}

	public String getMontantConseille()
	{
		return montantConseille;
	}

	public void setMontantConseille(String montantConseille)
	{
		this.montantConseille = montantConseille;
	}

	public String getDateDebutInscription()
	{
		return dateDebutInscription;
	}

	public void setDateDebutInscription(String dateDebutInscription)
	{
		this.dateDebutInscription = dateDebutInscription;
	}

	public String getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(String dateFinInscription)
	{
		this.dateFinInscription = dateFinInscription;
	}

	public String getTextPaiement()
	{
		return textPaiement;
	}

	public void setTextPaiement(String textPaiement)
	{
		this.textPaiement = textPaiement;
	}

	public String getLibCheque()
	{
		return libCheque;
	}

	public void setLibCheque(String libCheque)
	{
		this.libCheque = libCheque;
	}

	public String getDateRemiseCheque()
	{
		return dateRemiseCheque;
	}

	public void setDateRemiseCheque(String dateRemiseCheque)
	{
		this.dateRemiseCheque = dateRemiseCheque;
	}

	public String getDateAdhesion()
	{
		return dateAdhesion;
	}

	public void setDateAdhesion(String dateAdhesion)
	{
		this.dateAdhesion = dateAdhesion;
	}	
}
