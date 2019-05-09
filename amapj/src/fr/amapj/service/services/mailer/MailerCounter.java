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
 package fr.amapj.service.services.mailer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.amapj.common.DateUtils;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.service.services.parametres.ParametresDTO;

/**
 * Permet de compter les mails envoyer par chaque entité, et de vérifier que l'on n'a pas dépassé le quota autorisé
 * 
 * 
 */
public class MailerCounter
{
	
	private final static Logger logger = LogManager.getLogger();

	private final static MailerCounter instance = new MailerCounter();

	private LoadingCache<MailKey, MailCount> cache;

	private MailerCounter()
	{
		cache = CacheBuilder.newBuilder().
				expireAfterWrite(1, TimeUnit.DAYS).
				build(new CacheLoader<MailKey, MailCount>()
						{
							public MailCount load(MailKey key)
							{
								return new MailCount();
							}
						});
	}
	
	
	/**
	 * Retourne true si cette base est autorisée à envoyer un mail supplémentaire, 
	 * false si le quota est atteint
	 * 
	 * @param param
	 * @param dbName
	 * @return
	 */
	static public boolean isAllowed(ParametresDTO param)
	{
		MailKey key = new MailKey();
		key.dbName = DbUtil.getCurrentDb().getDbName();
		key.date = DateUtils.getDateWithNoTime();
		
		MailCount mailCount = instance.cache.getUnchecked(key);
		
		logger.info("Check nb mails "+mailCount.nbMail);
		logger.info("Cache size "+instance.cache.size());
		
		
		if (mailCount.nbMail<param.sendingMailNbMax)
		{
			synchronized (mailCount)
			{
				mailCount.nbMail++;
			}
			return true;
		}
		else
		{
			return false;
		}			
	}
	
	/**
	 * Retourne le nombre de mails envoyés aujourd'hui
	 * par l'instance indiquée
	 * 
	 * @return
	 */
	public static int getNbMails(String dbName)
	{
		MailKey key = new MailKey();
		key.dbName = dbName;
		key.date = DateUtils.getDateWithNoTime();
		
		MailCount mailCount = instance.cache.getIfPresent(key);
		if (mailCount==null)
		{
			return 0;
		}
		return mailCount.nbMail;
	}
	
	/**
	 * Retourne le nombre de mails envoyés aujourd'hui
	 * par l'instance courante
	 * 
	 * @return
	 */
	public static int getNbMails()
	{
		return getNbMails(DbUtil.getCurrentDb().getDbName());
	}

	

	/**
	 * La clé permet de mémoriser la date du jour et le nom de la base
	 */
	static public class MailKey
	{
		public String dbName;

		public Date date;
		
		@Override
		public boolean equals(Object o)
		{
			MailKey k = (MailKey) o;
			return dbName.equals(k.dbName) && date.equals(k.date);
		}
		
		
		@Override
		public int hashCode() 
		{
			return dbName.hashCode();
		}
	}

	/**
	 * La valeur permet de mémoriser le nombre de mail envoyé
	 */
	static public class MailCount
	{
		public int nbMail;
	}

	
	
	
}
