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

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.view.engine.notification.NotificationHelper;
import fr.amapj.view.engine.popup.formpopup.FormPopup;

/**
 * Popup pour la saisie de la nouvelle adresse e mail
 *  
 */
@SuppressWarnings("serial")
public class PopupSaisieEmail extends FormPopup
{
	/**
	 * 
	 */
	public PopupSaisieEmail()
	{
		popupTitle = "Perte de mot de passe";
		saveButtonTitle = "Confirmer";
	}
	
	
	protected void addFields()
	{
		// Contruction de l'item
		item.addItemProperty("email", new ObjectProperty<String>(""));
		

		
		
		// Construction des champs
		addLabel(	"Si vous avez perdu votre mot de passe, <br/>" +
					"merci de saisir votre adresse e mail ci dessous.<br/><br/>" +
					"Vous recevrez dans quelques minutes un mail qui <br/>" +
					"vous permettra de réinitialiser votre mot de passe.<br/><br/>",ContentMode.HTML);
		
		addTextField("Votre e-mail", "email");
		
		
		
	}

	protected void performSauvegarder()
	{
		String email = (String) item.getItemProperty("email").getValue();
		String msg = new PasswordManager().sendMailForResetPassword(email);
		if (msg==null)
		{
			Notification.show("Un mail vient de vous être envoyé. Merci de vérifier votre boîte mail", Type.WARNING_MESSAGE);
		}
		else
		{
			NotificationHelper.displayNotification(msg);
		}
	}
}
