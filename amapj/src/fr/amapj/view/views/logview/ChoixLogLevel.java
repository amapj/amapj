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
 package fr.amapj.view.views.logview;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.Container;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.ui.AmapJLogManager;

/**
 * Permet de choisir son niveau de log  
 */
@SuppressWarnings("serial")
public class ChoixLogLevel extends CorePopup
{
	
	private final static Logger logger = LogManager.getLogger();
	
	private OptionGroup group;
	
	/**
	 * 
	 */
	public ChoixLogLevel()
	{
		popupTitle = "Choix du niveau de logs";
		setWidth(50);
	}

	protected void createContent(VerticalLayout contentLayout)
	{
		boolean debug = AmapJLogManager.getLevel().intLevel() == Level.DEBUG.intLevel();
		
		
		group = new OptionGroup("Choix du niveau de log");
		group.addItem("INFO");
		group.addItem("DEBUG");
		
		if (debug)
		{	
			group.select("DEBUG");
		}
		else
		{
			group.select("INFO");
		}
		
		
		contentLayout.addComponent(group);
	}
	
	

	protected void createButtonBar()
	{
		addDefaultButton("Sauvegarder", new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleContinuer();
			}
		});
				
		
		addButton("Annuler", new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleAnnuler();
			}
		});
	}
	
	
	protected void handleAnnuler()
	{
		close();
	}

	protected void handleContinuer()
	{
		int index = ((Container.Indexed) group.getContainerDataSource()).indexOfId(group.getValue());
		
		if (index==0)
		{
			logger.info("Passage de logs au niveau INFO");
			AmapJLogManager.setLevel(Level.INFO);
		}
		else if (index==1)
		{
			logger.info("Passage de logs au niveau DEBUG");
			AmapJLogManager.setLevel(Level.DEBUG);
			
		}
			
		close();
	}

}
