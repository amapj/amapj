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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import fr.amapj.common.GenericUtils.GetFieldTyped;

/**
 * Outils generique pour le formatage des dates et autres 
 */

public class FormatUtils
{
	

	// PARTIE FORMATTAGE DE LA DATE + HEURE  
	
	/**
	 * Retourne un formatter au format dd/MM/yyyy HH:mm:ss
	 */
	static public SimpleDateFormat getTimeStd()
	{
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}
	
	
	
	// PARTIE FORMATTAGE DE LA DATE D'UN JOUR 

	/**
	 * Retourne un formatter de date au format classique dd/MM/yy 
	 */
	static public SimpleDateFormat getStdDate()
	{
		return new SimpleDateFormat("dd/MM/yy");
	}
	
	/**
	 * Retourne un formatter de date au format complet  
	 * 
	 * Exemple : lundi 12 mars 2018
	 */
	static public SimpleDateFormat getFullDate()
	{
		return new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	}
	
	
	/**
	 * Retourne un formatter de date pour les noms de fichiers 
	 * 
	 * Exemple : lundi 12 mars 2018
	 */
	static public SimpleDateFormat getDateFile()
	{
		return new SimpleDateFormat("dd-MM-yyyy");
	}
	
	
	
	// PARTIE FORMATTAGE DE LA DATE D'UN MOIS
	
	
	/**
	 * Retourne un formatter de mois pour les noms de fichiers 
	 * 
	 * Exemple : 03-2018
	 */
	static public SimpleDateFormat getMoisFile()
	{
		return new SimpleDateFormat("MM-yyyy");
	}
	
	
	/**
	 * Retourne un formatter de mois au format full text
	 * 
	 * Exemple : Mars 2018
	 */
	static public SimpleDateFormat getMoisFullText()
	{
		return new SimpleDateFormat("MMMMM yyyy");
	}
	
	
	// PARTIE FORMATTAGE DE LA DATE D'UN TRIMESTRE
	
	
	/**
	 * Permet de formatter un trimestre pour les noms de fichiers 
	 * 
	 * Exemple : Q1-2018
	 */
	static public String formatTrimestreFile(Date startDate)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Q-yyyy");
		return "T"+DateUtils.asLocalDate(startDate).format(formatter);

	}
	
	
	/**
	 * Retourne un formatter de trimestre au format full text
	 * startDate doit être la date du premier jour du trimestre 
	 */
	static public String  formatTrimestreFullText(Date startDate)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
		LocalDate l = DateUtils.asLocalDate(startDate);
		
		String str = l.format(formatter);
		l=l.plusMonths(1);
		str = str+" - "+l.format(formatter);
		l=l.plusMonths(1);
		str = str+" - "+l.format(formatter);
		
		formatter = DateTimeFormatter.ofPattern("yyyy");
		
		str = str+" - "+l.format(formatter);
		
		return str;
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
	
	
	/**
	 * Permet le formatage intelligent d'une liste de date 
	 * 
	 * Si la liste est vide : retourne ""
	 * 
	 * Si la liste contient une date : retourne "le 12/05/17"
	 * 
	 * Si la liste contient deux dates : retourne "les 12/05/17 et 19/05/17"
	 * 
	 * Si la liste contient trois dates et plus  : retourne "les 12/05/17, 19/05/17 et 26/05/17"
	 * 
	 */
	static public String listeDate(List<Date> dates)
	{
		SimpleDateFormat df = getStdDate();
		
		if (dates.size()==0)
		{
			return "";
		}
		
		if (dates.size()==1)
		{
			return "le "+df.format(dates.get(0));
		}
		
		StringBuilder buf = new StringBuilder();
		buf.append("les ");
		for (int i = 0; i < dates.size()-2; i++)
		{
			buf.append(df.format(dates.get(i))+", ");
		}
		buf.append(df.format(dates.get(dates.size()-2))+" et ");
		buf.append(df.format(dates.get(dates.size()-1)));
		
		return buf.toString();
	}
	
	/**
	 * Permet le formatage d'une liste de date dans une liste à puce 
	 * 
	 */
	static public <T> String puceDate(List<T> dates,GetFieldTyped<T,Date> f1)
	{
		SimpleDateFormat df = getStdDate();
		StringBuilder sb = new StringBuilder();
		
		sb.append("<ul>");
		
		for (T date : dates)
		{
			Date d = f1.getField(date);
			sb.append("<li>");
			sb.append(df.format(d));
			sb.append("</li>");
		}
		
		sb.append("</ul>");
		
		
		return sb.toString();
	}
	
	/**
	 * Permet le formatage d'une liste de date dans une liste à puce 
	 * 
	 */
	static public String puceDate(List<Date> dates)
	{
		return puceDate(dates, e->e);
	}
}
