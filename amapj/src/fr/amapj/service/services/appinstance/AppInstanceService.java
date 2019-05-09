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
 package fr.amapj.service.services.appinstance;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.model.engine.db.DbManager;
import fr.amapj.model.engine.dbms.DBMS;
import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DataBaseInfo;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.saas.AppInstance;
import fr.amapj.service.engine.appinitializer.AppInitializer;
import fr.amapj.service.services.appinstance.SqlRequestDTO.DataBaseResponseDTO;
import fr.amapj.service.services.appinstance.SqlRequestDTO.ResponseDTO;
import fr.amapj.service.services.appinstance.SqlRequestDTO.SqlType;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.service.services.logview.StatInstanceDTO;
import fr.amapj.service.services.mailer.MailerCounter;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.suiviacces.ConnectedUserDTO;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.ui.AppConfiguration;

/**
 * Permet la gestion des instances de l'application
 * 
 */
public class AppInstanceService
{
	
	private final static Logger logger = LogManager.getLogger();

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES INSTANCES

	/**
	 * Permet de charger la liste de tous les instances
	 */
	@DbRead
	public List<AppInstanceDTO> getAllInstances(boolean withMaster)
	{
		EntityManager em = TransactionHelper.getEm();	
		
		List<AppInstanceDTO> res = new ArrayList<>();

		Query q = em.createQuery("select a from AppInstance a ORDER BY a.nomInstance");

		List<AppInstance> ps = q.getResultList();
		List<ConnectedUserDTO> connected = SessionManager.getAllConnectedUser();
		for (AppInstance p : ps)
		{
			AppInstanceDTO dto = createAppInstanceDto(connected,p);
			res.add(dto);
		}
		
		// On ajoute ensuite la base master si besoin 
		if (withMaster)
		{
			AppInstanceDTO master = AppConfiguration.getConf().getMasterConf();
			addInfo(master,connected);
			res.add(master);
		}

		return res;

	}

	

	public AppInstanceDTO createAppInstanceDto(List<ConnectedUserDTO> connected, AppInstance a)
	{
		AppInstanceDTO dto = new AppInstanceDTO();
		
		dto.id = a.getId();
		dto.nomInstance = a.getNomInstance();
		dto.dateCreation = a.getDateCreation();
		dto.dbms = a.getDbms();
		dto.dbUserName = a.getDbUserName();
		dto.dbPassword = a.getDbPassword();		
		addInfo(dto, connected);
		return dto;
	}
	
	
	private void addInfo(AppInstanceDTO dto, List<ConnectedUserDTO> connected)
	{
		dto.state = getState(dto.getNomInstance());
		dto.nbUtilisateurs = getNbUtilisateurs(connected,dto.getNomInstance());
		dto.nbMails = MailerCounter.getNbMails(dto.getNomInstance());
		
	}


	private AppState getState(String nomInstance)
	{
		DataBaseInfo dataBaseInfo = DbUtil.findDataBaseFromName(nomInstance);
		if (dataBaseInfo == null)
		{
			return AppState.OFF;
		}
		return dataBaseInfo.getState();
	}
	
	private int getNbUtilisateurs(List<ConnectedUserDTO> connected, String nomInstance)
	{
		int res = 0;
		for (ConnectedUserDTO connectedUserDTO : connected)
		{
			if ((connectedUserDTO.isLogged==true) && (StringUtils.equals(connectedUserDTO.dbName,nomInstance)) )
			{
				res++;
			}
		}
		return res;
	}

	
	// CREATION D'UNE INSTANCE
	
	/**
	 * Permet de créer une instance 
	 * 
	 * Attention : ne pas mettre d'annotation dbWrite ou dbRead ici !!!
	 * 
	 * @param appInstanceDTO
	 * @throws OnSaveException
	 */
	public void create(final AppInstanceDTO appInstanceDTO) throws OnSaveException
	{
		// On vérifie que la base n'existe pas déjà
		if (DbUtil.findDataBaseFromName(appInstanceDTO.nomInstance)!=null)
		{
			throw new OnSaveException("La base existe déjà");
		}

		// On crée la base
		// Attention : ne pas créer de transaction ici, car on va ecrire dans la base de données fille
		AppInitializer.dbManager.createDataBase(appInstanceDTO);
		
		// On crée ensuite en base de données
		// On crée la transaction seulement ici dans le master
		NewTransaction.writeInMaster(new Call()
		{
			
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				AppInstance a = new AppInstance();

				a.setNomInstance(appInstanceDTO.nomInstance);
				a.setDateCreation(DateUtils.getDate());
				a.setDbms(appInstanceDTO.dbms);
				a.setDbUserName(appInstanceDTO.dbUserName);
				a.setDbPassword(appInstanceDTO.dbPassword);
						
				em.persist(a);

				return null;
			}
		});
	}


	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer un instance Ceci est fait dans une transaction en ecriture
	 */
	@DbWrite
	public void delete(final Long id)
	{
		EntityManager em = TransactionHelper.getEm();

		AppInstance a = em.find(AppInstance.class, id);

		em.remove(a);
	}

	// PARTIE DEMARRAGE DE LA BASE

	public void setDbState(AppInstanceDTO dto)
	{
		AppState appState = dto.getState();
		DataBaseInfo dataBaseInfo = DbUtil.findDataBaseFromName(dto.nomInstance);

		// La base n'existe pas du tout
		if (dataBaseInfo == null)
		{
			throw new AmapjRuntimeException("Base non trouvée");
		}

		// Réalisation des opérations nécessaires
		switch (dataBaseInfo.getState())
		{
		case OFF:
			changeStateFromOff(dataBaseInfo,appState);
			break;

		case DATABASE_ONLY:
			changeStateFromDatabaseOnly(dataBaseInfo,appState);
			break;

		case ON:
			changeStateFromOn(dataBaseInfo,appState);
			break;

		default:
			throw new AmapjRuntimeException("Erreur de programmation");
		}
		
		// On mémorise son état correctement
		dataBaseInfo.setState(appState);
		
	}

	private void changeStateFromOn(DataBaseInfo dataBaseInfo, AppState appState)
	{
		switch (appState)
		{
		case ON:
		case DATABASE_ONLY:
			// Rien à faire
			break;

		case OFF:
			DBMS dbms = dataBaseInfo.getDbms();
			dbms.stopOneBase(dataBaseInfo.getDbName());
			break;
		
		default:
			throw new AmapjRuntimeException("Erreur de programmation");
		}

	}

	private void changeStateFromDatabaseOnly(DataBaseInfo dataBaseInfo, AppState appState)
	{
		switch (appState)
		{
		case ON:
		case DATABASE_ONLY:
			// Rien à faire
			break;

		case OFF:
			DBMS dbms = dataBaseInfo.getDbms();
			dbms.stopOneBase(dataBaseInfo.getDbName());
			break;
		
		default:
			throw new AmapjRuntimeException("Erreur de programmation");
		}

	}

	private void changeStateFromOff(DataBaseInfo dataBaseInfo, AppState appState)
	{
		switch (appState)
		{
		case ON:
		case DATABASE_ONLY:
			DBMS dbms = dataBaseInfo.getDbms();
			dbms.startOneBase(dataBaseInfo.getDbName());
			break;

		case OFF:
			// Rien à faire
			break;
		
		default:
			throw new AmapjRuntimeException("Erreur de programmation");
		}
		
	}	
	
	
	// PARTIE SUDO
	public List<SudoUtilisateurDTO> getSudoUtilisateurDto(AppInstanceDTO dto)
	{
		return SpecificDbUtils.executeInSpecificDb(dto.nomInstance, ()->getSudoUtilisateurDto());
	}

	public List<SudoUtilisateurDTO> getSudoUtilisateurDto()
	{
		List<SudoUtilisateurDTO> res = new ArrayList<SudoUtilisateurDTO>();
		ParametresDTO param = new ParametresService().getParametres();
		
		List<UtilisateurDTO> utilisateurDTOs =  new UtilisateurService().getAllUtilisateurs(true);
		for (UtilisateurDTO utilisateur : utilisateurDTOs)
		{
			SudoUtilisateurDTO dto = new SudoUtilisateurDTO();
			dto.id = utilisateur.getId();
			dto.nom = utilisateur.getNom();
			dto.prenom = utilisateur.getPrenom();
			dto.roles = utilisateur.roles;
			dto.url = param.getUrl()+"?username="+utilisateur.getEmail();
			res.add(dto);
		}
		
		// Tri pour avoir les administrateurs en premier, puis les tresoriers , puis par ordre alphabetique
		CollectionUtils.sort(res, e->!e.roles.contains("ADMIN"),e->!e.roles.contains("TRESORIER"),e->e.nom,e->e.prenom);	
		
		return res;
	}



	/**
	 * 
	 * 
	 * @param selected
	 * @param appInstanceDTOs
	 * @param ddlRequest si true, alors les requetes SQL sont des inserts, des updates ou des commandes DDL, sinon ce sont des requetes SQL de type SELECT  
	 */
	public void executeSqlRequest(SqlRequestDTO selected,List<AppInstanceDTO>  appInstanceDTOs)
	{	
		
		boolean ddlRequest = (selected.sqlType == SqlType.UPDATE_OR_INSERT_OR_DDL);
		
		// Chaque requete est executée dans une transaction indépendante
		// On s'arrête dès qu'une requete échoue 
		
		selected.success = false;
		
		for (AppInstanceDTO appInstanceDTO : appInstanceDTOs)
		{
			logger.info("Execution des requetes sur la base "+appInstanceDTO.nomInstance);
			DataBaseResponseDTO dataBaseResponseDTO = new DataBaseResponseDTO();
			dataBaseResponseDTO.success = false;
			dataBaseResponseDTO.dbName = appInstanceDTO.nomInstance;
			selected.responses.add(dataBaseResponseDTO);
			
			int index = 1;
			for (String request : selected.verifiedRequests)
			{
				ResponseDTO res = executeOneSqlRequest(request,appInstanceDTO,index,ddlRequest);
				dataBaseResponseDTO.responses.add(res);
				if (res.success==false)
				{
					return ;
				}
				index++;
			}
			dataBaseResponseDTO.success = true;
		}
		
		selected.success = true;
	}


	private ResponseDTO executeOneSqlRequest(String request, AppInstanceDTO dto,int index, boolean ddlRequest) 
	{
		ResponseDTO res = new ResponseDTO();
		res.index = index;
		res.sqlRequest = request;
		
		DbManager dbManager = AppInitializer.dbManager;
		DBMS dbms = dbManager.getDbms(dto.getDbms());
		try
		{
			if (ddlRequest)
			{
				res.nbModifiedLines = dbms.executeUpdateSqlCommand(request, dto);
			}
			else
			{
				res.sqlResultSet = dbms.executeQuerySqlCommand(request, dto);
			}
			
			res.sqlResponse = "OK";
			res.success = true;	
		} 
		catch (SQLException e)
		{
			res.sqlResponse = "Erreur : "+ e.getMessage();
			res.success = false;	
		}
		
		return res;
	}

	/**
	 * Permet de sauvegarder toutes ces instances
	 * 
	 * @param appInstanceDTOs
	 * @return
	 */
	public List<String> saveInstance(List<AppInstanceDTO> appInstanceDTOs)
	{
		List<String> res = new ArrayList<String>();
		
		String backupDir = AppConfiguration.getConf().getBackupDirectory();
		if (backupDir==null)
		{
			throw new RuntimeException("Le répertoire de stockage des sauvegardes n'est pas défini");
		}
		
		for (AppInstanceDTO appInstanceDTO : appInstanceDTOs)
		{
			String msg = saveInstance(appInstanceDTO,backupDir);
			res.add(msg);
		}
		return res;
	}

	private String saveInstance(AppInstanceDTO appInstanceDTO, String backupDir)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String fileName = backupDir+"/"+appInstanceDTO.nomInstance+"_"+df.format(DateUtils.getDate())+".tar.gz";
		
		String request = "BACKUP DATABASE TO '"+fileName+"' BLOCKING";
		
		
		ResponseDTO res =executeOneSqlRequest(request, appInstanceDTO,0,true);
		if (res.success==false)
		{
			return "Erreur pour "+appInstanceDTO.nomInstance+": "+res.sqlResponse;
		}
		
		
		File file= new File(fileName);
		if (file.canRead()==false)
		{
			return "Erreur pour "+appInstanceDTO.nomInstance+": le fichier n'est pas trouvé";
		}
		
		return "Succès pour "+appInstanceDTO.nomInstance;
	}

	
	/**
	 * Permet de recuperer les mails de tous les adminitrateurs sur toutes les bases 
	 * @return
	 */
	public String getAllMails()
	{
		StringBuffer str = new StringBuffer();
		SpecificDbUtils.executeInAllDb(()->appendMails(str),false);
		return str.toString();
	}
	
	@DbRead
	private Void appendMails(StringBuffer str)
	{
		EntityManager em = TransactionHelper.getEm();
		
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		//Query q = em.createQuery("select distinct(u) from Utilisateur u  where u.id in (select a.utilisateur.id from RoleAdmin a) OR u.id in (select t.utilisateur.id from RoleTresorier t)  order by u.nom,u.prenom");
		Query q = em.createQuery("select distinct(u) from Utilisateur u  where u.id in (select a.utilisateur.id from RoleAdmin a) order by u.nom,u.prenom");
		List<Utilisateur> us = q.getResultList();
		str.append(CollectionUtils.asStringFinalSep(us, ",",t->"\""+dbName+"\" <"+t.getEmail()+">"));
		
		return null;
	}
	
	
	
	/**
	 * Permet de recuperer des infos générales sur toutes les instances 
	 * @return
	 */
	public String getStatInfo()
	{
		AdminTresorierDataDTO data = new AdminTresorierDataDTO();
		data.extractionDate = new Date();
		
		// Recuperation des statistiques sur les acces
		List<StatInstanceDTO> statAccess = new LogViewService().getStatInstance();
		
		SpecificDbUtils.executeInAllDb(()->appendStatInfo(data,statAccess),false);
		
		return new Gson().toJson(data);
	}
	
	@DbRead
	private Void appendStatInfo(AdminTresorierDataDTO data, List<StatInstanceDTO> statAccess)
	{
		EntityManager em = TransactionHelper.getEm();
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		AdminTresorierDataDTO.InstanceDTO stat = new AdminTresorierDataDTO.InstanceDTO();
		
		stat.code = dbName;
		stat.nom = new ParametresService().getParametres().nomAmap;
		stat.nbAccessLastMonth = statAccess.stream().filter(e->e.nomInstance.equals(dbName)).findFirst().map(e->e.detail[0].nbAccess).orElse(0);
		
		TypedQuery<Utilisateur> q = em.createQuery("select distinct(u) from Utilisateur u  where u.id in (select a.utilisateur.id from RoleAdmin a) order by u.nom,u.prenom",Utilisateur.class);
		stat.admins = q.getResultList().stream().map(e->new AdminTresorierDataDTO.ContactDTO(e.nom, e.prenom, e.email)).collect(Collectors.toList());
		
		q = em.createQuery("select distinct(u) from Utilisateur u  where u.id in (select a.utilisateur.id from RoleTresorier a) order by u.nom,u.prenom",Utilisateur.class);
		stat.tresoriers = q.getResultList().stream().map(e->new AdminTresorierDataDTO.ContactDTO(e.nom, e.prenom, e.email)).collect(Collectors.toList());
				
		data.instances.add(stat);
		
		return null;
	}
	
}
