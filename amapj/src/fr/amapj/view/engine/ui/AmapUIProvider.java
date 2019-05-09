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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;



/**
 * Permet de choisir l'UI en fonction du type de l'appareil (mobile ou non)
 */
public class AmapUIProvider extends UIProvider
{
	
	private final static Logger logger = LogManager.getLogger();

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event)
	{
		return AmapUI.class;
	}
	
	@Override
	public boolean isPreservedOnRefresh(UICreateEvent event) 
	{
		return true;
	}
	
	@Override
	public String getPageTitle(UICreateEvent event) 
	{
		return  "AMAP";
	}
	
}