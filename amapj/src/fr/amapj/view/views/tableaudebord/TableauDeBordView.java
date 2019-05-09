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
 package fr.amapj.view.views.tableaudebord;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.service.services.edgenerator.excel.stats.EGStatAnnuelleProducteur;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;


/**
 * Page permettant d'afficher le tableau de bord 
 *  
 *
 */
public class TableauDeBordView extends VerticalLayout implements View, PopupListener
{


	/**
	 * 
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		
		addLabel(this, "Tableau de bord");
		
		Label tf = new Label("<br/>",ContentMode.HTML);
		addComponent(tf);
		
		
		addButton(this, "Telecharger les statistiques",e->	handleStat());	
		
		this.setMargin(true);
		
	}



	

	
	private void handleStat()
	{
		TelechargerPopup popup = new TelechargerPopup("Tableau de bord");
		popup.addGenerator(new EGStatAnnuelleProducteur());
		CorePopup.open(popup,this);
	}
	

	
	
	private Label addLabel(VerticalLayout layout, String str)
	{
		Label tf = new Label(str);
		tf.addStyleName("h1");	
		layout.addComponent(tf);
		return tf;
		
	}

	
	
	private void addButton(VerticalLayout layout, String str,ClickListener listener)
	{
		Button b = new Button(str);
		b.addStyleName(ChameleonTheme.BUTTON_BIG);
		b.addClickListener(listener);
		layout.addComponent(b);
		
	}

	@Override
	public void onPopupClose()
	{

	}



}
