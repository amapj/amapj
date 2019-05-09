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
 package fr.amapj.view.views.cotisation.bilan;

import java.util.List;

import com.vaadin.ui.Table.Align;

import fr.amapj.service.services.edgenerator.excel.EGBilanAdhesion;
import fr.amapj.service.services.edgenerator.pdf.PGBulletinAdhesion;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationDTO;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * Affichage de la liste des périodes de cotisation 
 *
 */
public class BilanCotisationView extends StandardListPart<PeriodeCotisationDTO> implements PopupSuppressionListener
{

	public BilanCotisationView()
	{
		super(PeriodeCotisationDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des périodes de cotisation";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Créer une période",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Modifier une période",ButtonType.EDIT_MODE,()->handleUpdate());
		addButton("Supprimer une période",ButtonType.EDIT_MODE,()->handleSupprimer());
		addButton("Télécharger ...",ButtonType.EDIT_MODE,()->handleTelecharger());

		addSearchField("Rechercher par nom");
	}


	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "nom", "nbAdhesion","mntTotalAdhesion" ,"nbPaiementDonnes" ,"nbPaiementARecuperer"});
		
		cdesTable.setColumnHeader("nom","Nom de la période");
		cdesTable.setColumnHeader("nbAdhesion","Nombre d'adhérents");
		cdesTable.setColumnAlignment("nbAdhesion",Align.RIGHT);
		cdesTable.setColumnHeader("mntTotalAdhesion","Montant total des adhésions (en €)");
		cdesTable.setColumnAlignment("mntTotalAdhesion",Align.RIGHT);
		cdesTable.setColumnHeader("nbPaiementDonnes","Nb de paiements réceptionnés");
		cdesTable.setColumnAlignment("nbPaiementDonnes",Align.RIGHT);
		cdesTable.setColumnHeader("nbPaiementARecuperer","Nb de paiements à récupérer");
		cdesTable.setColumnAlignment("nbPaiementARecuperer",Align.RIGHT);
		
		cdesTable.setConverter("mntTotalAdhesion", new CurrencyTextFieldConverter());
	}



	@Override
	protected List<PeriodeCotisationDTO> getLines() 
	{
		return new GestionCotisationService().getAll();
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
		PeriodeCotisationEditorPart.open(new PeriodeCotisationEditorPart(true,null), this);
	}
	
	private void handleUpdate()
	{
		PeriodeCotisationDTO dto = getSelectedLine();
		PeriodeCotisationEditorPart.open(new PeriodeCotisationEditorPart(false,dto), this);
	}	
	

	private void handleSupprimer()
	{
		PeriodeCotisationDTO dto = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer la période de cotisation "+dto.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}

	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new GestionCotisationService().delete(idItemToSuppress);
	}

	
	private void handleTelecharger()
	{
		PeriodeCotisationDTO dto = getSelectedLine();
		TelechargerPopup popup = new TelechargerPopup("Bilan des cotisations");
		popup.addGenerator(new EGBilanAdhesion(dto.id));
		popup.addGenerator(new PGBulletinAdhesion(dto.id, null, null));
		CorePopup.open(popup,this);
	}
}
