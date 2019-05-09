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

public enum ChoixImpressionBilanLivraison
{
	// 
	TABLEUR ,
	
	// 
	PDF ,
	
	TABLEUR_ET_PDF;
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add(	"Ce champ vous permet de choisir le format des documents qui seront disponibles.<br/>"
					+ "Le format tableur est le plus simple, il permet d'avoir accès à un document générique qui devrait convenir dans la plupart des cas.<br/>"
					+ "Le format PDF nécessite plus de paramètrage, mais il permet par contre de disposer d'un document personnalisé.");

			add(TABLEUR, "Format Tableur" , "Un document tableur (format Excel XLS) sera proposé à l'utilisateur.");
			
			add(PDF, "Format PDF","Un document format PDF sera proposé à l'utilisateur, mais vous devrez paramétrer ce document en créant une édition spécifique (Menu Tresorier / Editions spécifiques).");
			
			add(TABLEUR_ET_PDF, "Format Tableur et PDF","L'utilisateur aura accès à deux documents, un dans chaque format.");
		}
	}
}
