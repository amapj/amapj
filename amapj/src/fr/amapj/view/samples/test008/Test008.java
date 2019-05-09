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
 package fr.amapj.view.samples.test008;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.samples.VaadinTest;

/**
 * Cette classe permet de tester le fonctionnement de base des panel
 * 
 *  
 *
 */
public class Test008 implements VaadinTest
{
	public void buildView(VaadinRequest request, UI ui)
	{
		Panel panel = new Panel();
		panel.addStyleName(ChameleonTheme.PANEL_BORDERLESS);
		
		VerticalLayout layout = new VerticalLayout(); 
		
		for (int i = 0; i < 100; i++)
		{
			Button b1 = new Button("XXX");
			layout.addComponent(b1);
		}
		
		
		panel.setContent(layout);
		
		

		ui.setContent(panel);

	}
}
