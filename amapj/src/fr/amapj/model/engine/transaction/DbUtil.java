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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.service.services.appinstance.AppState;
import fr.amapj.service.services.session.SessionManager;

/**
 * Utilitaires divers pour l'accés aux bases de données
 * 
 * Il faut noter que l'application gère x bases de données, une base de données pour chaque tenant
 *
 */
public class DbUtil
{
	
	static private DbUtil mainInstance = new DbUtil();
	
	/**
	 * Liste des bases de données gérées par l'application , y compris celles qui sont arrêtées
	 * 
	 * La premiere base de données est la base de données MASTER
	 */
	private List<DataBaseInfo> dataBaseInfos;
	
	
	/**
	 * Liste des EntityManagerFactory (une factory pour chaque base de données) 
	 */
	private Map<DataBaseInfo, EntityManagerFactory> entityManagerFactorys;
	
	/*
	 * Permet le stockage du nom de la base dans le cas des démons
	 */
	private ThreadLocal<DataBaseInfo> demonDbName = new ThreadLocal<DataBaseInfo>();
	
	
	private DbUtil()
	{
		dataBaseInfos = Collections.synchronizedList(new ArrayList<DataBaseInfo>());
		entityManagerFactorys = new ConcurrentHashMap<>();
	}
	
	/**
	 * Permet d'ajouter une base de données dans la liste des bases gérées par l'application
	 * 
	 * @param dbName
	 * @param url
	 * @param user
	 * @param password
	 */
	static public void addDataBase(String dbName,String url,String user,String password,DBMS dbms,AppState state)
	{
		mainInstance.addDataBaseNS(dbName,url, user, password,dbms,state);
	}
	
	
	
	
	
	/**
	 * Permet de créer un entity manager
	 * 
	 * Cette méthode doit être utilisée uniquement par TransactionHelper ou NewTransaction
	 * 
	 */
	static EntityManager createEntityManager()
	{
		DataBaseInfo dbName = getCurrentDb();
		return createEntityManager(dbName);
	}
	
	/**
	 * Permet de créer un entity manager avec la base donnée en paramètres
	 * 
	 * Cette méthode doit être utilisée uniquement par NewTransaction
	 * 
	 */
	static EntityManager createEntityManager(DataBaseInfo dbName)
	{
		if (dbName.getState()!=AppState.ON)
		{
			throw new AmapjRuntimeException("L'application est en maintenance et n'est plus accessible.");
		}
		
		EntityManagerFactory entityManagerFactory = mainInstance.entityManagerFactorys.get(dbName);
				
		EntityManager em = entityManagerFactory.createEntityManager();
		
		return em;
	}
	
	
	/**
	 * Retourne la base de données courante
	 */
	static public DataBaseInfo getCurrentDb()
	{
		// Le nom de la base provient soit d'une variable positionnée par le démon, soit du contexte de la session
		DataBaseInfo dbName = mainInstance.demonDbName.get();
		if (dbName==null)
		{
			dbName = SessionManager.getDb();
		}
		return dbName;
	}
	
	
	
	/**
	 * Permet d'ajouter une base de données dans la liste des bases de données gérées
	 * 
	 * A ce moment, la factory pour cette base est créée une seule fois, étant ressource consuming
	 */
	private void addDataBaseNS(String dbName,String url,String user,String password,DBMS dbms, AppState state)
	{
		// On vérifie d'abord que cette base n'existe pas déjà
		if (findDataBaseFromNameNS(dbName)!=null)
		{
			throw new AmapjRuntimeException("Il est interdit de créer deux fois la base de données : "+dbName+" url="+url);
		}
		
		// On ajoute cette base dans la liste des bases gérées
		DataBaseInfo dataBaseInfo = new DataBaseInfo(dbName,dbms,state);
		dataBaseInfos.add(dataBaseInfo);
			
		// On crée ensuite l'EntityManagerFactory
		Map<String, Object> mp = new HashMap<String, Object>(); 
	
		mp.put("eclipselink.jdbc.platform","org.eclipse.persistence.platform.database.HSQLPlatform");
		mp.put("javax.persistence.jdbc.driver","org.hsqldb.jdbcDriver" );
		mp.put("javax.persistence.jdbc.url",url);
		mp.put(PersistenceUnitProperties.JDBC_USER, user);
		mp.put(PersistenceUnitProperties.JDBC_PASSWORD, password);
		
		mp.put("eclipselink.logging.level" ,"INFO" );
		mp.put("eclipselink.logging.level.sql" ,"FINE" );
		
		
		mp.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		mp.put(PersistenceUnitProperties.LOGGING_LOGGER, "fr.amapj.model.engine.db.EclipseLinkLogger");
		
		
		
		DecimalFormat df = new DecimalFormat("000");
		int puNumber = entityManagerFactorys.size();
		String puName = "pu"+df.format(puNumber);
	
		EntityManagerFactory entityManagerFactory =  Persistence.createEntityManagerFactory(puName,mp);
		
		entityManagerFactorys.put(dataBaseInfo, entityManagerFactory);

	
	}

	/**
	 * Retourne la  base de données avec ce nom
	 * 
	 * Retourne null si elle n'existe pas
	 */
	public static DataBaseInfo findDataBaseFromName(String dbName)
	{
		return mainInstance.findDataBaseFromNameNS(dbName);
	}
	
	
	/**
	 * Retourne la  base de données avec ce nom
	 * 
	 * Retourne null si elle n'existe pas
	 */
	private DataBaseInfo findDataBaseFromNameNS(String dbName)
	{
		if (dbName==null)
		{
			return null;
		}
		for (DataBaseInfo dataBaseInfo : dataBaseInfos)
		{
			if (dataBaseInfo.getDbName().equals(dbName))
			{
				return dataBaseInfo;
			}
		}
		return null;
	}
	
	/*
	 * Partie specificque pour les demons
	 */
	
	/**
	 * Permet d'indiquer le nom de la base sur laquelle s'execute le demon
	 */
	public static void setDbForDeamonThread(DataBaseInfo dataBaseInfo)
	{
		mainInstance.demonDbName.set(dataBaseInfo);
	}
	
	/**
	 * Retourne la liste de toutes les bases, dans l'ordre de leur création
	 * @return
	 */
	public static List<DataBaseInfo> getAllDbs()
	{
		return mainInstance.dataBaseInfos;
	}
	
	/**
	 * Retourne la base MASTER
	 * @return
	 */
	public static DataBaseInfo getMasterDb()
	{
		return mainInstance.dataBaseInfos.get(0);
	}
	
	
	
	
}
