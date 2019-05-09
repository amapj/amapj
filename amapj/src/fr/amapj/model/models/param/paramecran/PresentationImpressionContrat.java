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
 package fr.amapj.model.models.param.paramecran;

import fr.amapj.model.engine.metadata.MetaDataEnum;

public enum PresentationImpressionContrat 
{
	
	ENGAGEMENT_FIRST ,
	// 
	CONTRAT_FIRST ,
	// 
	MELANGE;
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add(ENGAGEMENT_FIRST,"La liste de tous les contrats d'engagement en premier, puis la liste de toutes les feuilles de distribution amapien");
			add(CONTRAT_FIRST,"La liste de toutes les feuilles de distribution amapien en premier, puis la liste de tous les contrats d'engagement");
			add(MELANGE,"Pour chaque contrat, son contrat d'engagement et sa feuille de distribution amapien") ;
			
		}
	}	
	
	
}
