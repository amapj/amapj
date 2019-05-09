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
 package fr.amapj.common;


public class LongUtils
{
	
	/**
	 * L'objet en parametre doit être un Long ou null
	 * 
	 */
	public static int toInt(Object o)
	{
		if (o==null)
		{
			return 0;
		}
		return ( (Long) o).intValue();
	}

	public static boolean equals(Long id1, Long id2)
	{
		if ( (id1==null) && (id2==null))
		{
			return true;
		}
		
		if ( (id1==null) || (id2==null))
		{
			return false;
		}
		
		return id1.equals(id2);
	}
}
