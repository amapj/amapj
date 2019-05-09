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

import java.util.Date;
import java.util.List;

import com.vaadin.ui.AbstractField;

import fr.amapj.common.CollectionUtils.ToString;
import fr.amapj.common.GenericUtils.GetField;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.ObjectUtils;


/**
 * Pas de doublons dans une collection 
 *
 */
public class  CollectionNoDuplicates<T> implements IValidator
{
	private GetField<T> field;

	private ToString<T> toString;

 
	public CollectionNoDuplicates(GetField<T> field,ToString<T> toString)
	{
		super();
		this.field = field;
		this.toString = toString;
	}

	
	public CollectionNoDuplicates(GetField<T> field)
	{
		this(field,null);
	}



	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		List<T> val = (List<T>) value;
		
		
		for (T t : val)
		{
			int nb = count(val,t);
			if (nb!=1)
			{
				String lib;
				if (toString==null)
				{
					lib = prettyString(t);
				}
				else
				{
					lib = toString.toString(t);
				}
				
				a.addMessage("Le champ \""+a.title+"\" contient plusieurs fois "+lib+ "( "+nb+" fois) ");
				break;
			}
		}
	}




	private String prettyString(T t)
	{
		Object ref = field.getField(t);
		if (ref instanceof Date )
		{
			return "la date "+FormatUtils.getStdDate().format((Date) ref);
		}
		
		return "l'element "+ref;
	}




	private int count(List<T> val, T t)
	{
		int nb=0;
		Object ref = field.getField(t);
	
		for (T t2 : val)
		{
			Object r2 = field.getField(t2);
			if (ObjectUtils.equals(ref, r2))
			{
				nb++;
			}
		}
		return nb;
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
