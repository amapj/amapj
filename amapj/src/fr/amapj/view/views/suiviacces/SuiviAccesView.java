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
 package fr.amapj.view.views.suiviacces;

import java.util.List;

import fr.amapj.service.services.suiviacces.ConnectedUserDTO;
import fr.amapj.service.services.suiviacces.SuiviAccesService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;



/**
 * Page permettant de presenter la liste des utilisateurs
 * 
 *  
 *
 */
public class SuiviAccesView extends StandardListPart<ConnectedUserDTO>
{
	
	public SuiviAccesView()
	{
		super(ConnectedUserDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des personnes connectées";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Envoyer un message à tous",ButtonType.ALWAYS,()->FormPopup.open(new PopupSaisieMessage()));
		addButton("Rafraichir",ButtonType.ALWAYS,()->refreshTable());	
		
		addSearchField("Rechercher par le nom ou prénom");
	}


	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("nom" , "prenom" , "email" , "date" , "agent" ,"dbName");
		
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("prenom","Prénom");
		cdesTable.setColumnHeader("email","E mail");
		cdesTable.setColumnHeader("date","Date connexion");
		cdesTable.setColumnHeader("agent","Browser");
		cdesTable.setColumnHeader("dbName","Nom de la base");
		
		cdesTable.setConverter("date", new DateTimeToStringConverter());
	}



	@Override
	protected List<ConnectedUserDTO> getLines() 
	{
		return new SuiviAccesService().getConnectedUser();
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
	
}
