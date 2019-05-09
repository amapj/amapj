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
 package fr.amapj.view.samples.test005;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.samples.VaadinTest;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Cette classe permet de tester le fonctionnement de base des combo box
 * 
 *  
 *
 */
public class Test005 implements VaadinTest
{
	public void buildView(VaadinRequest request, UI ui)
	{
		VerticalLayout layout = new VerticalLayout();

		final Searcher box = new Searcher(SearcherList.PRODUCTEUR);

		Button b1 = new Button("GET VALUE", new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{

				Object o = box.getValue();
				String content = " o=" + o;
				ErrorPopup.open(content);

			}
		});
		
		
		Button b2 = new Button("GET CONVERTED VALUE", new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{

				Object o = box.getConvertedValue();
				String content = " o=" + o;
				ErrorPopup.open(content);

			}
		});
		
	
		
		Button b3 = new Button("SET VALUE 101", new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{

				box.setConvertedValue(new Long(101));

			}
		});
		
		
		

		layout.addComponent(box);
		layout.addComponent(b1);
		layout.addComponent(b2);
		layout.addComponent(b3);

		ui.setContent(layout);

	}
}
