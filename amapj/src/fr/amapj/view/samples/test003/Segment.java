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
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class Segment extends HorizontalLayout {

    public Segment() {
        addStyleName("segment");
    }

    public Segment addButton(Button b) {
        addComponent(b);
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (event.getButton().getStyleName().indexOf("down") == -1) {
                    event.getButton().addStyleName("down");
                } else {
                    event.getButton().removeStyleName("down");
                }
            }
        });
        updateButtonStyles();
        return this;
    }

    private void updateButtonStyles() {
        int i = 0;
        Component c = null;
        for (Iterator<Component> iterator = getComponentIterator(); iterator
                .hasNext();) {
            c = iterator.next();
            c.removeStyleName("first");
            c.removeStyleName("last");
            if (i == 0) {
                c.addStyleName("first");
            }
            i++;
        }
        if (c != null) {
            c.addStyleName("last");
        }
    }
}