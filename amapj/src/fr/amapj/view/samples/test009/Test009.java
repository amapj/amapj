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
 package fr.amapj.view.samples.test009;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.samples.VaadinTest;

/**
 * Cette classe permet de tester le fonctionnement de base des panel
 * 
 *  
 *
 */
public class Test009 implements VaadinTest
{
	public void buildView(VaadinRequest request, UI ui)
	{
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		

		Button button = new Button("Click Me");
		button.addClickListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				layout.addComponent(new Label("Thank you for clicking"));
			}
		});
		layout.addComponent(button);
		
		
		TextField tf = new TextField("Toto");
		tf.setValue("monconyent");
		tf.selectAll();
		
		layout.addComponent(tf);
		
		

		ui.setContent(layout);

	}
}
