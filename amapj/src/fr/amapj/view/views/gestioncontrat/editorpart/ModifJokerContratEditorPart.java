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

import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;

/**
 * Modification des jokers
 */
public class ModifJokerContratEditorPart extends GestionContratEditorPart
{

	static public enum Step
	{
		REGLE_JOKER;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.REGLE_JOKER, ()->drawRegleJoker(),()->checkJoker());
	}

	

	/**
	 * 
	 */
	public ModifJokerContratEditorPart(Long id)
	{
		setWidth(80);
		popupTitle = "Modification des règles de gestion des jokers";

		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);

	}
	
	
	private void drawRegleJoker()
	{
		setStepTitle("les règles de gestion des jokers");
		
		addBlocGestionJoker();
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
	
	/**
	 * Vérifie si il n'y a pas déjà des contrats signés, qui vont empecher de modifier les produits
	 */
	@Override
	protected String checkInitialCondition()
	{
		if (modeleContrat.nature!=NatureContrat.ABONNEMENT)
		{
			return "Les jokers sont utilisables uniquement avec les contrats de type abonnement.";
		}
		
		int nbInscrits = new GestionContratService().getNbInscrits(modeleContrat.id);
		if (nbInscrits!=0)
		{
			String str = "Vous ne pouvez plus modifier les règles de gestion des jokers pour ce contrat<br/>"+
						 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat.<br/>"+
						 "Si vous souhaitez vraiment modifier les règles de gestion des jokers, vous devez aller dans \"Gestion des contrats signés\", puis vous cliquez sur le bouton \"Modifier en masse\".<br/>";
			return str;
		}
		
		return null;
	}
	
}
