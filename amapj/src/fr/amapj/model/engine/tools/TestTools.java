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
 package fr.amapj.model.engine.tools;

import fr.amapj.model.engine.db.DbManager;
import fr.amapj.model.engine.transaction.DataBaseInfo;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.service.engine.appinitializer.VelocityInitializer;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppState;
import fr.amapj.service.services.appinstance.DemarrageAppInstanceService;
import fr.amapj.view.engine.ui.AppConfiguration;

public class TestTools
{
	
	static public void init()
	{
		AppConfiguration.initializeForTesting();
		DbManager dbManager = new DbManager();
		dbManager.startAllDbms();
		dbManager.startMasterBase();
		
		VelocityInitializer.load();
		
		
		AppInstanceDTO dto = new AppInstanceDTO();
		dto.nomInstance = "amap1";
		dto.dbms = "he";
		dto.dbUserName = "SA";
		dto.dbPassword = "";
		dbManager.addDataBase(dto,AppState.ON);
		
		DataBaseInfo dataBaseInfo = DbUtil.findDataBaseFromName("amap1");
		DbUtil.setDbForDeamonThread(dataBaseInfo);
	}
	
	
	/**
	 * Permet une initialisation en mode base de données interne
	 */
	static public DbManager initInternalDb()
	{
		AppConfiguration.initializeForTestingInternalDb();
		DbManager dbManager = new DbManager();
		dbManager.startAllDbms();
		dbManager.startMasterBase();
		
		DataBaseInfo dataBaseInfo = DbUtil.findDataBaseFromName("master");
		DbUtil.setDbForDeamonThread(dataBaseInfo);
		
		return dbManager;
	}
	

}
