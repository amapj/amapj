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
 package fr.amapj.service.engine.generator;

public class MiseEnPageUtils
{

	/**
	 * Convertit une distance en mm en une distanc en Point 
	 * @param sizeInMm
	 * @return
	 */
	static public int toPoints(int sizeInMm)
	{
		// Une page A4 fait 595x842 points , 595 correspond donc à un coté de 210 mm  
		double points = (sizeInMm * 595.0d) / 210.0d ;
		return (int) Math.round(points);
	}
	
	
	
}
