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
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelMessagePopup;

/**
 * Permet de modifier l'entete du contrat, c'est à dire son nom
 * et la date limite d'inscription 
 */
public class ModifEnteteContratEditorPart extends GestionContratEditorPart
{
	
	private ComboBox box;
	
	static public enum Step
	{
		INFO_GENERALES, DATE_FIN_INSCRIPTION;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES, ()->drawEntete());
		add(Step.DATE_FIN_INSCRIPTION, ()->drawFinInscription(false));
	}
	
	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public ModifEnteteContratEditorPart(Long id)
	{
		super();
		popupTitle = "Modification d'un contrat";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		modeleContrat = new GestionContratService().loadModeleContrat(id);
		
		item = new BeanItem<ModeleContratDTO>(modeleContrat);
		
	}
	
	/**
	 * Les modifications sont importantes par rapport à drawInformationsGenerales, donc 
	 * cette méthode est entierement re ecrite 
	 */
	private void drawEntete()
	{
		IValidator uniq = new UniqueInDatabaseValidator(ModeleContrat.class,"nom",modeleContrat.id);
		
		IValidator len_1_100 = new StringLengthValidator(1, 100);
		IValidator len_1_255 = new StringLengthValidator(1, 255);
		IValidator notNull = new NotNullValidator();
		
		// Champ 1
		addTextField("Nom du contrat", "nom",uniq,len_1_100);
		
		// Champ 2
		addTextField("Description du contrat", "description",len_1_255);
		
		//
		box = addComboEnumField("Nature du contrat", "nature",notNull);
		box.setEnabled(false);
		
		//
		HorizontalLayout hl = (HorizontalLayout) box.getParent();
		Button modif = new Button("Modifier la nature du contrat");
		modif.addClickListener(e->handleModifierNature());
		hl.addComponent(modif);
		
	}
	
	


	private void handleModifierNature()
	{
		String title = "Confirmer";
		String htmlMessage = "Etes vous sûr de vouloir modifier la nature de ce contrat ?<br/>"+
							 "En effet, modifier la nature d'un contrat modifie totalement le comportement de ce contrat<br/>"+
							 "et en particulier la façon dont les utilisateurs vont pouvoir modifier ce contrat.<br/><br/>"+
							 "Ne faites ceci que si vous êtes sûr de vous.";
		
		OKCancelMessagePopup popup = new OKCancelMessagePopup(title, htmlMessage, ()->box.setEnabled(true));
		OKCancelMessagePopup.open(popup);
	}

	

	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new GestionContratService().updateEnteteModeleContrat(modeleContrat);
	}
	
}
