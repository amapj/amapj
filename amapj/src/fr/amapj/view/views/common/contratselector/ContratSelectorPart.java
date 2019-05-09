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
 package fr.amapj.view.views.common.contratselector;

import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.service.services.access.AccessManagementService;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Outil permettant le choix du contrat sous la forme d'un bandeau en haut de l'écran
 * 
 * 
 */
public class ContratSelectorPart
{
	private Searcher producteurBox;
	private ComboBox contratBox;
	private Button reinitButton;

	private Long idModeleContrat;

	private List<Producteur> allowedProducteurs;

	private PopupListener listener;
	
	private boolean onlyOneProducteur;


	/**
	 * 
	 */
	public ContratSelectorPart(PopupListener listener)
	{
		this.listener = listener;
		allowedProducteurs = new AccessManagementService().getAccessLivraisonProducteur(SessionManager.getUserRoles(), SessionManager.getUserId());
		onlyOneProducteur = (allowedProducteurs.size()==1);
	}

	public HorizontalLayout getChoixContratComponent()
	{
		// Partie choix du contrat
		HorizontalLayout toolbar1 = new HorizontalLayout();
		toolbar1.addStyleName("contrat-selectorpart");

		Label pLabel = new Label("Producteur");
		pLabel.addStyleName("combobox");
		pLabel.setSizeUndefined();

		producteurBox = new Searcher(SearcherList.PRODUCTEUR,null,allowedProducteurs);
		producteurBox.setImmediate(true);
		

		
		producteurBox.addValueChangeListener(e->handleProducteurChange());
			
		

		Label cLabel = new Label("Contrat");
		cLabel.addStyleName("combobox");
		cLabel.setSizeUndefined();

		
		contratBox = new ComboBox();
		
		contratBox.setImmediate(true);
		contratBox.setWidth("300px");
		

		contratBox.addValueChangeListener(e->handleContratChange());
			

		reinitButton = new Button("Changer de contrat");
		reinitButton.addClickListener(e->handleReinit());
			

		toolbar1.addComponent(pLabel);
		toolbar1.addComponent(producteurBox);
		toolbar1.addComponent(cLabel);
		toolbar1.addComponent(contratBox);
		if (onlyOneProducteur==false)
		{
			toolbar1.addComponent(reinitButton);
			toolbar1.setExpandRatio(reinitButton, 1);
			toolbar1.setComponentAlignment(reinitButton, Alignment.TOP_RIGHT);
			contratBox.setEnabled(false);
		}
		else
		{
			// Ceci est nécessaire pour conserver un alignement correct  
			Label tf = new Label("");
			toolbar1.addComponent(tf);
			toolbar1.setExpandRatio(tf, 1);
			toolbar1.setComponentAlignment(tf, Alignment.TOP_RIGHT);
		}

		toolbar1.setSpacing(true);
		toolbar1.setWidth("100%");
		
		return toolbar1;
	}
	
	/**
	 * Doit être appelé à la fin de la construction de la page
	 */
	public void fillAutomaticValues()
	{
		// Gestion du cas ou il y a un seul producteur
		if (onlyOneProducteur)
		{
			Producteur prod = allowedProducteurs.get(0);
			producteurBox.setConvertedValue(prod.getId());
			handleProducteurChange();
		}
	}
	
	
	
	

	protected void handleReinit()
	{
		producteurBox.setValue(null);
		producteurBox.setEnabled(true);
		contratBox.setEnabled(false);
		idModeleContrat = null;

		listener.onPopupClose();
	}

	/**
	 * 
	 */
	private void handleProducteurChange()
	{
		idModeleContrat = null;

		//
		Long idProducteur = (Long) producteurBox.getConvertedValue();

		if (idProducteur == null)
		{
			contratBox.removeAllItems();
			contratBox.setEnabled(false);

		} 
		else
		{
			contratBox.setEnabled(true);
			producteurBox.setEnabled(false);
			contratBox.removeAllItems();
			List<ModeleContrat> mcs = new GestionContratSigneService().getModeleContratCreationOrActif(idProducteur);
			for (ModeleContrat mc : mcs)
			{
				contratBox.addItem(mc.getId());
				contratBox.setItemCaption(mc.getId(), mc.getNom());	
			}
			
			// Si il y a un seul contrat : on le selectionne tout de suite
			if (contratBox.getItemIds().size()==1)
			{
				contratBox.select(contratBox.getItemIds().iterator().next());
			}
		}
		listener.onPopupClose();
	}

	private void handleContratChange()
	{
		idModeleContrat = (Long) contratBox.getConvertedValue();
		listener.onPopupClose();
	}

	public Long getModeleContratId()
	{
		return idModeleContrat;
	}

}
