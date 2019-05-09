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
 package fr.amapj.view.views.remiseproducteur;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.remiseproducteur.PaiementRemiseDTO;
import fr.amapj.service.services.remiseproducteur.RemiseDTO;
import fr.amapj.service.services.remiseproducteur.RemiseProducteurService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Permet de saisir les remises
 * 
 *
 */
public class RemiseEditorPart extends WizardFormPopup
{
	private RemiseDTO remiseDTO;

	public enum Step
	{
		ACCUEIL, AFFICHAGE , CONFIRMATION;
	}

	/**
	 * 
	 */
	public RemiseEditorPart(RemiseDTO remiseDTO)
	{
		setWidth(80);
		popupTitle = "Réalisation d'une remise";
		
		this.remiseDTO = remiseDTO;
		
		item = new BeanItem<RemiseDTO>(remiseDTO);

	}
	
	@Override
	protected void configure()
	{	
		add(Step.ACCUEIL,()->addFieldAccueil());
		add(Step.AFFICHAGE,()->addFieldAffichage());
		add(Step.CONFIRMATION,()->addFieldConfirmation());
	}

	private void addFieldAccueil()
	{
		// Titre
		setStepTitle("saisie de la date de la remise");
		
		String montant = new CurrencyTextFieldConverter().convertToString(remiseDTO.mnt)+" €";
		String text = 	"Vous allez valider une remise de chèques à un producteur<br/>"+
						"Mois de la remise : "+remiseDTO.moisRemise+"<br/>"+
						"Montant total de la remise "+montant+"<br/><br/>"+
						"Merci de saisir ci dessous la date réelle de remise des chèques";
		
		addLabel(text, ContentMode.HTML);
		
		//
		addDateField("Date réelle de la remise", "dateReelleRemise");

	}

	private void addFieldAffichage()
	{
		// Titre
		setStepTitle("les chèques à inclure dans la remise");
		
		
		String text = 	"Voici la liste des chèques à inclure dans la remise :<br/>";
		
		for (PaiementRemiseDTO paiement : remiseDTO.paiements)
		{
			String montant = new CurrencyTextFieldConverter().convertToString(paiement.montant)+" €";
			text = text+paiement.nomUtilisateur+" "+paiement.prenomUtilisateur+" - Montant = "+montant;
			if (paiement.commentaire1!=null)
			{
				text = text+" - "+paiement.commentaire1;
			}
			if (paiement.commentaire2!=null)
			{
				text = text+" - "+paiement.commentaire2;
			}
			text=text+"<br/>";
		}
		
		addLabel(text, ContentMode.HTML);
		
	}
	
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation");
		
		String text = 	"Confirmez vous avoir tous les chèques ? <br/>";
		
		addLabel(text, ContentMode.HTML);
		
	}


	@Override
	protected void performSauvegarder()
	{
		new RemiseProducteurService().performRemise(remiseDTO);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
