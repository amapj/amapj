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
 package fr.amapj.view.samples.test006;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;
import fr.amapj.view.samples.VaadinTest;

/**
 * Test de la saisie d'un nombre
 * 
 *  
 *
 */
public class Test006 implements VaadinTest
{

	public class MyBean
	{
		Integer value;

		public Integer getValue()
		{
			return value;
		}

		public void setValue(Integer value)
		{
			this.value = value;
		}

	}

	public void buildView(VaadinRequest request, UI ui)
	{
		VerticalLayout layout = new VerticalLayout();

		final MyBean myBean = new MyBean();
		BeanItem<MyBean> beanItem = new BeanItem<MyBean>(myBean);

		final Property<Integer> integerProperty = (Property<Integer>) beanItem.getItemProperty("value");
		final TextField textField = new TextField("Text field", integerProperty);

		Button submitButton = new Button("Submit value", new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				String uiValue = textField.getValue();
				Integer propertyValue = integerProperty.getValue();
				int dataModelValue = myBean.getValue();

				ErrorPopup.open("UI value (String): " + uiValue + "\nProperty value (Integer): " + 
				propertyValue + "\nData model value (int): "+ dataModelValue);
			}
		});

		layout.addComponent(new Label("Text field type: " + textField.getType()));
		layout.addComponent(new Label("Text field type: " + integerProperty.getType()));
		layout.addComponent(textField);
		layout.addComponent(submitButton);

		ui.setContent(layout);

	}
}
