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
 package fr.amapj.model.engine.transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Outil permettant de faire une nouvelle transaction basique au sein d'une transaction existante
 * 
 * Attention : dans la méthode appelée, il ne faut faire aucun appel de service, car ils seraient
 * appeler dans l'ancienne transaction
 * 
 */
public class NewTransaction
{
	
	private final static Logger logger = LogManager.getLogger();
	
	/**
	 * Outil permettant de faire une nouvelle transaction basique au sein d'une transaction existante
	 * 
	 * Attention : dans la méthode appelée, il ne faire aucun appel de service, car ils seraient
	 * appelés dans l'ancienne transaction
	 */
	static public Object write(Call fn)
	{
		EntityManager em = DbUtil.createEntityManager();
		return write(fn, em);
	}
	
	static private Object write(Call fn,EntityManager em)
	{
		EntityTransaction transac = em.getTransaction();
		transac.begin();
		
		Object result = null;
		
		
		try
		{
			logger.info("Début d'une NOUVELLE transaction en ecriture");
			result = fn.executeInNewTransaction(em);
		}
		catch(Throwable t)
		{
			logger.info("Rollback d'une NOUVELLE transaction en ecriture");
			transac.rollback();
			em.close();
			throw t;
		}
		
		logger.info("Commit d'une NOUVELLE transaction en ecriture");
		transac.commit();
		em.close();
		return result;
		
	}
	
	
	/**
	 * Outil permettant de faire une nouvelle transaction basique au sein d'une transaction existante,
	 * en passant dans la base MASTER
	 * 
	 * Attention : dans la méthode appelée, il ne faire aucun appel de service, car ils seraient
	 * appelés dans l'ancienne transaction
	 */
	static public Object writeInMaster(Call fn)
	{
		DataBaseInfo dataBaseInfo = DbUtil.getMasterDb();
		EntityManager em = DbUtil.createEntityManager(dataBaseInfo);
		return write(fn, em);
	}
	

}
