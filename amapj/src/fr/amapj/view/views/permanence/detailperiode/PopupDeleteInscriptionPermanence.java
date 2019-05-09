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
 package fr.amapj.view.views.permanence.detailperiode;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Popup pour la planification des permanences
 * 
 *
 */
public class PopupDeleteInscriptionPermanence extends WizardFormPopup
{

	protected PeriodePermanenceDTO dto;

	public enum Step
	{
		AIDE , INFO_GENERALES , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupDeleteInscriptionPermanence(Long idPeriodePermanence)
	{
		setWidth(80);
		popupTitle = "Suppression en masse des inscriptions à une période de permanence";
		
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);

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
		
		String str = 	"Cet outil va vous permettre de supprimer une liste d'inscriptions à des permanences en une seule fois.</br>"+
				"<br/>"+
				"Vous allez pouvoir saisir une date de début, une date de fin,<br/>"+
				"et toutes les inscriptions sur cet intervalle seront supprimées.<br/>";

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

		String info = new DetailPeriodePermanenceService().getDeleteInscriptionInfo(dto);
		// Titre
		setStepTitle("confirmation avant suppression");
		
		
		addLabel("Vous allez apporter les modifications suivantes sur cette période de permanence:",ContentMode.HTML);
		
		addLabel(info,ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier",ContentMode.HTML);
		
	}
	


	

	@Override
	protected void performSauvegarder()
	{
		new DetailPeriodePermanenceService().performDeleteInscription(dto);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
