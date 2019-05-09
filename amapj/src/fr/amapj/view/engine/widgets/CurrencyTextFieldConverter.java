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
 * Permet la saisie d'un prix
 * 
 * Le prix est stocké dans le modele sous la forme d'un Integer
 * 
 * Exemple : si le prix est 8.92 , alors le prix est stocké sour la forme 892
 * 
 *  
 *
 */
public class CurrencyTextFieldConverter implements Converter
{
	boolean allowNegativeNumber;
	
	public CurrencyTextFieldConverter()
	{
		this(false);
	}
	

	public CurrencyTextFieldConverter(boolean allowNegativeNumber)
	{
		this.allowNegativeNumber = allowNegativeNumber;
	}

	public Integer convertToModel(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value==null)
		{
			return null;
		}
		
		Integer i = convertToCurrency((String) value);
		if (i==null)
		{
			throw new ConversionException("Valeur incorrecte : "+value);
		}
		return i;
		
	}

	public Object convertToPresentation(Object value, Class targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException
	{
		if (value == null)
		{
			return null;
		}
		
		return convertToString((Integer) value); 
		
		
	}

	/**
	 * Formatage d'un prix sous la forme 123.23 Eu
	 * @param value
	 * @return
	 * 
	 */
	public String convertToString(Integer value)
	{
		int prix = value.intValue();
		
		if (prix<0)
		{
			return "-"+convertToString(-value);
		}
		
		int leftPart = prix/100;
		int rightPart = prix % 100;
		
		if (rightPart<10)
		{
			return leftPart+".0"+rightPart;
		}
		else
		{
			return leftPart+"."+rightPart;
		}
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
	 * 123  ou 123.1 ou 123.15 en un Integer apres une multiplication par 100
	 * 
	 * Les chaines 123.456 sont invalides, les nombres negatifs aussi
	 * si allowNegativeNumber == false
	 * 
	 * 
	 * Return null si invalide 
	 * 
	 * @param value ne doit pas être null 
	 * @return
	 */
	public Integer convertToCurrency(String str)
	{		
		// Suppression des espaces
		str = str.trim();
		
		// Remplacement des virgules par des .
		str = str.replace(',', '.');
		
		if (str.length()==0)
		{
			return new Integer(0);
		}
		
		if ((str.charAt(0)=='-') && (allowNegativeNumber==true))
		{
			str = str.substring(1);
			Integer a = convertToCurrency(str);
			if (a==null)
			{
				return null;
			}
			return new Integer(-a.intValue());
		}
		
		
		// Uniquement des chiffres
		if (str.matches("^[0-9]*"))
		{
			return new Integer(Integer.parseInt(str)*100);
		}
		
		// Uniquement des chiffres et un point à la fin 
		if (str.matches("^[0-9]*\\.") && str.length()>=2)
		{
			str = str.substring(0,str.length()-1);
			return new Integer(Integer.parseInt(str)*100);
		}
		
		// Uniquement des chiffres , un point et des chiffres (1 ou 2) 
		if (str.matches("^[0-9]*\\.[0-9]*") && str.length()>=2)
		{
			String[] toks = str.split("\\.");
			String tok1 = toks[0];
			String tok2 = toks[1];
			
			int racine = 0;
			if (tok1.length()>0)
			{
				racine = new Integer(Integer.parseInt(tok1)*100);
			}
			
			if (tok2.length()==1)
			{
				return new Integer(racine+Integer.parseInt(tok2)*10);
			} 
			else if (tok2.length()==2)
			{
				return new Integer(racine+Integer.parseInt(tok2));
			}
		}
		
		return null;
			
	}


}
