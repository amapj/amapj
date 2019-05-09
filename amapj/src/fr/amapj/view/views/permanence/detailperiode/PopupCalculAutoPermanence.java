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

import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.permanence.detailperiode.calculauto.CalculAutoPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Popup pour le calcul automatique des permanences
 * 
 *
 */
public class PopupCalculAutoPermanence extends WizardFormPopup
{

	
	private Long idPeriodePermanence;

	public enum Step
	{
		AIDE , INFO_GENERALES, BILAN;
	}

	/**
	 * 
	 */
	public PopupCalculAutoPermanence(Long idPeriodePermanence)
	{
		setWidth(80);
		popupTitle = "Calcul automatique des inscriptions aux permanences";

		this.idPeriodePermanence = idPeriodePermanence;
	
	}
	
	@Override
	protected void configure()
	{
		add(Step.AIDE,()->addAide());
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.BILAN,()->addResultatAffectation());
	}
	
	private void addAide()
	{
		// Titre
		setStepTitle("explication sur le fonctionnement de cet outil");
		
		String str = 	"Cet outil va vous permettre de calculer automatique les inscripptions aux permanences sur une période complète.</br>"+
				"<br/>"+
				"Cet outil positionne lui même les amapiens sur les dates de permanence, de façon aléatoire<br/>"+
				"Par contre, cet outil essaye autant que possible de mettre les amapiens à des permanences où ils ont un panier à venir chercher<br/>"+
				"<br/><br/>"+
				"Cet outil prend bien sûr en compte le nombre de participations affectées à chaque personne.<br/><br/>"+
				"Il est possible d'utiliser cet outil même si des personnes sont déjà inscrites.";
			
								

		addLabel(str, ContentMode.HTML);

	}

	private void addFieldInfoGenerales()
	{
		setStepTitle("informations générales sur cette période");
		
		PeriodePermanenceDTO dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		String bilan = new PeriodePermanenceService().computeBilan(dto);
		
		addLabel(bilan, ContentMode.HTML);
		
		addLabel("Cliquer maintenant sur Sauvegarder pour lancer le calcul automatique des inscriptions, ou Annuler pour ne rien faire", ContentMode.HTML);
				

	}
	
	
	private void addResultatAffectation()
	{
		setAllButtonsAsOK();
		String bilan = new CalculAutoPeriodePermanenceService().performPlanification(idPeriodePermanence);
		
		setStepTitle("résultat du calcul");
		
		if (bilan.length()==0)
		{
			addLabel("L'affectation a été réalisée normalement", ContentMode.HTML);	
		}
		else
		{
			addLabel("L'affectation a été réalisé avec les remarques suivantes :", ContentMode.HTML);
			addLabel(bilan, ContentMode.HTML);
		}
	}
	
		

	@Override
	protected void performSauvegarder()
	{
		// Nothing to do 
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
