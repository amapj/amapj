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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.joker;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.views.gestioncontrat.editorpart.GestionContratEditorPart;

/**
 * Permet de modifier les regles des jokers, même quand des constrats sont signés  
 * 
 *
 */
public class PopupModifJoker extends GestionContratEditorPart
{

	public enum Step
	{
		INFO_GENERALES, SAISIE , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupModifJoker(Long mcId)
	{
		setWidth(80);
		popupTitle = "Modification des règles de gestion des jokers";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(mcId);
			
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE,()->drawRegleJoker(),()->checkJoker());
		add(Step.CONFIRMATION,()->addFieldConfirmation());
	}

	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		String str;
		
		if (nbInscrits==0)
		{
			str = "Aucun adhérent n'est inscrit à ce contrat. Vous pouvez donc modifier les règles de gestion des jokers librement.";
		}
		else
		{
			
			str = 	""+nbInscrits+" adhérents ont déjà souscrits à ce contrat.<br/>"+
					"La modification des règles de gestion des jokers peut donc impacter des contrats existants.<br/><br/>"+
					"Une fois que vous aurez modifié les règles, le programme vous affichera la liste des adhérents impactés pour que vous puissiez les prévenir.<br/>";						 
		}
		addLabel(str, ContentMode.HTML);
	}
	
	
	private void drawRegleJoker()
	{
		setStepTitle("les règles de gestion des jokers");
		addBlocGestionJoker();
	}
	
	
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation");
			
		String info = new GestionContratSigneService().getModifJokerInfo(modeleContrat);
			
		addLabel(info, ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		
	}


	@Override
	protected void performSauvegarder()
	{
		new GestionContratService().updateJoker(modeleContrat);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
