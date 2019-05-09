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
 package fr.amapj.view.views.compte;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.moncompte.MonCompteService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.formpopup.validator.EmailValidator;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.InLineFormHelper;


/**
 * Page permettant à l'utilisateur de gérer son compte :
 * -> changement de l'adresse e mail 
 * -> changement du mot de passe 
 * -> changement des coordonnées
 *  
 *
 */
public class MonCompteView extends FrontOfficeView implements PopupListener
{
	
	
	
	static private String TEXTFIELD_COMPTEINPUT = "compteinput";
	
	static private String PANEL_COMPTEFORM = "compteform";
	
	
	

	UtilisateurDTO u;
	
	TextField nom;
	TextField prenom;
	TextField mail;
	TextField pwd;
	
	TextField numTel1;
	TextField numTel2;
	TextField adresse;
	TextField codePostal;
	TextField ville;	
	
	FormLayout form1;
	FormLayout form2;
	FormLayout form3;
	

	public String getMainStyleName()
	{
		return "moncompte";
	}
	

	/**
	 * 
	 */
	@Override
	public void enter()
	{
		Panel p0 = new Panel();
		p0.setWidth("100%");
		p0.addStyleName(PANEL_COMPTEFORM);
		
		VerticalLayout vl1 = new VerticalLayout();
		vl1.setMargin(true);
		p0.setContent(vl1);
		addComponent(p0);
		
		// Bloc nom et prenom - Le nom et le prenom ne sont pas modifiables
		form1 = new FormLayout();
        form1.setMargin(false);
        form1.addStyleName("light");
        vl1.addComponent(form1);
        
        
        Label section = new Label("Nom et prénom");
        section.addStyleName("h2");
        section.addStyleName("colored");
        form1.addComponent(section);
		
		nom = addTextField("Votre nom ",form1);
		prenom = addTextField("Votre prénom ",form1);
		
		
		// Bloc Adresse mail  
		InLineFormHelper formHelper = new InLineFormHelper("Votre mail", "Modifier votre adresse mail", this,  e->handleSaveMail());
	    mail = addTextField("Votre mail",formHelper.getForm());
	    formHelper.getValidatorManager().add(mail, "Votre mail", "mail", new EmailValidator());
		formHelper.addIn(vl1);
		
		
		// Bloc mot de passe
		formHelper = new InLineFormHelper("Votre mot de passe", "Modifier votre mot de passe", this,  e->handleSavePassword());
	    pwd = addTextField("Votre mot de passe",formHelper.getForm());
		formHelper.addIn(vl1);
		
		
		// les coordonnées
		formHelper = new InLineFormHelper("Vos coordonnées", "Modifier vos coordonnées", this,  e->handleSaveChangerCoordonnees());
		numTel1 = addTextField("Numéro de tel 1",formHelper.getForm());
		numTel2 = addTextField("Numéro de tel 2",formHelper.getForm());
		adresse = addTextField("Adresse",formHelper.getForm());
		codePostal = addTextField("Code Postal",formHelper.getForm());
		ville = addTextField("Ville",formHelper.getForm());
		formHelper.addIn(vl1);
	
		
		refresh();
		
	}


	private void handleSaveMail()
	{
		String newValue = mail.getValue();
		new MonCompteService().setNewEmail(u.getId(),newValue);
	}


	private void handleSavePassword()
	{
		String newValue = pwd.getValue();
		new PasswordManager().setUserPassword(u.id,newValue);
	}
	
	
	
	
	
	
	private void handleSaveChangerCoordonnees()
	{
		u.setNumTel1(numTel1.getValue());
		u.setNumTel2(numTel2.getValue());
		u.setLibAdr1(adresse.getValue());
		u.setCodePostal(codePostal.getValue());
		u.setVille(ville.getValue());
		
		new MonCompteService().updateCoordoonees(u);
	}
	
	
	

	


	@Override
	public void onPopupClose()
	{
		refresh();
	}

	private void refresh()
	{
		u = new UtilisateurService().loadUtilisateurDto(SessionManager.getUserId());
		
		setValue(nom,u.getNom());
		setValue(prenom,u.getPrenom());
		setValue(mail,u.getEmail());
		setValue(pwd,"***********");
		setValue(numTel1,u.getNumTel1());
		setValue(numTel2,u.getNumTel2());
		setValue(adresse,u.getLibAdr1());
		setValue(codePostal,u.getCodePostal());
		setValue(ville,u.getVille());		
	}
	
	
	// TOOLS


	private void setValue(TextField tf, String val)
	{
		tf.setReadOnly(false);
		tf.setValue(val);
		tf.setReadOnly(true);
	}
	
	
	private TextField addTextField(String lib,FormLayout form)
	{
		TextField name = new TextField(lib);
		name.addStyleName(TEXTFIELD_COMPTEINPUT);
		name.setWidth("100%");
		name.setNullRepresentation("");
		name.setReadOnly(true);
		form.addComponent(name);

		return name;
	}

}
