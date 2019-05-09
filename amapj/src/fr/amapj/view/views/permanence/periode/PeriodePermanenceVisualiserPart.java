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
 package fr.amapj.view.views.permanence.periode;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;

import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de visualiser les periodes de permanences
 */
public class PeriodePermanenceVisualiserPart extends WizardFormPopup
{ 

	protected PeriodePermanenceDTO dto;
	
	private ComplexTableBuilder<PeriodePermanenceUtilisateurDTO> builder;

	static public enum Step
	{
		INFO_GENERALES, DETAIL_DATE , DETAIL_PARTICIPANTS , BILAN;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES, ()->drawInfoGenerales());
		add(Step.DETAIL_DATE, ()->drawDetailDate());
		add(Step.DETAIL_PARTICIPANTS, ()->drawDetailParticipants());
		add(Step.BILAN,()->addFieldBilan());
		
	}
	
	
	public PeriodePermanenceVisualiserPart(Long idPeriodePermanence)
	{
		setWidth(80);
		saveButtonTitle = "OK";
		popupTitle = "Visualisation d'une période de permanence";

		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);

	}
	
	

	private void drawInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales de cette période de permanence");
		
			
		// 
		addTextField("Nom de la période de permanence", "nom").setReadOnly(true);

		// 
		addTextField("Description de la période", "description").setReadOnly(true);
		
		//
		addComboEnumField("Nature de la période", "nature").setReadOnly(true);
		
		ComboBox box = addComboEnumField("Régle d'inscription sur une date", "regleInscription");
		box.setReadOnly(true);
		box.setWidth("600px");

	
		//
		addDateField("Date de la première permanence", "dateDebut").setReadOnly(true);
		
		//
		addDateField("Date de la dernière permanence", "dateFin").setReadOnly(true);
		
		if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_FLOTTANT)
		{
			addIntegerField("Délai en jour pour modification de son affectation avant permanence", "flottantDelai").setReadOnly(true);
		}
		else if (dto.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_AVEC_DATE_LIMITE)
		{	
			addDateField("Date de fin des inscriptions", "dateFinInscription").setReadOnly(true);
		}
		
	}
	
	
	
	private void drawDetailDate()
	{
		// Titre
		setStepTitle("détails des dates de permanences");

		CollectionEditor<PeriodePermanenceDateDTO> f1 = new CollectionEditor<PeriodePermanenceDateDTO>("Dates", (BeanItem) item, "datePerms", PeriodePermanenceDateDTO.class);
		f1.addColumn("datePerm", "Date permanence", FieldType.DATE, false,null);
		f1.addColumn("nbPlace", "Nb de personnes", FieldType.QTE, false,null);
		f1.disableAllButtons();
		binder.bind(f1, "datePerms");
		form.addComponent(f1);
		
	}
	
	

	private void drawDetailParticipants()
	{
		// Titre
		setStepTitle("détails des participants aux permanences");
		
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
		builder.addString("Nb de participations", false, 100,  e->e.nbParticipation);
				
		addComplexTable(builder);		
	}
	
	
	private void addFieldBilan()
	{
		// Titre
		setStepTitle("bilan");
		
		String bilan = new PeriodePermanenceService().computeBilan(dto);
		
		addLabel(bilan, ContentMode.HTML);
	}

	@Override
	protected void performSauvegarder()
	{
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	
}
