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
 package fr.amapj.service.engine.appinitializer;

import javax.servlet.ServletContext;


/**
 * Permet de lire les paramètres dans le servlet context
 * 
 */
public class StandardServletParameter implements ServletParameter
{
	private ServletContext servletContext;
	
	
	public StandardServletParameter(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}
	
	

	public String read(String paramName)
	{
		return servletContext.getInitParameter(paramName);
	}
	
	public String read(String paramName,String defaultValue)
	{
		String str= servletContext.getInitParameter(paramName);
		if ( (str==null) || (str.length()==0))
		{
			return defaultValue;
		}
		return str;
	}



	@Override
	public String getContextPath()
	{
		return servletContext.getContextPath();
	}
}
