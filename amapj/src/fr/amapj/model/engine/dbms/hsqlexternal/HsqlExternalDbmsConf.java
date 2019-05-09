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

import fr.amapj.model.engine.dbms.DBMSConf;
import fr.amapj.service.engine.appinitializer.ServletParameter;


/**
 * Paramètres de configuration pour un dbms HSQL Interne
 * 
 */
public class HsqlExternalDbmsConf extends DBMSConf
{
	// 
	private String ip;
	
	// Port d'écoute de la base
	private int port;
	

	public HsqlExternalDbmsConf(String dbmsName)
	{
		super(dbmsName);
	}
	
	
	public void load(ServletParameter param)
	{
		ip = param.read("dbms."+dbmsName+".ip");
			
		port =  Integer.parseInt(param.read("dbms."+dbmsName+".port"));
	
	}


	
	/**
	 * Permet la creation d'une url de connection à partir de l'alias de la base
	 * @param dbName
	 * @return
	 */
	public String createUrl(String dbName)
	{
		return "jdbc:hsqldb:hsql://"+ip+":"+port+"/"+dbName;
	}
	
	
	
	
	/*
	 * Getters 
	 */
	public int getPort()
	{
		return port;
	}



	public String getIp()
	{
		return ip;
	}

	
	
	

	
}
