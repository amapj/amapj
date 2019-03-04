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
 package fr.amapj.view.views.appinstance;

import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.advanced.maintenance.MaintenanceService;
import fr.amapj.service.services.advanced.patch.PatchService;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet uniquement de creer des instances
 * 
 *
 */
public class PatchEditorPart extends WizardFormPopup
{


	public enum Step
	{
		GENERAL , RESULTAT;
	}

	/**
	 * 
	 */
	public PatchEditorPart()
	{
		setWidth(80);
		popupTitle = "Application des patchs";
	}
	

	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.RESULTAT,()->addResultat());
	}

	


	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les informations générales");
		
		// Champ 1
		addLabel("Vous allez appliquer sur toutes les bases le patch V020. Cliquer sur OK pour Suivant pour continuer ...", ContentMode.HTML);
	}
	
	private void addResultat()
	{
		String resultat = new PatchService().applyPatchV020();
		
		// Titre
		setStepTitle("le résultat");
		
		addLabel("Résultat : ", ContentMode.HTML);
		
		addLabel(resultat, ContentMode.HTML);
		
		
	}
	
	
	@Override
	protected void performSauvegarder() throws OnSaveException
	{
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
