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
 package fr.amapj.view.engine.ui;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.view.engine.popup.formpopup.FormPopup;

/**
 * Popup pour la saisie de la nouvelle adresse e mail
 *  
 */
@SuppressWarnings("serial")
public class PopupSaisieNewPassword extends FormPopup
{
		
	String resetPasswordSalt;

	Utilisateur u;
	
	boolean valid = false;

	/**
	 * 
	 */
	public PopupSaisieNewPassword(String resetPasswordSalt)
	{
		popupTitle = "Changement de votre mot de passe"; 
		this.resetPasswordSalt = resetPasswordSalt;	
	}
	
	
	protected void addFields()
	{
		u = new PasswordManager().findUserWithResetPassword(resetPasswordSalt);
		
		if (u==null)
		{
			addLabel("Demande invalide", ContentMode.TEXT);
			return ;
		}
		
		
		Date datLimit = DateUtils.addDays(fr.amapj.common.DateUtils.getDate(), -1);
		if (u.getResetPasswordDate().before(datLimit))
		{
			addLabel("Votre demande est trop ancienne", ContentMode.TEXT);
			return ;
		}
		
		// Contruction de l'item
		item.addItemProperty("pwd", new ObjectProperty<String>(""));
		

		// Construction des champs
		PasswordField f = new PasswordField("Nouveau mot de passe");
		binder.bind(f, "pwd");
		// f.addValidator(new BeanValidator(getClazz(), propertyId));
		f.setNullRepresentation("");
		f.setStyleName(ChameleonTheme.TEXTFIELD_BIG);
		f.setWidth("80%");
		form.addComponent(f);
		
		valid = true;
	}

	protected void performSauvegarder()
	{
		if (valid)
		{
			String newValue = (String) item.getItemProperty("pwd").getValue();
			new PasswordManager().setUserPassword(u.getId(),newValue);
		}
	}
}
