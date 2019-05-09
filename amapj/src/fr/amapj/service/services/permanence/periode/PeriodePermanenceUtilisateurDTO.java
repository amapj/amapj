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
 package fr.amapj.service.services.permanence.periode;



/**
 * 
 */
public class PeriodePermanenceUtilisateurDTO 
{
	public Long idPeriodePermanenceUtilisateur;
	
	public Long idUtilisateur;
	
	public int nbParticipation;
	
	public String nom;
	
	public String prenom;
	
	public boolean toSuppress = false;
	
	public Long getIdUtilisateur()
	{
		return idUtilisateur;
	}

	public void setIdUtilisateur(Long idUtilisateur)
	{
		this.idUtilisateur = idUtilisateur;
	}

	public int getNbParticipation()
	{
		return nbParticipation;
	}

	public void setNbParticipation(int nbParticipation)
	{
		this.nbParticipation = nbParticipation;
	}
	
	public PeriodePermanenceUtilisateurDTO clone()
	{
		PeriodePermanenceUtilisateurDTO n = new PeriodePermanenceUtilisateurDTO();
		
		n.idUtilisateur = idUtilisateur;
		n.nbParticipation = nbParticipation;
		n.nom = nom;
		n.prenom = prenom;
		n.toSuppress = toSuppress;
		
		return n;
	}
	
	

	
	
}
