/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.views.common.dateviewer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.tools.BaseUiTools;


/**
 * Permet de créer un bloc permettant la gestion d'un calendrier semaine / semaine
 *
 */
public class DateViewer 
{
	
	
	
	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	private SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
	
	private Label titre;
		
	private Date date;
	
	private PopupListener listener;

	
	
	public DateViewer(PopupListener listener)
	{
		date = DateUtils.getDate();
		this.listener = listener;
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public VerticalLayout getComponent()
	{
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(false);
		vl.setSpacing(false);
		vl.addStyleName("date-viewer");
		
		
		// Bandeau avec le titre + le bouton 
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidth("100%");
		
		
		titre = new Label();
		titre.setWidth("100%");
		hl1.addComponent(titre);
		
		Button b = new Button("CHANGER DE DATE");
		b.addClickListener(e->avancer());
		b.addStyleName("large");
		b.addStyleName("fleche");
		b.setWidth("200px");
		hl1.addComponent(b);
		hl1.setComponentAlignment(titre, Alignment.MIDDLE_RIGHT);				
			
		vl.addComponent(hl1);
		
		return vl;
		
	}

	
	
	private void avancer()
	{
		date = DateUtils.addDays(date, 1);
		updateTitreValue();
		listener.onPopupClose();
		
	}



	private void reculer()
	{
		date = DateUtils.addDays(date, -1);
		updateTitreValue();
		listener.onPopupClose();
		
	}

	private void updateTitreValue()
	{

		SimpleDateFormat dfx = BaseUiTools.isWidthBelow(480) ? df2 : df1;
		titre.setValue(dfx.format(date));	
	}
	
	
	
		
	

	


	public Date getDate()
	{
		return date;
	}
	

	
	

}
