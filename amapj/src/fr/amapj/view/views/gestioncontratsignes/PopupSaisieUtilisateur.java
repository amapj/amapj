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
 package fr.amapj.view.views.gestioncontratsignes;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.gestioncontratsignes.GestionContratSignesListPart.AjouterData;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Popup pour la saisie de l'utilisateur
 *  
 */
public class PopupSaisieUtilisateur extends OKCancelPopup
{
	
	private Searcher box;
	
	private AjouterData data;
	
	/**
	 * 
	 */
	public PopupSaisieUtilisateur(AjouterData data)
	{
		this.data = data;
		
		popupTitle = "Selection de l'amapien";
		saveButtonTitle = "Continuer ...";
		
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		FormLayout f = new FormLayout();
		
		
		box = new Searcher(SearcherList.UTILISATEUR_SANS_CONTRAT);
		box.setParams(data.idModeleContrat);
		
		box.setWidth("80%");
		f.addComponent(box);
		contentLayout.addComponent(f);
		
		
		
	}

	protected boolean performSauvegarder()
	{
		Long userId = (Long) box.getConvertedValue();
		if (userId==null)
		{
			return false;
		}
		
		
		data.validate();
		data.userId =  userId;
		return true;
	}

}
