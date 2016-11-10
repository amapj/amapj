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
 package fr.amapj.view.engine.popup.formpopup.fieldlink;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidatorConditionnal;
import fr.amapj.view.engine.searcher.Searcher;

/**
 * Permet de créer un lien classique entre un choix OUI/NON et la saisie dans un searcher
 * et la présence eventuelle d'un texte à saisir 
 *
 */
public class ClassicFieldLink
{
	
	public ComboBox box;
	
	public Searcher searcher;
	
	public TextField textField;

	// Sera appliqué sur le searcher
	NotNullValidatorConditionnal notNull;
	
	public ClassicFieldLink()
	{
		notNull = new NotNullValidatorConditionnal();
	}

	public IValidator getValidator()
	{
		return notNull;
	}
	
	public void doLink()
	{
		//
		notNull.noCheckIf(box, ChoixOuiNon.NON);
		
		//
		box.addValueChangeListener(e->valueChanged(e));
		
		if (searcher!=null)
		{
			searcher.setEnabled(box.getValue()==ChoixOuiNon.OUI);
		}
		if (textField!=null)
		{
			textField.setEnabled(box.getValue()==ChoixOuiNon.OUI);
		}
		
		
	}

	
	private void valueChanged(ValueChangeEvent e)
	{
		ChoixOuiNon choix = (ChoixOuiNon) e.getProperty().getValue();
		
		
		if(searcher!=null)
		{
			searcher.setEnabled(choix==ChoixOuiNon.OUI);
			if (choix==ChoixOuiNon.NON)
			{
				searcher.setValue(null);
			}
		}
		
		if (textField!=null)
		{
			textField.setEnabled(choix==ChoixOuiNon.OUI);
			if (choix==ChoixOuiNon.NON)
			{
				textField.setValue("");
			}
		}
	}
	

}
