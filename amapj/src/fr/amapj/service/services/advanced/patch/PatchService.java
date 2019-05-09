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
 package fr.amapj.service.services.advanced.patch;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;

/**
 * Permet la gestion des pacths pour les migrations 
 */
public class PatchService
{
	
	private final static Logger logger = LogManager.getLogger();
	
	public PatchService()
	{

	}


//	/**
//	 * Application du patch V019
//	 */
//	public String applyPatchV019()
//	{
//		StringBuffer str = new StringBuffer();
//		SpecificDbUtils.executeInAllDb(()->patch(str),false);
//		return str.toString();
//	}
//	
//	@DbWrite
//	private Void patch(StringBuffer str)
//	{
//		EntityManager em = TransactionHelper.getEm();
//		
//		String dbName = DbUtil.getCurrentDb().getDbName();
//		
//		Query q = em.createQuery("select p from EditionSpecifique p");
//
//		List<EditionSpecifique> ps = q.getResultList();
//		for (EditionSpecifique p : ps)
//		{
//			zipContent(p);
//		}
//		
//		str.append("ok pour "+dbName+"<br/>");
//		
//		return null;
//	}
//
//
//	/**
//	 * On zippe uniquement si cela n'a pas déja été fait 
//	 * @param p
//	 */
//	private void zipContent(EditionSpecifique p)
//	{
//		if (p.content==null)
//		{
//			return ;
//		}
//		if (p.content.startsWith("{")==false)
//		{
//			return;
//		}
//		
//		p.content = GzipUtils.compress(p.content);
//	}

	
	/**
	 * Application du patch V020
	 */
	public String applyPatchV020()
	{
		StringBuffer str = new StringBuffer();
		SpecificDbUtils.executeInAllDb(()->patch(str),false);
		return str.toString();
	}
	
	@DbWrite
	private Void patch(StringBuffer str)
	{
		EntityManager em = TransactionHelper.getEm();
		
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		int nb= 0;
		
		str.append("Nombre de données transférées="+nb+" - ok pour "+dbName+"<br/>");
		
		logger.info("Nombre de données transférées="+nb+" - ok pour "+dbName);
		
		return null;
	}
	
}
