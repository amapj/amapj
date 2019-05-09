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
 package fr.amapj.view.views.permanence.permanencerole;

import java.util.List;

import fr.amapj.service.services.permanence.role.PermanenceRoleDTO;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;


/**
 * Gestion des roles de permanences
 *
 */
@SuppressWarnings("serial")
public class PermanenceRoleListPart extends StandardListPart<PermanenceRoleDTO> implements PopupSuppressionListener
{

	public PermanenceRoleListPart()
	{
		super(PermanenceRoleDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des rôles de permanence";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Créer un nouveau rôle",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Modifier",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());
		
		addSearchField("Rechercher par nom");
	}


	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nom"  });
		cdesTable.setColumnHeader("nom","Nom");
	}



	@Override
	protected List<PermanenceRoleDTO> getLines() 
	{
		return new PermanenceRoleService().getAllRoles();
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" };
	}
	

	private void handleAjouter()
	{
		PermanenceRoleEditorPart.open(new PermanenceRoleEditorPart(true,null), this);
	}


	private void handleEditer()
	{
		PermanenceRoleDTO dto = getSelectedLine();
		PermanenceRoleEditorPart.open(new PermanenceRoleEditorPart(false,dto), this);
	}

	private void handleSupprimer()
	{
		PermanenceRoleDTO dto = getSelectedLine();
		
		if (dto.defaultRole==true)
		{
			String str = "Vous ne pouvez pas supprimer ce role, car c'est le role par défaut"; 
			MessagePopup.open(new MessagePopup("Impossible",ColorStyle.RED,str), this);
			return ;
		}
		
		
		
		String text = "Etes vous sûr de vouloir supprimer le rôle "+dto.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new PermanenceRoleService().delete(idItemToSuppress);
	}	
}
