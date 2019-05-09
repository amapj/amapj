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
 package fr.amapj.view.views.sendmail;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.template.BackOfficeLongView;


/**
 * Page permettant l'envoi d'un mail 
 */
public class SendMailView extends BackOfficeLongView implements View
{

	TextField titre;
	TextField destinataires;
	RichTextArea zoneTexte;
	
	
	@Override
	public String getMainStyleName()
	{
		return "sendmail";
	}

	/**
	 * 
	 */
	@Override
	public void enterIn(ViewChangeEvent event)
	{

		ParametresDTO param = new ParametresService().getParametres();
		
		VerticalLayout layout = this;
		
		addLabel(layout, "Cet outil permet d'envoyer un mail à une personne, ou à tous les utilisateurs actifs.");
		addLabel(layout, "Cet outil permet de tester le bon fonctionnement de l'envoi des mails");
		addLabel(layout, "Cet outil ne devrait pas être utilisé pour une communication régulière avec les amapiens.");
		addEmptyLine(layout);
		
		
		
		
		TextField expediteur = addTextField(layout,"Expéditeur du mail");
		expediteur.setEnabled(false);
		expediteur.setValue(param.sendingMailUsername);
		
		titre =addTextField(layout,"Titre du mail");
				
		destinataires = addTextField(layout,"Destinataire du mail");

		zoneTexte = new RichTextArea("Message");
		zoneTexte.setWidth("100%");
		zoneTexte.setHeight("500px");
		
		layout.addComponent(zoneTexte);
		

		addButton(layout, "Envoyer",new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleEnvoyerMail();
			}
		});
				
	}

	private TextField addTextField(VerticalLayout layout, String lib)
	{
		HorizontalLayout h = new HorizontalLayout();
		h.setSpacing(true);
		//h.setWidth("100%");
		
		TextField tf = new TextField();
		tf.setWidth("500px");
		
		Label l = new Label(lib);
		l.setWidth("150px");
		h.addComponent(l);
		h.addComponent(tf);
		layout.addComponent(h);
		return tf;
		
	}

	private void handleEnvoyerMail()
	{
		String email  = destinataires.getValue();
		String link = new ParametresService().getParametres().getUrl()+"?username="+email;
		String subject = titre.getValue();
		String htmlContent = zoneTexte.getValue();
		htmlContent = htmlContent.replaceAll("#LINK#", link);	
		new MailerService().sendHtmlMail( new MailerMessage(email, subject, htmlContent));
		Notification.show("Message envoyé à "+email);
		
	}

	
	private void addButton(Layout layout, String str,ClickListener listener)
	{
		Button b = new Button(str);
		b.addStyleName("primary");
		b.addClickListener(listener);
		layout.addComponent(b);
		
	}
	
	
	private Label addLabel(VerticalLayout layout, String str)
	{
		Label tf = new Label(str);
		layout.addComponent(tf);
		return tf;

	}
	
	private Label addEmptyLine(VerticalLayout layout)
	{
		Label tf = new Label("<br/>",ContentMode.HTML);
		layout.addComponent(tf);
		return tf;

	}

}
