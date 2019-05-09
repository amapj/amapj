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
 package fr.amapj.view.views.producteur.basicform;

import java.util.List;

import fr.amapj.service.services.producteur.ProducteurDTO;
import fr.amapj.service.services.producteur.ProducteurService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;


/**
 * Gestion des producteurs
 *
 */
@SuppressWarnings("serial")
public class ProducteurListPart extends StandardListPart<ProducteurDTO> implements PopupSuppressionListener
{

	public ProducteurListPart()
	{
		super(ProducteurDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des producteurs";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Créer un nouveau producteur",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Modifier",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());
		
		addSearchField("Rechercher par nom");
	}


	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nom", "utilisateurInfo" ,"referentInfo" });
		
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("utilisateurInfo","Producteurs");
		cdesTable.setColumnHeader("referentInfo","Referents");
		
	}



	@Override
	protected List<ProducteurDTO> getLines() 
	{
		return new ProducteurService().getAllProducteurs();
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" };
	}
	

	private void handleAjouter()
	{
		ProducteurEditorPart.open(new ProducteurEditorPart(true,null), this);
	}


	private void handleEditer()
	{
		ProducteurDTO dto = getSelectedLine();
		ProducteurEditorPart.open(new ProducteurEditorPart(false,dto), this);
	}

	private void handleSupprimer()
	{
		ProducteurDTO dto = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer le producteur "+dto.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new ProducteurService().delete(idItemToSuppress);
	}	
}
