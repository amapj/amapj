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
 package fr.amapj.view.engine.popup.cascadingpopup.sample;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * 
 */
public class BPopup extends CorePopup
{	
	
	private ABCData data;
	
	public BPopup(ABCData data)
	{
		this.popupTitle = "Cascading";
		this.data = data;
	}
	
	protected void createContent(VerticalLayout contentLayout)
	{
		
		contentLayout.addComponent(new Label("Je suis B"));
	}
	
	
	protected void createButtonBar()
	{
		addButton("Annuler", e->handleAnnuler());
		addDefaultButton("VoirA", e->voirA());
		addDefaultButton("VoirB", e->voirB());
		addDefaultButton("VoirC", e->voirC());
	}
	
	
	protected void handleAnnuler()
	{
		close();
	}
	
	
	protected void voirA()
	{
		data.validate();
		data.choix = 1;
		
		close();
	}

	protected void voirB()
	{
		data.validate();
		data.choix = 2;
		
		close();
	}

	protected void voirC()
	{
		data.validate();
		data.choix = 3;
		
		close();
	}
	
}
