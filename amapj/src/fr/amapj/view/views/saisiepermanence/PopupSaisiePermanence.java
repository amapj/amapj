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

import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;
import fr.amapj.service.services.saisiepermanence.PermanenceUtilisateurDTO;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet à un utilisateur de mettre à jour ses coordonnées
 * 
 *
 */
@SuppressWarnings("serial")
public class PopupSaisiePermanence extends FormPopup
{

	private PermanenceDTO dto;
	private boolean create;



	/**
	 * 
	 */
	public PopupSaisiePermanence(PermanenceDTO dto)
	{
		setWidth(80);
		this.create = (dto==null);
		
		if (create)
		{
			popupTitle = "Création d'une permanence";
			this.dto = new PermanenceDTO();
		}
		else
		{
			popupTitle = "Modification d'une permanence";
			this.dto = dto;
		}

		
		item = new BeanItem<PermanenceDTO>(this.dto);

	}
	
	@Override
	protected void addFields()
	{
		
		// Champ 1
		addDateField("Date de la permanence", "datePermanence");
		
		//
		CollectionEditor<PermanenceUtilisateurDTO> f1 = new CollectionEditor<PermanenceUtilisateurDTO>("Liste des participants", (BeanItem) item, "permanenceUtilisateurs", PermanenceUtilisateurDTO.class);
		f1.addSearcherColumn("idUtilisateur", "Nom des participants",FieldType.SEARCHER, null,SearcherList.UTILISATEUR_ACTIF,null);
		f1.addColumn("numSession", "Numéro de la session (optionnel)",FieldType.INTEGER, new Integer(0));
		binder.bind(f1, "permanenceUtilisateurs");
		form.addComponent(f1);
		
	}

	

	@Override
	protected void performSauvegarder()
	{
		new PermanenceService().updateorCreateDistribution(dto, create);
	}

	
}
