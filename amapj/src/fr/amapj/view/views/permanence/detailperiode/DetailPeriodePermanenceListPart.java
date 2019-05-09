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
 package fr.amapj.view.views.permanence.detailperiode;

import java.util.List;

import fr.amapj.service.services.edgenerator.excel.permanence.EGBilanInscriptionPermanence;
import fr.amapj.service.services.edgenerator.excel.permanence.EGPlanningPermanence;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.views.permanence.PeriodePermanenceSelectorPart;
import fr.amapj.view.views.permanence.detailperiode.grille.ModifierInscriptionGrillePart;
import fr.amapj.view.views.permanence.periode.grille.VisualiserPeriodePermanenceGrillePart;


/**
 * Gestion des inscriptions aux permanences
 *
 */
@SuppressWarnings("serial")
public class DetailPeriodePermanenceListPart extends StandardListPart<PeriodePermanenceDateDTO> 
{
	private PeriodePermanenceSelectorPart periodeSelector;
	
	public DetailPeriodePermanenceListPart()
	{
		super(PeriodePermanenceDateDTO.class,false);
		periodeSelector = new PeriodePermanenceSelectorPart(this);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Gestion des inscriptions sur une période de permanence";
	}
	
	@Override
	protected void addSelectorComponent()
	{
		addComponent(periodeSelector.getChoixPeriodeComponent());	
	}
	

	@Override
	protected void drawButton() 
	{
	
		addButton("Visualiser grille",ButtonType.ALWAYS,()->handleVisualiserGrille());
		addButton("Modifier grille",ButtonType.ALWAYS,()->handleModifierGrille());
		addButton("Modifier cette date",ButtonType.EDIT_MODE,()->handleModifierInscrit());
		addButton("Autre ...",ButtonType.ALWAYS,()->handleAutre());
		addButton("Télécharger",ButtonType.ALWAYS,()->handleTelecharger());
		
		/*
		addButton("Effacer les inscriptions",ButtonType.ALWAYS,()->handleEffacer());
		addButton("Calcul automatique du planning",ButtonType.ALWAYS,()->handleCalculAuto());
		addButton("Envoyer ...",ButtonType.ALWAYS,()->handleEnvoi());*/
		
		addSearchField("Rechercher par nom");
		
	}



	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "datePerm", "nbPlace","nbInscrit" ,"complet" , "nomInscrit" } );
				
		
		cdesTable.setColumnHeader("datePerm","Date permanence ");
		cdesTable.setColumnHeader("nbPersonneSouhaite","Nb souhaité");
		cdesTable.setColumnHeader("nbPersonneInscrit","Nb inscrits");
		cdesTable.setColumnHeader("complet","Complet");
		
		cdesTable.setColumnHeader("nomInscrit","Noms des inscrits");
		
		
		cdesTable.setConverter("datePerm", new DateToStringConverter());
		
		cdesTable.setColumnWidth("datePerm", 100);
		cdesTable.setColumnWidth("nbPersonneSouhaite", 140);
		cdesTable.setColumnWidth("nbPersonneInscrit", 140);
		cdesTable.setColumnWidth("complet", 80);
		
	}



	@Override
	protected List<PeriodePermanenceDateDTO> getLines() 
	{
		Long idPeriodePermanence = periodeSelector.getPeriodePermanenceId();
		if (idPeriodePermanence==null)
		{
			return null;
		}
		return new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence).datePerms;
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "datePerm"};
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "datePerm", "nomInscrit" };
	}

	
	
	private void handleVisualiserGrille()
	{
		Long idPeriodePermanence = periodeSelector.getPeriodePermanenceId();
		VisualiserPeriodePermanenceGrillePart.open(new VisualiserPeriodePermanenceGrillePart(idPeriodePermanence,null), this);
	}
	
	protected void handleModifierGrille()
	{
		Long idPeriodePermanence = periodeSelector.getPeriodePermanenceId();
		ModifierInscriptionGrillePart.open(new ModifierInscriptionGrillePart(idPeriodePermanence), this);
	}
	
	private void handleModifierInscrit()
	{
		PeriodePermanenceDateDTO dto = getSelectedLine();
		PopupModifPermanence modifPermanence = new PopupModifPermanence(dto.idPeriodePermanenceDate);
		PopupModifPermanence.open(modifPermanence, this);	
	}
	
	
	private void handleAutre()
	{
		Long idPeriodePermanence = periodeSelector.getPeriodePermanenceId();
		ChoixAutreAction.open(new ChoixAutreAction(idPeriodePermanence), this);
	}

	
	private void handleTelecharger()
	{
		Long idPeriodePermanence = periodeSelector.getPeriodePermanenceId();
		
		TelechargerPopup popup = new TelechargerPopup("Période de permanence",80);
		popup.addGenerator(new EGPlanningPermanence(idPeriodePermanence,null));
		popup.addGenerator(new EGBilanInscriptionPermanence(idPeriodePermanence));
		CorePopup.open(popup,this);
		
	}

	

	
}
