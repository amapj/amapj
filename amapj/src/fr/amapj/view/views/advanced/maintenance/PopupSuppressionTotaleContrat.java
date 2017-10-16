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
 package fr.amapj.view.views.advanced.maintenance;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.advanced.maintenance.MaintenanceService;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Popup pour la saisie de l'utilisateur
 *  
 */
@SuppressWarnings("serial")
public class PopupSuppressionTotaleContrat extends OKCancelPopup
{
	
	private Searcher box;
	
	
	
	
	/**
	 * 
	 */
	public PopupSuppressionTotaleContrat()
	{
		popupTitle = "Selection du contrat à supprimer ";
		saveButtonTitle = "Supprimer";	
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		FormLayout f = new FormLayout();
		
		
		box = new Searcher(SearcherList.MODELE_CONTRAT);
		
		box.setWidth("80%");
		f.addComponent(box);
		contentLayout.addComponent(f);
		
		
		
	}

	protected boolean performSauvegarder()
	{
		Long idModele =  (Long) box.getConvertedValue();
		new MaintenanceService().deleteModeleContratAndContrats(idModele);
		return true;
	}

	
}
