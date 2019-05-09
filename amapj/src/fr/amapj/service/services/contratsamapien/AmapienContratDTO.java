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
 package fr.amapj.service.services.contratsamapien;

import java.util.Date;

import fr.amapj.view.engine.tools.TableItem;

/**
 * Represente un contrat signe
 *
 */
public class AmapienContratDTO implements TableItem
{
	public String nomContrat;

	public String nomProducteur;

	public Date dateDebut;

	public Date dateFin;

	public Date dateCreation;

	public Date dateModification;

	public int montant;

	public Long idUtilisateur;

	public Long idContrat;

	public Long idModeleContrat;
	
	public Long idProducteur;
	
	public String nomUtilisateur;
	
	public String prenomUtilisateur;

	public String getNomContrat()
	{
		return nomContrat;
	}

	public void setNomContrat(String nomContrat)
	{
		this.nomContrat = nomContrat;
	}

	public String getNomProducteur()
	{
		return nomProducteur;
	}

	public void setNomProducteur(String nomProducteur)
	{
		this.nomProducteur = nomProducteur;
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

	public int getMontant()
	{
		return montant;
	}

	public void setMontant(int montant)
	{
		this.montant = montant;
	}

	/**
	 * Element permettant de distinguer les lignes
	 */
	public Long getId()
	{
		return idContrat;
	}

}
