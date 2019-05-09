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
 package fr.amapj.view.engine.collectioneditor.columns;

import fr.amapj.view.engine.collectioneditor.FieldType;

public class ColumnInfo
{

	public String propertyId;
	
	public String title;
	
	public FieldType fieldType;
	
	public Object defaultValue;
	
	public boolean editable;
	
	public ColumnInfo(String propertyId, String title,FieldType fieldType,boolean editable,Object defaultValue)
	{
		this.propertyId = propertyId;
		this.title = title;
		this.fieldType = fieldType;
		this.editable = editable;
		this.defaultValue = defaultValue;
		this.editable = editable;
	}
}
