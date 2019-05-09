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
 package fr.amapj.view.engine.enumselector;

import java.util.EnumSet;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.GenericUtils.ToString;
import fr.amapj.model.engine.metadata.MetaDataEnum;
import fr.amapj.model.engine.metadata.MetaDataEnum.HelpInfo;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;

public class EnumSearcher 
{
	
	/**
	 * Permet de créer une combo box permettant de choisir parmi une liste de Enum
	 * avec possibilité d'indiquer comment afficher les libelles 
	 *  
	 * @param binder
	 * @param title
	 * @param enumeration donne la liste à afficher
	 * @param propertyId
	 * @return
	 */
	static public <T extends Enum<T>> HorizontalLayout createEnumSearcher(FieldGroup binder,String title,String propertyId,T...enumsToExcludes)
	{
		Class<T> enumeration = binder.getItemDataSource().getItemProperty(propertyId).getType();
		if (enumeration.isEnum()==false)
		{
			throw new AmapjRuntimeException("Le champ "+title+" n'est pas de type enum");
		}
		
		HelpInfo metaData = MetaDataEnum.getHelpInfo(enumeration);
		
		ComboBox comboBox = new ComboBox();
		comboBox.setWidth("300px");
		
		EnumSet<T> enums = EnumSet.allOf(enumeration);
		for (T en : enums)
		{
			if (isAllowed(en,enumsToExcludes))
			{
				String caption = getCaption(metaData,en);
				
				comboBox.addItem(en);
				comboBox.setItemCaption(en, caption);
			}
		}
			
		binder.bind(comboBox, propertyId);
		
		comboBox.setImmediate(true);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setCaption(title);
		hl.addComponent(comboBox);
		
		if (metaData!=null)
		{
			
			Button aide = new Button();
			aide.setIcon(FontAwesome.QUESTION_CIRCLE);
			aide.addStyleName("borderless-colored");
			aide.addStyleName("question-mark");
			aide.addClickListener(e->handleAide(metaData));
			
			hl.addComponent(aide);
		}
		
		
		return hl;
	}
	
	private static void handleAide(HelpInfo metaData)
	{
		String fullText = metaData.getFullText();
		MessagePopup m = new MessagePopup("Aide", ContentMode.HTML, ColorStyle.GREEN, fullText);
		MessagePopup.open(m);
	}
	
	
	

	static private <T extends Enum<T>> String getCaption(HelpInfo metaData,T en)
	{
		if (metaData==null)
		{
			return  en.toString();
		}
		else
		{
			return metaData.getLib(en);
		}
	}
	
	
	
	private static <T extends Enum<T>> boolean isAllowed(T en, T[] enumsToExcludes)
	{
		if (enumsToExcludes==null)
		{
			return true;
		}
		for (int i = 0; i < enumsToExcludes.length; i++)
		{
			T enum1 = enumsToExcludes[i];
			if (enum1.equals(en))
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Permet de créer une combo box permettant de choisir parmi une liste de Enum
	 * pour etre utilisé dans les tableaux
	 *  
	 * @param binder
	 * @param title
	 * @param enumeration donne à la fois la liste à afficher et la valeur par défaut 
	 * @param propertyId
	 * @return
	 */
	static public <T extends Enum<T>> ComboBox createEnumSearcher(String title,T enumeration)
	{
		ComboBox comboBox = new ComboBox(title);
		
		EnumSet<T> enums = EnumSet.allOf(enumeration.getDeclaringClass());
		for (T en : enums)
		{
			String caption = en.toString();	
			comboBox.addItem(en);
			comboBox.setItemCaption(en, caption);
		}
				
		comboBox.setValue(enumeration);
		
		return comboBox;
	}
	
}
