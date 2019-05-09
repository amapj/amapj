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

import fr.amapj.common.CollectionUtils.ToString;
import fr.amapj.common.GenericUtils.GetField;
import fr.amapj.common.GenericUtils.GetFieldTyped;


/**
 * Nombre maxi et mini dans une collection 
 *
 */
public class  CollectionNotIn<T,V> implements IValidator
{
	
	private GetFieldTyped<T,V> field;
	private List<V> notIn;
	private ToString<T> toString;

 
	public CollectionNotIn(GetFieldTyped<T,V> field,List<V> notIn,ToString<T> toString)
	{
		super();
		this.field = field;
		this.toString = toString;
		this.notIn = notIn;
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		List<T> val = (List<T>) value;
		
		for (T t : val)
		{
			V ref = field.getField(t);
			if (notIn.contains(ref))
			{
				a.addMessage(toString.toString(t));
			}
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
