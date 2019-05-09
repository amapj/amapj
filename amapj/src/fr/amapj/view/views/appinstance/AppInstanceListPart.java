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
 package fr.amapj.view.views.appinstance;

import java.util.List;

import com.vaadin.ui.Notification;

import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppInstanceService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.copypopup.CopyPopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.popup.swicthpopup.DirectSwitchPopup;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;


/**
 * Gestion des instances
 *
 */
public class AppInstanceListPart extends StandardListPart<AppInstanceDTO> implements  PopupSuppressionListener
{

	public AppInstanceListPart()
	{
		super(AppInstanceDTO.class,true);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des instances";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Créer une nouvelle instance", ButtonType.ALWAYS, ()->handleAjouter());
		addButton("Changer l'état", ButtonType.EDIT_MODE, ()->handleStart());
		addButton("Se connecter", ButtonType.EDIT_MODE, ()->handleConnect());
		addButton("Requete SQL", ButtonType.EDIT_MODE, ()->handleSql());
		addButton("Sauvegarder", ButtonType.EDIT_MODE, ()->handleSave());
		addButton("Supprimer", ButtonType.EDIT_MODE, ()->handleSupprimer());
		// addButton("PATCH V020", ButtonType.ALWAYS, ()->handlePatchV020());
		addButton("Autre ...", ButtonType.ALWAYS, ()->handleAutre());

		addSearchField("Rechercher par nom");
		
	}
	

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nomInstance", "dbms","dateCreation" ,"state" ,"nbUtilisateurs" , "nbMails" });
		
		cdesTable.setColumnHeader("nomInstance","Nom");
		cdesTable.setColumnHeader("dbms","Dbms");
		cdesTable.setColumnHeader("dateCreation","Date de création");
		cdesTable.setColumnHeader("state","Etat");
		cdesTable.setColumnHeader("nbUtilisateurs","Nb utilisateurs");
		cdesTable.setColumnHeader("nbMails","Mails envoyés");
		
		cdesTable.setConverter("dateCreation", new DateTimeToStringConverter());
	}



	@Override
	protected List<AppInstanceDTO> getLines() 
	{
		return new AppInstanceService().getAllInstances(true);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nomInstance" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nomInstance" };
	}
	
	
	/*private void handlePatchV020()
	{
		PatchEditorPart.open(new PatchEditorPart(), this);
	}*/

	private void handleAjouter()
	{
		AppInstanceEditorPart.open(new AppInstanceEditorPart(), this);
	}
	
	private void handleAutre()
	{
		DirectSwitchPopup popup = new DirectSwitchPopup("Autres actions sur les instances",60);
			
		popup.setLine1("Veuillez indiquer ce que vous souhaitez faire :");

		popup.addLine("Extraire les mails de tous les administrateurs", new CopyPopup("Mails des administrateurs", ()->new AppInstanceService().getAllMails()));
		popup.addLine("Extraire les mails de tous les administrateurs + tresoriers + stats", new CopyPopup("Mails admin + stats", ()->new AppInstanceService().getStatInfo()));
			
		popup.open(this);
	}
	
	private void handleStart()
	{
		List<AppInstanceDTO> dtos = getSelectedLines();
		PopupEtatAppInstance.open(new PopupEtatAppInstance(dtos), this);
	}
	
	private void handleConnect()
	{
		List<AppInstanceDTO> dtos = getSelectedLines();
		if (dtos.size()==1)
		{
			AppInstanceDTO dto = dtos.get(0);
			PopupConnectAppInstance.open(new PopupConnectAppInstance(dto), this);
		}
		else
		{
			Notification.show("Vous devez selectionner une et une seule instance");
		}
		
	}
	
	private void handleSql()
	{
		List<AppInstanceDTO> dtos = getSelectedLines();
		PopupSqlAppInstance.open(new PopupSqlAppInstance(dtos), this);
	}
	
	private void handleSave()
	{
		List<AppInstanceDTO> dtos = getSelectedLines();
		PopupSaveAppInstance.open(new PopupSaveAppInstance(dtos), this);
	}



	protected void handleSupprimer()
	{
		List<AppInstanceDTO> dtos = getSelectedLines();
		if (dtos.size()!=1)
		{
			Notification.show("Vous devez selectionner une et une seule instance");
			return ;
		}
		
		AppInstanceDTO dto = dtos.get(0);
		String text = "Etes vous sûr de vouloir supprimer l'instance "+dto.nomInstance+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new AppInstanceService().delete(idItemToSuppress);
	}

	
}
