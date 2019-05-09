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
 package fr.amapj.view.views.common.utilisateurselector;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.searcher.SearcherList;


/**
 * Outil permettant le choix de l'utilisateur  
 * sous la forme d'un bandeau en haut de l'écran
 */
public class UtilisateurSelectorPart
{	
	private Searcher utilisateurBox;
	
	private Long idUtilisateur;
	
	private Button reinitButton;	
	
	private PopupListener listener;
	
	private boolean isCompactMode;

	/**
	 * 
	 */
	public UtilisateurSelectorPart(PopupListener listener)
	{
		this.listener = listener;
		isCompactMode = BaseUiTools.isCompactMode();
	}


	public HorizontalLayout getChoixUtilisateurComponent()
	{
		// Partie choix de l'utilisateur
		HorizontalLayout toolbar1 = new HorizontalLayout();	
		toolbar1.addStyleName("utilisateur-selectorpart");
	
		constructMultipleUtilisateur(toolbar1);
		
		toolbar1.setSpacing(true);
		toolbar1.setWidth("100%");
	
		return toolbar1;
	}
	
	
	
	


	private void constructMultipleUtilisateur(HorizontalLayout toolbar1)
	{
		if (isCompactMode==false)
		{
			Label pLabel = new Label("Amapien");
			pLabel.addStyleName("xutilisateurs");
			pLabel.setSizeUndefined();
			toolbar1.addComponent(pLabel);
		}
		
		
		utilisateurBox = new Searcher(SearcherList.UTILISATEUR_ACTIF,null);
		utilisateurBox.setImmediate(true);
		utilisateurBox.addValueChangeListener(e->handleUtilisateurChange());
			
		reinitButton = new Button("Changer d'amapien");
		reinitButton.addClickListener(e->handleReinit());
					
		toolbar1.addComponent(utilisateurBox);
		toolbar1.addComponent(reinitButton);
		toolbar1.setExpandRatio(reinitButton, 1);
		toolbar1.setComponentAlignment(reinitButton, Alignment.TOP_RIGHT);
		
	}


	/**
	 * 
	 */
	private void handleUtilisateurChange()
	{
		idUtilisateur = (Long) utilisateurBox.getConvertedValue();
		if (idUtilisateur!=null)
		{
			utilisateurBox.setEnabled(false);
		}
		listener.onPopupClose();
	}
	
	
	protected void handleReinit()
	{
		utilisateurBox.setValue(null);
		utilisateurBox.setEnabled(true);
		idUtilisateur = null;
		listener.onPopupClose();
	}
	
	
	public Long getUtilisateurId()
	{
		return idUtilisateur;
	}
	

}
