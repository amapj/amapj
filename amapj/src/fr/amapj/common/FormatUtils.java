/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Outils generique pour le formatage
 * 
 *
 */

public class FormatUtils
{

	/**
	 * Retourne un formatter de date au format classique dd/MM/yy 
	 */
	static public SimpleDateFormat getStdDate()
	{
		return new SimpleDateFormat("dd/MM/yy");
	}
	
	/**
	 * Retourne un formatter de date au format complet  
	 */
	static public SimpleDateFormat getFullDate()
	{
		return new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	}
	
	
	/**
	 * Retourne la division de 2 entiers sous un format explicite 
	 * 
	 * Si la division entière est possible : retourne un entier
	 * Exemple (10,5) retourne la chaine de caractère "2"
	 * 
	 * Si la division entière n'est pas possible : retourne un nombre avec deux digits après la virgule
	 * Exemple (11,5) retourne la chaine de caractère "2.20"
	 */
	static public String div2int2digit(int a,int b)
	{
		if ( (a % b)==0)
		{
			return ""+a/b;
		}
		DecimalFormat df = new DecimalFormat(".00");
		return df.format( ((double) a) / ((double) b));
	}
	
	
}
