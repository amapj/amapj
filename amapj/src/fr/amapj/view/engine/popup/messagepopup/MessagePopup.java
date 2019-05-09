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
 package fr.amapj.view.engine.popup.messagepopup;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup permettant d'afficher un message ou une liste de message, avec 1 bouton OK
 *  
 */

public class MessagePopup extends CorePopup
{
	protected Button okButton;
	
	List<String> messages = new ArrayList<>();
	
	private ContentMode contentMode = ContentMode.TEXT;
	
	private ColorStyle colorStyle = ColorStyle.RED;
	
	
	/**
	 * Crée un message popup, par défaut avec un style RED 
	 */
	public MessagePopup(String title, List<String> strs)
	{
		setHeight("50%");
		popupTitle = title;
		messages.addAll(strs);
	}
	
	public MessagePopup(String title, ColorStyle colorStyle,String ... msgs)
	{
		this(title,ContentMode.TEXT,colorStyle,msgs);
	}
	
	public MessagePopup(String title, ContentMode contentMode,ColorStyle colorStyle,String ... msgs)
	{
		setHeight("50%");
		this.contentMode = contentMode;
		this.colorStyle = colorStyle;
		popupTitle = title;
		for (int i = 0; i < msgs.length; i++)
		{
			messages.add(msgs[i]);
		}
	}
	
	
	protected void createButtonBar()
	{		
		okButton = addDefaultButton("OK",e->close());
	}
	

	protected void createContent(VerticalLayout contentLayout)
	{
		setColorStyle(colorStyle);
		for (String message : messages)
		{	
			Label la = new Label(message,contentMode);	
			contentLayout.addComponent(la);
		}
	}
	
}
