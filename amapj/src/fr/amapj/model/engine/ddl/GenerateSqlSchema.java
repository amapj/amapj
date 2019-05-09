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

import fr.amapj.view.engine.ui.AmapJLogManager;

/**
 * Cette classe permet de generer le schema SQL
 */
public class GenerateSqlSchema
{

	public void createData()
	{
		
		

		Map<String, Object> mp = new HashMap<String, Object>(); 
		
		
		
		mp.put("eclipselink.jdbc.platform","org.eclipse.persistence.platform.database.HSQLPlatform");
		
		
		
		mp.put("javax.persistence.jdbc.driver","org.hsqldb.jdbcDriver" );
		mp.put("javax.persistence.jdbc.url","jdbc:hsqldb:hsql://localhost/amap1" );
		mp.put(PersistenceUnitProperties.JDBC_USER, "SA");
		mp.put(PersistenceUnitProperties.JDBC_PASSWORD, "");
		mp.put("eclipselink.logging.level" ,"FINE" );
		
		mp.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
		mp.put(PersistenceUnitProperties.DDL_GENERATION_MODE,PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION );
		mp.put("eclipselink.create-ddl-jdbc-file-name","create-script.sql" );
		mp.put("eclipselink.application-location","db/");
		
		
		
		
		
		
		
		
	
		
		EntityManager em = Persistence.createEntityManagerFactory("pu000",mp).createEntityManager();

		em.getTransaction().begin();
		em.getTransaction().commit();
		
	}

	public static void main(String[] args)
	{
		// TODO remettre au propre la gestion des logs
		// On pourrait mettre une valeur par défaut dans le fichier log4j2.xml de developpement 
		AmapJLogManager.setLogDir("../../logs/");
		
		GenerateSqlSchema generateSqlSchema = new GenerateSqlSchema();
		System.out.println("Debut de la generation du schema sql");
		generateSqlSchema.createData();
		System.out.println("Fin de la generation du schema sql");

	}

}
