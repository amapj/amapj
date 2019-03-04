/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.appinstance.AppInstanceService;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup pour permettre la copie facile de tous les mails
 *  
 */
@SuppressWarnings("serial")
public class PopupCopyAllMail extends CorePopup
{
	
	private String mails;	
	
	public PopupCopyAllMail()
	{
		popupTitle = "Mails des administrateurs";
		setWidth(60);
				
		this.mails = new AppInstanceService().getAllMails();
		
	}
	
	
	protected void createButtonBar()
	{		
		Button okButton = addDefaultButton("OK", new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}
	

	protected void createContent(VerticalLayout contentLayout)
	{
		
		// Construction de la zone d'affichage des mails
		HorizontalLayout hlTexte = new HorizontalLayout();
		hlTexte.setMargin(true);
		hlTexte.setSpacing(true);
		hlTexte.setWidth("100%");
		
		
		TextArea listeMails = new TextArea("");
		listeMails.setValue(mails);
		listeMails.setReadOnly(true);
		listeMails.selectAll();
		listeMails.setWidth("80%");
		listeMails.setHeight(5, Unit.CM);
		
		
		hlTexte.addComponent(listeMails);
		hlTexte.setExpandRatio(listeMails, 1);
		hlTexte.setComponentAlignment(listeMails, Alignment.MIDDLE_CENTER);
		
		contentLayout.addComponent(hlTexte);
		
	}
	

	
}
