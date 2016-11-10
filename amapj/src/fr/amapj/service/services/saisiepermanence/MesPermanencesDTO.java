/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.service.services.saisiepermanence;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informations sur les distributions d'un utilisateur
 *
 */
public class MesPermanencesDTO
{
	// Toutes les distributions futures pour cet utilisateur
	public List<PermanenceDTO> permanencesFutures = new ArrayList<PermanenceDTO>();
	
	
	// Pour la semaine courante : date de début et de fin de la semaine
	public Date dateDebut;
	
	public Date dateFin;
	
	public List<PermanenceDTO> permanencesSemaine = new ArrayList<PermanenceDTO>();

}
