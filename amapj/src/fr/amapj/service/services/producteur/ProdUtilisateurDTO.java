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
 package fr.amapj.service.services.producteur;

import fr.amapj.service.services.utilisateur.util.IUtilisateur;


/**
 * 
 * 
 */
public class ProdUtilisateurDTO implements IUtilisateur
{
	public Long idUtilisateur;
	
	public String nom;
	
	public String prenom;
	
	public boolean etatNotification;

	public Long getIdUtilisateur()
	{
		return idUtilisateur;
	}

	public void setIdUtilisateur(Long idUtilisateur)
	{
		this.idUtilisateur = idUtilisateur;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public String getPrenom()
	{
		return prenom;
	}

	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}

	public boolean isEtatNotification()
	{
		return etatNotification;
	}

	public void setEtatNotification(boolean etatNotification)
	{
		this.etatNotification = etatNotification;
	}

	
	

	
	
	
}
