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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;


/**
 * Gestion générale des dates 
 *
 */
public class DateUtils
{
	
	static private Date fixedDate = null;
	
	/**
	 * Permet de récupérer la date courante  , avec l'heure et les minutes
	 * 
	 * En mode développeur, il est possible de fixer cette valeur 
	 */
	static public Date getDate()
	{
		if (fixedDate!=null)
		{
			return fixedDate;
		}
		return new Date();
	}
	
	
	static public void developperModeSetDate(String dateTime)
	{
		String pattern = "dd/MM/yyyy HH:mm:ss";
		if (pattern.length()!=dateTime.length())
		{
			throw new AmapjRuntimeException("Erreur sur le format de la date");
		}
		
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try
		{
			fixedDate = df.parse(dateTime);
		} 
		catch (ParseException e)
		{
			throw new AmapjRuntimeException("Erreur sur date :"+dateTime);
		}
	}
	
	
	
	/**
	 * Permet de récupérer la date courante  , sans  l'heure et les minutes
	 */
	static public Date getDateWithNoTime()
	{
		return suppressTime(getDate());
	}

	
	

	static public Date suppressTime(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);

		return c.getTime();
	}

	/**
	 * Retrouve le premier lundi avant cette date, et supprime la notion de
	 * temps
	 */
	static public Date firstMonday(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);

		c.setTime(d);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);

		// Retourne 1 pour dimanche, 2 pour lundi, ...
		int delta = c.get(Calendar.DAY_OF_WEEK);

		if (delta == 1)
		{
			c.add(Calendar.DAY_OF_MONTH, -6);
		} 
		else
		{
			c.add(Calendar.DAY_OF_MONTH, 2 - delta);
		}

		return c.getTime();
	}
	
	/**
	 * Retrouve le premier jour du mois indiqué par d
	 */
	public static Date firstDayInMonth(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);

		c.setTime(d);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);

		// Retourne 1 pour dimanche, 2 pour lundi, ...
		int delta = c.get(Calendar.DAY_OF_MONTH);

		c.add(Calendar.DAY_OF_MONTH, 1-delta);

		return c.getTime();
	}
	
	
	/**
	 * Retrouve le numero du jour dans le mois, 
	 * 
	 * c'est a dire 12 pour le 12/03/2014
	 */
	static public int getDayInMonth(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		return c.get(Calendar.DAY_OF_MONTH);

	}
	
	
	

	/**
	 * Retrouve le numero du jour dans le mois, c'est a dire le 1er jeudi du
	 * mois ou le 2eme ou le 3eme ou le ...
	 */
	static public int getDayOfWeekInMonth(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		return c.get(Calendar.DAY_OF_WEEK_IN_MONTH);

	}

	public static Date addDays(Date date, int amount)
	{

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.add(Calendar.DAY_OF_MONTH, amount);
		return c.getTime();
	}
	
	public static Date addHour(Date date, int amount)
	{

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.add(Calendar.HOUR_OF_DAY, amount);
		return c.getTime();
	}
	
	public static Date addMinute(Date date, int amount)
	{

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.add(Calendar.MINUTE, amount);
		return c.getTime();
	}
	
	
	/**
	 * Permet d'ajouter x mois à la date courante 
	 * 
	 */
	public static Date addMonth(Date date, int amount)
	{

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.add(Calendar.MONTH, amount);
		return c.getTime();
	}
	
	/**
	 * Permet d'ajouter x ans à la date courante 
	 * 
	 */
	public static Date addYear(Date date, int amount)
	{

		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.add(Calendar.YEAR, amount);
		return c.getTime();
	}
	

	public static int getDeltaDay(Date d1, Date d2)
	{
		// TODO ce n'est pas bon aux changements d'heure ete hiver
		return (int) ((d2.getTime()-d1.getTime())/(1000L*3600L*24L));
	}

	
	public static boolean equals(Date d1,Date d2)
	{
		if ( (d1==null) && (d2==null) )
		{
			return true;
		}
		
		if ( (d1==null) || (d2==null) )
		{
			return false;
		}
		
		return d1.equals(d2);
	}
	
	
	/**
	 * Retourne true si la date est dans l'intervalle
	 * 
	 * Les bornes de l'intervalles sont des dates valides
	 * 
	 */
	public static boolean isInIntervalle(Date ref, Date debut, Date fin)
	{
		if (ref.after(fin))
		{
			return false;
		}
		
		if (ref.before(debut))
		{
			return false;
		}

		return true;
	}
	
	

	/**
	 * Permet de fixer la date 
	 */
	public static void main(String[] args) throws ParseException
	{
		Date d = new SimpleDateFormat("dd/MM/yyyy").parse("17/09/2013");

		System.out.println("d=" + addMonth(d,1));
	}


	public static LocalDateTime getLocalDateTime()
	{
		return asLocalDateTime(getDate());
		
	}
	
	public static LocalDate getLocalDate()
	{
		return asLocalDate(getDate());
		
	}

	
	
	public static Date asDate(LocalDate localDate)
	{
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime)
	{
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date)
	{
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date)
	{
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	

}
