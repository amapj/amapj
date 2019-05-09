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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.gestioncontrat.DateModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionNoDuplicates;
import fr.amapj.view.engine.popup.formpopup.validator.CollectionSizeValidator;
import fr.amapj.view.engine.popup.formpopup.validator.ColumnNotNull;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;

/**
 * Permet uniquement de creer des contrats
 * 
 *
 */
public class ModifDateContratEditorPart extends WizardFormPopup
{
	private ModeleContratDTO modeleContrat;

	public enum Step
	{
		CHOIX_FREQUENECE, DATE_LIVRAISON;
	}

	/**
	 * 
	 */
	public ModifDateContratEditorPart(Long id)
	{
		setWidth(80);
		popupTitle = "Modification des dates de livraison d'un contrat";
		
		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	
	
	@Override
	protected void configure()
	{
		add(Step.CHOIX_FREQUENECE,()->addFieldChoixFrequence());
		add(Step.DATE_LIVRAISON,()->addFieldDateLivraison());
			
	}

	private void addFieldChoixFrequence()
	{
		// Titre
		setStepTitle("frequence des livraisons");
		
		//
		addComboEnumField("Fréquence des livraisons", "frequence");
		
		//
		addLabel("Nota : si vous souhaitez modifier uniquement une date ou deux dans la liste et ne pas tout recalculer,<br/>"
				+ "merci de choisir \"Autre ...\" dans la liste déroulante ci dessus.", ContentMode.HTML);

	}

	private void addFieldDateLivraison()
	{
		// Titre
		setStepTitle("les dates de livraison");
		
		if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
		{
			addDateField("Date de la livraison", "dateDebut");
		}
		else if (modeleContrat.frequence==FrequenceLivraison.AUTRE)
		{
			IValidator size = new CollectionSizeValidator<DateModeleContratDTO>(1, null);
			IValidator noDuplicates = new CollectionNoDuplicates<DateModeleContratDTO>(e->e.dateLiv);
								
			//
			addCollectionEditorField("Liste des dates", "dateLivs", DateModeleContratDTO.class,size,noDuplicates);
			addColumn("dateLiv", "Date",FieldType.DATE, null,new ColumnNotNull<DateModeleContratDTO>(e->e.dateLiv));			
		}
		else
		{
			addDateField("Date de la première livraison", "dateDebut");
			addDateField("Date de la dernière livraison", "dateFin");
		}
	}
	
	
	/**
	 * Retourne null si tout est ok, un message sinon
	 * ATTENTION CODE COPIE DEPUIS GESTIONCONTRATEDITORPART
	 * @return
	 */
	private String checkDate()
	{
		if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
		{
			// C'est toujours bon 
			return null;
		}
		else if (modeleContrat.frequence==FrequenceLivraison.AUTRE)
		{
			// C'est toujours bon 
			return null;
			
		}
		else
		{
			if (modeleContrat.dateDebut.after(modeleContrat.dateFin))
			{
				return "La date de début doit être avant la date de fin ";
			}
			else
			{
				return null;
			}
		}
	}


	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		String msg = checkDate();
		if (msg!=null)
		{
			throw new OnSaveException("Erreur dans la saisie des dates",msg);
		}
		
		new GestionContratService().updateDateModeleContrat(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	/**
	 * Vérifie si il n'y a pas déjà des contrats signés, qui vont empecher de modifier les dates
	 */
	@Override
	protected String checkInitialCondition()
	{
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		if (nbInscrits!=0)
		{
			String str = "Vous ne pouvez plus modifier les dates de livraison de ce contrat<br/>"+
						 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat.<br/>"+
						 "Trois cas sont possibles :<br/><ul>"+
						 "<li>Vous souhaitez déplacer une date. Dans ce cas, vous allez dans \"Gestion des contrats signés\", puis vous cliquez sur le bouton \"Modifier en masse\", puis sur \"Déplacer une date de livraison\".</li>"+						 
						 "<li>Vous souhaitez ajouter une date. Dans ce cas, vous allez dans \"Gestion des contrats signés\", puis vous cliquez sur le bouton \"Modifier en masse\", puis sur \"Ajouter une date de livraison\".</li>"+
						 "<li>Une date a été réellement annulée suite à un problème avec le producteur par exemple. Dans ce cas, vous allez dans \"Gestion des contrats signés\", puis vous cliquez sur le bouton \"Modifier en masse\", puis sur \"Mettre à zéro les quantités commandées sur une ou plusieurs dates de livraison\"."+
						 "Un assistant vous aidera à gérer le cas où une ou plusieurs livraisons sont annulées.</li>"+					
						 "</ul>";
			return str;
		}
		
		return null;
	}
}
