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
 package fr.amapj.view.samples.test001;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.UI;

import fr.amapj.view.samples.VaadinTest;


/**
 * Ce test permet de visualiser un menu à la Itunes
 * en s'appuyant sur le theme Chameleon 
 * 
 * Il y a un problème non résolu : les textes sont centrés, alors 
 * que l'on veut les aligner à gauche
 *
 */
public class Test001 implements VaadinTest
{
	public void buildView(VaadinRequest request,UI ui)
	{
		
		Test001SidebarMenu sidebar = new Test001SidebarMenu();
        sidebar.setWidth("200px");
        sidebar.addComponent(new Label("Fruits"));
        NativeButton b = new NativeButton("Apples");
        b.setIcon(new ThemeResource("../runo/icons/16/note.png"));
        sidebar.addButton(b);
        sidebar.setSelected(b);
        sidebar.addButton(new NativeButton("Oranges"));
        sidebar.addButton(new NativeButton("Bananas"));
        sidebar.addButton(new NativeButton("Grapes"));
        sidebar.addComponent(new Label("Vegetables"));
        sidebar.addButton(new NativeButton("Tomatoes"));
        sidebar.addButton(new NativeButton("Gabbages"));
        sidebar.addButton(new NativeButton("Potatoes"));
        sidebar.addButton(new NativeButton("Carrots"));
        sidebar.addButton(new NativeButton("Texte très très très long"));
        sidebar.addButton(new NativeButton("court"));
        
		
		
		ui.setContent(sidebar);

	}
}