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
 package fr.amapj.view.views.saisiepermanence;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.saisiepermanence.planif.PlanifDTO;
import fr.amapj.service.services.saisiepermanence.planif.PlanifDateDTO;
import fr.amapj.service.services.saisiepermanence.planif.PlanifPermanenceService;
import fr.amapj.service.services.saisiepermanence.planif.PlanifUtilisateurDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.views.gestioncontrat.editorpart.FrequenceLivraison;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Popup pour la planification des permanences
 * 
 *
 */
public class PopupPlanificationPermanence extends WizardFormPopup
{

	private PlanifDTO planif;


	public enum Step
	{
		AIDE , INFO_GENERALES, CHOIX_DATES , UTILISATEURS;
	}

	/**
	 * 
	 */
	public PopupPlanificationPermanence()
	{
		setWidth(80);
		popupTitle = "Planification des permanences";

		// Chargement de l'objet à créer
		planif = new PlanifDTO();
		item = new BeanItem<PlanifDTO>(planif);

	}
	
	@Override
	protected void configure()
	{
		add(Step.AIDE,()->addAide());
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.CHOIX_DATES,()->addFieldChoixDates());
		add(Step.UTILISATEURS,()->addFieldUtilisateurs());
	}
	
	private void addAide()
	{
		// Titre
		setStepTitle("explication sur le fonctionnement de cet outil");
		
		String str = 	"Cet outil va vous permettre de planifier les permanences sur une année complète.</br>"+
				"<br/>"+
				"Cet outil positionne lui même les amapiens sur les dates de distributions, de façon aléatoire<br/>"+
				"Par contre, cet outil essaye autant que possible de mettre les amapiens à des permanences où ils ont un panier à venir chercher<br/>"+
				"<br/><br/>"+
				"Il est possible d'attribuer un bonus à un amapien : s'il a un bonus de 2, alors il fera 2 distributions en moins<br/>"+
				"De même, il est possible d'affranchir complètement une personne de permanence.<br/>";
			
								

		addLabel(str, ContentMode.HTML);

	}

	private void addFieldInfoGenerales()
	{
		IValidator notNull = new NotNullValidator();
		
		// Titre
		setStepTitle("les informations générales pour la planification.");
		
		addDateField("Date de début", "dateDebut",notNull);
		
		// 
		addDateField("Date de fin", "dateFin",notNull);
		
		//
		Enum[] enumsToExclude = new Enum[] { FrequenceLivraison.AUTRE , FrequenceLivraison.UNE_SEULE_LIVRAISON };
		addComboEnumField("Fréquence des permanences",  "frequence",enumsToExclude,notNull);
		
		//  
		addIntegerField("Nombre de personnes par permanence", "nbPersonne");
		
		//
		addSearcher("Période de cotisation à prendre en compte ", "idPeriodeCotisation", SearcherList.PERIODE_COTISATION, null);
		
		String str = 	"Le champ précédent permet de préciser les personnes à prendre en compte pour les permanences.</br>"+
				"<br/>"+
				"Si vous saississez une période de cotisation, alors uniquement les amapiens ayant cotisé sur cette période et qui sont ACTIF seront affectées aux permanences.<br/>"+
				"Si vous laissez ce champ vide, alors tous les utilisateurs ACTIF  seront affectées aux permanences.<br/>"+
				"<br/><br/>"+
				"A noter : en étape 4, il vous sera possible de supprimer une personne en particulier si vous le souhaitez<br/>";
			
								

		addLabel(str, ContentMode.HTML);

	}
	
	
	private void addFieldChoixDates()
	{
		// Chargement des données
		new PlanifPermanenceService().fillPlanifInfo(planif);

		// Titre
		setStepTitle("le choix des dates");
		
		//
		CollectionEditor<PlanifDateDTO> f1 = new CollectionEditor<PlanifDateDTO>("Liste des dates", (BeanItem) item, "dates", PlanifDateDTO.class);
		f1.addColumn("datePermanence","Date",FieldType.DATE,null);
		binder.bind(f1, "dates");
		form.addComponent(f1);

	}
	
	
	
	
	

	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("les utilisateurs de permanences");
		
	
		//
		CollectionEditor<PlanifUtilisateurDTO> f1 = new CollectionEditor<PlanifUtilisateurDTO>("Liste des utilisateurs", (BeanItem) item, "utilisateurs", PlanifUtilisateurDTO.class);
		f1.addSearcherColumn("idUtilisateur", "Nom de l'utilisateur",FieldType.SEARCHER, null,SearcherList.UTILISATEUR_ACTIF,null);
		f1.addColumn("actif","Actif",FieldType.CHECK_BOX,true);
		f1.addColumn("bonus", "Bonus",FieldType.QTE, null);
		binder.bind(f1, "utilisateurs");
		form.addComponent(f1);
		
		

	}

	

	@Override
	protected void performSauvegarder()
	{
		new PlanifPermanenceService().savePlanification(planif);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
