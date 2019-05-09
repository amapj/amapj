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
 package fr.amapj.view.engine.collectioneditor;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractField;


/**
 * Modelisation d'une ligne dans la table
 * 
 */
public class Row
{
	// Attention : ceci correspond à l'itemId dans l'objet Table
	private Object itemId;
	
	private List<AbstractField> fields = new ArrayList<AbstractField>();
	
	// Optionnel - correspond a un identifiant eventuel du bean qui sera préservé 
	private Object idBeanInfo;
	
	
	public Row()
	{
		
	}
	
	public Object[] getColumnTable()
	{
		Object[] row = new Object[fields.size()];
				
		for (int i = 0; i < row.length; i++)
		{
			row[i]= fields.get(i);
		}
		
		return row;
		
	}
	
	
	public void addField(AbstractField<?> f)
	{
		fields.add(f);
	}
	
	
	public Object getFieldValue(int columns)
	{
		return fields.get(columns).getConvertedValue();
	}
	
	public void setFieldValue(int columns,Object val)
	{
		AbstractField f = fields.get(columns);
		
		if (f.isReadOnly())
		{
			f.setReadOnly(false);
			f.setConvertedValue(val);
			f.setReadOnly(true);
		}
		else
		{
			f.setConvertedValue(val);
		}
		
		
	}
	
	
	public int getNbFields()
	{
		return fields.size();
	}
	
	

	public Object getItemId()
	{
		return itemId;
	}

	public void setItemId(Object itemId)
	{
		this.itemId = itemId;
	}

	public Object getIdBeanInfo()
	{
		return idBeanInfo;
	}

	public void setIdBeanInfo(Object idBeanInfo)
	{
		this.idBeanInfo = idBeanInfo;
	}

	
	
}
