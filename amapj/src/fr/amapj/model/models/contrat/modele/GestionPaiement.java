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

/**
 * Permet de definir le mode de paiement
 */
public enum GestionPaiement 
{
	// Pas de gestion des paiements
	NON_GERE,
	
	// gestion standard
	GESTION_STANDARD;
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add("Ce champ vous permet de choisir coment vous allez gérer le paiement des adhérents");
		
			
			add(NON_GERE,"Pas de gestion des paiements","Dans ce mode, vous ne gérez pas les paiements avec AmapJ. Par contre, en fin de saisie du contrat, vous pouvez afficher un message spécifique qui vous permettra d'indiquer comment se fait le paiement.");
			add(GESTION_STANDARD,"Gestion standard","Dans ce mode, l'adhérent va pouvoir saisir ces paiements, et le référent pourra ensuite collecter les chèques et faire les remises au producteur") ;
			
		}
	}	

}
