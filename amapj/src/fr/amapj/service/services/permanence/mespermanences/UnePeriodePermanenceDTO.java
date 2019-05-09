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
 package fr.amapj.service.services.permanence.mespermanences;

import java.util.Date;

import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;

/**
 * Description d'une periode de permanence pour un utilisateur 
 *
 */
public class UnePeriodePermanenceDTO 
{
	
	public Long idPeriodePermanence;
	
	public String nom;
	
	public String description;

	public Date dateFinInscription;
	
	public Date dateDebut;
	
	public Date dateFin;

	public int nbDatePermanence;
	
	public int nbSouhaite;
	
	public int nbInscription;
	
	public NaturePeriodePermanence nature;
	
	// Dans le cas des contrats flottant, contient la premiere date de permanence modifiable 
	public Date firstDateModifiable;
	
	public RegleInscriptionPeriodePermanence regleInscription;
	
}
