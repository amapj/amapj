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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.ui.ValoMenuLayout;


/**
 * Page de base back office , avec 
 * 
 */
abstract public class ListPartView extends VerticalLayout implements View
{

	abstract public void enterIn(ViewChangeEvent event);

	/**
	 * 
	 */
	@Override
	final public void enter(ViewChangeEvent event)
	{
		
		// On se positionne en mode back office 
		ValoMenuLayout.setListPart();
		
		enterIn(event);
		
		
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		addStyleName("block-center");
		addStyleName("stdlistpart");
		setResponsive(true);
	}
	
	
	
	protected Table createTable(BeanItemContainer container)
	{
		Table t  = new Table("", container);
		
		t.addStyleName("no-stripes");
		t.addStyleName("no-vertical-lines");
		t.addStyleName("no-horizontal-lines");
		
		return t;
	}
	
	
}
