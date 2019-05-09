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
 package fr.amapj.model.models.editionspe;

import fr.amapj.model.engine.metadata.MetaDataEnum;


public enum PageFormat 
{
	// 
	A4_PORTRAIT ,
	
	// 
	A4_PAYSAGE ,
	
	//
	A3_PORTRAIT ,
	
	//
	A3_PAYSAGE ;

	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{		
			add(A4_PORTRAIT,"A4 Portrait");
			add(A4_PAYSAGE,"A4 Paysage");
			add(A3_PORTRAIT,"A3 Portrait");
			add(A3_PAYSAGE,"A3 Paysage");
			
		}
	}	
	

}
