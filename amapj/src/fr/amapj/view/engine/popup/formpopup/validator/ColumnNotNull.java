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

import fr.amapj.common.GenericUtils.GetField;


/**
 * Pas de null dans une colonne d'une collection 
 *
 */
public class  ColumnNotNull<T> implements IValidator
{
	private GetField<T> field;


	
	public ColumnNotNull(GetField<T> field)
	{
		this.field = field;
	}



	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		List<T> val = (List<T>) value;
		
		
		for (int i = 0; i < val.size(); i++)
		{
			T t = val.get(i);
			Object o = field.getField(t);
			if (o==null)
			{				
				a.addMessage("A la ligne "+(i+1)+" La colonne \""+a.title+"\" n'est pas renseignée.");
				break;
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
