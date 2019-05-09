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

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;

import fr.amapj.model.engine.transaction.TransactionInfo.Status;

/**
 * Gestion des transactions
 *
 */
public class TransactionHelper
{
	
	private final static Logger logger = LogManager.getLogger();
	
	
	static public TransactionHelper mainInstance = new TransactionHelper();
	
	private ThreadLocal<TransactionInfo> threadLocal = new ThreadLocal<TransactionInfo>();
	
	private boolean debug = true;
	
	public static EntityManager getEm()
	{
		TransactionInfo transactionInfo = mainInstance.threadLocal.get();
		return transactionInfo.em;
	}
	
	public void start_read()
	{
		TransactionInfo transactionInfo = threadLocal.get();
		
		//
		if (transactionInfo==null)
		{
			transactionInfo = new TransactionInfo();
			threadLocal.set(transactionInfo);
		}
		
		if (debug)
		{
			logger.debug("nbAppel = {} em = {} transac = {}",transactionInfo.nbAppel,transactionInfo.em,transactionInfo.transac);
		}
		
		
		transactionInfo.nbAppel++;
		
		// Si c'est le premier appel, on crée une session
		// Si ce n'est pas le premier appel, il n'y a rien à faire, on reste dans les conditions précédentes
		if (transactionInfo.nbAppel==1)
		{
			transactionInfo.startSessionLecture();
		}
	}
	
	public void stop_read(boolean rollback)
	{
		TransactionInfo transactionInfo = threadLocal.get();
		
		transactionInfo.nbAppel--;
		
		// Si c'est le dernier appel de la stack, alors on ferme le tout
		// Sinon on ne fait rien
		if (transactionInfo.nbAppel==0)
		{
			transactionInfo.closeSession(rollback);
		}
	}
	
	
	
	public void start_write()
	{
		TransactionInfo transactionInfo = threadLocal.get();
		
		//
		if (transactionInfo==null)
		{
			transactionInfo = new TransactionInfo();
			threadLocal.set(transactionInfo);
		}
		
		transactionInfo.nbAppel++;
		
		// Si c'est le premier appel , on crée directement une session en écriture
		if (transactionInfo.nbAppel==1)
		{
			transactionInfo.startSessionEcriture();
		}
		// Si ce n'est pas le premier appel et que la session est en lecture, on upgrade la session en écriture
		else 
		{ 
			if (transactionInfo.getType()==Status.LECTURE)
			{
				transactionInfo.upgradeLectureToEcriture();
			}
		}
	}
	
	
	public void stop_write(boolean rollback)
	{
		TransactionInfo transactionInfo = threadLocal.get();
		
		// Dans le cas d'une exception au commit , dans AspectJ on passe dans returning puis throwing
		// On se retrouve alors dans ce cas là, ou il ne faut rien faire 
		if ((rollback==true) && (transactionInfo.nbAppel==0))
		{
			return ;
		}
		
		transactionInfo.nbAppel--;
		
		// Si c'est le dernier appel de la stack, alors on ferme le tout
		// Sinon on ne fait rien
		if (transactionInfo.nbAppel==0)
		{
			transactionInfo.closeSession(rollback);
		}
	}
	
}
