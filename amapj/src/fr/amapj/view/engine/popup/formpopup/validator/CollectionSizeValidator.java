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


/**
 * Nombre maxi et mini dans une collection 
 *
 */
public class  CollectionSizeValidator<T> implements IValidator
{
	
	private Integer minSize = null;

    private Integer maxSize = null;

 
	public CollectionSizeValidator(Integer minSize, Integer maxSize)
	{
		super();
		this.minSize = minSize;
		this.maxSize = maxSize;
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		List<T> val = (List<T>) value;
		
		
		 int len = val.size();
	     if (minSize != null && len < minSize)
	     {
	    	 a.addMessage("Le champ \""+a.title+"\" doit contenir au moins "+minSize+" élements");
	     }
	    		 
	     if (maxSize != null && len > maxSize)
	     {
	    	 a.addMessage("Le champ \""+a.title+"\"  doit contenir au maximum "+maxSize+" élements");
	     }
	}




	@Override
	public boolean canCheckOnFly()
	{
		return false;
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return null;
	}

}
