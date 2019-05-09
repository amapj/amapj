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
 package fr.amapj.view.samples.test003;

import java.util.Iterator;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.NativeButton;

@SuppressWarnings("serial")
public class SidebarMenu extends CssLayout {

    public SidebarMenu() {
        addStyleName("sidebar-menu");
    }

    public SidebarMenu addButton(NativeButton b) {
        addComponent(b);
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                updateButtonStyles();
                event.getButton().addStyleName("selected");
            }
        });
        return this;
    }

    private void updateButtonStyles() {
        for (Iterator<Component> iterator = getComponentIterator(); iterator
                .hasNext();) {
            Component c = iterator.next();
            c.removeStyleName("selected");
        }
    }

    public void setSelected(NativeButton b) {
        updateButtonStyles();
        b.addStyleName("selected");
    }
}