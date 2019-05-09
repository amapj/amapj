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
 package fr.amapj.view.views.advanced.devtools;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.gestioncontrat.DemoDateDTO;
import fr.amapj.view.engine.popup.formpopup.tab.TabFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Essai de popup tab 
 *
 */
public class DevToolsTabDemo extends TabFormPopup
{

	private DemoDateDTO demoDateDTO;

	/**
	 * 
	 */
	public DevToolsTabDemo()
	{
		popupTitle = "Test des tabs";

		//
		demoDateDTO = new DemoDateDTO();
		item = new BeanItem<DemoDateDTO>(demoDateDTO);

	}
	
	

	@Override
	protected void addTabInfo()
	{
		addTab("Onglet1", ()->onglet1());
		addTab("Onglet2", ()->onglet2());
	}

	

	private void onglet1()
	{
		// Titre
		setStepTitle("date onglet 1");
		
		addDateField("Date de la première livraison", "dateDebut", new NotNullValidator());
		
	}
	
	
	private void onglet2()
	{
		// Titre
		setStepTitle("date onglet 2");
		
		addDateField("Date de la dernière livraison", "dateFin", new NotNullValidator());
	
		addDateField("Date du premier paiement", "premierCheque");
		
		addDateField("Date du dernier paiement", "dernierCheque");
		
		addTextField("Mot de passe", "password");
	}
	
	
	
	

	@Override
	protected void performSauvegarder()
	{
		// TODO 		
	}


}
