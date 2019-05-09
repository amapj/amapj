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
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.common.GenericUtils;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.tools.BaseUiTools;


/**
 * Permet de créer un bloc permettant de naviguer dans une liste de date 
 * 
 * La liste d'entrée doit être triée dans l'ordre croissant 
 *
 */
public class  DatePerDateViewer<T>
{
	
	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	private SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
	
	private Label titre;
		
	private int currentIndex;
	
	private GenericUtils.ToDate<T> toDate;
	
	private PopupListener listener;
	
	private List<T> dates;
	
	private Button previous;
	
	private Button next;

	
	
	public DatePerDateViewer(List<T> dates,GenericUtils.ToDate<T> toDate,PopupListener listener)
	{
		this.dates = dates;
		this.toDate = toDate;
		currentIndex = 0;
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
		
		
		// Bandeau avec les boutons droit / gauche 
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.setWidth("100%");
		
		
		previous = addButton(false,hl1,e->reculer());
		
		Label empty = new Label();
		hl1.addComponent(empty);
		hl1.setExpandRatio(empty, 1.0f);
		
		
		next = addButton(true,hl1,e->avancer());
			
		vl.addComponent(hl1);
		
		
		// Bandeau avec la date visualisée
		titre = new Label();
		hl1.addStyleName("titre");
		titre.setSizeUndefined();
		vl.addComponent(titre);
		vl.setComponentAlignment(titre, Alignment.MIDDLE_CENTER);
		
		updateButtonState();
	
		return vl;
		
	}

	
	
	private void updateButtonState()
	{
		previous.setEnabled(currentIndex!=0);
		next.setEnabled(currentIndex!=dates.size()-1);
		
		Date toDisplay = toDate.toDate(dates.get(currentIndex));
		SimpleDateFormat dfx = BaseUiTools.isWidthBelow(480) ? df2 : df1;
		titre.setValue(dfx.format(toDisplay));	
	}


	private void avancer()
	{
		if (currentIndex<dates.size()-1)
		{
			currentIndex++;
			updateButtonState();
			listener.onPopupClose();  
		}
	}



	private void reculer()
	{
		if (currentIndex>0)
		{
			currentIndex--;
			updateButtonState();
			listener.onPopupClose();  
		}
	}

	
	
	private Button addButton(boolean toRight,HorizontalLayout layout, ClickListener listener)
	{
		String str;
		if (toRight)
		{
			str = "SUIVANT";
		}
		else
		{
			str = "PRECEDENT";
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
		
		return b;
		
	}


	public void updateTitreValue(Date dateDebut,Date dateFin)
	{

		SimpleDateFormat dfx = BaseUiTools.isWidthBelow(480) ? df2 : df1;
		titre.setValue(dfx.format(dateDebut)+" - "+dfx.format(dateFin));	
	}


	public T getDate()
	{
		return dates.get(currentIndex);
	}

	/**
	 * Permet la mise à jour du modéle de données
	 * 
	 * Si la mise à jour remet en cause l'index, alors on force celui ci a 0
	 * 
	 *  Ceci peut arriver si on supprime des dates de la liste alors qu'une personne est en train de s'inscrire 
	 */
	public void updateDates(List<T> newDates)
	{
		Date oldDate = toDate.toDate(dates.get(currentIndex));
		Date newDate=null;
		if (currentIndex<newDates.size())
		{
			newDate = toDate.toDate(newDates.get(currentIndex));
		}
		
		if  (DateUtils.equals(oldDate,newDate)==false)
		{
			currentIndex=0;
		}
		dates.clear();
		dates.addAll(newDates);
	}
	

	
	

}
