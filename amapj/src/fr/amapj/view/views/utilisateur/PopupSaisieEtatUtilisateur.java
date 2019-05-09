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
 package fr.amapj.view.views.utilisateur;

import com.vaadin.data.util.BeanItem;

import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Popup pour la saisie de l'état d'un utilisateur
 *  
 */
@SuppressWarnings("serial")
public class PopupSaisieEtatUtilisateur extends FormPopup
{
	private UtilisateurDTO utilisateurDTO;


	/**
	 * 
	 */
	public PopupSaisieEtatUtilisateur(UtilisateurDTO utilisateurDTO)
	{
		popupTitle = "Saisie de l'état d'un utilisateur";
		this.utilisateurDTO = utilisateurDTO;
		
		item = new BeanItem<UtilisateurDTO>(utilisateurDTO);
		
	}
	
	
	protected void addFields()
	{
		// Construction des champs
		addComboEnumField("Nouvel état", "etatUtilisateur", new NotNullValidator());
			
	}

	protected void performSauvegarder()
	{
		EtatUtilisateur newValue = utilisateurDTO.etatUtilisateur;
		new UtilisateurService().updateEtat(newValue,utilisateurDTO.id);
	}
}
