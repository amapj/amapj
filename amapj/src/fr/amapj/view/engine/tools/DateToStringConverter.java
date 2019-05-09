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
 package fr.amapj.view.engine.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * 
 */
public class DateToStringConverter implements Converter<String, Date>
{
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale) throws ConversionException
	{
		// TODO
		return null;
	}

	@Override
	public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale) throws ConversionException
	{
		if (value==null)
		{
			return "";
		}
		return df.format(value);
	}

	@Override
	public Class<Date> getModelType()
	{
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType()
	{
		return String.class;
	}

}
