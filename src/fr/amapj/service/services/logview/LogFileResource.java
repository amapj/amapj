/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.service.services.logview;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.vaadin.server.StreamResource;

import fr.amapj.common.StackUtils;
import fr.amapj.view.engine.ui.AmapJLogManager;


public class LogFileResource implements StreamResource.StreamSource
{

	
	String logFileName;

	public LogFileResource(String fullFileName)
	{
		this.logFileName = fullFileName;
	}

	@Override
	public InputStream getStream()
	{
		try
		{
			return new FileInputStream(AmapJLogManager.getFullFileName(logFileName));
		}
		catch(FileNotFoundException e)
		{
			String message ="Impossible de charger le fichier."+StackUtils.asString(e);
			return new ByteArrayInputStream(message.getBytes());
		}
		
	}

}
