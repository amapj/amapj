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
 package fr.amapj.service.services.remiseproducteur;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import fr.amapj.model.models.contrat.reel.EtatPaiement;



/**
 * 
 *
 */
public class PaiementRemiseDTO 
{
	public Long idPaiement;
	
	// Montant du paiement
	public int montant;
	
	public String nomUtilisateur;;
	
	public String prenomUtilisateur;
	
	public EtatPaiement etatPaiement;
	
	public String commentaire1;

	public String commentaire2;
	
	

	public int getMontant()
	{
		return montant;
	}

	public void setMontant(int montant)
	{
		this.montant = montant;
	}

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
	};
		
	
	
	
	
	
	
	
	

}
