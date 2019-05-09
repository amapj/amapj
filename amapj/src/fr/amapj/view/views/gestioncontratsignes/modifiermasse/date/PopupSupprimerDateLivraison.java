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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.date;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.service.services.gestioncontratsigne.update.GestionContratSigneUpdateService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Popup pour supprimer une ou plusieurs dates de livraison 
 * 
 *
 */
public class PopupSupprimerDateLivraison extends WizardFormPopup
{

	public enum Step
	{
		INFO_GENERALES, SAISIE_DATE , CONFIRMATION;
	}
	
	private ContratDTO contratDTO;
	
	private List<ContratLigDTO> datesBarrees;

	private ComplexTableBuilder<ContratLigDTO> builder;
	
	private List<ContratLigDTO> modeleContratDateToDebarrer;
	
	
	/**
	 * 
	 */
	public PopupSupprimerDateLivraison(Long idModeleContrat)
	{
		setWidth(80);
		popupTitle = "Supprimer une ou plusieurs dates de livraison";
		
		// Chargement du contrat et calcul des dates barrées
		contratDTO = new MesContratsService().loadContrat(idModeleContrat,null);
		contratDTO.expandExcluded();
		
		datesBarrees = computeDateBarrees();
				
		modeleContratDateToDebarrer = new ArrayList<ContratLigDTO>();
	}
	

	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales(),()->checkDateBarrees());
		add(Step.SAISIE_DATE,()->addFieldSaisieDate(),()->readDateToDebarrer());
		add(Step.CONFIRMATION,()->addFieldConfirmation());		
	}
	
	
	private List<ContratLigDTO> computeDateBarrees()
	{
		return CollectionUtils.filter(contratDTO.contratLigs, e->isBarree(e));
	}

	/**
	 * Permet de savoir si toute la ligne est barrée
	 */
	private boolean isBarree(ContratLigDTO ligDTO)
	{
		for (int j = 0; j < contratDTO.contratColumns.size(); j++)
		{
			if (contratDTO.excluded[ligDTO.i][j]==false)
			{
				return false;
			}
		}
		return true;
	}


	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		String str =    "Cet outil va vous permettre de supprimer définitivement une date de livraison.<br/>"+
				        "Pour pouvoir supprimer une date de livraison, vous devez d'abord barrer la date, en faisant \"Modifier en masse\" puis \"Barrer une ou plusieurs dates de livraison\".<br/>";			
		
		addLabel(str, ContentMode.HTML);
		
	}
	
	private String checkDateBarrees()
	{
		if (datesBarrees.size()==0)
		{
			return "Il n'y a pas de dates barrées sur ce contrat. Vous ne pouvez donc pas continuer.";
		}
		return null;
	}
	
	

	private void addFieldSaisieDate()
	{
		// Titre
		setStepTitle("les dates barrées à supprimer définitivement");
		
		String str = 	"Veuillez indiquer les dates que vous souhaitez supprimer : </br>";
		addLabel(str, ContentMode.HTML);
		
		builder = new ComplexTableBuilder<ContratLigDTO>(datesBarrees);
		builder.setPageLength(7);
		
		builder.addDate("Date", false, 300, e->e.date);
		builder.addCheckBox("Supprimer cette date", "cb",true, 150, e->modeleContratDateToDebarrer.contains(e.modeleContratDateId), null);
		
		addComplexTable(builder);

	}
	
	
	private String readDateToDebarrer()
	{
		modeleContratDateToDebarrer.clear();
		
		
		for (int i = 0; i < datesBarrees.size(); i++)
		{
			ContratLigDTO lig = datesBarrees.get(i);
			
			// case à cocher
			CheckBox cb = (CheckBox) builder.getComponent(i, "cb");
			
			if (cb.getValue()==true)
			{
				modeleContratDateToDebarrer.add(lig);
			}
		}	
		
		if (modeleContratDateToDebarrer.size()==0)
		{
			return "Vous devez choisir au moins une date pour pouvoir continuer.";
		}
		
		return null;
	}
	
	
	

	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation avant modification");
		
		addLabel("Vous allez supprimer définitivement  "+modeleContratDateToDebarrer.size()+" dates de ce contrat:", ContentMode.HTML);
		
		addLabel(FormatUtils.puceDate(modeleContratDateToDebarrer, e->e.date), ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		
	}


	

	@Override
	protected void performSauvegarder()
	{
		// On sauvegarde en base
		new GestionContratSigneUpdateService().suppressManyDateLivs(modeleContratDateToDebarrer);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
