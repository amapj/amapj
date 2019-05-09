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
 package fr.amapj.service.engine.deamons;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.transaction.DataBaseInfo;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.models.saas.TypLog;
import fr.amapj.service.services.appinstance.AppState;
import fr.amapj.service.services.appinstance.LogAccessDTO;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.view.engine.ui.AmapJLogManager;



/**
 * Utilitaires pour les demons
 * 
 */
public class DeamonsUtils 
{
	private final static Logger logger = LogManager.getLogger();

	/**
	 * Permet d'executer un demon dans toutes les bases 
	 * 
	 */
	static public void executeAsDeamon(Class clazz,DeamonsImpl... deamons)
	{
		// On fait une copie de la liste des bases : on ne veut pas tenir compte des ajouts enventuels de base
		// avant la fin du déroulement complet du démon
		List<DataBaseInfo> dataBaseInfos = new ArrayList<DataBaseInfo>(DbUtil.getAllDbs());
	
		internalExecuteAsDeamon(clazz, dataBaseInfos, deamons);
	}
	
	
	
	/**
	 * Permet d'executer un demon UNIQUEMENT dans la base MASTER 
	 * 
	 */
	static public void executeAsDeamonInMaster(Class clazz,DeamonsImpl... deamons)
	{
		// On récupere la base de données MASTER
		List<DataBaseInfo> dataBaseInfos = new ArrayList<DataBaseInfo>();
		dataBaseInfos.add(DbUtil.getMasterDb());
	
		internalExecuteAsDeamon(clazz, dataBaseInfos, deamons);
	}
	
	
	
	/**
	 * Permet l'execution d'un demon dans la liste des bases indiqués
	 */
	static private void internalExecuteAsDeamon(Class clazz,List<DataBaseInfo> dataBaseInfos,DeamonsImpl... deamons)
	{
		String deamonName = clazz.getSimpleName();
		
		for (DataBaseInfo dataBaseInfo : dataBaseInfos)
		{
			if (dataBaseInfo.getState()==AppState.ON)
			{
				LogAccessDTO dto = new LogViewService().saveAccess(deamonName, null, null, null, null, dataBaseInfo.getDbName(), TypLog.DEAMON,false);
				DeamonsContext deamonsContext = new DeamonsContext();
				DbUtil.setDbForDeamonThread(dataBaseInfo);
				logger.info("Début du démon "+deamonName+" pour la base "+dataBaseInfo.getDbName());
				
				for (int i = 0; i < deamons.length; i++)
				{
					DeamonsImpl deamonsImpl = deamons[i];
					try
					{	
						deamonsImpl.perform(deamonsContext);
					}
					catch(Throwable t)
					{
						deamonsContext.nbError++;
						logger.info("Erreur sur le démon "+deamonName+" pour la base "+dataBaseInfo.getDbName()+"\n"+StackUtils.asString(t));
					}
				}
				
				logger.info("Fin du démon "+deamonName+" pour la base "+dataBaseInfo.getDbName());
				
				DbUtil.setDbForDeamonThread(null);
				new LogViewService().endAccess(dto.id,deamonsContext.nbError);
				AmapJLogManager.endLog(true,dto.logFileName);
			}
		}
	}
	
}
