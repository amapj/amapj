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
 * Permet de modifier l'entete de la periode de permanence
 */
public class PopupModifEnteteForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	static public enum Step
	{
		INFO_GENERALES, DATE_FIN_INSCRIPTION;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES, ()->drawEntete());
		add(Step.DATE_FIN_INSCRIPTION, ()->drawFinInscription());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupModifEnteteForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Modification d'une période de permanence";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}
	
	/**
	 *  
	 */
	private void drawEntete()
	{
		// Titre
		setStepTitle("les informations générales de cette période de permanence");
		
		// Liste des validators
		IValidator len_1_100 = new StringLengthValidator(1, 100);
		IValidator len_1_255 = new StringLengthValidator(1, 255);
		IValidator uniq = new UniqueInDatabaseValidator(PeriodePermanence.class,"nom",dto.id);
	
		// 
		addTextField("Nom de la période de permanence", "nom",len_1_100,uniq);

		// 
		addTextField("Description de la période", "description",len_1_255);
		
	}
	
	
	private void drawFinInscription()
	{
		if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_FLOTTANT)
		{
			//
			dto.dateFinInscription = null;
			
			// Titre
			setStepTitle("Période de permanence sans date limite d'inscription - Délai pour modification des affectations");
			
			addIntegerField("Délai en jour pour modification de son affectation avant permanence", "flottantDelai");
			
		}
		else if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_AVEC_DATE_LIMITE)
		{	
			// Titre
			setStepTitle("la date de fin des inscriptions");
			
			IValidator notNull = new NotNullValidator();  
			
			// Champ 4
			addDateField("Date de fin des inscriptions", "dateFinInscription",notNull);
		}
	}

	

	

	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new PeriodePermanenceUpdateService().updateEntete(dto);
	}
	
}
