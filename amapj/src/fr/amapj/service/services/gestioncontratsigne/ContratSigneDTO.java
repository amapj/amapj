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
 package fr.amapj.service.services.gestioncontratsigne;


import java.util.Date;

import fr.amapj.view.engine.tools.TableItem;

/**
 * Represente un contrat signe
 *
 */
public class ContratSigneDTO implements TableItem
{
	public String nomUtilisateur;
	
	public String prenomUtilisateur;
	
	public Date dateCreation;
	
	public Date dateModification;
	
	public Long idUtilisateur;
	
	public Long idContrat;
	
	public Long idModeleContrat;
	
	// Montant des produits commandés
	public int mntCommande;
	
	// Montant de l'avoir initial
	public int mntAvoirInitial;
	
	// Montant du solde
	public int mntSolde;
	
	public int nbChequePromis;
	
	public int nbChequeRecus;
	
	public int nbChequeRemis;

	
	
	
	

	public String getNomUtilisateur()
	{
		return nomUtilisateur;
	}

	public void setNomUtilisateur(String nomUtilisateur)
	{
		this.nomUtilisateur = nomUtilisateur;
	}

	public String getPrenomUtilisateur()
	{
		return prenomUtilisateur;
	}

	public void setPrenomUtilisateur(String prenomUtilisateur)
	{
		this.prenomUtilisateur = prenomUtilisateur;
	}

	public Date getDateCreation()
	{
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation)
	{
		this.dateCreation = dateCreation;
	}

	public Date getDateModification()
	{
		return dateModification;
	}

	public void setDateModification(Date dateModification)
	{
		this.dateModification = dateModification;
	}

	public Long getIdContrat()
	{
		return idContrat;
	}

	public void setIdContrat(Long idContrat)
	{
		this.idContrat = idContrat;
	}

	
	

	
	public int getMntCommande()
	{
		return mntCommande;
	}

	public void setMntCommande(int mntCommande)
	{
		this.mntCommande = mntCommande;
	}


	public int getNbChequeRemis()
	{
		return nbChequeRemis;
	}

	public void setNbChequeRemis(int nbChequeRemis)
	{
		this.nbChequeRemis = nbChequeRemis;
	}


	/**
	 * Element permettant de distinguer les lignes
	 */
	public Long getId()
	{
		return idContrat;
	}

	public int getMntAvoirInitial()
	{
		return mntAvoirInitial;
	}

	public void setMntAvoirInitial(int mntAvoirInitial)
	{
		this.mntAvoirInitial = mntAvoirInitial;
	}

	public int getMntSolde()
	{
		return mntSolde;
	}

	public void setMntSolde(int mntSolde)
	{
		this.mntSolde = mntSolde;
	}

	public int getNbChequePromis()
	{
		return nbChequePromis;
	}

	public void setNbChequePromis(int nbChequePromis)
	{
		this.nbChequePromis = nbChequePromis;
	}

	public int getNbChequeRecus()
	{
		return nbChequeRecus;
	}

	public void setNbChequeRecus(int nbChequeRecus)
	{
		this.nbChequeRecus = nbChequeRecus;
	}
	
	
	
	
	
	

}
