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
 package fr.amapj.view.views.historiquecontrats;

import java.util.List;

import com.vaadin.ui.Table.Align;

import fr.amapj.service.services.edgenerator.excel.EGListeAdherent;
import fr.amapj.service.services.edgenerator.excel.EGListeAdherent.Type;
import fr.amapj.service.services.historiquecontrats.HistoriqueContratDTO;
import fr.amapj.service.services.historiquecontrats.HistoriqueContratsService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Page permettant de presenter l'hsitorique des contrats à un utilisateur 
 * 
 */
public class HistoriqueContratsView extends StandardListPart<HistoriqueContratDTO>
{
	public HistoriqueContratsView()
	{
		super(HistoriqueContratDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste de vos anciens contrats";
	}


	@Override
	protected void drawButton() 
	{
		addButton("Voir le détail",ButtonType.EDIT_MODE,()->handleVoir());
		
		addSearchField("Rechercher par le producteur ou le nom du contrat");
	}

	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("nomProducteur" , "nomContrat" ,"dateDebut" , "dateFin" , "dateCreation" , "dateModification" , "montant");
		
		cdesTable.setColumnHeader("nomProducteur","Producteur");
		cdesTable.setColumnHeader("nomContrat","Contrat");
		cdesTable.setColumnHeader("dateDebut","Première livraison");
		cdesTable.setColumnHeader("dateFin","Dernière livraison");
		cdesTable.setColumnHeader("dateCreation","Date création");
		cdesTable.setColumnHeader("dateModification","Date modification");
		cdesTable.setColumnHeader("montant","Montant (en €)");
		cdesTable.setColumnAlignment("montant",Align.RIGHT);
		
		//
		cdesTable.setConverter("dateCreation", new DateTimeToStringConverter());
		cdesTable.setConverter("dateModification", new DateTimeToStringConverter());
		cdesTable.setConverter("dateDebut", new DateToStringConverter());
		cdesTable.setConverter("dateFin", new DateToStringConverter());
		cdesTable.setConverter("montant", new CurrencyTextFieldConverter());
	}



	@Override
	protected List<HistoriqueContratDTO> getLines() 
	{
		return new HistoriqueContratsService().getHistoriqueContrats(SessionManager.getUserId());
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nomProducteur" , "dateFin"}; 
	}
	
	@Override
	protected boolean[] getSortAsc()
	{
		return new boolean[] { true, false};
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nomProducteur" , "nomContrat" };
	}
	

	private void handleVoir()
	{
		HistoriqueContratDTO c = getSelectedLine();
		SaisieContrat.saisieContrat(c.idModeleContrat,c.idContrat,c.idUtilisateur,null,ModeSaisie.READ_ONLY,null);

	}
}

