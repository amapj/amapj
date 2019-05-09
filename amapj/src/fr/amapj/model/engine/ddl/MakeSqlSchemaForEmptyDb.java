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
 package fr.amapj.model.engine.ddl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Cette classe permet de generer le schema SQL pour une base qui est vide
 * 
 * Attention : tout le contenu est perdu !!!
 */
public class MakeSqlSchemaForEmptyDb
{

	public void createSqlSchema(String url,String platform,String driver,String user,String password)
	{
		Map<String, Object> mp = new HashMap<String, Object>(); 
		
		mp.put("eclipselink.jdbc.platform",platform);
		

		mp.put("javax.persistence.jdbc.driver",driver );
		mp.put("javax.persistence.jdbc.url",url );
		mp.put(PersistenceUnitProperties.JDBC_USER, user);
		mp.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
		mp.put("eclipselink.logging.level" ,"FINE" );
		
		mp.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
		mp.put(PersistenceUnitProperties.DDL_GENERATION_MODE,PersistenceUnitProperties.DDL_DATABASE_GENERATION );
			
		
		EntityManager em = Persistence.createEntityManagerFactory("ddl",mp).createEntityManager();

		em.getTransaction().begin();
		em.getTransaction().commit();
		
		em.getEntityManagerFactory().close();
		
	}

	public static void main(String[] args)
	{
		MakeSqlSchemaForEmptyDb generateSqlSchema = new MakeSqlSchemaForEmptyDb();
		System.out.println("Debut de la generation du schema sql");
		
		String platform = "org.eclipse.persistence.platform.database.HSQLPlatform";
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:hsql://localhost/amap31";
		String user = "SA";
		String password = "";
		
		generateSqlSchema.createSqlSchema(url, platform, driver, user, password);
		System.out.println("Fin de la generation du schema sql");

	}

}
