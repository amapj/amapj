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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.grille;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;

import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService.ResBarrerProduit;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingData;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet de barrer des dates et des produits, même si des contrats sont déjà signés 
 *
 */
public class PopupBarrerProduit extends WizardFormPopup
{

	private Long idModeleContrat;
	
	private BarrerData barrerData;

	public enum Step
	{
		INFO_GENERALES, SAISIE_DATE , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupBarrerProduit(Long idModeleContrat)
	{
		setWidth(80);
		popupTitle = "Barrer des produits sur certaines dates";

		// 
		this.idModeleContrat = idModeleContrat;
		
	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE_DATE,()->addFieldSaisieDate(),()->checkSaisieProduitBarre());
		add(Step.CONFIRMATION,()->addFieldConfirmation());
			
	}

	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		String str = 	"Cet outil va vous permettre de barrer des produits sur certaines dates, pour tous les adhérents à ce contrat.<br/>"+
						"Les quantités commandées sur les produits barrés seront alors remises à zéro pour les contrats déjà signés.<br/>"+
						"<br/>"+
						"Exemple de cas d'utilisation : un producteur a prévu de livrer des pommes et des poires pendant 2 mois à l'AMAP.<br/>"+
						"20 adhérents ont souscrits à ce contrat sur les 2 mois<br/>"+
						"Suite à un problème agricole ou autre, le producteur peut bien livrer les pommes sur 2 mois, mais il peut livrer les poires sur 1 mois seulement.<br/>"+
						"Cet outil permet alors de barrer les poires sur le dernier mois et de mettre à zéro les quantités commandées pour les poires sur le mois barré.<br/>"+
						"Il faut ensuite gérer le trop payé des adhérents sous la forme d'un avoir.<br/>"+
						"<br/><br/>"+
						"Cet outil permet aussi de faire l'opération inverse : rendre accessible un produit qui était barré.<br/>";
					
										
		
		addLabel(str, ContentMode.HTML);
		

	}
	
	

	private void addFieldSaisieDate()
	{
		// Titre
		setStepTitle("choix des produits et des dates à barrer");
		
		String str = 	"Cliquez sur le bouton ci dessous pour choisir les produits et dates à barrer / ne pas barrer, puis après cliquez sur Etape suivante";
		addLabel(str, ContentMode.HTML);
		
		//
		Button button = new Button("Choix des dates / produits", e->handleChoixDateProduit());
		
		form.addComponent(button);

	}
	
	public class BarrerData extends CascadingData
	{
		public Long idModeleContrat;
		public ContratDTO contratDTO;
		public ResBarrerProduit resBarrerProduit;
	}
	
	
	private void handleChoixDateProduit()
	{
		barrerData = new BarrerData();
		barrerData.idModeleContrat = idModeleContrat;
		GrilleBarrerDateProduit.open(new GrilleBarrerDateProduit(barrerData),()->endChoixDateProduit(barrerData));
	}
	
	private void endChoixDateProduit(BarrerData barrerData)
	{
		// Si appui sur annuler
		if (barrerData.shouldContinue()==false)
		{
			close();
		}
	}
	
	private String checkSaisieProduitBarre()
	{
		// Si l'utilisateur n'a pas appuyé sur le bouton "Choix des dates / produits"
		if (barrerData==null)
		{
			return "Vous devez cliquer sur le bouton \"Choix des dates / produits\"";
		}
		return null;
	}
	
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation avant suppression");
		
		
		addLabel("Vous allez apporter les modifications suivantes sur ce contrat:", ContentMode.HTML);
		
		addLabel(barrerData.resBarrerProduit.msg, ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		
	}


	

	@Override
	protected void performSauvegarder()
	{
		new GestionContratSigneService().performBarrerProduitInfo(barrerData.resBarrerProduit, barrerData.contratDTO);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
