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
 package fr.amapj.view.engine.popup.swicthpopup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.data.Container;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Permet de créer un popup avec une liste de choix, qui ménera ensuite 
 * à un autre popup 
 */
abstract public class SwitchPopup extends CorePopup
{	
	
	private OptionGroup group;
	
	private int index;
	
	private List<SwitchPopupInfo> infos = new ArrayList<>();
	
	protected String line1;
	
	// Permet de definir un texte additionnel qui sera affiché en haut 
	protected String header;
	
	
	static public class SwitchPopupInfo
	{
		public String lib;
		public CorePopup popup;
		
		public SwitchPopupInfo(String lib, CorePopup popup)
		{
			this.lib = lib;
			this.popup = popup;
		}
		
		
	}
	
	protected void createContent(VerticalLayout contentLayout)
	{
		contentLayout.addStyleName("popup-switch");
		
		loadFollowingPopups();
		
		if (header!=null)
		{
			contentLayout.addComponent(new Label(header,ContentMode.HTML));
		}
		
		group = new OptionGroup(line1);
		group.setHtmlContentAllowed(true);
		for (SwitchPopupInfo info : infos)
		{
			group.addItem(info.lib);
		}
		
		contentLayout.addComponent(group);
	}
	
	abstract protected void loadFollowingPopups();
	
	
	protected void addLine(String lib,CorePopup popup)
	{
		infos.add(new SwitchPopupInfo(SafeHtmlUtils.htmlEscape(lib),popup));
	}
	
	
	protected void addSeparator()
	{
		// On ne fait rien si c'est la première ligne
		if(infos.size()==0)
		{
			return ;
		}
		SwitchPopupInfo info = infos.get(infos.size()-1);
		info.lib = info.lib+"<br/><br/>";
	}
	
	

	protected void createButtonBar()
	{
		addButton("Annuler", e->handleAnnuler());
		addDefaultButton("Continuer ...", e->handleContinuer());
	}
	
	
	protected void handleAnnuler()
	{
		close();
	}

	protected void handleContinuer()
	{
		index = ((Container.Indexed) group.getContainerDataSource()).indexOfId(group.getValue());
		
		if (index==-1)
		{
			close();
			return;
		}
		
		
		changeCloseListener(e->swithToNextPopup());
		close();
	}

	protected void swithToNextPopup()
	{
		SwitchPopupInfo info = infos.get(index);
		info.popup.changeCloseListener(popupCloseListener);
		CorePopup.open(info.popup);
	}
	
	
		
		
	
	
}
