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
 package fr.amapj.view.views.archivage;

import java.util.Date;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.common.DateUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.service.services.archivage.ArchivageContratService;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Popup pour la suppression définitive d'un contrat 
 *  
 */
public class PopupSuppressionDefinitive extends WizardFormPopup
{
	private ModeleContratSummaryDTO mcDto;
	
	private Date lastLivraison;

	private TextField textField;
	
		
	static public enum Step
	{
		SAISIE , CONFIRMATION;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE,()->addSaisie(),()->checkSaisie());
		add(Step.CONFIRMATION, ()->addConfirmation(),()->checkConfirmation());
	}
	
	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	/**
	 * 
	 */
	public PopupSuppressionDefinitive(ModeleContratSummaryDTO mcDto)
	{
		popupTitle = "Suppression définitive d'un contrat.";
		this.mcDto = mcDto;
		this.lastLivraison = new GestionContratService().getLastDate(mcDto.id);
		saveButtonTitle = "Supprimer";
		
		setWidth(80);
	}
	
	
	private void addSaisie()
	{
		addLabel("Le contrat "+mcDto.nom+" est actuellement à l'état Archivé.", ContentMode.HTML);
		
		addLabel("Avec cet outil, vous allez pouvoir supprimer complètement ce contrat vierge et tous les contrats signés associés.", ContentMode.HTML);
		
		addLabel("Il est conseillé de supprimer les contrats 1 an après la date de la dernière livraison.", ContentMode.HTML);
	}

	private String checkSaisie()
	{
		Date ref1 = DateUtils.getDateWithNoTime();
		
		if (lastLivraison.after(ref1))
		{
			return "Il reste des dates de livraisons dans le futur. Vous ne pouvez pas supprimer ce contrat";
		}
				
		Date ref2 = DateUtils.addDays(ref1,-10);
		
		if (lastLivraison.after(ref2))
		{
			return "La dernière livraison de ce contrat est trop récente (moins de 10 jours). Vous ne pouvez pas supprimer ce contrat";  
		}
		return null;
	}


	
	private void addConfirmation()
	{
		addLabel("La dernière livraison de ce contrat a été faite il y a "+getLib(), ContentMode.HTML);
		
		boolean needConfirm = needConfirm();
		
		if (needConfirm)
		{
			addLabel("Merci de saisir ci dessous le texte \"SUPPRESSION\" puis de cliquez sur Supprimer pour supprimer définitivement ce contrat.", ContentMode.HTML);
			
			textField = new TextField();
			textField.setNullRepresentation("");
			textField.setStyleName(ChameleonTheme.TEXTFIELD_BIG);
			textField.setWidth("80%");
			form.addComponent(textField);	
			
		}
		else
		{
			addLabel("Cliquez sur Supprimer pour supprimer définitivement ce contrat.", ContentMode.HTML);
		}
	}
	
	
	private String getLib()
	{
		int delta = DateUtils.getDeltaDay(lastLivraison, DateUtils.getDateWithNoTime());
		
		if (delta<60)
		{
			return delta+" jours";
		}
		else
		{
			return (delta/30)+" mois";
		}
	}

	/**
	 * Une confirmation est necessaire si le contrat date de moins de 6 mois 
	 */
	private boolean needConfirm()
	{
		Date ref1 = DateUtils.addDays(DateUtils.getDateWithNoTime(),-180);
		
		if (lastLivraison.after(ref1))
		{
			return true;
		}
		return false;
	}
	
	
	private String checkConfirmation()
	{
		if (textField==null)
		{
			return null;
		}
		
		String str = textField.getValue();
		
		if (StringUtils.equals(str, "SUPPRESSION")==false)
		{
			return "Vous devez saisir le texte \"SUPPRESSION\"";
		}
		return null;
	}

	protected void performSauvegarder()
	{
		new ArchivageContratService().deleteModeleContratAndContrats(mcDto.id);
	}
}
