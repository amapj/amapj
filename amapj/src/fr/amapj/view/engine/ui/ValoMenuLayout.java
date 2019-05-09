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
 package fr.amapj.view.engine.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;

/**
 * Gestion de la page de login ou de l'affichage standard
 */
public class ValoMenuLayout extends HorizontalLayout
{
	CssLayout contentArea;

	CssLayout menuArea;
	
	CssLayout loginAera;
	
	
	public ValoMenuLayout()
	{
		contentArea = new  CssLayout();
		menuArea = new CssLayout();
		loginAera = new CssLayout();
	}

	/**
	 * Prepare pour la page de login et retourne le layout a utiliser
	 * 
	 * @return
	 */
	public CssLayout prepareForLoginPage()
	{
		removeAllComponents();
		setSizeFull();
		
		loginAera.removeAllComponents();
		
		loginAera.setPrimaryStyleName("valo-content");
		loginAera.addStyleName("v-scrollable");
		loginAera.setSizeFull();

		addComponents(loginAera);
		setExpandRatio(loginAera, 1);
		
		return loginAera;
	}
	
	
	public void prepareForMainPage()
	{
		removeAllComponents();
		setSizeFull();
	
		contentArea.removeAllComponents();
		menuArea.removeAllComponents();
	
		
		menuArea.setPrimaryStyleName("valo-menu");
		
		contentArea.setPrimaryStyleName("valo-content");
		contentArea.addStyleName("v-scrollable");
		contentArea.addStyleName("front-office-backgroundimage");
		contentArea.setSizeFull();
		contentArea.setResponsive(true);
	
		addComponents(menuArea, contentArea);
		setExpandRatio(contentArea, 1);
	}
	
	

	public ComponentContainer getContentContainer()
	{
		return contentArea;
	}

	public void addMenu(Component menu)
	{
		menu.addStyleName("valo-menu-part");
		menuArea.addComponent(menu);
	}
	
	
	public static void setFrontOffice()
	{
		((AmapUI) UI.getCurrent()).getRoot().contentArea.addStyleName("front-office-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("listpart-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("back-office-long-backgroundimage");
	}
	

	public static void setListPart()
	{
		((AmapUI) UI.getCurrent()).getRoot().contentArea.addStyleName("listpart-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("front-office-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("back-office-long-backgroundimage");
		
	}

	
	public static void setBackOfficeLong()
	{
		((AmapUI) UI.getCurrent()).getRoot().contentArea.addStyleName("back-office-long-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("front-office-backgroundimage");
		((AmapUI) UI.getCurrent()).getRoot().contentArea.removeStyleName("listpart-backgroundimage");
		
	}
	
	
		

}
