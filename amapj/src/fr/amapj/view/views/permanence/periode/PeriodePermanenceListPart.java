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
 package fr.amapj.view.views.permanence.periode;

import java.util.List;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.SmallPeriodePermanenceDTO;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.views.gestioncontratsignes.ChoixActionContratSigne;
import fr.amapj.view.views.permanence.grille.AbstractPeriodePermanenceGrillePart;
import fr.amapj.view.views.permanence.periode.grille.ModifierPeriodePermanenceGrillePart;
import fr.amapj.view.views.permanence.periode.grille.VisualiserPeriodePermanenceGrillePart;
import fr.amapj.view.views.permanence.periode.role.PeriodePermanenceGestionRolePart;
import fr.amapj.view.views.permanence.periode.update.ChoixActionModificationPeriodePermanence;


/**
 * Listes des periodes de permanences
 *
 */
@SuppressWarnings("serial")
public class PeriodePermanenceListPart extends StandardListPart<SmallPeriodePermanenceDTO> implements PopupSuppressionListener 
{
	

	public PeriodePermanenceListPart()
	{
		super(SmallPeriodePermanenceDTO.class,false);
		
	}
	
	@Override
	protected String getTitle() 
	{
		return "Liste des périodes de permanences";
	}


	@Override
	protected void drawButton() 
	{		
		addButton("Créer une nouvelle période",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Visualiser grille",ButtonType.EDIT_MODE,()->handleVisualiserGrille());
		addButton("Visualiser détail",ButtonType.EDIT_MODE,()->handleVisualiserDetail());
		addButton("Modifier grille",ButtonType.EDIT_MODE,()->handleModifierGrille());
		addButton("Modifier ...",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Changer l'état",ButtonType.EDIT_MODE,()->handleEtat());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());
		
		addSearchField("Rechercher par nom");
		
	}

	
	

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "etat" , "nom", "dateDebut" , "dateFin" ,"nbDatePerm" , "pourcentageInscription"});
		cdesTable.setColumnHeader("etat","Etat");
		cdesTable.setColumnHeader("nom","Nom de la période");
		cdesTable.setColumnHeader("dateDebut","Date de début");
		cdesTable.setColumnHeader("dateFin","Date de fin");
		cdesTable.setColumnHeader("nbDatePerm","Nombre de dates");
		cdesTable.setColumnHeader("pourcentageInscription","% Inscription");
		
		
		
		cdesTable.setConverter("dateDebut", new DateToStringConverter());
		cdesTable.setConverter("dateFin", new DateToStringConverter());
		
	}



	@Override
	protected List<SmallPeriodePermanenceDTO> getLines() 
	{
		return new PeriodePermanenceService().getAllPeriodePermanence();
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
		PeriodePermanenceCreationEditorPart.open(new PeriodePermanenceCreationEditorPart(), this);
	}

	private void handleVisualiserGrille()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		VisualiserPeriodePermanenceGrillePart.open(new VisualiserPeriodePermanenceGrillePart(dto.id,null), this);
	}

	
	
	private void handleVisualiserDetail()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		PeriodePermanenceVisualiserPart.open(new PeriodePermanenceVisualiserPart(dto.id), this);
	}
	
	protected void handleModifierGrille()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		ModifierPeriodePermanenceGrillePart.open(new ModifierPeriodePermanenceGrillePart(dto.id), this);
	}
	
	protected void handleEditer()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		ChoixActionModificationPeriodePermanence.open(new ChoixActionModificationPeriodePermanence(dto.id), this);
	}
	
	private void handleEtat()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		PeriodePermanenceModifEtat.open(new PeriodePermanenceModifEtat(dto), this);
	}


	protected void handleSupprimer()
	{
		SmallPeriodePermanenceDTO dto = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer la période de permanence "+dto.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new PeriodePermanenceService().deletePeriodePermanence(idItemToSuppress);
	}
	
}
