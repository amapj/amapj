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

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.util.IOUtils;

public class ResourceUtils
{
	/**
	 * Permet de charger un fichier resource sous forme de String
	 * 
	 * @param resourceName exemple /amapj_version.txt pour le chemin complet 
	 */
	static public String toString(Object ref,String resourceName)
	{
		try
		{
			InputStream in = ref.getClass().getResourceAsStream(resourceName);
			byte[] bs = IOUtils.toByteArray(in);
			return new String(bs);
		} 
		catch (IOException e)
		{
			throw new AmapjRuntimeException();
		}
	}
	
	
	/**
	 * Permet de charger un fichier resource sous forme de String
	 * 
	 * @param resourceName exemple /amapj_version.txt pour le chemin complet 
	 */
	static public String toStringClass(Class clazz,String resourceName)
	{
		try
		{
			InputStream in = clazz.getResourceAsStream(resourceName);
			byte[] bs = IOUtils.toByteArray(in);
			return new String(bs);
		} 
		catch (IOException e)
		{
			throw new AmapjRuntimeException();
		}
	}
}
