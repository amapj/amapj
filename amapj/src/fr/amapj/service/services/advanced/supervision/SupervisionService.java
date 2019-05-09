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
 package fr.amapj.service.services.advanced.supervision;

import java.util.Iterator;
import java.util.Vector;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;

import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.service.services.advanced.maintenance.MaintenanceService;

/**
 * Permet la supervision
 */
public class SupervisionService
{
	
	private final static Logger logger = LogManager.getLogger();
	
	public SupervisionService()
	{

	}

	/**
	 * Permet de vider le cache de toutes les bases
	 */
	public void resetAllDataBaseCache()
	{
		SpecificDbUtils.executeInAllDb(()->resetOneDatabaseCache(),false);	
	}
	
	private Void resetOneDatabaseCache()
	{
		MaintenanceService service = new MaintenanceService();
		service.resetDatabaseCache();
		return null;
	}
	
	

	/**
	 * Permet d'afficher le cache de toutes les bases
	 */
	public String dumpCacheForAllBases()
	{
		StringBuilder sb = new StringBuilder();
		SpecificDbUtils.executeInAllDb(()->dumpCache(sb),false);
		return sb.toString();
	}
	
	@DbRead
	private Void dumpCache(StringBuilder sb)
	{
		EntityManager em = TransactionHelper.getEm();	
		
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		sb.append("---------------------------"+dbName+"------------------------<br/>");
		
		
		IdentityMapAccessor ima = (IdentityMapAccessor) em.getEntityManagerFactory().getCache().unwrap(org.eclipse.persistence.sessions.IdentityMapAccessor.class);
		Iterator<Class> iter = ima.getIdentityMapManager().getIdentityMapClasses();
		while(iter.hasNext())
		{
			Class clz = iter.next();
			sb.append("Class = "+clz);
			sb.append("<br/>");
			
			Vector vs = ima.getAllFromIdentityMap(null, clz, null,InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION, false);
			for (Object v : vs)
			{
				sb.append("v="+v);
				sb.append("<br/>");
			}
		}
		return null;
	}
	
	
}
