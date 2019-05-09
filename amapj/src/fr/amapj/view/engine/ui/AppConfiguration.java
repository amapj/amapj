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
 package fr.amapj.view.engine.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.dbms.DBMSConf;
import fr.amapj.model.engine.dbms.hsqlexternal.HsqlExternalDbmsConf;
import fr.amapj.model.engine.dbms.hsqlinternal.HsqlInternalDbmsConf;
import fr.amapj.service.engine.appinitializer.MockServletParameter;
import fr.amapj.service.engine.appinitializer.ServletParameter;
import fr.amapj.service.services.appinstance.AppInstanceDTO;

/**
 * Paramètres de configuration de l'application
 * 
 */
public class AppConfiguration
{

	static private AppConfiguration mainInstance;

	static public AppConfiguration getConf()
	{
		if (mainInstance == null)
		{
			throw new RuntimeException("Vous devez d'abord charger les parametres avec la methode load");
		}
		return mainInstance;
	}

	/**
	 * Permet le chargement des parametres
	 */
	static public void load(ServletParameter param)
	{
		if (mainInstance != null)
		{
			throw new RuntimeException("Impossible de charger deux fois les parametres");
		}

		mainInstance = new AppConfiguration();
		mainInstance.loadInternal(param);
	}

	private AppConfiguration()
	{
	}

	// Est on est mode de test ?
	private String testMode;

	// Répertoire pour la sauvegarde de la base
	private String backupDirectory;
	
	// Commande additionnelle qui sera executée après la sauvegarde (par exemple, copie sur un autre disque, ...)
	private String backupCommand;
	
	// Chemin complet jusqu'a l'executable wkhtmltopdf
	private String wkhtmltopdfCommand = null;
	
	// Indique si les administrateurs ont des droits complets ou limités
	// Dans le cas limités, il ne peuvent pas modifier les paramètres envoi de mail , ...
	private boolean adminFull = true;
	
	// Memorisation du contextPath (retourné par 
	private String contextPath;
	
	// Inqiue si il est possible de modifier l'heure courante 
	private boolean allowTimeControl = false;
	
	// Indique que les mails ne sont pas envoyés mais uniquement stockés en mémoire pour debug  
	private boolean allowMailControl = false;
	

	//
	private List<DBMSConf> dbmsConfs = new ArrayList<DBMSConf>();
	
	private AppInstanceDTO masterConf;

	private void loadInternal(ServletParameter param)
	{
		contextPath = param.getContextPath();

		testMode = param.read("test");

		// TODO verifier que c'est bien un directory
		backupDirectory = param.read("database.backupdir");
		
		backupCommand = param.read("database.backupCmd");
		
		wkhtmltopdfCommand = param.read("wkhtmltopdf");
		
		// TODO verifier que c'est bien un directory
		String logDir = param.read("logDir");
		AmapJLogManager.setLogDir(logDir);
		
		// 
		adminFull = (param.read("adminFull", "TRUE")).equalsIgnoreCase("TRUE");
		
		//
		allowTimeControl = (param.read("allowTimeControl", "FALSE")).equalsIgnoreCase("TRUE");
		
		//
		allowMailControl = (param.read("allowMailControl", "FALSE")).equalsIgnoreCase("TRUE");

		
		// Lecture des DBMS
		String dbmsList =  param.read("dbms");
		
		String[] dbmss = dbmsList.split(",");
		for (int i = 0; i < dbmss.length; i++)
		{
			String dbmsName = dbmss[i];
			DBMSConf dbmsConf = createDbmsConf(dbmsName,param);
			dbmsConf.load(param);
			dbmsConfs.add(dbmsConf);	
		}
		
		
		// Lecture de la base master
		masterConf = createMasterConf(param);
		
	}
	
	
	private AppInstanceDTO createMasterConf(ServletParameter param)
	{
		AppInstanceDTO dto = new AppInstanceDTO();
		
		dto.id = 0L;
		
		dto.nomInstance = param.read("master.name");
		
		dto.dbUserName = param.read("master.user");
		
		dto.dbPassword = param.read("master.password");
		
		dto.dbms = param.read("master.dbms");
		
		dto.dateCreation = new Date(0);
		
		return dto;
	}

	private DBMSConf createDbmsConf(String dbmsName, ServletParameter param)
	{
		DBMSConf res;
		
		String type =  param.read("dbms."+dbmsName+".type");
		
		if (type.equals("hsql_internal"))
		{
			res = new HsqlInternalDbmsConf(dbmsName);
		}
		else if (type.equals("hsql_external"))
		{
			res = new HsqlExternalDbmsConf(dbmsName);
		} 
		else
		{
			throw new AmapjRuntimeException("Le type <"+type+"> n'est pas reconnu ");
		}
		return res;
	}
	
	
	

	public String getTestMode()
	{
		return testMode;
	}

	public String getBackupDirectory()
	{
		return backupDirectory;
	}
	
	public List<DBMSConf> getDbmsConfs()
	{
		return dbmsConfs;
	}
	
	public AppInstanceDTO getMasterConf()
	{
		return masterConf;
	}
	
	public boolean isAdminFull()
	{
		return adminFull;
	}
	
	public boolean isAllowTimeControl()
	{
		return allowTimeControl;
	}
	

	public boolean isAllowMailControl()
	{
		return allowMailControl;
	}


	public String getBackupCommand()
	{
		return backupCommand;
	}
	
	
	public String getWkhtmltopdfCommand()
	{
		return wkhtmltopdfCommand;
	}

	public String getContextPath()
	{
		return contextPath;
	}

	/**
	 * Permet de créer une configuration pour les tests
	 * 
	 * ATTENTION : cette méthode doit être appelée uniquement par TestTools.init()
	 */
	public static void initializeForTesting()
	{
		mainInstance = new AppConfiguration();

		Properties prop = new Properties();
		
		prop.put("dbms", "he");
		prop.put("dbms.he.type", "hsql_external");
		prop.put("dbms.he.ip", "127.0.0.1");
		prop.put("dbms.he.port", "9001");
		 
		prop.put("master.dbms","he");
		prop.put("master.name","master");
		prop.put("master.user","SA");
		prop.put("master.password","");
		 
		prop.put("logDir","../logs/");

		MockServletParameter param = new MockServletParameter(prop);
		
		mainInstance.loadInternal(param);
		
		
	}
	
	
	/**
	 * Permet de créer une configuration pour les tests
	 * 
	 * ATTENTION : cette méthode doit être appelée uniquement par TestTools.init()
	 */
	public static void initializeForTestingInternalDb()
	{
		mainInstance = new AppConfiguration();

		Properties prop = new Properties();
		
		prop.put("dbms", "hi");
		prop.put("dbms.hi.type", "hsql_internal");
		prop.put("dbms.hi.port", "9001");
		prop.put("dbms.hi.dir", "c:/prive/dev/amapj/git/amapj-dev/amapj/db/data");
		
		 
		prop.put("master.dbms","hi");
		prop.put("master.name","master"); 
		
		prop.put("logDir","../logs/");
		


		MockServletParameter param = new MockServletParameter(prop);
		
		mainInstance.loadInternal(param);
		
		
	}
	
	
	
	/**
	 * Permet de créer une configuration pour les tests UNITAIRES 
	 * 
	 * ATTENTION : cette méthode doit être appelée uniquement par EngineTester
	 */
	public static void initializeForUnitTesting()
	{
		mainInstance = new AppConfiguration();

		Properties prop = new Properties();
		
		//  
		prop.put("dbms", "hi");
		prop.put("dbms.hi.type", "hsql_internal");
		prop.put("dbms.hi.port", "9500");
		prop.put("dbms.hi.dir", "c:/prive/dev/amapj/git/amapj-dev/tests-units/db/data");
		
		 
		prop.put("master.dbms","hi");
		prop.put("master.name","master");
		
		prop.put("allowMailControl","true");
		
		prop.put("logDir","../../logs/");
		


		MockServletParameter param = new MockServletParameter(prop);
		
		mainInstance.loadInternal(param);
		
		
	}

}
