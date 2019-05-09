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
 package fr.amapj.view.engine.template;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.ui.ValoMenuLayout;


/**
 * Page de base back office , avec une scrollbar 
 * 
 */
abstract public class BackOfficeLongView extends VerticalLayout implements View
{

	abstract public void enterIn(ViewChangeEvent event);
	
	abstract public String getMainStyleName();

	/**
	 * 
	 */
	@Override
	final public void enter(ViewChangeEvent event)
	{
		
		// On se positionne en mode back office 
		ValoMenuLayout.setBackOfficeLong();
		
		enterIn(event);
		
		// Ceci permet de garantir que l'on sera bien centré et à 100% sur la largeur 960 px
		// Je ne comprends pas pourquoi il y a besoin de ca! 
		Label l =new Label();
		l.setSizeUndefined();
		addComponent(l);
		
		
		setMargin(false);
		setSpacing(true);
		setSizeUndefined();
		addStyleName("block-center");
		addStyleName(getMainStyleName());
		setResponsive(true);
	}

	
}
