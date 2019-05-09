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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.gestioncontrat.DatePaiementModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet de modifier les dates de paiements
 * 
 *
 */
public class ModifDatePaiementContratEditorPart extends WizardFormPopup
{
	private ModeleContratDTO modeleContrat;

	public enum Step
	{
		INFO, DATE;
	}

	/**
	 * 
	 */
	public ModifDatePaiementContratEditorPart(Long id)
	{
		setWidth(80);
		popupTitle = "Modification des dates de paiement d'un contrat (ajout / suppression)";
		
		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO,()->addFieldInfo());
		add(Step.DATE,()->addFieldDatePaiement());
		
	}

	private void addFieldInfo()
	{
		// Titre
		setStepTitle("information");
		
		//
		String content = 	"Cet outil vous permet d'ajouter des dates de paiement ou d'en supprimer<br/>"+
							"même si il y déjà des contrats signés.<br/><br/>"+
							"Une date  ne pourra pas être supprimée uniquement si un adhérent a positionné un chèque dessus<br/>"+
							"Pour l'ajout, une seule date de paiement est possible par mois (il n'est pas possible d'avoir deux paiements dans le même mois)<br/>";
		addLabel(content, ContentMode.HTML);

	}

	private void addFieldDatePaiement()
	{
		setStepTitle("la modification des dates de paiements");
		
		CollectionEditor<DatePaiementModeleContratDTO> f1 = new CollectionEditor<DatePaiementModeleContratDTO>("Liste des dates de paiement", (BeanItem) item, "datePaiements", DatePaiementModeleContratDTO.class);
		f1.addColumn("datePaiement", "Date",FieldType.DATE, null);
		binder.bind(f1, "datePaiements");
		form.addComponent(f1);
	}


	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		if (checkFieldDate()==false)
		{
			List<String> ls = new ArrayList<String>();
			ls.add("Il y a des erreurs dans la saisie des dates");
			ls.add("Vous ne devez pas avoir de lignes vides");
			throw new OnSaveException(ls);
		}
		
		
		new GestionContratService().updateDatePaiement(modeleContrat);
	}
	
	
	
	private boolean checkFieldDate()
	{
		List<DatePaiementModeleContratDTO> us = modeleContrat.datePaiements;
		for (DatePaiementModeleContratDTO lig : us)
		{
			if (lig.datePaiement==null)
			{
				return false;
			}
		}
		return true;
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
}
