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
 package fr.amapj.model.engine.dbms.hsqlexternal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.model.engine.dbms.DBMSTools;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppState;


/**
 * Cette classe permet de gérer un serveur Hsql externe à l'application
 *
 */
public class HsqlExternalDbms implements DBMS
{
	
	private final static Logger logger = LogManager.getLogger();
	
	private HsqlExternalDbmsConf conf;
	
	
	public HsqlExternalDbms(HsqlExternalDbmsConf conf)
	{
		this.conf = conf;
	}
	
	/**
	 * Réalise l'initialisation du DBMS , si nécessaire
	 */
	@Override
	public void startDBMS()
	{
		// Nothing to do 	
	}


	
	/**
	 * Permet le démarrage d'une nouvelle base de données avec HSQL
	 * 
	 *  Si la base de données n'existe pas, elle est créée , vide
	 * 
	 */
	@Override
	public void startOneBase(String dbName)
	{
		// DO NOTHING
	}
	
	
	/**
	 * Permet l'arret d'une base de données
	 * @param dataBaseInfo
	 */
	@Override
	public void stopOneBase(String dbName)
	{	
		// DO NOTHING
	}
	
	
	/**
	 * Permet l'arret du DBMS , si nécessaire 
	 */
	@Override
	public void stopDBMS()
	{
		// DO NOTHING
		
	}


	public String createUrl(AppInstanceDTO dto)
	{
		return conf.createUrl(dto.nomInstance);
	}

	@Override
	public void registerDb(AppInstanceDTO dto,AppState state)
	{
		String name1 = dto.nomInstance;
		String url1 = conf.createUrl(name1);
		String user1 = dto.dbUserName;
		String password1 = dto.dbPassword;
		DbUtil.addDataBase(name1, url1, user1, password1,this,state);
	}


	@Override
	public void createOneBase(AppInstanceDTO appInstanceDTO)
	{
		// TODO 
		
	}
	
		
	
	@Override
	public int executeUpdateSqlCommand(String sqlCommand,AppInstanceDTO dto) throws SQLException
	{
		String url1 = conf.createUrl(dto.nomInstance);
		String user1 = dto.dbUserName;
		String password1 = dto.dbPassword;
				
		Connection conn = DriverManager.getConnection(url1, user1, password1);
		Statement st = conn.createStatement();
		int res = st.executeUpdate(sqlCommand);	
		conn.commit();
		conn.close();
		
		return res;
		   			
	}
	
	
	
	@Override
	public List<List<String>> executeQuerySqlCommand(String sqlCommand,AppInstanceDTO dto) throws SQLException
	{
		String url1 = conf.createUrl(dto.nomInstance);
		String user1 = dto.dbUserName;
		String password1 = dto.dbPassword;
				
		Connection conn = DriverManager.getConnection(url1, user1, password1);
		Statement st = conn.createStatement();
		
		ResultSet resultset = st.executeQuery(sqlCommand);	
		
		List<List<String>> result = DBMSTools.readResultSet(resultset);	
		
		conn.commit();
		conn.close();
		
		return result;
	}
	
	
	
}
