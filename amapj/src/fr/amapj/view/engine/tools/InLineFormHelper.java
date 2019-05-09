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
 package fr.amapj.view.engine.tools;

import java.util.Iterator;
import java.util.List;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.formpopup.ValidatorManager;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;

/**
 * Permet de créer un formulaire in line , tel que celui utilisé dans
 * "Mon compte"
 * 
 *
 */
public class InLineFormHelper
{

	private FormLayout form;
	private HorizontalLayout footer;

	private String libModifier;

	private ClickListener saveListener;
	
	private PopupListener refreshListener;
	
	private ValidatorManager validatorManager = new ValidatorManager();

	public InLineFormHelper(String sectionName, String libModifier, PopupListener refreshListener,ClickListener saveListener)
	{
		super();
		this.libModifier = libModifier;
		this.saveListener = saveListener;
		this.refreshListener = refreshListener;

		form = new FormLayout();
		form.setMargin(false);

		Label section = new Label(sectionName);
		section.addStyleName("h2");
		section.addStyleName("colored");
		form.addComponent(section);

	}

	public void addIn(Layout layout)
	{
		footer = new HorizontalLayout();
		footer.setWidth("100%");
		form.addComponent(footer);
		formInLectureMode(form);

		layout.addComponent(form);
	}

	/**
	 * Permet de positionner une form en mode Edition, avec un bouton cancel et
	 * save
	 * 
	 * @param footer
	 * @param canceListener
	 * @param saveListener
	 * @param form
	 */
	private void formInCancelSaveMode(FormLayout form)
	{
		//
		form.removeStyleName("light");
		
		//
		Iterator<Component> i = form.iterator();
		while (i.hasNext())
		{
			Component c = i.next();
			if (c instanceof com.vaadin.ui.AbstractField)
			{
				AbstractField field = (AbstractField) c;
				field.setReadOnly(false);
			}
		}

		//
		footer.removeAllComponents();
		footer.setMargin(false);
		footer.setSpacing(true);

		Label l = new Label();
		footer.addComponent(l);
		footer.setExpandRatio(l, 1.0f);

		Button cancel = new Button("Annuler");
		cancel.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				refreshListener.onPopupClose();
				formInLectureMode(form);
			}
		});

		footer.addComponent(cancel);

		Button save = new Button("Sauvegarder");
		save.addStyleName("primary");
		save.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				List<String> msg = validatorManager.validate();
				if (msg.size()>0)
				{
					msg.add(0, "Merci de corriger les points suivants :");
					MessagePopup.open(new MessagePopup("Notification", msg));
					return;
				}
				
				saveListener.buttonClick(null);
				refreshListener.onPopupClose();
				formInLectureMode(form);
			}
		});

		footer.addComponent(save);
		footer.setComponentAlignment(save, Alignment.MIDDLE_LEFT);
	}

	/**
	 * Permet de positionner une form en mode Lecture, avec un bouton Editer
	 * 
	 * @param footer
	 * @param canceListener
	 * @param saveListener
	 * @param form
	 */
	private void formInLectureMode(FormLayout form)
	{
		//
		form.addStyleName("light");

		//
		Iterator<Component> i = form.iterator();
		while (i.hasNext())
		{
			Component c = i.next();
			if (c instanceof com.vaadin.ui.AbstractField)
			{
				AbstractField field = (AbstractField) c;
				field.setReadOnly(true);
			}
		}

		//
		footer.removeAllComponents();

		footer.setMargin(new MarginInfo(true, false, true, false));
		footer.setSpacing(true);

		Label l = new Label();
		footer.addComponent(l);
		footer.setExpandRatio(l, 1.0f);

		Button edit = new Button(libModifier, new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				formInCancelSaveMode(form);
			}
		});
		footer.addComponent(edit);
	}

	public FormLayout getForm()
	{
		return form;
	}

	public ValidatorManager getValidatorManager()
	{
		return validatorManager;
	}
	
	
	
	
	

}
