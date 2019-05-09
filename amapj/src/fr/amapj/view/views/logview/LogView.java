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
 package fr.amapj.view.views.logview;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.Table.ColumnGenerator;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.param.ChoixOnOff;
import fr.amapj.model.models.saas.TypLog;
import fr.amapj.service.services.appinstance.LogAccessDTO;
import fr.amapj.service.services.logview.LogFileResource;
import fr.amapj.service.services.logview.LogViewDTO;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;



/**
 * Page permettant de presenter la liste des utilisateurs
 */
public class LogView extends StandardListPart<LogAccessDTO> 
{
	
	private LogViewDTO logViewDTO;
	
	private Label infos;
	
	public LogView()
	{
		super(LogAccessDTO.class,false);
		
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des logs";
	}
	
	protected void addSelectorComponent()
	{
		logViewDTO = new LogViewDTO();
		logViewDTO.dateMin = DateUtils.getDateWithNoTime();
		logViewDTO.status = ChoixOnOff.ON;
		logViewDTO.typLog = TypLog.USER;
		
		infos = new Label();
		infos.setWidth("80%");
		updateLabelInfo();
		
		Button b = new Button("Parametres");
		b.addClickListener(e->handleParametres());
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.addComponent(infos);
		hl.addComponent(b);
		hl.setExpandRatio(infos,1.0f);
		
		addComponent(hl);		
	}

	private void updateLabelInfo()
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
		String str ="";
		// 
		if (logViewDTO.dbName!=null && logViewDTO.dbName.length()>0)
		{
			str = str +" Instance="+logViewDTO.dbName;
		}
		
		if (logViewDTO.status!=null)
		{
			str = str +" Etat="+logViewDTO.status;
		}
		
		if (logViewDTO.typLog!=null)
		{
			str = str +" Type="+logViewDTO.typLog;
		}
		
		if (logViewDTO.dateMin!=null)
		{
			str = str +" DateMin="+df.format(logViewDTO.dateMin);
		}
		
		if (logViewDTO.dateMax!=null)
		{
			str = str +" DateMax="+df.format(logViewDTO.dateMax);
		}
		
		if (logViewDTO.nbError>0)
		{
			str = str +" Erreur>="+logViewDTO.nbError;
		}
		
		if (logViewDTO.nom!=null && logViewDTO.nom.length()>0)
		{
			str = str +" Nom Util="+logViewDTO.nom;
		}
		
		if (logViewDTO.ip!=null && logViewDTO.ip.length()>0)
		{
			str = str +" Ip="+logViewDTO.ip;
		}
		infos.setValue(str);		
	}


	@Override
	protected void drawButton() 
	{
		addButton("Rafraichir",ButtonType.ALWAYS,()->refreshTable());
		addButton("Télécharger",ButtonType.ALWAYS,()->handleTelecharger());
		addButton("Niveau de log",ButtonType.ALWAYS,()->handleLogLevel());
		addButton("Configuration temporaire",ButtonType.ALWAYS,()->handleConfTemporaire());

	}

	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("sudo" ,"nom" , "prenom" , "dbName" , "status" ,"typLog" , "ip" , "browser" , "dateIn" , "dateOut" , "nbError");
		
		cdesTable.setColumnHeader("sudo","Sudo");
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("prenom","Prénom");
		cdesTable.setColumnHeader("dbName","Base");
		cdesTable.setColumnHeader("ip","Ip");
		cdesTable.setColumnHeader("browser","Browser");
		cdesTable.setColumnHeader("dateIn","Date connexion");
		cdesTable.setColumnHeader("dateOut","Date déconnexion");
		cdesTable.setColumnHeader("status","Etat");
		cdesTable.setColumnHeader("typLog","Type");
		cdesTable.setColumnHeader("nbError","Erreur");
		cdesTable.setColumnAlignment("nbError",Align.CENTER);
		
		cdesTable.setConverter("dateIn", new DateTimeToStringConverter());
		cdesTable.setConverter("dateOut", new DateTimeToStringConverter());
		


		
		cdesTable.addGeneratedColumn("Télécharger ...", new ColumnGenerator() 
		{ 
		    @Override
		    public Object generateCell(final Table source, final Object itemId, Object columnId) 
		    {
		    	LogAccessDTO dto = (LogAccessDTO) itemId;
		    	if(dto.logFileName==null)
		    	{
		    		Label l = new Label("Pas de fichier");
		    		return l;
		    	}
		    	LogFileResource logFileResource = new LogFileResource(dto.logFileName);
				
				Link extractFile = new Link("Télécharger le fichier de log",new StreamResource(logFileResource, dto.logFileName+".log"));
				return extractFile;
		    }
		});
	}



	@Override
	protected List<LogAccessDTO> getLines() 
	{
		return new LogViewService().getLogs(logViewDTO);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom" , "prenom" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" , "prenom" };
	}
	
	@Override
	public void onPopupClose()
	{
		super.onPopupClose();
		updateLabelInfo();
	}
	
	
	private void handleParametres()
	{
		LogViewEditorPart.open(new LogViewEditorPart(logViewDTO), this);
	}
	
	private void handleTelecharger()
	{
		CorePopup.open(new TelechargerLogPopup(),this);	
	}
	
	private void handleLogLevel()
	{
		CorePopup.open(new ChoixLogLevel(),this);	
	}
	
	private void handleConfTemporaire()
	{
		CorePopup.open(new PopupAppTempConfiguration(),this);	
	}
}
