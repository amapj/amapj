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
 package fr.amapj.view.engine.tools.table;

import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.view.engine.searcher.SearcherDefinition;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder.CallBack;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder.ToGenerator;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder.ToValue;

public class TableColumnInfo<T>
{
	public String title;
	
	public boolean editable;
	
	public int width;

	public TableColumnType type;
	
	public ToValue<T> toVal;
	
	public String property;
	
	public CallBack<T> onClic;
	
	public ToGenerator<T> generator;
	
	public SearcherDefinition searcher;

	public TableColumnInfo(String title, String property,boolean editable,int width,TableColumnType type, ToValue<T> toVal,CallBack<T> onClic,ToGenerator<T> generator,SearcherDefinition searcher)
	{
		super();
		this.title = title;
		this.property = property;
		this.editable = editable;
		this.width = width;
		this.type = type;
		this.toVal = toVal;
		this.onClic = onClic;
		this.generator = generator;
		this.searcher = searcher;
	}
	
	
	
}
