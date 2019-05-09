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

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;

import fr.amapj.common.DateUtils;
import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.db.DbManager;
import fr.amapj.service.services.appinstance.DemarrageAppInstanceService;
import fr.amapj.service.services.backupdb.BackupDatabaseService;
import fr.amapj.service.services.logview.LogDeleteService;
import fr.amapj.service.services.notification.NotificationService;
import fr.amapj.view.engine.ui.AppConfiguration;

/**
 * Initialisation de l'application
 * 
 * Réalise dans l'ordre : 
 * -> le chargement du fichier de configuration 
 * -> le démarrage de la base 
 * -> le démarrage des démons
 * 
 * 
 */
@WebListener
public class AppInitializer implements ServletContextListener
{
	private final static Logger logger = LogManager.getLogger();

	static public DbManager dbManager = new DbManager();

	//
	Scheduler sched;

	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		logger.info("Debut de l'arret de l'application");

		// Arret des deamons
		logger.info("Debut de l'arret des démons");
		stopDeamons();
		logger.info("Demons arretes");

		// Arret de la base
		logger.info("Debut de l'arret des DBMS");
		dbManager.stopAllDbms();
		logger.info("DBMS arretes");

		// De enregistrement des drivers
		deregisterDriver();
	}

	private void deregisterDriver()
	{
		// This manually deregisters JDBC driver, which prevents Tomcat 7 from
		// complaining about memory leaks wrto this class
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements())
		{
			Driver driver = drivers.nextElement();
			try
			{
				DriverManager.deregisterDriver(driver);
				logger.info(String.format("deregistering jdbc driver: %s", driver));
			}
			catch (SQLException e)
			{
				logger.warn(String.format("Error deregistering driver %s", driver) + e.getMessage());
			}

		}

	}

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		// TODO à décommenter quand log4J sera installé correctement en mode web  
		// logger.info("Debut de l'initialisation de l'application");

		// Chargement du fichier de configuration
		AppConfiguration.load(new StandardServletParameter(event.getServletContext()));
		
		// Démarrage de Velocity
		VelocityInitializer.load();
		
		//Affichage du charset
		logger.info("Utilisation du charset : "+Charset.defaultCharset());

		// Demarrage des DBMS
		logger.info("Debut de démarrage des DBMS");
		dbManager.startAllDbms();
		logger.info("Fin de démarrage des DBMS");
		
		// Demarrage de la base Master
		logger.info("Debut de démarrage de la base MASTER");
		dbManager.startMasterBase();
		logger.info("Fin de démarrage de la base MASTER");
		
		// Nettoyage des logs dans la base MASTER
		new LogDeleteService().cleanLogFile();
		
		// Demarrage des bases additionnelles
		logger.info("Debut de démarrage des bases additionnelles");
		new DemarrageAppInstanceService().startAllDbs();
		logger.info("Fin de démarrage des bases additionnelles");
				
		
		// Demarrage des deamons
		logger.info("Debut de démarrage des démons");
		startDeamons();
		logger.info("Demons démarrés");

	}

	private void startDeamons()
	{

		try
		{
			// Les demons se lancent 10 minutes après le lancement de l'application 
			Date ref = DateUtils.addMinute(new Date(),10);
			
			SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

			sched = schedFact.getScheduler();

			sched.start();
			
			
			// Déclaration du service de notification, qui s'active toutes les heures
			JobDetail job = newJob(NotificationService.class).withIdentity("myJob1", "group1").build();

			Trigger trigger = newTrigger().withIdentity("myTrigger1", "group1").startAt(ref)
					.withSchedule(simpleSchedule().withIntervalInHours(1).repeatForever()).build();

			sched.scheduleJob(job, trigger);

			// Déclaration du service de backup, qui s'active tous les matins à 5:00
			job = newJob(BackupDatabaseService.class).withIdentity("myJob2", "group2").build();
			trigger = newTrigger().withIdentity("myTrigger2", "group2").startAt(ref).withSchedule(dailyAtHourAndMinute(5, 0)).build();
			sched.scheduleJob(job, trigger);

			
			// Déclaration du service d'effacement des logs, qui s'active tous les matins à 4:00
			job = newJob(LogDeleteService.class).withIdentity("myJob3", "group3").build();
			trigger = newTrigger().withIdentity("myTrigger3", "group3").startAt(ref).withSchedule(dailyAtHourAndMinute(4, 0)).build();
			sched.scheduleJob(job, trigger);

			
		}
		catch (SchedulerException e)
		{
			logger.warn("Impossible de demarrer correctement des démons" + StackUtils.asString(e));
		}
	}

	private void stopDeamons()
	{
		// Dans le cas ou l'application n'a pas pu démarrer, on ne peut pas arreter le scheduler
		if (sched==null)
		{
			return;
		}
		
		try
		{
			sched.shutdown();
		}
		catch (SchedulerException e)
		{
			logger.warn("Impossible d'arreter correctement des démons" + StackUtils.asString(e));
		}

	}
}
