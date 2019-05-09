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
 package fr.amapj.model.engine.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;


/**
 * Gestion basique des logs de EclipseLink 
 */
public class EclipseLinkLogger extends AbstractSessionLog implements SessionLog 
{
	private final static Logger log = LogManager.getLogger();
	
	private final static Logger logSQL = LogManager.getLogger("SQL");

	@Override
	public void log(SessionLogEntry entry) 
	{
		// On ignore les messages de type metadata
		if ( entry.getNameSpace()!=null && entry.getNameSpace().equals("metadata"))
		{
			return ;
		}
		
		if ( entry.getNameSpace()!=null && entry.getNameSpace().equals("sql"))
		{
			logSQL.debug(entry.getMessage());
			return;
		}
		
		int level = entry.getLevel();
		String message = entry.getMessage();
		if (entry.getParameters() != null) 
		{
			message += " [";
			int index = 0;
			for (Object object : entry.getParameters()) 
			{
				message += (index++ > 0 ? "," : "") + object;
			}
			message += "]";
		}
		switch (level) 
		{
		case SessionLog.SEVERE:
			log.error(message);
			break;
		case SessionLog.WARNING:
			log.warn(message);
			break;
		case SessionLog.INFO:
			log.info(message);
			break;
		case SessionLog.CONFIG:
			log.info(message);
			break;
		default:
			log.debug(message);
			break;
		}
	}
}