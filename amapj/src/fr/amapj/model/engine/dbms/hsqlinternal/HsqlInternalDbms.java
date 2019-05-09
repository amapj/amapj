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
 package fr.amapj.model.engine.dbms.hsqlinternal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerConfiguration;
import org.hsqldb.server.ServerConstants;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.StackUtils;
import fr.amapj.model.engine.db.DbManager;
import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.model.engine.dbms.DBMSTools;
import fr.amapj.model.engine.ddl.MakeSqlSchemaForEmptyDb;
import fr.amapj.model.engine.tools.SpecificDbImpl;
import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DataBaseInfo;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.models.param.SmtpType;
import fr.amapj.model.models.saas.TypDbExemple;
import fr.amapj.service.engine.appinitializer.AppInitializer;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppState;
import fr.amapj.service.services.appinstance.SudoUtilisateurDTO;
import fr.amapj.service.services.demoservice.DemoService;
import fr.amapj.view.engine.ui.AmapJLogManager;


/**
 * Cette classe permet de gérer un serveur Hsql intégré à l'application
 *
 */
public class HsqlInternalDbms implements DBMS
{
	
	private final static Logger logger = LogManager.getLogger();
	
	// Reference vers le serveur base de données
	private Server server;
	
	private HsqlInternalDbmsConf conf;
	
	// Dans le cas d'une base de données interne, le user password est celui par défaut : SA , vide
	private String user = "SA";
	
	private String password = "";
	
	public HsqlInternalDbms(HsqlInternalDbmsConf conf)
	{
		this.conf = conf;
	}
	
	/**
	 * Réalise l'initialisation du DBMS , si nécessaire
	 * @param conf
	 */
	@Override
	public void startDBMS()
	{
		// Chargement du driver pour pouvoir ouvrir les bases plus tard
		try
		{
			Class.forName("org.hsqldb.jdbcDriver");
		} 
		catch (ClassNotFoundException e1)
		{
			throw new AmapjRuntimeException("Erreur au chargement du driver hsqldb",e1);
		}
		
		//
		HsqlProperties argProps = new HsqlProperties();

		argProps.setProperty("server.no_system_exit", "true");
		argProps.setProperty("server.port", conf.getPort());
		argProps.setProperty("server.remote_open", true);
		argProps.setProperty("server.maxdatabases", 200);
		

		ServerConfiguration.translateAddressProperty(argProps);

		// finished setting up properties;
		server = new Server();

		try
		{
			server.setProperties(argProps);
		} 
		catch (Exception e)
		{
			logger.warn("Impossible de démarrer correctement le DBMS : " + StackUtils.asString(e));
			throw new RuntimeException("Impossible de démarrer le DBMS");
		}

		server.start();
		server.checkRunning(true);		
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
		
		logger.info("Demarrage de la base "+dbName);
		
		// Ajout de la base dans HSQL
		// Voir la doc ici : http://hsqldb.org/doc/guide/listeners-chapt.html#lsc_remote_open 
		
		String url1 = "jdbc:hsqldb:hsql://localhost:"+conf.getPort()+"/"+dbName+";file:"+conf.getContentDirectory()+"/"+dbName;	
		
		try
		{
			Connection conn = DriverManager.getConnection(url1, user, password);
			conn.close();
		} 
		catch (SQLException e)
		{
			throw new AmapjRuntimeException("Impossible de créer correctement la base de données url = "+url1,e);
		}   	
	}
	
	
	/**
	 * Permet l'arret d'une base de données
	 * @param dataBaseInfo
	 */
	@Override
	public void stopOneBase(String dbName)
	{	
		logger.info("Arret de la base "+dbName);
		
		String url = conf.createUrl(dbName);
		try
		{
			Connection conn = DriverManager.getConnection(url,user,password);
			Statement st = conn.createStatement();
			st.execute("SHUTDOWN");
			conn.close();
		} 
		catch (SQLException e)
		{
			// Do nothing, only log
			logger.warn("Impossible d'arreter correctement la base de données "+url+ StackUtils.asString(e));
		}   
		
	}
	
	
	/**
	 * Permet l'arret du DBMS , si nécessaire 
	 */
	@Override
	public void stopDBMS()
	{
		List<DataBaseInfo> dataBaseInfos = DbUtil.getAllDbs();
		for (DataBaseInfo dataBaseInfo : dataBaseInfos)
		{
			if (dataBaseInfo.getDbms()==this)
			{
				stopOneBase(dataBaseInfo.getDbName());
			}
		}
		
		server.shutdown();
		
		// On attend ensuite la fin de la base (attente max de 15 secondes) 
		for(int i=0;i<15;i++)
		{
			if (server.getState()==ServerConstants.SERVER_STATE_SHUTDOWN)
			{
				return ;
			}
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e)
			{
				// Nothing to do
			}
			logger.info("Attente de l'arret complet de la base "+i+"/15");
		}
		
	}

	@Override
	public void registerDb(AppInstanceDTO dto,AppState state)
	{
		String name1 = dto.nomInstance;
		String url1 = conf.createUrl(name1);
		String user1 = user;
		String password1 = password;
		DbUtil.addDataBase(name1, url1, user1, password1,this,state);
	}

	
	
	@Override
	public int executeUpdateSqlCommand(String sqlCommand,AppInstanceDTO dto) throws SQLException
	{
		String url1 = conf.createUrl(dto.nomInstance);
				
		Connection conn = DriverManager.getConnection(url1, user, password);
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
				
		Connection conn = DriverManager.getConnection(url1, user, password);
		Statement st = conn.createStatement();
		
		ResultSet resultset = st.executeQuery(sqlCommand);	
		
		List<List<String>> result = DBMSTools.readResultSet(resultset);	
		
		conn.commit();
		conn.close();
		
		return result;
	}
	
	
	
	

	@Override
	public void createOneBase(final AppInstanceDTO appInstanceDTO)
	{
		String dbName = appInstanceDTO.nomInstance; 
		
		// On démarre tout d'abord la base de données, ce qui permet de créer le fichier vide
		startOneBase(dbName);
		
		// On vérifie ensuite que la base est bien vide
		if ( (numberOfTables(dbName))!=0 )
		{
			throw new AmapjRuntimeException("La base n'est pas vide");
		}
		
		// On crée ensuite le schéma 
		String url1 = conf.createUrl(appInstanceDTO.nomInstance);
		String platform = "org.eclipse.persistence.platform.database.HSQLPlatform";
		String driver = "org.hsqldb.jdbcDriver";
		new MakeSqlSchemaForEmptyDb().createSqlSchema(url1, platform, driver, user, password);
		
		// On update ensuite le sequence counter
		updateSequenceCounter(dbName);
		
		// On enregistre la base de données pour qu'elle soit accessible par les services
		registerDb(appInstanceDTO, AppState.ON);
		
		// On remplit la base de données, en faisant un appel de service dans cette base
		SpecificDbUtils.executeInSpecificDb(dbName, ()->new DemoService().generateDemoData(appInstanceDTO));
					
		
	}
	
	
	
	private void updateSequenceCounter(String dbName)
	{
		String url1 = conf.createUrl(dbName);
		
		try
		{
			Connection conn = DriverManager.getConnection(url1, user, password);
			Statement st = conn.createStatement();
			st.execute("update SEQUENCE set SEQ_COUNT = 10000");	
			conn.commit();
			conn.close();
		} 
		catch (SQLException e)
		{
			throw new AmapjRuntimeException("Impossible d'accèder la base de données url = "+url1,e);
		}   			
	}

	/**
	 * COmpte le nombre de tables présentes dans cette base de données 
	 * @param dbName
	 * @return
	 */
	private int numberOfTables(String dbName)
	{
		
		String url1 = conf.createUrl(dbName);
		
		try
		{
			Connection conn = DriverManager.getConnection(url1, user, password);
			Statement st = conn.createStatement();
			st.execute("SELECT * FROM   INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'");	
			int nb = 0;
			ResultSet set = st.getResultSet();
			while(set.next())
			{
				nb++;
			}
			conn.close();
			return nb;
		} 
		catch (SQLException e)
		{
			throw new AmapjRuntimeException("Impossible d'accèder la base de données url = "+url1,e);
		}   	
	}
	
	
	
	
	/*
	private void copyFile(String nomInstance) throws IOException, OnSaveException
	{
		InternalDbConf conf = AppConfiguration.getConf().getInternalDbConf();

		String fileName = conf.getContentDirectory() + "/" + nomInstance;

		copyFile("/fr/amapj/model/resource/amap1.properties", fileName + ".properties");
		copyFile("/fr/amapj/model/resource/amap1.script", fileName + ".script");

	}

	private void copyFile(String src, String dest) throws IOException, OnSaveException
	{
		File fdest = new File(dest);
		if (fdest.exists())
		{
			throw new OnSaveException("le fichier " + dest + " existe déjà");
		}

		InputStream in = this.getClass().getResourceAsStream(src);
		OutputStream os = null;
		try
		{
			os = new FileOutputStream(fdest);
			IOUtils.copy(in, os);
		} 
		finally
		{
			os.close();
		}
	}*/
	
	
	/**
	 * Ce script permet de créer les deux bases de données pour la distribution
	 * 
	 * Base m1 : correspond au master
	 * Base a1 : correspond à amap1
	 * 
	 */
	public static void main(String[] args) throws ParseException
	{		
		DbManager dbManager = TestTools.initInternalDb();
		
		HsqlInternalDbms dbms = (HsqlInternalDbms) dbManager.getDbms("hi");
		
		// Création de la base m1
		AppInstanceDTO dto = new AppInstanceDTO();
		
		dto.nomInstance="m1";
		dto.dbms="hi";
		
		dto.nomAmap = "MASTER";
		dto.villeAmap = "MASTER";
		dto.smtpType = SmtpType.GMAIL;
		dto.adrMailSrc = "";
		dto.nbMailMax = 0;
		dto.url = "xx";
		dto.typDbExemple = TypDbExemple.BASE_MASTER;
		
		dbms.createOneBase(dto);
		
		// Création de la base a1
		dto = new AppInstanceDTO();
		
		dto.nomInstance="a1";
		dto.dbms="hi";
		
		dto.nomAmap = "AMAP1";
		dto.villeAmap = "VILLE AMAP1";
		dto.smtpType = SmtpType.GMAIL;
		dto.adrMailSrc = "";
		dto.nbMailMax = 200;
		dto.url = "http://amapj.fr/";
		
		// La base est valable 80 jours, donc la fin des inscriptions est placée dans 80 jours
		Date d1 = DateUtils.addDays(DateUtils.getDate(), 80);
		Date d2 = DateUtils.firstMonday(d1);
		Date d3 = DateUtils.addDays(d2, 3);
		Date d4 = DateUtils.addDays(d2, 7*12);
		
		
		dto.dateDebut = d3;
		dto.dateFin = d4;
		dto.dateFinInscription = d3;
		
		dto.typDbExemple = TypDbExemple.BASE_EXEMPLE;
		dto.password = "a";
		
		dbms.createOneBase(dto);
		
		dbManager.stopAllDbms();
	
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy");
		System.out.println("===================================================");
		System.out.println("f[\"d1\"]=\""+df.format(d3)+"\";");
		System.out.println("f[\"d2\"]=\""+df.format(d3)+"\";");
		System.out.println("f[\"d3\"]=\""+df.format(d3)+"\";");
		System.out.println("f[\"d4\"]=\""+df.format(d4)+"\";");
		System.out.println("===================================================");
		
		
		
		
	}


	
}
