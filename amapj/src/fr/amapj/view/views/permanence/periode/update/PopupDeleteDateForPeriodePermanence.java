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
 package fr.amapj.view.views.permanence.periode.update;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Permet d'ajouter des dates 
 */
public class PopupDeleteDateForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	private List<PeriodePermanenceDateDTO> existingDatePerms;
	
	static public enum Step
	{
		INFOS , SAISIE_DATE_DEBUT_FIN , CONFIRMATION;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFOS, ()->infos());
		add(Step.SAISIE_DATE_DEBUT_FIN, ()->saisieDate(),()->checkDates());
		add(Step.CONFIRMATION, ()->confirmation());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupDeleteDateForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Supprimer des dates à une période de permanence";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		dto.dateDebut = null;
		dto.dateFin = null;
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);
	}
	
	private void infos()
	{
		// Titre
		setStepTitle(" les informations générales.");
		
		//
		addLabel("Cet outil va vous permettre de supprimer une ou plusieurs dates de permanences, même si des adhérents sont déjà inscrits", ContentMode.HTML);
	}
	
	
	/**
	 *  
	 */
	private void saisieDate()
	{
		// Titre
		setStepTitle("les dates à supprimer");
		
		addLabel("Toutes les dates de permanences comprises entre ces deux dates seront supprimées", ContentMode.HTML);
		

		// Liste des validators
		IValidator notNull = new NotNullValidator();

		
		addDateField("Date de la première permanence à supprimer", "dateDebut",notNull);
		addDateField("Date de la dernière permanence à supprimer", "dateFin",notNull);
	}
	
	
	private String checkDates()
	{
		if (dto.dateDebut.after(dto.dateFin))
		{
			return "La date de début doit être avant la date de fin";
		}
		
		return null;
	}

	/**
	 *  
	 */
	private void confirmation()
	{
		String info = new PeriodePermanenceUpdateService().getDeleteDateInfo(dto);
		// Titre
		setStepTitle("confirmation avant suppression");
		
		
		addLabel("Vous allez apporter les modifications suivantes sur cette période de permanence:",ContentMode.HTML);
		
		addLabel(info,ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier",ContentMode.HTML);
		
	}
	

	protected void performSauvegarder()
	{	
		// 
		new PeriodePermanenceUpdateService().performDeleteDatePermanence(dto);
	}
	
}
