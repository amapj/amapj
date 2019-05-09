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
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;

/**
 * Permet de modifier les infos de paiements
 * 
 *
 */
public class ModifPaiementContratEditorPart extends WizardFormPopup
{
	private ModeleContratDTO modeleContrat;

	public enum Step
	{
		CHOIX_TYPE, PAIEMENT;
	}

	/**
	 * 
	 */
	public ModifPaiementContratEditorPart(Long id)
	{
		setWidth(80);
		popupTitle = "Modification des conditions de paiement d'un contrat";
		
		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	@Override
	protected void configure()
	{
		add(Step.CHOIX_TYPE,()->addFieldChoixTypPaiement());
		add(Step.PAIEMENT,()->addFieldPaiement());
	}

	private void addFieldChoixTypPaiement()
	{
		// Titre
		setStepTitle("le type de paiement");
		
		//
		addComboEnumField("Gestion des paiements", "gestionPaiement");

	}

	private void addFieldPaiement()
	{
		IValidator len_0_255 = new StringLengthValidator(0, 255);
		
		setStepTitle("les informations sur le paiement");
		
		if (modeleContrat.gestionPaiement==GestionPaiement.GESTION_STANDARD)
		{	
			addTextField("Ordre du chèque", "libCheque",len_0_255);
			
			if (modeleContrat.frequence==FrequenceLivraison.UNE_SEULE_LIVRAISON)
			{
				PopupDateField p = addDateField("Date de remise du chèque", "dateRemiseCheque");
				if (modeleContrat.getDateRemiseCheque()==null)
				{
					p.setValue(modeleContrat.dateDebut);
				}
			}
			else
			{
				PopupDateField p = addDateField("Date de remise des chèques", "dateRemiseCheque");
				if (modeleContrat.getDateRemiseCheque()==null)
				{
					p.setValue(modeleContrat.dateFinInscription);
				}
				
				p = addDateField("Date du premier paiement", "premierCheque");
				if (modeleContrat.getPremierCheque()==null)
				{
					p.setValue(DateUtils.firstDayInMonth(modeleContrat.dateDebut));
				}
				
				
				p = addDateField("Date du dernier paiement", "dernierCheque");
				if (modeleContrat.getDernierCheque()==null)
				{
					p.setValue(DateUtils.firstDayInMonth(modeleContrat.dateFin)); 
				}
			}
		}
		else
		{
			TextField f = (TextField) addTextField("Texte affiché dans la fenêtre paiement", "textPaiement");
			f.setMaxLength(2048);
			f.setHeight(5, Unit.CM);
		}
	}


	@Override
	protected void performSauvegarder()
	{
		new GestionContratService().updateInfoPaiement(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	/**
	 * Vérifie si il n'y a pas déjà des contrats signés, qui vont empecher de modifier les produits
	 */
	@Override
	protected String checkInitialCondition()
	{
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		if (nbInscrits!=0)
		{
			String str = "Vous ne pouvez plus modifier les conditions de paiement de ce contrat<br/>"+
						 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat<br/>."+
						 "Une seule solution est possible :<br/><ul>"+
						 "<li>Supprimez les contrats signés par les adhérents, si ce sont des données de test</li>"+
						 "</ul>";
			return str;
		}
		
		return null;
	}
}
