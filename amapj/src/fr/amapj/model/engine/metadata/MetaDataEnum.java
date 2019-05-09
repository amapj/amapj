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
 package fr.amapj.model.engine.metadata;

import java.util.HashMap;
import java.util.Map;

import fr.amapj.common.AmapjRuntimeException;


/**
 * Permet de gerer les meta data des Enum 
 *
 */
abstract public class MetaDataEnum
{
	private HelpInfo helpInfo = new HelpInfo();
	
	
	/**
	 * Permet de déclarer le contenu de l'aide pour cette combo box
	 */
	abstract public void fill();
	
	
	/**
	 * Ajout d'un texte 
	 */
	public void add(String texte)
	{
		helpInfo.fullText = helpInfo.fullText+texte+"<br/>";
	}
	
	/**
	 * Texte pour chaque élément de l'énumération 
	 */
	public void add(Enum en,String lib)
	{
		helpInfo.libs.put(en, lib);
		helpInfo.fullText = helpInfo.fullText+"<br/><b>"+lib+"</b><br/>";
	}
	
	/**
	 * Texte pour chaque élément de l'énumération 
	 */
	public void add(Enum en,String lib, String aide)
	{
		helpInfo.libs.put(en, lib);
		helpInfo.fullText = helpInfo.fullText+"<br/><b>"+lib+"</b><br/>"+aide+"<br/>";
	}
	
	public HelpInfo getHelpInfo()
	{
		return helpInfo;
	}

	
	static public class HelpInfo
	{
		String fullText="";
		Map<Enum,String> libs = new HashMap<Enum, String>();
		
		public String getLib(Enum en)
		{
			String lib = libs.get(en);
			if (lib==null)
			{
				lib = en.toString();
			}
			return lib;
		}
		
		public String getFullText()
		{
			return fullText;
		}
	}
	
	
	/**
	 * Permet de construire les informations pour l'aide à partir de la classe d'un enum, contenant lui meme 
	 * une sous classe implementant AbstractEnumHelp
	 * 
	 * Retourne null si il n'y a pas une sous classe implementant AbstractEnumHelp
	 */
	static public HelpInfo getHelpInfo(Class en)
	{
		try
		{
			Class[] cls = en.getDeclaredClasses();
			for (Class cl : cls)
			{
				if (MetaDataEnum.class.isAssignableFrom(cl))
				{
					MetaDataEnum enumHelp = ( (MetaDataEnum) cl.newInstance());
					enumHelp.fill();
					return enumHelp.getHelpInfo();
				}
			}
		} 
		catch (SecurityException | InstantiationException | IllegalAccessException e)
		{
			throw new AmapjRuntimeException(e);
		}
		return null;
	}
	
	
}
