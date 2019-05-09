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
 package fr.amapj.model.models.contrat.modele;

import fr.amapj.model.engine.metadata.MetaDataEnum;


public enum JokerMode
{
	// Les dates jokers doivent être décidées lors de l'inscription au contrat 
	INSCRIPTION,
	
	//  Les dates jokers doivent être decidées x jours avant la date livraison 
	LIBRE;

	
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add("Ce champ vous permet de choisir comment les dates jokers seront choisies par les amapiens.");

			add(INSCRIPTION, "A l'inscription" , "Dans ce mode, les dates jokers doivent être décidées lors de l'inscription au contrat.<br/>"
					+ "Après la fin des inscriptions, les dates jokers ne sont plus modifiables.");
			
			add(LIBRE, "Libre choix avec délai de prévenance","Dans ce mode, l'adhérent peut choisir ses dates jokers jusqu'à x jours avant la date de livraison,<br/>"
					+ "x étant le délai de prévenance.<br/><br/>"
					+ "Exemple :<br>" 
					+ "Si les livraisons ont lieu le jeudi et si vous mettez 3 dans le champ délai de prevenance<br>"
					+ "alors les amapiens pourront modifier leur joker jusqu'au dimanche soir minuit précédent la distribution.<br/><br/>"
					+ "Par contre, le lundi, l'amapien ne peut plus modifier son choix pour la livraison du jeudi.<br/>");
			

		}
	}	
}
