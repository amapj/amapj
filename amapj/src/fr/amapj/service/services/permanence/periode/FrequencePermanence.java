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

import fr.amapj.model.engine.metadata.MetaDataEnum;

public enum FrequencePermanence 
{
	UNE_FOIS_PAR_SEMAINE,
	QUINZE_JOURS,
	UNE_FOIS_PAR_MOIS;
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add("Ce champ vous permet de choisir la fréquence des permanences.<br/><br/>"
					+ "A noter : si votre periode de permanence est toutes les semaines SAUF une ou deux dates, alors choississez quand même <b>Une fois par semaine</b>,"
					+ " et ensuite vous pourrez modifiez votre periode pour enlever le ou les dates en trop avec le bouton Modifier puis Modifier les dates de permanences.<br/>"
					+ "Ceci est plus simple que de saisir toutes les dates une par une.<br/><br/>");
						
			add(UNE_FOIS_PAR_SEMAINE,"Une fois par semaine","Cette periode de permanence comprend une permanence toutes les semaines (tous les jeudis par exemple)"); 
			add(QUINZE_JOURS,"Une fois tous les quinze jours"); 
			add(UNE_FOIS_PAR_MOIS,"Une fois par mois","Cette periode de permanence comprend une permanence tous les mois, par exemple tous les premiers jeudis du mois"); 

			
		}
	}	
}
