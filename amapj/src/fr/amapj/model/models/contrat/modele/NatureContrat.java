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


public enum NatureContrat
{
	// 
	ABONNEMENT,
	
	//  
	LIBRE,

	
	CARTE_PREPAYEE;
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add("Ce champ vous permet de choisir la nature du contrat, c'est à dire comment les adhérents pourront choisir les quantitées livrées "
					+ "et comment ils pourront modifier le contrat.");

			add(ABONNEMENT, "Abonnement" , "Dans ce mode, les quantités livrées sont égales pour chaque date de livraison. L'amapien ne peut pas moduler la quantité suivant la date de livraison.<br/>"
					+ "Après la fin des inscriptions, le contrat n'est plus modifiable et la date de fin des inscriptions est obligatoirement avant la date de la première livraison.<br/>"
					+ "Ce mode est le mode à privilégier pour les paniers de légumes par exemple.");
			
			add(LIBRE, "Choix pour chaque date","Dans ce mode, l'adhérent peut choisir les quantités livrées pour chaque date de livraison.<br/>"
					+ "Après la fin des inscriptions, le contrat n'est plus modifiable et la date de fin des inscriptions est obligatoirement avant la date de la première livraison.<br/>"
					+ "Ce mode peut être utilisé pour les commandes de pains, de fromage, ce qui permet à l'adhérent de changer de type de pain par exemple suivant les dates.");
			
			add(CARTE_PREPAYEE, "Carte prépayée","Dans ce mode, l'adhérent peut choisir les quantités livrées pour chaque date de livraison , et il peut modifier son contrat même après la date de fin des inscriptions pour les livraisons à venir.<br/>"
					+ "Par exemple, l'adhérent peut modifier jusqu'à mardi pour une livraison le samedi.<br/>"
					+ "Ce mode devrait être utilisé de façon exceptionnelle ou pour les ajustements.");
		}
	}	
}
