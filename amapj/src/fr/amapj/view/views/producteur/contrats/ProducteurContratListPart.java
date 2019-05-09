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
 package fr.amapj.view.views.producteur.contrats;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGFeuilleDistributionProducteur;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGSyntheseContrat;
import fr.amapj.service.services.edgenerator.excel.producteur.EGPaiementProducteur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.producteur.ProducteurService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.views.producteur.ProducteurSelectorPart;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Affichage des contrats pour un producteur 
 *
 */
public class ProducteurContratListPart extends StandardListPart<ModeleContratSummaryDTO>
{
	private ProducteurSelectorPart producteurSelector;
	
	public ProducteurContratListPart()
	{
		super(ModeleContratSummaryDTO.class,false);
		
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des contrats d'un producteur";
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
		addButton("Tester",ButtonType.EDIT_MODE,()->handleTester());
		addButton("Télécharger ...",ButtonType.EDIT_MODE,()->handleTelecharger());

		addSearchField("Rechercher par nom");
	}
	
	


	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "etat", "nom", "finInscription","dateDebut" , "dateFin" , "nbLivraison" ,"nbProduit"});
		cdesTable.setColumnHeader("etat","Etat");
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("nomProducteur","Producteur");
		cdesTable.setColumnHeader("finInscription","Fin inscription");
		cdesTable.setColumnHeader("dateDebut","Première livraison");
		cdesTable.setColumnHeader("dateFin","Dernière livraison");
		cdesTable.setColumnHeader("nbLivraison","Nb de livraisons");
		cdesTable.setColumnHeader("nbProduit","Nb de produits");
		
		
		//
		cdesTable.setConverter("finInscription", new DateToStringConverter());
		cdesTable.setConverter("dateDebut", new DateToStringConverter());
		cdesTable.setConverter("dateFin", new DateToStringConverter());
	}



	@Override
	protected List<ModeleContratSummaryDTO> getLines() 
	{
		Long idProducteur = producteurSelector.getProducteurId();
		// Si le producteur n'est pas défini : la table est vide et les boutons desactivés
		if (idProducteur==null)
		{
			return null;
		}
		
		return new ProducteurService().getModeleContratInfo(idProducteur);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "etat" , "dateDebut"  };
	}
	
	protected boolean[] getSortAsc()
	{
		return new boolean[] { true , false  };
	}
	
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom"  };
	}
		

	private void handleTelecharger()
	{
		ModeleContratSummaryDTO mcDto = getSelectedLine();
		
		TelechargerPopup popup = new TelechargerPopup("Producteur");
		popup.addGenerator(new EGFeuilleDistributionProducteur(mcDto.id));
		popup.addGenerator(new EGPaiementProducteur(mcDto.id));
		popup.addGenerator(new EGSyntheseContrat(mcDto.id));
				
		CorePopup.open(popup,this);
	}


	private void handleTester()
	{
		ModeleContratSummaryDTO mcDto = getSelectedLine();
		SaisieContrat.saisieContrat(mcDto.id,null,null,"Mode Test",ModeSaisie.FOR_TEST,this);
		
	}
}
