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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItem;

import fr.amapj.common.DateUtils;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet d'ajouter des dates 
 */
public class PopupAddDateForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	private List<PeriodePermanenceDateDTO> existingDatePerms;
	
	static public enum Step
	{
		SAISIE_DATES;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE_DATES, ()->saisieDate(),()->checkDates());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupAddDateForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Ajouter des dates à une période de permanence";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		// On sauvegarde dans une autre variable la liste des dates existantes
		existingDatePerms = new ArrayList<PeriodePermanenceDateDTO>();
		existingDatePerms.addAll(dto.datePerms);
		
		// On efface la liste des dates déjà présentes dans le dto  
		dto.datePerms.clear();
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}
	
	/**
	 *  
	 */
	private void saisieDate()
	{
		// Titre
		setStepTitle("les dates à ajouter");
		
		//
		CollectionEditor<PeriodePermanenceDateDTO> f1 = new CollectionEditor<PeriodePermanenceDateDTO>("Dates", (BeanItem) item, "datePerms", PeriodePermanenceDateDTO.class);
		f1.addColumn("datePerm", "Date permanence", FieldType.DATE, null);
		f1.addColumn("nbPlace", "Nb de personnes", FieldType.QTE, null);
		binder.bind(f1, "datePerms");
		form.addComponent(f1);
		
	}
	
	
	private String checkDates()
	{
		if (dto.datePerms.size()==0)
		{
			return "Il faut ajouter au moins une date de permanence";
		}
		
		
		for (PeriodePermanenceDateDTO datePerm : dto.datePerms)
		{
			if (isExisting(datePerm))
			{
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
				return "La date "+df.format(datePerm.datePerm)+" est déjà existante, vous ne pouvez pas l'ajouter de nouveau";
			}
		}
		
		return null;
	}

	
	private boolean isExisting(PeriodePermanenceDateDTO datePerm)
	{
		for (PeriodePermanenceDateDTO d : existingDatePerms)
		{
			if (DateUtils.equals(datePerm.datePerm, d.datePerm))
			{
				return true;
			}
		}
		return false;
	}


	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new PeriodePermanenceUpdateService().addDates(dto);
	}
	
}
