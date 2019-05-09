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
 package fr.amapj.view.engine.popup.copypopup;

import java.util.function.Supplier;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup pour permettre la copie facile d'un long texte
 * 
 * Le texte sera affiché et sélectionné, prêt à être copier 
 *  
 */
public class CopyPopup extends CorePopup
{
	
	private Supplier<String> contentSupplier;
	
	/**
	 * Le contentSupplier permet de retarder le calcul de la chaine : la chaine à afficher est calculée uniquement à l'ouverture du popup, pas avant
	 * 
	 * @param title
	 * @param contentSupplier
	 */
	public CopyPopup(String title,Supplier<String> contentSupplier)
	{
		this.contentSupplier = contentSupplier;
		popupTitle = title;
		setWidth(60);		
	}
	
	
	protected void createButtonBar()
	{		
		addDefaultButton("OK", e->close());
	}
	

	protected void createContent(VerticalLayout contentLayout)
	{
		// Calcul du texte a afficher
		String str = contentSupplier.get();
		
		// Construction de la zone d'affichage du texte
		HorizontalLayout hlTexte = new HorizontalLayout();
		hlTexte.setMargin(true);
		hlTexte.setSpacing(true);
		hlTexte.setWidth("100%");
		
		
		TextArea listeMails = new TextArea("");
		listeMails.setValue(str);
		listeMails.setReadOnly(true);
		listeMails.selectAll();
		listeMails.setWidth("80%");
		listeMails.setHeight(5, Unit.CM);
		
		
		hlTexte.addComponent(listeMails);
		hlTexte.setExpandRatio(listeMails, 1);
		hlTexte.setComponentAlignment(listeMails, Alignment.MIDDLE_CENTER);
		
		contentLayout.addComponent(hlTexte);
		
	}
	

	
}
