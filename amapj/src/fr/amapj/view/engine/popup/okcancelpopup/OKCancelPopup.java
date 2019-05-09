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
 package fr.amapj.view.engine.popup.okcancelpopup;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup de base , avec deux boutons Sauvegarder et Annuler
 *  
 */
@SuppressWarnings("serial")
abstract public class OKCancelPopup extends CorePopup
{
	protected Button saveButton;
	protected String saveButtonTitle = "Sauvegarder";
	protected boolean hasSaveButton = true;
	protected Button cancelButton;
	protected String cancelButtonTitle = "Annuler";
	protected boolean hasCancelButton = true;
	
	
	
	protected void createButtonBar()
	{		
		if (hasCancelButton)
		{
			cancelButton = addButton(cancelButtonTitle, new Button.ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event)
				{
					handleAnnuler();
				}
			});
		}
		
		if (hasSaveButton)
		{
			saveButton = addDefaultButton(saveButtonTitle, new Button.ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event)
				{
					handleSauvegarder();
				}
			});
		}
				
	}
	

	protected void handleAnnuler()
	{
		close();
	}

	protected void handleSauvegarder()
	{
		boolean ret = performSauvegarder();
		if (ret)
		{
			close();
		}
	}
	
	/**
	 * Retourne true si on doit fermer la fenetre, false sinon
	 * @return
	 */
	abstract protected boolean performSauvegarder();

	abstract protected void createContent(VerticalLayout contentLayout);
	
}
