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
 package fr.amapj.service.services.mespaiements;


import java.util.Date;

import fr.amapj.view.engine.tools.TableItem;

/**
 * 
 *
 */
public class PaiementHistoriqueDTO implements TableItem
{
	
	public Long id;

	// Nom du producteur
	public String nomProducteur;

	
	// Nom du contrat
	public String nomContrat;

	
	// Date d'encaissement prévu
	public Date datePrevu;
	
	// Date d'encaissement réelle
	public Date dateReelle;
	
	
	// Montant du paiement
	public int montant;


	public String getNomProducteur()
	{
		return nomProducteur;
	}


	public void setNomProducteur(String nomProducteur)
	{
		this.nomProducteur = nomProducteur;
	}


	public String getNomContrat()
	{
		return nomContrat;
	}


	public void setNomContrat(String nomContrat)
	{
		this.nomContrat = nomContrat;
	}


	public Date getDatePrevu()
	{
		return datePrevu;
	}


	public void setDatePrevu(Date datePrevu)
	{
		this.datePrevu = datePrevu;
	}


	public Date getDateReelle()
	{
		return dateReelle;
	}


	public void setDateReelle(Date dateReelle)
	{
		this.dateReelle = dateReelle;
	}


	public int getMontant()
	{
		return montant;
	}


	public void setMontant(int montant)
	{
		this.montant = montant;
	}


	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}



}
