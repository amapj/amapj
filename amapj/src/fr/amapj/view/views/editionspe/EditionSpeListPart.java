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

import java.util.List;

import fr.amapj.service.services.editionspe.EditionSpeDTO;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;


/**
 * Gestion des étiquettes
 *
 */
public class EditionSpeListPart extends StandardListPart<EditionSpeDTO> implements PopupSuppressionListener
{

	public EditionSpeListPart()
	{
		super(EditionSpeDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des éditions spécifiques";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Créer une nouvelle édition spécifique",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Modifier",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Dupliquer",ButtonType.EDIT_MODE,()->handleDupliquer());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());

		addSearchField("Rechercher par nom ");
	}

	

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nom" , "typEditionSpecifique"});
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("typEditionSpecifique","Type de l'édition");
	}



	@Override
	protected List<EditionSpeDTO> getLines() 
	{
		return new EditionSpeService().getAllEtiquettes();
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom"  };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" };
	}
	
	

	private void handleAjouter()
	{
		ChoixEditionSpecifiqueEditorPart.open(new ChoixEditionSpecifiqueEditorPart(), this);
	}

	

	protected void handleEditer()
	{
		EditionSpeDTO dto = getSelectedLine();
		ChoixEditionSpecifiqueEditorPart.openEditorPart(dto, this);
	}
	
	protected void handleDupliquer()
	{
		EditionSpeDTO dto = getSelectedLine();
		DupliquerEditionSpeEditorPart.open(new DupliquerEditionSpeEditorPart(dto),this);
	}

	protected void handleSupprimer()
	{
		EditionSpeDTO dto = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer l'édition spécifique "+dto.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new EditionSpeService().delete(idItemToSuppress);
	}

}
