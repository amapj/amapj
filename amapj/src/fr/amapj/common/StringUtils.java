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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtils
{
	public static String sansAccent(String s)
	{
		final String accents = "ÀÁÂÃÄÅàáâãäåÈÉÊËèéêëîïùû"; 
		final String letters = "AAAAAAaaaaaaEEEEeeeeiiuu"; 

		StringBuffer buffer = null;
		for (int i = s.length() - 1; i >= 0; i--)
		{
			int index = accents.indexOf(s.charAt(i));
			if (index >= 0)
			{
				if (buffer == null)
				{
					buffer = new StringBuffer(s);
				}
				buffer.setCharAt(i, letters.charAt(index));
			}
		}
		return buffer == null ? s : buffer.toString();
	}
	
	
	public static boolean equalsIgnoreCase(String s1,String s2)
	{
		if (s1==null)
		{
			s1 = "";
		}
		
		if (s2==null)
		{
			s2 = "";
		}
		
		return s1.equalsIgnoreCase(s2);
	}
	
	public static boolean equals(String s1,String s2)
	{
		if (s1==null)
		{
			s1 = "";
		}
		
		if (s2==null)
		{
			s2 = "";
		}
		
		return s1.equals(s2);
	}
	
	
	
	/**
	 * Convertit une liste d'objet en une String 
	 * Exemple : ls = [ "Bob" , "Marc" , "Paul" ]
	 * 
	 *  asString(ls,",") =>  "Bob,Marc,Paul"
	 * 
	 */
	public static String asString(List ls,String sep)
	{
		if (ls.size()==0)
		{
			return "";
		}
		
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < ls.size()-1; i++)
		{
			Object l = ls.get(i);
			str.append(l.toString());
			str.append(sep);
		}
		
		Object l = ls.get(ls.size()-1);
		str.append(l.toString());
		return str.toString();
	}
	
	
	/**
	 * Convertit un tableau d'objet en une String 
	 * Exemple : ls = [ "Bob" , "Marc" , "Paul" ]
	 * 
	 *  asString(ls,",") =>  "Bob,Marc,Paul"
	 * 
	 */
	public static String asString(Object[] ls,String sep)
	{
		List l = new ArrayList();
		for (int i = 0; i < ls.length; i++)
		{
			l.add(ls[i]);
		}
		return asString(l, sep);
	}
	
	
	public static String asHtml(String htmlContent)
	{
		if (htmlContent==null)
		{
			return null;
		}
		
		// Mise en place des <br/>
		htmlContent = htmlContent.replaceAll("\r\n", "<br/>");
		htmlContent = htmlContent.replaceAll("\n", "<br/>");
		htmlContent = htmlContent.replaceAll("\r", "<br/>");
		
		return htmlContent;
	}
	
	
	public static List<String> asList(String input)
	{
		List<String> res = new ArrayList<String>();
		
		if (input==null)
		{
			return res;
		}
		
		input = input.replaceAll("\r\n", "\n");
		input = input.replaceAll("\r", "\n");
		
		String[] rs = input.split("\n");
		
		for (int i = 0; i < rs.length; i++)
		{
			res.add(rs[i]);
		}
		return res;
	}
	
	/**
	 * Supprime la dernière chaine character de content si content se termine par character
	 * 
	 * @param character
	 * @return
	 */
	public static String removeLast(String content, String character)
	{
		if (content==null)
		{
			return null;
		}
		
		if (content.endsWith(character)==false)
		{
			return content;
		}
		
		return content.substring(0,content.length()-character.length());
		
	}
	
	
	public static void main(String[] args)
	{
		//List<String> res = asList("  \n toto \ntiti ");
		List<String> res = asList("titi");
		for (String string : res)
		{
			System.out.println("str="+string+"!");
		}
	}

}
