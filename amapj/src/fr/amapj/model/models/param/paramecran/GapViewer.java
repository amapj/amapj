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

public enum GapViewer
{
	WEEK , 
	
	MONTH ;
	
	/*DATE_PER_DATE;*/
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add("Ce champ vous permet de choisir le contenu affiché dans l'écran mes livraisons ");

			add(WEEK, "Semaine par semaine" , "Dans ce mode, la page affiche les livraisons prévues sur une semaine, et l'amapien peut naviguer entre les semaines");
			
			add(MONTH, "Mois par mois" , "Dans ce mode, la page affiche les livraisons prévues sur un mois, et l'amapien peut naviguer entre les mois");
			
			// add(DATE_PER_DATE, "Date par Date" , "Dans ce mode, la page affiche les livraisons prévues sur une journée, et l'amapien peut naviguer entre les journées. Les journées sans livraisons ne sont pas affichées.");
			
		}
	}	
}
