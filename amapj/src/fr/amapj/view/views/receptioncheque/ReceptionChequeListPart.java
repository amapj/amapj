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
 package fr.amapj.view.views.receptioncheque;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;

import fr.amapj.service.services.gestioncontratsigne.ContratSigneDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.common.contratselector.ContratSelectorPart;
import fr.amapj.view.views.common.contrattelecharger.TelechargerContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Réception des chéques
 */
@SuppressWarnings("serial")
public class ReceptionChequeListPart extends StandardListPart<ContratSigneDTO> 
{
	private ContratSelectorPart contratSelectorPart;

	public ReceptionChequeListPart()
	{
		super(ContratSigneDTO.class,false);
	}
	
	@Override
	protected String getTitle() 
	{
		return "Réception des chèques";
	}


	@Override
	protected void drawButton() 
	{		
		addButton("Visualiser",ButtonType.EDIT_MODE,()->handleVoir());
		addButton("Réceptionner les chèques",ButtonType.EDIT_MODE,()->handleReceptionCheque());
		addButton("Modifier les chèques",ButtonType.EDIT_MODE,()->handleModifierCheque());
		addButton("Saisir un avoir",ButtonType.EDIT_MODE,()->handleSaisirAvoir());
		addButton("Autre...",ButtonType.ALWAYS,()->handleMore());
		addButton("Télécharger ...",ButtonType.ALWAYS,()->handleTelecharger());
		
		addSearchField("Rechercher par nom ou prénom");
		
		
	}

	@Override
	protected void addSelectorComponent()
	{
		// Partie choix du contrat
		contratSelectorPart = new ContratSelectorPart(this);
		HorizontalLayout toolbar1 = contratSelectorPart.getChoixContratComponent();
		
		addComponent(toolbar1);
		
		contratSelectorPart.fillAutomaticValues();
	}
	

	@Override
	protected void drawTable() 
	{
		cdesTable.setVisibleColumns(new Object[] { "nomUtilisateur", "prenomUtilisateur", "mntCommande" ,"nbChequePromis" , "nbChequeRecus" , "nbChequeRemis" , "mntSolde" , "mntAvoirInitial"});
		
		cdesTable.setColumnHeader("nomUtilisateur","Nom");
		cdesTable.setColumnHeader("prenomUtilisateur","Prénom");
		
		cdesTable.setColumnHeader("mntAvoirInitial","Avoir initial(en €)");
		cdesTable.setColumnAlignment("mntAvoirInitial",Align.RIGHT);
		cdesTable.setColumnHeader("mntCommande","Commandé(en €)");
		cdesTable.setColumnAlignment("mntCommande",Align.RIGHT);
		cdesTable.setColumnHeader("mntSolde","Solde final(en €)");
		cdesTable.setColumnAlignment("mntSolde",Align.RIGHT);

		
		cdesTable.setColumnHeader("nbChequePromis","Chèques promis");
		cdesTable.setColumnAlignment("nbChequePromis",Align.CENTER);
		cdesTable.setColumnHeader("nbChequeRecus","Chèques reçus");
		cdesTable.setColumnAlignment("nbChequeRecus",Align.CENTER);
		cdesTable.setColumnHeader("nbChequeRemis","Chèques remis");
		cdesTable.setColumnAlignment("nbChequeRemis",Align.CENTER);
		
		
		//
		cdesTable.setConverter("mntAvoirInitial", new CurrencyTextFieldConverter());
		cdesTable.setConverter("mntCommande", new CurrencyTextFieldConverter());
		cdesTable.setConverter("mntSolde", new CurrencyTextFieldConverter());
	}



	@Override
	protected List<ContratSigneDTO> getLines() 
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		if (idModeleContrat==null)
		{
			return null;
		}
		return new GestionContratSigneService().getAllContratSigne(idModeleContrat);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nomUtilisateur" , "prenomUtilisateur" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nomUtilisateur" , "prenomUtilisateur" };
	}
	
	private void handleMore()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		ChoixActionReceptionCheque.open(new ChoixActionReceptionCheque(idModeleContrat), this);
	}

	private void handleTelecharger()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		ContratSigneDTO contratSigneDTO = getSelectedLine();
		Long idContrat = null;
		if (contratSigneDTO!=null)
		{
			idContrat = contratSigneDTO.idContrat;
		}
		TelechargerContrat.displayPopupTelechargerContrat(idModeleContrat, idContrat, this);
	}


	private void handleSaisirAvoir()
	{
		ContratSigneDTO c = getSelectedLine();
		CorePopup.open(new PopupSaisieAvoir(c),this);
	}


	private void handleModifierCheque()
	{
		ContratSigneDTO c = getSelectedLine();
		
		String message = "Modification des chèques de "+c.prenomUtilisateur+" "+c.nomUtilisateur;
	
		SaisieContrat.saisieContrat(c.idModeleContrat,c.idContrat,c.idUtilisateur,message,ModeSaisie.CHEQUE_SEUL,this);
		
	}


	private void handleReceptionCheque()
	{
		ContratSigneDTO dto = getSelectedLine();
		CorePopup.open(new ReceptionChequeEditorPart(dto.idContrat,dto.nomUtilisateur,dto.prenomUtilisateur),this);
	}


	private void handleVoir()
	{
		ContratSigneDTO c = getSelectedLine();
		
		String message = "Visualisation du contrat de "+c.prenomUtilisateur+" "+c.nomUtilisateur;
	
		SaisieContrat.saisieContrat(c.idModeleContrat,c.idContrat,c.idUtilisateur,message,ModeSaisie.READ_ONLY,this);
		
	}
	
}
