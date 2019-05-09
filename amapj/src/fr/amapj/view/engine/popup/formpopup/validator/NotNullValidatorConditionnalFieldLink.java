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
 package fr.amapj.view.engine.popup.formpopup.validator;

import java.util.List;

import com.vaadin.ui.AbstractField;

import fr.amapj.common.StringUtils;
import fr.amapj.view.engine.popup.formpopup.fieldlink.FieldLink;


/**
 * Gestion des champs obligatoires, sous condition 
 * 
 */
public class NotNullValidatorConditionnalFieldLink implements IValidator
{
	private FieldLink fieldLink;
	
	public NotNullValidatorConditionnalFieldLink()
	{
		super();
	}


	@Override
	public void performValidate(Object value,ValidatorHolder a)
	{
		if (fieldLink.isActif()==false)
		{
			return;
		}
		
		List<Enum<?>> enums = fieldLink.getActives();
		String comboTitle = fieldLink.getComboTitle(); 
		
		if (value==null)
		{
			String msg = null;
			if (enums.size()==0)
			{
				msg = "Le champ \""+a.title+"\" doit être renseigné";
			}
			else
			{
				msg ="Le champ \""+a.title+"\" doit être renseigné OU vous devez positionner  le champ \""+comboTitle+"\" à une valeur différente de "+StringUtils.asString(enums, ",");
			}
			
	    	 a.addMessage(msg);
	     }	    		 
	}

	@Override
	public boolean canCheckOnFly()
	{
		return true;
	}
	
	/**
	 * Les verifications sont faites uniquement si le fieldLink est actif
	 * @param comboName 
	 */
	public void checkIf(FieldLink fieldLink)
	{
		this.fieldLink = fieldLink;
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return new AbstractField[] { fieldLink.getBox() };
	}
	
}
