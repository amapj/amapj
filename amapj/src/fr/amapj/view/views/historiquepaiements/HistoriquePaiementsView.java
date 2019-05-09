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
 package fr.amapj.view.views.historiquepaiements;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.service.services.edgenerator.excel.EGListeAdherent;
import fr.amapj.service.services.edgenerator.excel.EGListeAdherent.Type;
import fr.amapj.service.services.mespaiements.MesPaiementsService;
import fr.amapj.service.services.mespaiements.PaiementHistoriqueDTO;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.template.ListPartView;
import fr.amapj.view.engine.tools.DateAsMonthToStringConverter;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * Page permettant de presenter la liste des paiements passés
 * 
 *  
 *
 */
public class HistoriquePaiementsView extends StandardListPart<PaiementHistoriqueDTO>
{
	public HistoriquePaiementsView()
	{
		super(PaiementHistoriqueDTO.class,false);
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des paiements passés";
	}


	@Override
	protected void drawButton() 
	{
		addSearchField("Rechercher par le nom du contrat ou du producteur");
	}


	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		cdesTable.setVisibleColumns("nomProducteur", "nomContrat" , "datePrevu" , "dateReelle" , "montant");
		
		cdesTable.setColumnHeader("nomProducteur","Producteur");
		cdesTable.setColumnHeader("nomContrat","Contrat");
		cdesTable.setColumnHeader("datePrevu","Mois de paiement prévu");
		cdesTable.setColumnHeader("dateReelle","Date réelle de remise");
		cdesTable.setColumnHeader("montant","Montant (en €)");
		cdesTable.setColumnAlignment("montant",Align.RIGHT);
		
		cdesTable.setConverter("datePrevu", new DateAsMonthToStringConverter());
		cdesTable.setConverter("dateReelle", new DateToStringConverter());
		cdesTable.setConverter("montant", new CurrencyTextFieldConverter());
		
	}



	@Override
	protected List<PaiementHistoriqueDTO> getLines() 
	{
		return new MesPaiementsService().getMesPaiements(SessionManager.getUserId()).paiementHistorique;
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "datePrevu" , "nomContrat" };
	}
	
	@Override
	protected boolean[] getSortAsc()
	{
		return new boolean[] { false, true };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nomProducteur" , "nomContrat" };
	}
}
