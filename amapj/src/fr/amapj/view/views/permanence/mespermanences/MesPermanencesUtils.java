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
 package fr.amapj.view.views.permanence.mespermanences;

import java.util.Date;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;

public class MesPermanencesUtils
{
	
	static public String getLibCompteur(UnePeriodePermanenceDTO periodePermanenceDTO)
	{
		String msg = "Vous êtes inscrit sur "+periodePermanenceDTO.nbInscription+" positions, vous devez vous inscrire pour "+periodePermanenceDTO.nbSouhaite+" positions.";
		int delta = (periodePermanenceDTO.nbSouhaite-periodePermanenceDTO.nbInscription);
		if (delta<=0)
		{
			msg = msg+" C'est OK!";
		}
		else
		{
			msg = msg+" Encore "+delta+"!";
		}
		return msg;
	}
	
	/**
	 * Retourne true si l'adhérent doit encore s'inscrire 
	 * 
	 * @param periodePermanenceDTO
	 * @return
	 */
	static public boolean needSubcribeMore(UnePeriodePermanenceDTO periodePermanenceDTO)
	{
		int delta = (periodePermanenceDTO.nbSouhaite-periodePermanenceDTO.nbInscription);
		return delta>0;
	}
	
	
	
	
	static public boolean isDateModifiable(UnePeriodePermanenceDTO periodePermanenceDTO,PeriodePermanenceDateDTO detail)
	{
		Date nowNoTime = DateUtils.getDateWithNoTime();
		
		switch (periodePermanenceDTO.nature)
		{
		case INSCRIPTION_NON_LIBRE:
			return false;
			
		case INSCRIPTION_LIBRE_AVEC_DATE_LIMITE:
			// Dans ce cas, on peut modifier uniquement les dates du futur et uniquement si la date courante est avant la date limite des inscriptions
			if (nowNoTime.after(periodePermanenceDTO.dateFinInscription))
			{
				return false;
			}
			if (nowNoTime.after(detail.datePerm))
			{
				return false;
			}
			return true;
			
		case INSCRIPTION_LIBRE_FLOTTANT:
			if (detail.datePerm.before(periodePermanenceDTO.firstDateModifiable))
			{
				return false;
			}
			return true;

		default:
			throw new AmapjRuntimeException();
		}
	}
}
