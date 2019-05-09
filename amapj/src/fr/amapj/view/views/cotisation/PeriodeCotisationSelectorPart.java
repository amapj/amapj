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
 package fr.amapj.view.views.cotisation;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;


/**
 * Outil permettant le choix de la periode de cotisation
 * sous la forme d'un bandeau en haut de l'écran
 *  
 *
 */
public class PeriodeCotisationSelectorPart
{

	
	private Searcher periodeCotisationBox;
	
	private Long idPeriodeCotisation;
	
	private Button reinitButton;
	
	
	private PopupListener listener;

	/**
	 * 
	 */
	public PeriodeCotisationSelectorPart(PopupListener listener)
	{
		this.listener = listener;
		
	}


	public HorizontalLayout getChoixPeriodeComponent()
	{
		// Partie choix 
		HorizontalLayout toolbar1 = new HorizontalLayout();	
		toolbar1.addStyleName("periode-selectorpart");
	
		
		Label pLabel = new Label("Période de cotisation");
		pLabel.addStyleName("periode");
		pLabel.setSizeUndefined();
		
		toolbar1.addComponent(pLabel);
		
		constructMultiplePeriode(toolbar1);
		
		toolbar1.setSpacing(true);
		toolbar1.setMargin(false);
		toolbar1.setWidth("100%");
	
		return toolbar1;
	}
	
	
	


	private void constructMultiplePeriode(HorizontalLayout toolbar1)
	{
		periodeCotisationBox = new Searcher(SearcherList.PERIODE_COTISATION,null);
		periodeCotisationBox.setImmediate(true);
		periodeCotisationBox.addValueChangeListener(e->handleChange());
		
		
		reinitButton = new Button("Changer de période");
		reinitButton.addClickListener(e->handleReinit());
	
		
		toolbar1.addComponent(periodeCotisationBox);
		toolbar1.addComponent(reinitButton);
		toolbar1.setExpandRatio(reinitButton, 1);
		toolbar1.setComponentAlignment(reinitButton, Alignment.TOP_RIGHT);
		
	}


	/**
	 * 
	 */
	private void handleChange()
	{
		idPeriodeCotisation = (Long) periodeCotisationBox.getConvertedValue();
		if (idPeriodeCotisation!=null)
		{
			periodeCotisationBox.setEnabled(false);
		}
		listener.onPopupClose();
	}
	
	
	protected void handleReinit()
	{
		periodeCotisationBox.setValue(null);
		periodeCotisationBox.setEnabled(true);
		idPeriodeCotisation = null;
		listener.onPopupClose();
	}
	
	
	public Long getPeriodeId()
	{
		return idPeriodeCotisation;
	}
	

}
