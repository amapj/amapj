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
 package fr.amapj.view.samples.test004;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Button.ClickEvent;

import fr.amapj.view.samples.VaadinTest;

/**
 * Cette classe permet de tester le fonctionnement de base des tables
 *  
 *
 */
public class Test004 implements VaadinTest
{
	public void buildView(VaadinRequest request, UI ui)
	{
		// Create a table and add a style to allow setting the row height in
		// theme.
		final Table table = new Table();
		// TODO table.addStyleName("components-inside");

		/*
		 * Define the names and data types of columns. The "default value"
		 * parameter is meaningless here.
		 */
		table.addContainerProperty("Sum", Label.class, null);
		table.addContainerProperty("Is Transferred", CheckBox.class, null);
		table.addContainerProperty("Comments", TextField.class, null);
		table.addContainerProperty("Details", Button.class, null);

		/* Add a few items in the table. */
		for (int i = 0; i < 100; i++)
		{
			// Create the fields for the current table row
			Label sumField = new Label(String.format("Sum is <b>$%04.2f</b><br/><i>(VAT incl.)</i>", new Object[] { new Double(Math.random() * 1000) }),
					Label.CONTENT_XHTML);
			CheckBox transferredField = new CheckBox("is transferred");

			// Multiline text field. This required modifying the
			// height of the table row.
			TextField commentsField = new TextField();
			commentsField.setHeight(3, Unit.CM);
			

			// The Table item identifier for the row.
			Integer itemId = new Integer(i);

			// Create a button and handle its click. A Button does not
			// know the item it is contained in, so we have to store the
			// item ID as user-defined data.
			Button detailsField = new Button("show details");
			detailsField.setData(itemId);
			detailsField.addClickListener(new Button.ClickListener()
			{

				@Override
				public void buttonClick(ClickEvent event)
				{
					// Get the item identifier from the user-defined data.
					Integer iid = (Integer) event.getButton().getData();
					Notification.show("Link " + iid.intValue() + " clicked.");
				}
			});
			detailsField.addStyleName("link");

			// Create the table row.
			table.addItem(new Object[] { sumField, transferredField, commentsField, detailsField }, itemId);
		}

		// Show just three rows because they are so high.
		table.setPageLength(3);

		ui.setContent(table);

	}

}
