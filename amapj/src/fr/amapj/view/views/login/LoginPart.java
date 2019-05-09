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
 package fr.amapj.view.views.login;

import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.ui.AmapUI;
import fr.amapj.view.engine.ui.ValoMenuLayout;

public class LoginPart
{
	private PasswordManager passwordManager = new PasswordManager();
		
	private AmapUI ui;
	
	private int nbComposant = 6;
	
	public LoginPart()
	{
		
	}
	
	public void buildLoginView(ValoMenuLayout root,AmapUI ui,String loginFromUrl,String passwordFromUrl,String sudo)
	{
		this.ui = ui;
		CssLayout loginAera = root.prepareForLoginPage();
		
		VerticalLayout loginLayout = new VerticalLayout();
		loginLayout.setSizeFull();
		
	
		loginAera.setStyleName("login-backgroundimage");
		loginAera.addComponent(loginLayout);
		
		// Recuperation des parametres
		String nomAmap = new ParametresService().getParametres().nomAmap;
		ui.getPage().setTitle(nomAmap); 
		
		// Zone de saisie login/password 
		MyLoginForm myLoginForm = new MyLoginForm(loginFromUrl,passwordFromUrl,sudo,nomAmap);
		myLoginForm.addStyleName("login-layout");
		loginLayout.addComponent(myLoginForm);
		loginLayout.setComponentAlignment(myLoginForm, Alignment.MIDDLE_CENTER);
		loginLayout.setExpandRatio(myLoginForm, 10);
		
		Label l1 = new Label("Application fonctionnant avec AmapJ - ");
		Link link = new Link("Plus d'infos", new ExternalResource("http://amapj.fr"));
		link.setTargetName("_blank");
		
		HorizontalLayout hL = new HorizontalLayout();
		hL.addComponent(l1);
		hL.setComponentAlignment(l1, Alignment.MIDDLE_CENTER);
		hL.addComponent(link);
		hL.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
		hL.setMargin(true);
		
		loginLayout.addComponent(hL);
		loginLayout.setComponentAlignment(hL, Alignment.BOTTOM_CENTER);
		loginLayout.setExpandRatio(hL, 1);
		
		// Si les deux champs ont été remplis on tente une validation automatique
		if ((passwordFromUrl!=null) && (loginFromUrl!=null))
		{
			myLoginForm.login(loginFromUrl, passwordFromUrl);
		}
		
	}

	

	/**
	 * Gestion de l'appui sur mot de passe perdu
	 */
	protected void handleLostPwd()
	{
		FormPopup.open(new PopupSaisieEmail());
	}
	
	
	/**
	 * Zone de saisie du password 
	 *
	 */
    public class MyLoginForm extends LoginForm 
    {
    	String loginFromUrl;
    	String passwordFromUrl;
    	String sudo;
    	String nomAmap;
    	
    	VerticalLayout layout;
    	TextField userNameField;
    	
    	
		public MyLoginForm(String loginFromUrl, String passwordFromUrl,	String sudo,String nomAmap) 
		{
			this.loginFromUrl = loginFromUrl;
			this.passwordFromUrl = passwordFromUrl;
			this.sudo = sudo;
			this.nomAmap = nomAmap;
		}

		@Override
        protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) 
        {
			Panel p = new Panel();
			p.setWidth("100%");
			
            layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.setMargin(true);
            layout.setSizeFull();
            
            p.setContent(layout);
            
           
            
            Label section = new Label(nomAmap);
            section.addStyleName("h2");
            section.addStyleName("colored");
            section.setSizeUndefined();
            layout.addComponent(section);
            layout.setComponentAlignment(section, Alignment.MIDDLE_CENTER);

            this.userNameField = userNameField;
            userNameField.setCaption("Adresse Email");
            userNameField.setStyleName("name");
            userNameField.setWidth("100%");
            userNameField.setId("amapj.login.email");
    		if (loginFromUrl!=null)
    		{
    			userNameField.setValue(loginFromUrl);
    		}
    		layout.addComponent(userNameField);

    		passwordField.setCaption("Mot de passe");
    		passwordField.setStyleName("password");
    		passwordField.setWidth("100%");
    		passwordField.setId("amapj.login.password");
    		if (passwordFromUrl!=null)
    		{
    			passwordField.setValue(passwordFromUrl);
    		}
    		layout.addComponent(passwordField);
    		
    		if ((loginFromUrl==null) || (loginFromUrl.length()==0))
    		{
    			userNameField.focus();
    		}
    		else
    		{
    			passwordField.focus();
    		}

    		BaseUiTools.addEmptyLine(layout);
    		if (BaseUiTools.isCompactMode()==false)
    		{
    			BaseUiTools.addEmptyLine(layout);
    			nbComposant++;
    		}
    		
    		loginButton.setCaption("S'identifier");
    		loginButton.setId("amapj.login.signin");
    		if (sudo!=null)
    		{
    			loginButton.setCaption("SUDO");
    		}
    		loginButton.addStyleName("primary");
    		
    		
    		layout.addComponent(loginButton);
    		layout.setComponentAlignment(loginButton, Alignment.MIDDLE_CENTER);
    		
    		
    		Button lostPwd = new Button("Mot de passe perdu ?");
    		lostPwd.addStyleName("link");
    		lostPwd.addStyleName("perdu");
    		layout.addComponent(lostPwd);
    		layout.setComponentAlignment(lostPwd, Alignment.BOTTOM_LEFT);
    		
    		
    		
    		lostPwd.addClickListener(new ClickListener()
    		{
    			@Override
    			public void buttonClick(ClickEvent event)
    			{
    				handleLostPwd();
    			}
    		});
            return p;
        }

 
        @Override
        protected void login(String userName, String password) 
        {
        	String msg = passwordManager.checkUser(userName, password,sudo); 
    		
    		if ( msg == null)
    		{
    			// Si le mot de passe est correct : on passe à la vue principale
    			ui.buildMainView();
    		} 
    		else
    		{	
    			if (layout.getComponentCount() > nbComposant)
    			{
    				// Remove the previous error message
    				layout.removeComponent(layout.getComponent(nbComposant));
    			}
    			// Add new error message
    			Label error = new Label(msg, ContentMode.HTML);
    			error.addStyleName("failure");
    			error.setSizeUndefined();
    			layout.addComponent(error);
    			userNameField.focus();
    		}
        }
    }
	
	
}
