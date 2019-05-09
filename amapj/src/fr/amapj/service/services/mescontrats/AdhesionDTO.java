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
 package fr.amapj.service.services.mescontrats;


import fr.amapj.service.services.gestioncotisation.PeriodeCotisationDTO;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationUtilisateurDTO;

/**
 * Informations sur l'adhesion d'un utilisateur
 *
 */
public class AdhesionDTO
{	
	public Long idUtilisateur;
	
	// Si null,  alors il n'y a pas de de periode de cotisation auquel on peut adherer en ce moment
	public PeriodeCotisationDTO periodeCotisationDTO;
	
	// Si null, alors l'utilisateur n'a pas adhéré
	public PeriodeCotisationUtilisateurDTO periodeCotisationUtilisateurDTO;
	
	// Non null uniquement si periodeCotisationDTO est null
	// Permet d'afficher l'état de l'adhesion pendant 30 jours encore après la fin de la periode d'inscription 
	public AffichageOnly affichageOnly;
	
	/**
	 * Retourne true si on doit afficher le bloc sur les adhésions en haut de la page de Mes contrats
	 * 
	 */
	public boolean displayAdhesionTop()
	{
		return (periodeCotisationDTO!=null);
	}


	/**
	 * Retourne true si l'utilisateur a adheré à l'AMAP
	 * @return
	 */
	public boolean isCotisant()
	{
		return (periodeCotisationUtilisateurDTO!=null);
	}
	
	
	static public class AffichageOnly
	{
		public Long idPeriode;
		public Long idPeriodeUtilisateur;
		public String nomPeriode;
		public int montantAdhesion;
		public Long idBulletin;
	}
	
	
}
