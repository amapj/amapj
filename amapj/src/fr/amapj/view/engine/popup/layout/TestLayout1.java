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
 package fr.amapj.view.engine.popup.layout;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Pour tester les problemes d'ascenseur verticaux et horizontaux 
 * 
 * 
si je reduis la fenetre, bizarrement il y a bien l'ascenceur vertical, mais 
il n'y a pas l'ascenseur horizontal 
 */
public class TestLayout1 extends CorePopup 
{	
	public TestLayout1()
	{
	}

	protected void createContent(VerticalLayout contentLayout)
	{
		setHeight("90%"); 
		setWidth(80);  		
	
		VerticalLayout vl = new VerticalLayout();
		vl.setWidth("600px");
		vl.setHeight("600px");
		
		vl.addComponent(new Label("toto eest partie a la plahe toto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahetoto eest partie a la plahe"));
		
		
		contentLayout.addComponent(vl);
	}

	protected void createButtonBar()
	{
		addButton("OK", e->handleOK());
		
	}
	
	
	protected void handleOK()
	{
		close();
	}
	
	
	
	
}
