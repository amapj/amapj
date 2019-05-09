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
 package fr.amapj.view.views.permanence.mespermanences.grille;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.FormatUtils;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;

/**
 * Popup pour se desinscrire
 *  
 */
public class DesinscriptionPopup extends OKCancelPopup
{
	
	PeriodePermanenceDateDTO date;
	Long userId;
	
	/**
	 * 
	 */
	public DesinscriptionPopup(PeriodePermanenceDateDTO date,Long userId)
	{
		this.userId = userId;
		popupTitle = "Annulation inscription";
		saveButtonTitle =  "Se désinscrire";
		this.date = date;
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		Label l = new Label("Etes vous sûr de vouloir vous désinscrire pour la date du "+FormatUtils.getFullDate().format(date.datePerm)+" ? ");
		contentLayout.addComponent(l);
		
		
		
	}

	protected boolean performSauvegarder()
	{
		new MesPermanencesService().deleteInscription(userId,date.idPeriodePermanenceDate);
		return true;
	}

}
