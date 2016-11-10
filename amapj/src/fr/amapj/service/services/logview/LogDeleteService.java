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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fr.amapj.common.DateUtils;
import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.models.saas.LogAccess;
import fr.amapj.view.engine.ui.AmapJLogManager;


public class LogDeleteService implements Job
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public LogDeleteService()
	{
		
	}
	
	/**
	 * Permet l'effacement de tous les logs plus vieux de 30 jours dans la base master
	 * les logs avec erreur sont conservés par contre pendant 90 jours
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		NewTransaction.writeInMaster(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				deleteAllLog(em);
				return null;
			}
		});
		
	}
	
	
	private void deleteAllLog(EntityManager em)
	{
		Query q = em.createQuery("select a from LogAccess a where "
				+ " ( (a.dateOut<=:d1 and a.nbError=0) or (a.dateOut<=:d2 and a.nbError>0) ) "
				+ " and a.logFileName is not null");

		Date now = DateUtils.getDate();
		Date d1 = DateUtils.addDays(now, -30);
		Date d2 = DateUtils.addDays(now, -90);
		q.setParameter("d1", d1);
		q.setParameter("d2", d2);
		
		List<LogAccess> ps = q.getResultList();
		for (LogAccess logAccess : ps)
		{
			deleteLogFile(logAccess);
			logAccess.setLogFileName(null);
		}
		
	}

	private void deleteLogFile(LogAccess logAccess)
	{
		try
		{
			Path path = Paths.get(AmapJLogManager.getFullFileName(logAccess.getLogFileName()));
			Files.delete(path);
		} 
		catch (IOException e)
		{
			logger.error("Impossible d'effacer le fichier de log : "+StackUtils.asString(e));
		}
	}

	
	
	// MISE A JOUR DES LOGS AU DEMARRAGE DE L'APPLICATION

	/**
	 * Permet de positionner une date de fin sur  tous les logs sans date de fin 
	 * 
	 *  Doit être appelé au démarrage de l'application, pour cloturer les sessions 
	 *  qui se sont mal terminées lors du dernier arrêt
	 */
	public void cleanLogFile()
	{
		NewTransaction.writeInMaster(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				cleanLogFile(em);
				return null;
			}
		});
		
	}
	
	
	private void cleanLogFile(EntityManager em)
	{
		Query q = em.createQuery("select a from LogAccess a where a.dateOut is null");
		
		List<LogAccess> ps = q.getResultList();
		
		Date ref = DateUtils.getDate();
		
		for (LogAccess logAccess : ps)
		{
			logAccess.setDateOut(ref);
			// On poistionne une valeur arbitraire à 1 minute
			logAccess.setActivityTime(60);
		}
		
	}

}
