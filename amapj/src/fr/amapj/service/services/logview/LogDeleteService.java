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
import fr.amapj.common.SQLUtils;
import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.saas.LogAccess;
import fr.amapj.model.models.saas.TypLog;
import fr.amapj.service.engine.deamons.DeamonsUtils;
import fr.amapj.view.engine.ui.AmapJLogManager;


/**
 * Ce service permet deux choses
 *  - l'effacement des fichiers de logs au bout d'un certain temps (30 jours typiquement)
 *  - l'effacement de la trace de la connexion de l'utilisateur (90 jours typiquement) 
 *
 */
public class LogDeleteService implements Job
{
	private final static Logger logger = LogManager.getLogger();
	
	/**
	 * TODO : 
	 * 
	 * -> voir le time out (30 secondes) pour le transfert de la base
	 * 
	 * 
	 * 
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		DeamonsUtils.executeAsDeamonInMaster(getClass(), e->deleteOldLogFiles(),e->deleteOldLogAccess());
	}
	
	
	
	
	/**
	 * Permet l'effacement de tous les fichiers de logs plus vieux de 30 jours 
	 * les fichiers de logs avec erreur sont conservés par contre pendant 90 jours
	 * 
	 */
	@DbWrite
	public void deleteOldLogFiles()
	{
		EntityManager em = TransactionHelper.getEm();
		
		logger.info("Debut de l'effacement des fichiers de logs");
		
		int nbFile = 0;
		
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
			nbFile++;
		}
		
		logger.info("Fin de l'effacement des fichiers de logs. "+nbFile+" fichiers effacés.");
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
	
	
	/**
	 * Permet l'effacement de tous les LogAccess qui n'ont plus de fichiers associées 
	 * 
	 * Les LogAccess des utilisateurs sont conservés 90 jours
	 * Les LogAccess des démons sont conservés 30 jours   
	 * 
	 */
	@DbWrite
	public void deleteOldLogAccess()
	{	
		
		EntityManager em = TransactionHelper.getEm();
		
		logger.info("Debut de l'effacement des LogAccess dans la base master");
		
		Query q = em.createQuery("select a from LogAccess a where "
				+ " ( (a.dateOut<=:d1 and a.typLog=:typLog1) or (a.dateOut<=:d2 and a.typLog=:typLog2) ) "
				+ " and a.logFileName is null");

		Date now = DateUtils.getDate();
		Date d1 = DateUtils.addDays(now, -30);
		Date d2 = DateUtils.addDays(now, -90);
		
		q.setParameter("d1", d1);
		q.setParameter("typLog1", TypLog.DEAMON);
		
		q.setParameter("d2", d2);
		q.setParameter("typLog2", TypLog.USER);
		
		int nb =  SQLUtils.deleteAll(em, q);
		
		logger.info("Fin de l'effacement des LogAcess de logs. "+nb+" lignes effacées.");
		
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
