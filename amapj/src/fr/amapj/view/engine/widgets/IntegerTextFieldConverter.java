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

public class IntegerTextFieldConverter implements Converter
{


	public IntegerTextFieldConverter()
	{
	}

	public Integer convertToModel(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value!=null)
		{
			String str = (String) value;
			
			// Suppression des espaces
			str = str.trim();
			
			if (str.length()==0)
			{
				return new Integer(0);
			}
			
			return new Integer((String) str);
		}
		return null;
	}

	public Object convertToPresentation(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value != null)
		{
			Integer l = (Integer) value;
			return l.toString();
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

	

}
