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
 package fr.amapj.view.views.saisiepermanence;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.saisiepermanence.planif.PlanifDTO;
import fr.amapj.service.services.saisiepermanence.planif.PlanifPermanenceService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Popup pour la planification des permanences
 * 
 *
 */
public class PopupDeletePermanence extends WizardFormPopup
{

	private PlanifDTO planif;


	public enum Step
	{
		AIDE , INFO_GENERALES , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupDeletePermanence()
	{
		setWidth(80);
		popupTitle = "Suppression en masse des permanences";

		// Chargement de l'objet à créer
		planif = new PlanifDTO();
		item = new BeanItem<PlanifDTO>(planif);

	}
	
	@Override
	protected void configure()
	{
		add(Step.AIDE,()->addAide());
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.CONFIRMATION,()->addFieldConfirmer());
	}
	
	private void addAide()
	{
		// Titre
		setStepTitle("présentation");
		
		String str = 	"Cet outil va vous permettre de supprimer une liste de permanences en une seule fois.</br>"+
				"<br/>"+
				"Vous allez pouvoir saisir une date de début, une date de fin,<br/>"+
				"et toutes les permanences sur cet intervalle seront supprimées.<br/>";

		addLabel(str, ContentMode.HTML);

	}

	private void addFieldInfoGenerales()
	{
		IValidator notNull = new NotNullValidator();
		
		// Titre
		setStepTitle("l'intervalle de dates à supprimer");
		
		//
		addDateField("Date de début", "dateDebut",notNull);
		
		// 
		addDateField("Date de fin", "dateFin",notNull);
		

	}
	
	
	private void addFieldConfirmer()
	{

		// Titre
		setStepTitle("confirmation");
		
		String str = 	"Etes vous sûr ? </br>";

		addLabel(str, ContentMode.HTML);

	}
	
	


	

	@Override
	protected void performSauvegarder()
	{
		new PlanifPermanenceService().deletePlanification(planif);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
