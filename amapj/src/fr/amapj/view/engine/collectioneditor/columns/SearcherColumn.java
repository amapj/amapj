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
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.engine.searcher.SearcherDefinition;

public class SearcherColumn extends ColumnInfo
{

	public SearcherDefinition searcher;
	public Object params;
	public Searcher linkedSearcher;
	

	public SearcherColumn(String propertyId, String title, FieldType fieldType, boolean editable,Object defaultValue, SearcherDefinition searcher,Object params)
	{
		super(propertyId, title, fieldType, editable,defaultValue);
		this.searcher = searcher;
		this.params = params;
	}
	
	
	public SearcherColumn(String propertyId, String title, FieldType fieldType, boolean editable,Object defaultValue, SearcherDefinition searcher,Searcher linkedSearcher)
	{
		super(propertyId, title, fieldType, editable,defaultValue);
		this.searcher = searcher;
		this.linkedSearcher = linkedSearcher;
	}

}
