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


public enum RegleInscriptionPeriodePermanence
{
	// 
	UNE_INSCRIPTION_PAR_DATE,
	
	//  
	MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT,

	
	TOUT_AUTORISE;
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
		
			add("Ce champ vous permet de choisir les vérifications faites lors de l'inscription des adhérents aux permanences.");
			
			add(UNE_INSCRIPTION_PAR_DATE, "Inscription possible une seule fois sur une date" , "Dans ce mode, un adhérent peut s'inscrire une seule fois sur une date.<br/>Il ne peut pas s'insrire deux fois sur une même date, même sur deux rôles différents.");
			
			add(MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT, "Inscription possible plusieurs fois sur une date, mais sur des rôles différents","Dans ce mode, un adhérent peut s'inscrire plusieurs fois sur une même date, mais uniquement sur des rôles différents.<br/>Il ne peut pas s'insrire deux fois sur une même date sur le même rôle.");
			
			add(TOUT_AUTORISE, "Tout autorisé","Dans ce mode, l'adhérent peut s'inscrire autant de fois qu'il le veut sur une date, même sur le même rôle.<br/>");
		}
	}	
}
