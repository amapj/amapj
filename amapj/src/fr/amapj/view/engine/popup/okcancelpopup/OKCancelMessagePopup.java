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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.GenericUtils;

/**
 * Popup affichant un message, et appelant une methode  si l'utilisateur a appuyé sur OK
 *  
 */
public class OKCancelMessagePopup extends OKCancelPopup
{
	
	private String htmlMessage;
	private GenericUtils.VoidAction okPressed;
		
	/**
	 * 
	 */
	public OKCancelMessagePopup(String title,String htmlMessage,GenericUtils.VoidAction okPressed)
	{
		popupTitle = title;
		this.htmlMessage = htmlMessage;
		this.okPressed = okPressed;
		
		saveButtonTitle = "OK";
	}
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		// 
		Label l = new Label(htmlMessage,ContentMode.HTML);
		contentLayout.addComponent(l);
	}

	@Override
	protected boolean performSauvegarder()
	{
		okPressed.action();
		return true;
	}
	
}
