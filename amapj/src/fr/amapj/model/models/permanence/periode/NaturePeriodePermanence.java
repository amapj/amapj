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
 package fr.amapj.model.models.permanence.periode;

import fr.amapj.model.engine.metadata.MetaDataEnum;


public enum NaturePeriodePermanence
{
	// 
	INSCRIPTION_LIBRE_AVEC_DATE_LIMITE,
	
	//  
	INSCRIPTION_LIBRE_FLOTTANT,

	
	INSCRIPTION_NON_LIBRE;
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
		
			add("Ce champ vous permet de choisir la nature de la période de permanence, c'est à dire comment les adhérents pourront choisir leurs dates de participations aux permanences. ");
			

			add(INSCRIPTION_LIBRE_AVEC_DATE_LIMITE, "Inscription libre AVEC date limite" , "Dans ce mode, les adhérents vont pouvoir s'inscrire librement eux même sur les dates de permanences jusqu'à une date de fin des inscriptions.<br/>"
					+ "Après la fin des inscriptions, l'amapien ne peut plus changer ses dates de permanences.<br/>"
					+ "Logiquement, cette date de fin des inscriptions doit être avant la date de la première permanence, mais le trésorier peut placer librement cette date s'il le souhaite.<br/>");
			
			add(INSCRIPTION_LIBRE_FLOTTANT, "Inscription libre SANS date limite","Dans ce mode, les adhérents vont pouvoir s'inscrire librement eux même sur les dates de permanences, et ceci sans date limite.<br/>"
					+ "Les inscriptions sont bloqués uniquement 3 jours par exemple avant la date de la permanence (vous pouvez choisir vous même la valeur de ce délai, 3 jours par exemple).<br/>");
			
			
			add(INSCRIPTION_NON_LIBRE, "Inscription imposée","Dans ce mode, l'adhérent ne choisit pas ses dates de permanence. Lors de la génération du planning par le tresorier, les amapiens sont placés de façon aléatoire, et les amapiens ne peuvent pas choisir leur date.<br/>");
		}
	}	
}
