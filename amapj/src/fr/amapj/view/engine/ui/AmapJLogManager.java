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
 package fr.amapj.view.engine.ui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.routing.RoutingAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.vaadin.ui.UI;

import fr.amapj.model.models.saas.TypLog;
import fr.amapj.service.services.session.SessionData;
import fr.amapj.service.services.session.SessionParameters;

/**
 * Gestion des logs<br/>
 * 
 * Il y a 3 types de fichiers de logs
 * <ul>
 * 	<li> Les fichiers de log attachés à une session utilisateur (une UI), ils ont pour nom : user__[dbName]__[date]__[id].log</li>
 *  <li> Les fichiers de log attachés à un démon , ils ont pour nom : deamon__[dbName]__[date].log</li>
 *  <li> Le fichier pour tout le reste , il a pour nom global.log</li>
 * </ul> 
 * 
 */
public class AmapJLogManager
{
	static public final String LOG4J_ID = "fileId";
	
	static public final String LOG4J_LOGDIR_ID = "amapjLogDir";
	
	static public final String FILE_USER = "user__";
	
	static public final String FILE_DEAMON = "deamon__";
	
	static public final String DATE_FORMAT = "yyyy_MM_dd__HH_mm_ss";
	
	private final static Logger logger = LogManager.getLogger();
	
	// Répertoire pour les logs
	private static String logDir;
	
	/**
	 * Cette méthode permet de créer le nom du fichier lors du logging de l'utilisateur ou d'un démon
	 * 
	 * On positionne aussi le contexte
	 * 
	 * @return
	 */
	public static String createLogFileName(String dbName,Long idLogAccess,Date dateIn,TypLog typLog) 
	{
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		String logFileName = dbName+"/"+((typLog==TypLog.USER) ? FILE_USER  : FILE_DEAMON) + dbName+"__"+df.format(dateIn)+"__"+idLogAccess;
		
		ThreadContext.put(LOG4J_ID,logFileName);
		
		return logFileName;
	}
	
	
	
		

	/**
	 * Initialisation des logs au démarrage d'une UI
	 */
	public static void initializeLogUI(UI ui) 
	{
		if ( (ui!=null) && (ui.getData()!=null) )
		{
			SessionParameters sessionParameters= ((SessionData) ui.getData()).sessionParameters;
			if (sessionParameters!=null)
			{
				ThreadContext.put(LOG4J_ID,sessionParameters.logFileName);
				return ;
			}
		}
		ThreadContext.put(LOG4J_ID,null);
	}

	/**
	 * Doit être utilisé pour terminer la session de logging
	 * A partir de ce moment là, tous les logs vont dans le fichier par défault
	 * 
	 * Si definitive est égal à true, alors le fichier est fermé et il ne sera plus possible de logguer dans ce contexte
	 * 
	 * Si definitive est égal à false, alors le fichier est laissé ouvert
	 * 
	 * fileNameToClose contient le nom du fichier a fermer, on ne le prend pas avec ThreadContext.get(LOG4J_ID) car on peut obtenir null
	 * dans certains cas (logout par time out par exemple) 
	 */
	public static void endLog(boolean definitive,String fileNameToClose)
	{
		if (definitive==true)
		{
			stopLogger(fileNameToClose);
		}
		
		ThreadContext.put(LOG4J_ID,null);
	}
	
	/**
	 * Doit être utilisé pour fermer le fichier de log 
	 * 
	 * @param l
	 */
	private static void stopLogger(String fileNameToClose) 
	{
		if (fileNameToClose==null)
		{
			logger.error("FUITE-FICHIER : impossible de fermer le fichier null");
			return;
		}
		
		org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) logger; 
		org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext)coreLogger.getContext(); 
		RoutingAppender appender = (RoutingAppender) context.getConfiguration().getAppender("Routing");
		
		
		try
		{
			Method method = appender.getClass().getDeclaredMethod("getControl",String.class,LogEvent.class);
			method.setAccessible(true);
			AppenderControl appenderControl = (AppenderControl) method.invoke(appender,fileNameToClose,null);
			appenderControl.getAppender().stop();
			
			// Attention : il faut bien ensuite enlever cet AppenderControl de la map appenders de RoutingAppender
			// Sinon il y a une fuite mémoire importante 
			Field f = appender.getClass().getDeclaredField("appenders");
			f.setAccessible(true);
			ConcurrentMap<String, AppenderControl> appenders = (ConcurrentMap<String, AppenderControl>) f.get(appender);
			appenders.remove(fileNameToClose);
			
		} 
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e)
		{
			// Shouldn't happen - log anyway 
			logger.error("Unable to close the logger",e);
		}
	}
	
	
	/**
	 * Permet de positionner le niveau de log de façon programatique 
	 * @param level
	 */
	public static void setLevel(Level level)
	{
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		loggerConfig.setLevel(level);
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig
	}
	
	
	
	/**
	 * Permet de récuprer le niveau de log actuel  
	 * @param level
	 */
	public static Level getLevel()
	{
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
		return loggerConfig.getLevel();
		
	}
	
	

	
	
	
	/**
	 * Permet d'obtenir une référence vers le fichier sur le disque, en prenant 
	 * en compte le repertoire de stockage des fichiers
	 * 
	 * @param logFileName
	 * @return
	 */
	public static String getFullFileName(String logFileName)
	{
		return AmapJLogManager.logDir+"/"+logFileName+".log";
	}





	public static void setLogDir(String logDir)
	{
		AmapJLogManager.logDir = logDir;
		System.setProperty(LOG4J_LOGDIR_ID, logDir);
		
	}

}
