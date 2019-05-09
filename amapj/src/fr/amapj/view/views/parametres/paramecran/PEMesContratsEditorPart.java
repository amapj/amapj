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
 package fr.amapj.view.views.parametres.paramecran;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.model.models.param.paramecran.PEMesContrats;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Permet la saisie des paramètres de l'écran "mes contrats"
 * 
 */
public class PEMesContratsEditorPart extends WizardFormPopup
{

	private PEMesContrats pe;

	public enum Step
	{
		IMPRESSION_CONTRAT , ADHESION ;
	}

	/**
	 * 
	 */
	public PEMesContratsEditorPart()
	{
		pe = (PEMesContrats) new ParametresService().loadParamEcran(MenuList.MES_CONTRATS);
		
		setWidth(80);
		popupTitle = "Paramètrage de l'écran \""+pe.getMenu().getTitle()+"\"";

		item = new BeanItem<PEMesContrats>(this.pe);

	}
	
	@Override
	protected void configure()
	{
		add(Step.IMPRESSION_CONTRAT,()->addFieldImpressionContrat());
		add(Step.ADHESION,()->addFieldAdhesion());
	}

	private void addFieldImpressionContrat()
	{
		// Titre
		setStepTitle("Impression des contrats");
		
		addComboEnumField("L'amapien peut imprimer ses contrats au format Excel ", "canPrintContrat",  new NotNullValidator());
		
		addComboEnumField("L'amapien peut imprimer ses contrats d'engagement au format Pdf ", "canPrintContratEngagement", new NotNullValidator());
	
		addComboEnumField("Présentation des contrats à imprimer", "presentationImpressionContrat", new NotNullValidator());	
		
		String msg = "Soyez prudent si vous autorisez l'impression des contrats d'engagements par les amapiens :<br>"+	
				" Le cas suivant peut se produire :<br/><ul>"
				+ "<li>L'amapien s'inscrit à un contrat puis l'imprime</li>"
				+ "<li>l'amapien modifie le contrat, volontairement ou par erreur </li></ul><br/>"
				+ "Il est préférable que le référent imprime lui même les contrats après la date limite d'inscription<br/>"
				+ "ou alors vous devez bien vérifier la cohérence des contrats";
				
	
		addLabel(msg, ContentMode.HTML);
		
	}
	
	

	private void addFieldAdhesion()
	{
		// Titre
		setStepTitle("Adhesion");
		
		addComboEnumField("L'amapien peut imprimer son bulletin d'adhésion ", "canPrintAdhesion", new NotNullValidator());		
	}


	

	@Override
	protected void performSauvegarder()
	{
		new ParametresService().update(pe);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
