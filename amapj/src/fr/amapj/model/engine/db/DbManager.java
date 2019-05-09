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
 package fr.amapj.model.engine.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.model.engine.dbms.DBMSConf;
import fr.amapj.model.engine.dbms.hsqlexternal.HsqlExternalDbms;
import fr.amapj.model.engine.dbms.hsqlexternal.HsqlExternalDbmsConf;
import fr.amapj.model.engine.dbms.hsqlinternal.HsqlInternalDbms;
import fr.amapj.model.engine.dbms.hsqlinternal.HsqlInternalDbmsConf;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppState;
import fr.amapj.view.engine.ui.AppConfiguration;

public class DbManager
{
	
	private final static Logger logger = LogManager.getLogger();
	
	// 
	private Map<String,DBMS> mapDbms = new HashMap<String, DBMS>(); 
	
	

	/**
	 * Démarrage de tous les DBMS qui sont configurés
	 */
	public void startAllDbms()
	{
		AppConfiguration conf = AppConfiguration.getConf();
		
		List<DBMSConf> dbmsConfs = conf.getDbmsConfs();
		
		for (DBMSConf dbmsConf : dbmsConfs)
		{
			DBMS dbms = createDBMS(dbmsConf);
			
			dbms.startDBMS();
			
			mapDbms.put(dbmsConf.getDbmsName(), dbms);
		}
	}
	
	
	
	
	private DBMS createDBMS(DBMSConf dbmsConf)
	{
		DBMS res;
		if (dbmsConf instanceof HsqlInternalDbmsConf)
		{
			res = new HsqlInternalDbms( (HsqlInternalDbmsConf) dbmsConf); 
		}
		else if (dbmsConf instanceof HsqlExternalDbmsConf)
		{
			res = new HsqlExternalDbms( (HsqlExternalDbmsConf) dbmsConf);
		}
		else
		{
			throw new AmapjRuntimeException("Erreur");
		}
		return res;
	}



	/**
	 * Démarrage de la base MASTER
	 */
	public void startMasterBase()
	{
		AppConfiguration conf = AppConfiguration.getConf();
		AppInstanceDTO masterConf = conf.getMasterConf();
		
		addDataBase(masterConf, AppState.ON);
	}
	
	
	
	/**
	 * Permet l'ajout d'une nouvelle base de données 
	 * au niveau de la liste des bases gérées, avec l'état indiqué
	 * 
	 * @param dbName
	 * @param url
	 * @param user
	 * @param password
	 */
	public void addDataBase(AppInstanceDTO dto,AppState state)
	{	
		DBMS dbms = getDbms(dto.getDbms());
		
		// Le DBMS rend la base accessible 
		if (state!=AppState.OFF)
		{
			dbms.startOneBase(dto.getNomInstance());
		}
		
		// On enregistre la base , à l'etat indiqué
		dbms.registerDb(dto, state);
		
	}
	
	
	
	/**
	 * Arret de tous les DBMS qui sont configurés
	 */
	public void stopAllDbms()
	{
		for (DBMS dbms : mapDbms.values())
		{
			dbms.stopDBMS();
		}
	}



	/**
	 * Cette méthode permet la création d'une base de donnée
	 * 
	 * Cette méthode doit :
	 * - créer la base 
	 * - la démarrer 
	 * - l'enregistrer au niveau de DbUtil
	 * - la remplir avec les données indiquées
	 * 
	 * En fin de cette méthode , la base est à l'état démarrée 
	 * 
	 * @param dto
	 */
	public void createDataBase(AppInstanceDTO dto)
	{
		DBMS dbms = getDbms(dto.getDbms());
		
		// Le DBMS crée la base
		dbms.createOneBase(dto);
		
	}
	
	
	/**
	 * Permet d'obtenir un dbms
	 * 
	 * @param dbmsName
	 * @return
	 */
	public DBMS getDbms(String dbmsName)
	{
		DBMS dbms = mapDbms.get(dbmsName);
		if (dbms==null)
		{
			throw new AmapjRuntimeException("Erreur de configuration : pas de DBMS ayant pour nom"+dbmsName);
		}
		return dbms;
	}


	
}
