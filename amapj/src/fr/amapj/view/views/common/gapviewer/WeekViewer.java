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
 package fr.amapj.view.views.common.gapviewer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.tools.BaseUiTools;


/**
 * Permet de créer un bloc permettant la gestion d'un calendrier semaine / semaine
 *
 */
public class WeekViewer implements AbstractGapViewer
{
	
	
	
	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	private SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
	
	private Label titre;
		
	private Date date;
	
	private PopupListener listener;

	
	
	public WeekViewer(PopupListener listener)
	{
		date = DateUtils.getDate();
		date = DateUtils.firstMonday(date);
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
		vl.addStyleName("semaine-viewer");
		
		
		// Bandeau avec les boutons droit / gauche 
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidth("100%");
		
		
		addButton(false,hl1,e->reculer());
		
		Label empty = new Label();
		hl1.addComponent(empty);
		hl1.setExpandRatio(empty, 1.0f);
		
		
		addButton(true,hl1,e->avancer());
			
		vl.addComponent(hl1);
		
		
		// Bandeau avec la date de la semaine visualisée
		titre = new Label();
		hl1.addStyleName("titre");
		titre.setSizeUndefined();
		vl.addComponent(titre);
		vl.setComponentAlignment(titre, Alignment.MIDDLE_CENTER);
		
		updateTitreValue();
	
		return vl;
		
	}

	
	
	private void avancer()
	{
		date = DateUtils.addDays(date, 7);
		updateTitreValue();
		listener.onPopupClose();
		
	}



	private void reculer()
	{
		date = DateUtils.addDays(date, -7);
		updateTitreValue();
		listener.onPopupClose();
		
	}

	
	
	private void addButton(boolean toRight,HorizontalLayout layout, ClickListener listener)
	{
		String str;
		if (BaseUiTools.isWidthBelow(480))
		{
			if (toRight)
			{
				str = "SUIVANT";
			}
			else
			{
				str = "PRECEDENT";
			}
		}
		else
		{
			if (toRight)
			{
				str = "SEMAINE SUIVANTE";
			}
			else
			{
				str = "SEMAINE PRECEDENTE";
			}
		}
		
		
		
		
		Button b = new Button(str);
		b.addClickListener(listener);
	
		
		if (toRight)
		{
			b.setIcon(FontAwesome.ANGLE_DOUBLE_RIGHT);
			b.addStyleName("icon-align-right");
			b.addStyleName("large");
		}
		else
		{
			b.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
			b.addStyleName("large");
		}
		
		b.addStyleName("fleche");
		
		layout.addComponent(b);
		
		
	}


	private void updateTitreValue()
	{
		SimpleDateFormat dfx = BaseUiTools.isWidthBelow(480) ? df2 : df1;
		titre.setValue(dfx.format(getDateDebut())+" - "+dfx.format(getDateFin()));	
	}


	public Date getDateDebut()
	{
		return date;
	}
	

	public Date getDateFin()
	{
		return DateUtils.addDays(date, 6);
	}
	
	@Override
	public TypPeriode getTypPeriode()
	{
		return TypPeriode.SEMAINE;
	}

	

}
