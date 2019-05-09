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
 package fr.amapj.service.services.gestioncotisation;

import java.util.Date;

import fr.amapj.view.engine.tools.TableItem;

/**
 * Permet la gestion des instances
 * 
 */
public class PeriodeCotisationDTO implements TableItem
{
	public Long id;
	
	public String nom;
	
	public int montantMini;
	
	public int montantConseille;
	
	public Date dateDebutInscription;
	
	public Date dateFinInscription;
	
	public String textPaiement;
	
	public String libCheque;
	
	public Date dateRemiseCheque;
	
	public Date dateDebut;
	
	public Date dateFin;
	
	
	// Nombre d'adhérents
	public int nbAdhesion;
	
	// Montant total des adhésions
	public int mntTotalAdhesion;
	
	// Nb de paiements récupérés
	public int nbPaiementDonnes;
	
	// Nb de paiements à récupérer
	public int nbPaiementARecuperer;

	public Long idBulletinAdhesion;

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

	public int getMontantMini()
	{
		return montantMini;
	}

	public void setMontantMini(int montantMini)
	{
		this.montantMini = montantMini;
	}

	public int getMontantConseille()
	{
		return montantConseille;
	}

	public void setMontantConseille(int montantConseille)
	{
		this.montantConseille = montantConseille;
	}


	public Date getDateFinInscription()
	{
		return dateFinInscription;
	}

	public void setDateFinInscription(Date dateFinInscription)
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

	public Date getDateRemiseCheque()
	{
		return dateRemiseCheque;
	}

	public void setDateRemiseCheque(Date dateRemiseCheque)
	{
		this.dateRemiseCheque = dateRemiseCheque;
	}

	public int getNbAdhesion()
	{
		return nbAdhesion;
	}

	public void setNbAdhesion(int nbAdhesion)
	{
		this.nbAdhesion = nbAdhesion;
	}

	public int getNbPaiementDonnes()
	{
		return nbPaiementDonnes;
	}

	public void setNbPaiementDonnes(int nbPaiementDonnes)
	{
		this.nbPaiementDonnes = nbPaiementDonnes;
	}

	public int getNbPaiementARecuperer()
	{
		return nbPaiementARecuperer;
	}

	public void setNbPaiementARecuperer(int nbPaiementARecuperer)
	{
		this.nbPaiementARecuperer = nbPaiementARecuperer;
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

	public int getMntTotalAdhesion()
	{
		return mntTotalAdhesion;
	}

	public void setMntTotalAdhesion(int mntTotalAdhesion)
	{
		this.mntTotalAdhesion = mntTotalAdhesion;
	}

	public Date getDateDebutInscription()
	{
		return dateDebutInscription;
	}

	public void setDateDebutInscription(Date dateDebutInscription)
	{
		this.dateDebutInscription = dateDebutInscription;
	}

	public Long getIdBulletinAdhesion()
	{
		return idBulletinAdhesion;
	}

	public void setIdBulletinAdhesion(Long idBulletinAdhesion)
	{
		this.idBulletinAdhesion = idBulletinAdhesion;
	}
	
}
