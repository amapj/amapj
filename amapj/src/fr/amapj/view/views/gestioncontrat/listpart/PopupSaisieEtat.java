/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.views.gestioncontrat.listpart;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;

import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Popup pour la saisie de l'état du contrat
 *  
 */
@SuppressWarnings("serial")
public class PopupSaisieEtat extends FormPopup
{
	private ModeleContratSummaryDTO mcDto;

	/**
	 * 
	 */
	public PopupSaisieEtat(ModeleContratSummaryDTO mcDto)
	{
		popupTitle = "Saisie de l'état du contrat";
		this.mcDto = mcDto;
		
		item = new BeanItem<ModeleContratSummaryDTO>(mcDto);
		
	}
	
	
	protected void addFields()
	{
		// Construction des champs 
		ComboBox box = addComboEnumField("Nouvel état", "etat", new NotNullValidator());	
	}

	protected void performSauvegarder()
	{
		EtatModeleContrat newValue = mcDto.etat;
		new GestionContratService().updateEtat(newValue,mcDto.id);
	}
}
