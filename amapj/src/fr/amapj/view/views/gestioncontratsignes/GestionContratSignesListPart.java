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
 package fr.amapj.view.views.gestioncontratsignes;

import java.util.List;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;

import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.dbservice.DbService;
import fr.amapj.service.services.gestioncontratsigne.ContratSigneDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.cascadingpopup.CInfo;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingData;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingPopup;
import fr.amapj.view.engine.popup.cascadingpopup.sample.APopup;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.common.contratselector.ContratSelectorPart;
import fr.amapj.view.views.common.contrattelecharger.TelechargerContrat;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.ModifierEnMasseContratSigne;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Gestion des contrats signes
 *
 * 
 *
 */
@SuppressWarnings("serial")
public class GestionContratSignesListPart extends StandardListPart<ContratSigneDTO> implements PopupSuppressionListener 
{
	
	private ContratSelectorPart contratSelectorPart;

	public GestionContratSignesListPart()
	{
		super(ContratSigneDTO.class,false);
	}
	
	@Override
	protected String getTitle() 
	{
		return "Liste des contrats signés";
	}


	@Override
	protected void drawButton() 
	{		
		addButton("Ajouter un contrat signé",ButtonType.ALWAYS,()->	handleAjouter());
		addButton("Visualiser",ButtonType.EDIT_MODE,()->handleVoir());
		addButton("Modifier les quantités",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());
		addButton("Modifier en masse ...",ButtonType.ALWAYS,()->handleModifMasse());
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
		cdesTable.setVisibleColumns(new Object[] { "nomUtilisateur", "prenomUtilisateur", "dateCreation" , "dateModification" , "mntCommande" });
		
		cdesTable.setColumnHeader("nomUtilisateur","Nom");
		cdesTable.setColumnHeader("prenomUtilisateur","Prénom");
		cdesTable.setColumnHeader("dateCreation","Date création");
		cdesTable.setColumnHeader("dateModification","Date modification");
		
		cdesTable.setColumnHeader("mntCommande","Commandé(en €)");
		cdesTable.setColumnAlignment("mntCommande",Align.RIGHT);		
		
		//
		cdesTable.setConverter("dateCreation", new DateTimeToStringConverter());
		cdesTable.setConverter("dateModification", new DateTimeToStringConverter());
		cdesTable.setConverter("mntCommande", new CurrencyTextFieldConverter());
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
	
	
	private void handleModifMasse()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		ModifierEnMasseContratSigne.open(new ModifierEnMasseContratSigne(idModeleContrat), this);
	}

	
	private void handleMore()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		ChoixActionContratSigne.open(new ChoixActionContratSigne(idModeleContrat), this);
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



	private void handleVoir()
	{
		ContratSigneDTO c = getSelectedLine();
		
		String message = "Visualisation du contrat de "+c.prenomUtilisateur+" "+c.nomUtilisateur;
	
		
		SaisieContrat.saisieContrat(c.idModeleContrat,c.idContrat,c.idUtilisateur,message,ModeSaisie.READ_ONLY,this);
		
	}


	private void handleEditer()
	{
		ContratSigneDTO c = getSelectedLine();
		
		String message = "Contrat de "+c.prenomUtilisateur+" "+c.nomUtilisateur;
		
		SaisieContrat.saisieContrat(c.idModeleContrat,c.idContrat,c.idUtilisateur,message,ModeSaisie.QTE_SEUL,this);
	}

	private void handleSupprimer()
	{
		ContratSigneDTO contratSigneDTO = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer le contrat de "+contratSigneDTO.prenomUtilisateur+" "+contratSigneDTO.nomUtilisateur+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,contratSigneDTO.idContrat);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new MesContratsService().deleteContrat(idItemToSuppress);
	}


	private void handleAjouter()
	{
		AjouterData data= new AjouterData();
		data.idModeleContrat = contratSelectorPart.getModeleContratId();
		
		CascadingPopup cascading = new CascadingPopup(this,data);
		
		CInfo info = new CInfo();
		info.popup = new PopupSaisieUtilisateur(data);
		info.onSuccess = ()->successSaisieUtilisateur(data);
		
		cascading.start(info);
	}
		
	private CInfo successSaisieUtilisateur(AjouterData data)
	{
		Long userId = data.userId;
		String message = "Contrat de "+new UtilisateurService().prettyString(userId);
					
		SaisieContrat.saisieContrat(data.idModeleContrat,null,userId,message,ModeSaisie.QTE_CHEQUE_REFERENT,this);
		
		return null;
		
	}

	public class AjouterData extends CascadingData
	{
		Long idModeleContrat;
		Long userId;
	}
	
}
