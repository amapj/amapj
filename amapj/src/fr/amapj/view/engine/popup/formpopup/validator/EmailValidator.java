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

import java.util.regex.Pattern;

import com.vaadin.ui.AbstractField;


/**
 * Permet de verifier la validite de l'adresse e mail
 * 
 */
public class EmailValidator implements IValidator
{
	
	private static final String EMAIL_REGEXP = "^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
	
	public EmailValidator()
	{
		super();
	}




	@Override
	public void performValidate(Object value,ValidatorHolder a)
	{
		String val = (String) value;
		if ( isValidEmail(val)==false) 
		{
			a.addMessage("Le champ \""+a.title+"\" n'est pas une adresse e mail valide. Si la personne n'a pas d'email, merci de mettre son nom ou prénom suivi d'un #. Exemple : geraldine#");
		}
	}


	@Override
	public boolean canCheckOnFly()
	{
		return true;
	}
	
	
	/**
	 * Return true si l'adresse e mail est valide
	 * 
	 * @param val
	 * @return
	 */
	static public boolean isValidEmail(String val)
	{
		if ( (val==null) || (val.length()<=1))
		{
			return false;
		}
		
		if (val.endsWith("#"))
		{
			return true;
		}
		return Pattern.matches(EMAIL_REGEXP, val);
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return null;
	}

}
