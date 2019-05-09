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
 package fr.amapj.view.views.produit;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.produit.ProduitDTO;
import fr.amapj.service.services.produit.ProduitService;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.StringLengthValidator;

/**
 * Permet uniquement de creer des contrats
 * 
 *
 */
public class ProduitEditorPart extends FormPopup
{

	private ProduitDTO producteurDTO;

	private boolean create;

	/**
	 * 
	 */
	public ProduitEditorPart(boolean create,ProduitDTO p,Long idProducteur)
	{
		this.create = create;
		
		setWidth(80);
		
		if (create)
		{
			popupTitle = "Création d'un produit";
			this.producteurDTO = new ProduitDTO();
			this.producteurDTO.producteurId = idProducteur;
		}
		else
		{
			popupTitle = "Modification d'un produit";
			this.producteurDTO = p;
		}	
		
	
		
		item = new BeanItem<ProduitDTO>(this.producteurDTO);

	}
	
	@Override
	protected void addFields()
	{
		IValidator len_1_255 = new StringLengthValidator(1, 255);
		IValidator len_1_500 = new StringLengthValidator(1, 255);
		
		// Champ 1
		addTextField("Nom", "nom",len_1_255);
		
		// Champ 2
		addTextField("Conditionnement", "conditionnement",len_1_500);
		
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		new ProduitService().update(producteurDTO, create);
	}
}
