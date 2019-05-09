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

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

import fr.amapj.view.engine.tools.TableItem;


/**
 * Permet de construire une table simple avec une liste de bean
 */
public class SimpleTableBuilder<T extends TableItem>
{

	private BeanItemContainer<T> mcInfos;

	protected Table cdesTable;
	
	private Class<T> beanClazz;
	
	private List<T> beans;
	
	public SimpleTableBuilder(Class<T> beanClazz,List<T> beans)
	{
		this.beanClazz = beanClazz;
		this.beans = beans;
	}
	
	
	public Table buildTable()
	{
		// Lecture dans la base de données
		mcInfos = new BeanItemContainer<T>(beanClazz);
			
		// Bind it to a component
		cdesTable = new Table("", mcInfos);
		
		cdesTable.addStyleName("no-stripes");
		cdesTable.addStyleName("no-vertical-lines");
		cdesTable.addStyleName("no-horizontal-lines");
		

		cdesTable.setSelectable(true);
		cdesTable.setMultiSelect(false);
		cdesTable.setImmediate(true);
		
		mcInfos.addAll(beans);

		return cdesTable;
	}



	
	
		
	
}
