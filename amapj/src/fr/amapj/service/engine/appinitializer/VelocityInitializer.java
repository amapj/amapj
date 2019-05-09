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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.RuntimeServices;

import fr.amapj.common.AmapjRuntimeException;

public class VelocityInitializer implements LogChute
{

	private final static Logger logger = LogManager.getLogger();
	
	
	static public void load()
	{
		VelocityInitializer velocityInitializer = new VelocityInitializer();
	
		try
		{
			/*
			 * register this class as a logger with the Velocity singleton (NOTE: this would not work for the non-singleton method.)
			 */
			Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, velocityInitializer);
			Velocity.init();
		} 
		catch (Exception e)
		{
			throw new AmapjRuntimeException(e);
		}
	}

	/**
	 * This init() will be invoked once by the LogManager to give you the current RuntimeServices intance
	 */
	public void init(RuntimeServices rsvc)
	{
		// do nothing
	}

	/**
	 * This is the method that you implement for Velocity to call with log messages.
	 */
	public void log(int level, String message)
	{
		logger.debug("Velocity=" + message);
	}

	/**
	 * This is the method that you implement for Velocity to call with log messages.
	 */
	public void log(int level, String message, Throwable t)
	{
		logger.debug("Velocity=" + message, t);
	}

	/**
	 * This is the method that you implement for Velocity to check whether a specified log level is enabled.
	 */
	public boolean isLevelEnabled(int level)
	{
		return logger.isDebugEnabled();
	}

}