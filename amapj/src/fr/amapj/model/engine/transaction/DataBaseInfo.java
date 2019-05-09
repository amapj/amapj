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

import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.service.services.appinstance.AppState;


/**
 * Cette classe memorise les infos d'une base de données
 * 
 * Il faut noter que l'application gère x bases de données, une base de données pour chaque tenant
 *
 * Les bases de données sont elles mêmes réparties dans des DBMS
 */
public class DataBaseInfo
{
	
	private String dbName;
	
	private DBMS dbms;
	
	private AppState state;

	public DataBaseInfo(String dbName,DBMS dbms,AppState state)
	{
		this.dbName = dbName;
		this.dbms = dbms;
		this.state = state;
	}

	public String getDbName()
	{
		return dbName;
	}


	public AppState getState()
	{
		return state;
	}

	public void setState(AppState state)
	{
		this.state = state;
	}

	public DBMS getDbms()
	{
		return dbms;
	};
	
	
	
}
