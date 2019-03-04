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
 package fr.amapj.view.engine.popup.formpopup.validator;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

import fr.amapj.common.StringUtils;


/**
 * Gestion des champs obligatoires, sous condition 
 * 
 */
public class NotNullValidatorConditionnal implements IValidator
{
	
	private ComboBox box;
	
	private Enum[] enums;
	
	public NotNullValidatorConditionnal()
	{
		super();
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		for (Enum enu : enums)
		{
			if (box.getValue()==enu)
			{
				return;
			}
		}
		
		if (value==null)
		{
			String msg = null;
			if (enums.length==0)
			{
				msg = "Le champ \""+a.title+"\" doit être renseigné";
			}
			else if (enums.length==1)
			{
				msg ="Le champ \""+a.title+"\" doit être renseigné OU vous devez positionner  le champ \""+box.getCaption()+"\" à la valeur "+enums[0];
			}
			else
			{
				msg ="Le champ \""+a.title+"\" doit être renseigné OU vous devez positionner  le champ \""+box.getCaption()+"\" à l'une des valeurs suivantes : "+StringUtils.asString(enums, ",");
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
	 * Si la valeur de combox est egale à enum, alors on ne fait pas la verif 
	 */
	public void noCheckIf(ComboBox box,Enum... enums)
	{
		this.box = box;
		this.enums = enums;
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return new AbstractField[] { box };
	}
	
}
