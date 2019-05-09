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
 package fr.amapj.view.views.contratsamapien;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.views.common.contratselector.ContratSelectorPart;
import fr.amapj.view.views.contratsamapien.ContratsAmapienListPart.AjouterData;

/**
 * Popup pour la saisie de l'utilisateur
 *  
 */
public class PopupSaisieProducteurContrat extends OKCancelPopup implements PopupListener
{
	
	private ContratSelectorPart contratSelectorPart;
	
	private AjouterData data;
	
	/**
	 * 
	 */
	public PopupSaisieProducteurContrat(AjouterData data)
	{
		this.data = data;
		
		popupTitle = "Selection du contrat";
		saveButtonTitle = "Continuer ...";
		setType(PopupType.CENTERFIT);
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		// Partie choix du contrat
		contratSelectorPart = new ContratSelectorPart(this);
		HorizontalLayout toolbar1 = contratSelectorPart.getChoixContratComponent();
		
		contentLayout.addComponent(toolbar1);
		
		contratSelectorPart.fillAutomaticValues();
	}

	protected boolean performSauvegarder()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		if (idModeleContrat==null)
		{
			return false;
		}
		
		boolean hasContrat = new GestionContratSigneService().checkIfUserHasContrat(idModeleContrat,data.userId);
		if (hasContrat)
		{
			MessagePopup popup = new MessagePopup("Impossible",ColorStyle.RED,"Cet utilisateur possède déjà ce contrat");
			MessagePopup.open(popup);
			return false;
		}
		
		
		data.validate();
		data.idModeleContrat =  idModeleContrat;
		return true;
	}


	@Override
	public void onPopupClose()
	{
		// Nothing to do 
	}

}
