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

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;

import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;

/**
 * 
 */
public class PopupRegleInscriptionPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	static public enum Step
	{
		REGLES;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.REGLES, ()->drawRegles());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupRegleInscriptionPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Modification régles d'inscriptions sur une date";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}
	
	/**
	 *  
	 */
	private void drawRegles()
	{
		// Titre
		setStepTitle("les régles d'inscriptions sur une date");
		
		ComboBox box = addComboEnumField("Régle d'inscription sur une date", "regleInscription", new NotNullValidator());
		box.setWidth("600px");
				
	}
	

	protected void performSauvegarder()
	{	
		new PeriodePermanenceUpdateService().updateRegleInscription(dto);
	}
	
}
