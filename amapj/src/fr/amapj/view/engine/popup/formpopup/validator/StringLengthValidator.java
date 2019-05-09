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

import com.vaadin.ui.AbstractField;



public class StringLengthValidator implements IValidator
{
	
	private Integer minLength = null;

    private Integer maxLength = null;

 
	public StringLengthValidator(Integer minLength, Integer maxLength)
	{
		super();
		this.minLength = minLength;
		this.maxLength = maxLength;
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		String val = (String) value;
		if (val==null)
		{
			val="";
		}
		
		 int len = val.length();
	     if (minLength != null && len < minLength)
	     {
	    	 a.addMessage("Le champ \""+a.title+"\" doit contenir au moins "+minLength+" caractères");
	     }
	    		 
	     if (maxLength != null && len > maxLength)
	     {
	    	 a.addMessage("Le champ \""+a.title+"\"  doit contenir au maximum "+maxLength+" caractères");
	     }
	}




	@Override
	public boolean canCheckOnFly()
	{
		return true;
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return null;
	}

}
