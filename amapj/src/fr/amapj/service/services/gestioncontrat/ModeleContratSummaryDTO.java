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
 package fr.amapj.service.services.gestioncontrat;


import java.util.Date;

import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Bean permettant l'affichage des modeles de contrats
 *
 */
public class ModeleContratSummaryDTO implements TableItem
{
	public Long id;
	
	public String nom;

	public String nomProducteur;
	
	public Long producteurId;
	
	public Date dateDebut;
	
	public Date dateFin;

	public int nbLivraison;
	
	public int nbProduit;
	
	public Date finInscription;
	
	public EtatModeleContrat etat;
	
	// Nombre d'adherents ayant souscrit à ce contrat
	public int nbInscrits;

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

	public String getNomProducteur()
	{
		return nomProducteur;
	}

	public void setNomProducteur(String nomProducteur)
	{
		this.nomProducteur = nomProducteur;
	}

	public int getNbLivraison()
	{
		return nbLivraison;
	}

	public void setNbLivraison(int nbLivraison)
	{
		this.nbLivraison = nbLivraison;
	}

	public int getNbProduit()
	{
		return nbProduit;
	}

	public void setNbProduit(int nbProduit)
	{
		this.nbProduit = nbProduit;
	}

	public Date getFinInscription()
	{
		return finInscription;
	}

	public void setFinInscription(Date finInscription)
	{
		this.finInscription = finInscription;
	}
	
	public EtatModeleContrat getEtat()
	{
		return etat;
	}

	public void setEtat(EtatModeleContrat etat)
	{
		this.etat = etat;
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

	public int getNbInscrits()
	{
		return nbInscrits;
	}

	public void setNbInscrits(int nbInscrits)
	{
		this.nbInscrits = nbInscrits;
	}
	
	
}
