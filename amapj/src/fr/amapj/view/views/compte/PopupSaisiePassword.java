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

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.view.engine.popup.formpopup.FormPopup;

/**
 * Popup pour la saisie de la nouvelle adresse e mail
 *  
 */
@SuppressWarnings("serial")
public class PopupSaisiePassword extends FormPopup
{
		
	private Long u;


	/**
	 * 
	 */
	public PopupSaisiePassword(UtilisateurDTO u)
	{
		popupTitle = "Changement de votre password";
		this.u = u.getId();
		
	}
	
	public PopupSaisiePassword(Long idUtitilisateur)
	{
		popupTitle = "Changement de votre password";
		this.u = idUtitilisateur;
		
	}
	
	
	protected void addFields()
	{
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
		
		
		
		
	}

	protected void performSauvegarder()
	{
		String newValue = (String) item.getItemProperty("pwd").getValue();
		new PasswordManager().setUserPassword(u,newValue);
	}

	
	

}
