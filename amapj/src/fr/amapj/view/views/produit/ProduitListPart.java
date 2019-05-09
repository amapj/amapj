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

import java.util.List;

import fr.amapj.service.services.edgenerator.excel.EGListeProduitProducteur;
import fr.amapj.service.services.edgenerator.excel.EGListeProduitProducteur.Type;
import fr.amapj.service.services.produit.ProduitDTO;
import fr.amapj.service.services.produit.ProduitService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.views.producteur.ProducteurSelectorPart;


/**
 * Gestion des utilisateurs
 *
 */
@SuppressWarnings("serial")
public class ProduitListPart extends StandardListPart<ProduitDTO> implements PopupSuppressionListener
{

	private ProducteurSelectorPart producteurSelector;
	
	public ProduitListPart()
	{
		super(ProduitDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des produits";
	}
	
	@Override
	protected void addSelectorComponent()
	{
		producteurSelector = new ProducteurSelectorPart(this);
		addComponent(producteurSelector.getChoixProducteurComponent());
	}


	@Override
	protected void drawButton() 
	{
		addButton("Ajouter un produit", ButtonType.ALWAYS, ()->handleAjouter());
		addButton("Modifier", ButtonType.EDIT_MODE, ()->handleEditer());
		addButton("Supprimer", ButtonType.EDIT_MODE, ()->handleSupprimer());

		addSearchField("Rechercher par nom ou conditionnement");
	}

	@Override
	protected void addExtraComponent() 
	{
		addComponent(LinkCreator.createLink(new EGListeProduitProducteur(Type.STD)));
		
	}

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nom", "conditionnement"});
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("conditionnement","Conditionnement");
	}



	@Override
	protected List<ProduitDTO> getLines() 
	{
		Long idProducteur = producteurSelector.getProducteurId();
		if (idProducteur==null)
		{
			return null;
		}
		return new ProduitService().getAllProduitDTO(idProducteur);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom" , "conditionnement" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" , "conditionnement" };
	}
	
	

	

	private void handleAjouter()
	{
		Long idProducteur = producteurSelector.getProducteurId();
		ProduitEditorPart.open(new ProduitEditorPart(true,null,idProducteur), this);
	}

	protected void handleEditer()
	{
		ProduitDTO dto = getSelectedLine();
		ProduitEditorPart.open(new ProduitEditorPart(false,dto,null), this);
	}

	protected void handleSupprimer()
	{
		ProduitDTO dto = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer le produit "+dto.nom+" , "+dto.conditionnement+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new ProduitService().deleteProduit(idItemToSuppress);
	}	
	
}
