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
 package fr.amapj.view.views.editionspe;

import com.vaadin.data.util.BeanItem;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.access.AdminTresorierDTO;
import fr.amapj.service.services.editionspe.EditionSpeDTO;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet d'ajouter les droits tresorier ou administrateur 
 * 
 *
 */
public class DupliquerEditionSpeEditorPart extends FormPopup
{

	private EditionSpeDTO dto;


	/**
	 * 
	 */
	public DupliquerEditionSpeEditorPart(EditionSpeDTO dto)
	{
		this.dto = dto;
		setWidth(80);
		popupTitle = "Dupliquer une édition spécifique";
		
		item = new BeanItem<EditionSpeDTO>(this.dto);

	}
	
	@Override
	protected void addFields()
	{
		IValidator uniq = new UniqueInDatabaseValidator(EditionSpecifique.class,"nom",null);
		IValidator len_1_100 = new StringLengthValidator(1, 100);
		
		// Champ 1
		addTextField("Dupliquer cette édition sous le nom suivant", "nom",len_1_100,uniq);
		
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		new EditionSpeService().dupliquer(dto);
	}
}
