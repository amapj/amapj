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

import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.edgenerator.excel.EGStatAccess;
import fr.amapj.service.services.logview.LogViewService;
import fr.amapj.service.services.logview.StatAccessDTO;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.tools.DateToStringConverter;



/**
 * Page permettant de presenter les statistiques d'acces
 * 
 *  
 *
 */
@SuppressWarnings("serial")
public class StatAccessView extends StandardListPart<StatAccessDTO>
{
	
	
	public StatAccessView()
	{
		super(StatAccessDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Statistiques des accès";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Rafraichir",ButtonType.ALWAYS,()->refreshTable());
		addButton("Télécharger",ButtonType.ALWAYS,()->handleTelecharger());
		addButton("Authentification",ButtonType.ALWAYS,()->handleInfoAuthentification());
	}
	
	@Override
	protected void addExtraComponent()
	{
		addComponent(new Label("Authentification : "+PasswordManager.authentificationCounter.getLastInfo()));
	}


	private void handleInfoAuthentification()
	{
		String msg = PasswordManager.authentificationCounter.getAllInfos();
		MessagePopup p = new MessagePopup("Info authentification",ContentMode.HTML,ColorStyle.GREEN,msg);
		MessagePopup.open(p, this);
	}


	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("date" , "nbAcces" , "nbVisiteur" , "tempsTotal" );
		
		cdesTable.setColumnHeader("date","Date");
		cdesTable.setColumnHeader("nbAcces","Nb d'accès");
		cdesTable.setColumnHeader("nbVisiteur","Nb de visiteurs différents");
		cdesTable.setColumnHeader("tempsTotal","temps total en minutes");
				
		cdesTable.setConverter("date", new DateToStringConverter());

	}


	@Override
	protected List<StatAccessDTO> getLines() 
	{
		return new LogViewService().getStats();
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
	
	private void handleTelecharger()
	{
		TelechargerPopup popup = new TelechargerPopup("Statistiques");
		popup.addGenerator(new EGStatAccess());
		CorePopup.open(popup,this);
	}
}
