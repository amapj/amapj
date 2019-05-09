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
 package fr.amapj.view.views.droits;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.access.AdminTresorierDTO;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet d'ajouter les droits tresorier ou administrateur 
 * 
 *
 */
public class AdminTresorierEditorPart extends FormPopup
{

	private AdminTresorierDTO dto;

	private boolean forAdmin;

	/**
	 * 
	 */
	public AdminTresorierEditorPart(boolean forAdmin)
	{
		this.forAdmin = forAdmin;
		dto = new AdminTresorierDTO();
		setWidth(80);
		
		if (forAdmin)
		{
			popupTitle = "Ajout d'un nouvel administrateur";
		}
		else
		{
			popupTitle = "Ajout d'un nouveau trésorier";
		}	
		
		item = new BeanItem<AdminTresorierDTO>(this.dto);

	}
	
	@Override
	protected void addFields()
	{
		// Champ 1
		addSearcher( "Utilisateur", "utilisateurId", SearcherList.UTILISATEUR_TOUS, null,new NotNullValidator());
		
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		if (forAdmin)
		{
			new AccessManagementService().createAdmin(dto);
		}
		else
		{
			new AccessManagementService().createTresorier(dto);
		}
	}
}
