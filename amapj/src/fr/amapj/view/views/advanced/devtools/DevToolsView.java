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
 package fr.amapj.view.views.advanced.devtools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.common.DateUtils;
import fr.amapj.common.GenericUtils;
import fr.amapj.common.GenericUtils.StringAction;
import fr.amapj.service.services.mailer.MailerCounter;
import fr.amapj.view.engine.popup.cascadingpopup.sample.ABCSample;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.template.BackOfficeLongView;
import fr.amapj.view.engine.ui.AppConfiguration;

/**
 * Outils pour le developpement 
 */
public class DevToolsView extends BackOfficeLongView implements View
{

	private final static Logger logger = LogManager.getLogger();
	
	@Override
	public String getMainStyleName()
	{
		return "maintenance";
	}
	
	Label labelDateHeure;
	TextField textDateHeure;
	

	@Override
	public void enterIn(ViewChangeEvent event)
	{
		boolean allowTimeControl = AppConfiguration.getConf().isAllowTimeControl();
		boolean allowMailControl = AppConfiguration.getConf().isAllowMailControl();
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		

		addLabel(this, "Outils pour le developpement","titre");
	
		
		// Partie date 
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label("Date et heure courante :"));
		String valDate = df.format(DateUtils.getDate());
		labelDateHeure = new Label(valDate);
		hl.addComponent(labelDateHeure);
		if (allowTimeControl==true)
		{
			
			textDateHeure = new TextField();
			textDateHeure.setWidth("200px");
			textDateHeure.setValue(valDate);
			hl.addComponent(textDateHeure);
			
			Button b = new Button("Control time");
			hl.addComponent(b);
			b.addClickListener(e->controlTime());
		}
		
		addComponent(hl);
		
		//
		addLabel(this, "Nombre d'emails envoyés aujourd'hui : "+MailerCounter.getNbMails());
		
		if  (allowMailControl==true) 		
		{
			
			Button b = new Button("Visualiser tous les mails locaux");
			this.addComponent(b);
			b.addClickListener(e->controlMail());
		}
		
		
		
		Panel devToolsPanel = new Panel("Outils de développement");
		devToolsPanel.addStyleName("action");
		devToolsPanel.setContent(getDevToolsPanel());
		
		
		addComponent(devToolsPanel);
		
	}
	
	
	
	private void controlMail()
	{
		PopupMailStorage.open(new PopupMailStorage());
	}



	private void controlTime()
	{
		String str = textDateHeure.getValue();
		if (str.length()!=19)
		{
			MessagePopup p = new MessagePopup("Erreur", ColorStyle.RED, "Erreur dans le format - il faut 19 caractères");
			MessagePopup.open(p);
			return;
		}
		DateUtils.developperModeSetDate(str);
		
		// Refresh 
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		labelDateHeure.setValue(df.format(DateUtils.getDate()));
	}

	
	
	private Component getDevToolsPanel()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		addEmptyLine(layout);
		addLabel(layout, "Outils divers réservés aux experts.");
		addEmptyLine(layout);
		addLabel(layout, "ATTENTION !!! Ne pas utiliser sur une base en production !!! ATTENTION !!!!.");
		

		Button b99 = new Button("Décoder le contenu d'un champ zippé", e->decodeZipField());
		
		
		Button b0 = new Button("Générer une erreur", e->generateEror());
		
		Button b10 = new Button("Générer une requete longue (60secondes)", e->generateLongWait());
				
		Button b1 = new Button("Test du cascading de popup ", e->cascading());
		layout.addComponent(b1);
		addEmptyLine(layout);
		
		Button b2 = new Button("Test des popup de type tab", e->CorePopup.open(new DevToolsTabDemo()));
			
		
		Button b3 = new Button("La selection dans une table", e->CorePopup.open(new DevToolsSelectionTable()));
		
		Button b4 = new Button("La gestion des entités JPA - égalité", e->jpaEntity());
		
		Button b5 = new Button("Transformation HTML To Pdf", e->htmlToPdf());
		
		
		
		
		layout.addComponent(b99);
		addEmptyLine(layout);
		layout.addComponent(b0);
		addEmptyLine(layout);
		layout.addComponent(b10);
		addEmptyLine(layout);
		layout.addComponent(b1);
		addEmptyLine(layout);
		layout.addComponent(b2);
		addEmptyLine(layout);
		layout.addComponent(b3);
		addEmptyLine(layout);
		layout.addComponent(b4);
		addEmptyLine(layout);
		layout.addComponent(b5);
		addEmptyLine(layout);
		
		
		
		
		
		return layout;
	}
	
	
	private void decodeZipField()
	{
		PopupDecodeZipField.open(new PopupDecodeZipField());
	}
	
	private void jpaEntity()
	{
		PopupJpaEntityEquality.open(new PopupJpaEntityEquality());
	}
	
	
	private void htmlToPdf()
	{
		PopupHtmlToPdf.open(new PopupHtmlToPdf());
	}
	
	
	private void generateEror()
	{
		String str = null;
		String s = 	str.trim();
	}
	
	
	private void generateLongWait()
	{
		try
		{
			Thread.sleep(60*1000);
		} 
		catch (InterruptedException e)
		{
			// Do nothing 
		}
	}
	



	private void cascading()
	{
		ABCSample sample = new ABCSample();
		sample.doIt(null);
	}

	/**
	 * Chaque appel a cette fonction provoque une fuite memoire d'environ 80 Mo
	 */
	private  void performLeakMemory()
	{
		
		int n = 1000000;
		MyObject[] obs = new MyObject[n];
		for (int i = 0; i < n; i++)
		{
			obs[i] = new MyObject();
			obs[i].a = i;
			obs[i].b = 2*i;
			obs[i].w = "toto"+i;
			
		}
		
		ls.add(obs);
	}
	
	static List<MyObject[]> ls = new ArrayList<DevToolsView.MyObject[]>();
	
	static class MyObject
	{
		int a;
		long b;
		String w;
	}

	
	private Label addLabel(VerticalLayout layout, String str,String stylename)
	{
		Label tf = new Label(str);
		if (stylename!=null)
		{
			tf.addStyleName(stylename);
		}
		layout.addComponent(tf);
		return tf;
	}
	
	private Label addLabel(VerticalLayout layout, String str)
	{
		return addLabel(layout, str, null);
	}
	
	
	private Label addEmptyLine(VerticalLayout layout)
	{
		Label tf = new Label("<br/>",ContentMode.HTML);
		tf.addStyleName(ChameleonTheme.LABEL_BIG);
		layout.addComponent(tf);
		return tf;

	}
	
}
