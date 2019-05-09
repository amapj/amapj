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
 package fr.amapj.view.engine.widgets;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;


/**
 * Gestion des quantités
 *
 */
public class QteTextFieldConverter implements Converter
{


	public QteTextFieldConverter()
	{
	}

	public Integer convertToModel(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value==null)
		{
			return null;
		}
		
		Integer i = convertToInteger((String) value);
		if (i==null)
		{
			throw new ConversionException("Valeur incorrecte");
		}
		return i;
	}

	public Object convertToPresentation(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value != null)
		{
			Integer l = (Integer) value;
			if (l.intValue()==0)
			{
				return "";
			}
			else
			{
				return l.toString();
			}
		}
		return null;
	}

	public Class<Integer> getModelType()
	{
		return Integer.class;
	}

	public Class<String> getPresentationType()
	{
		return String.class;
	}
	
	
	/**
	 * Cette méthode convertit une chaine de caractères de la forme
	 * 
	 * 123   en un Integer 
	 * 
	 * Return null si invalide 
	 * 
	 * @param value ne doit pas être null 
	 * @return
	 */
	public Integer convertToInteger(String str)
	{
		// Suppression des espaces
		str = str.trim();
				
		if (str.length()==0)
		{
			return new Integer(0);
		}
		
		// Uniquement des chiffres
		if (str.matches("^[0-9]*"))
		{
			return new Integer(str);
		}
		
		return null;
			
	}

	

}
