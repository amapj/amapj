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
import java.util.Set;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Simplifie la gestion des stacks d'erreurs
 * 
 *  
 */
public class StackUtils 
{
	
	public static final String SEP = System.getProperty("line.separator");

	/**
	 * 
	 */
	static public void popStack(List<String> messages,Throwable e1)
	{
		if (e1==null)
		{
			return ;
		}
		
		messages.add(e1.getClass().toString()+" : "+e1.getMessage());
		StackTraceElement[] elts = e1.getStackTrace();
		for (int i = 0; i < elts.length; i++)
		{
			messages.add("at "+elts[i].toString());
		}
		
		if (e1.getCause()!=null)
		{
			messages.add("Cause : ");
			popStack(messages,e1.getCause());
		}
	}
	
	/**
	 * Retourne la stack complete sous forme de String
	 * 
	 * @param e1
	 */
	static public String asString(Throwable e1)
	{
		StringBuffer result = new StringBuffer();
		List<String> messages = new ArrayList<String>();
		popStack(messages,e1);
		
		for (String str : messages)
		{
			result.append(str);
			result.append(SEP);
		}
		return result.toString();
	}
	
	
	/**
	 * Retourne la stack complete sous forme de String
	 * 
	 * @param e1
	 */
	static public String getConstraints(ConstraintViolationException e)
	{
		StringBuffer result = new StringBuffer();
		Set<ConstraintViolation<?>> set = e.getConstraintViolations();
		
		for (ConstraintViolation<?> constraintViolation : set)
		{
			result.append("Le champ ");
			result.append(constraintViolation.getPropertyPath());
			result.append(" : ");
			result.append(constraintViolation.getMessage());
			result.append(". Valeur incorrecte : ");
			result.append(constraintViolation.getInvalidValue());
			
			result.append(SEP);
		}
		
		return result.toString();
	}
	

}
