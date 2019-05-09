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
 package fr.amapj.view.engine.tools;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.print.attribute.HashAttributeSet;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;

import fr.amapj.common.DebugUtil;

/**
 * Outil pour la gestion des tables
 * 
 *
 */
public class TableTools
{
	
	static public boolean updateTable(Table cdesTable,List<? extends TableItem> res,String[] sortColumns,boolean[] sortAscending)
	{
		boolean oneLineSelected = false;
		
		BeanItemContainer mcInfos = (BeanItemContainer) cdesTable.getContainerDataSource();
		
		
		// Conservation de la valeur actuellement sélectionnée (si il y en a une)
		TableItem current = (TableItem) cdesTable.getValue();
		
		// Affacement de tous les elements
		mcInfos.removeAllItems();
				
		// Chargement des elements
		mcInfos.addAll(res);
				
		// Tris 
		mcInfos.sort(sortColumns,sortAscending);
				
		// Gestion de la selection
		if (current!=null)
		{
			boolean done = false;
			for (TableItem dto : res)
			{
				if (dto.getId().equals(current.getId()))
				{
					cdesTable.setValue(dto);
					done = true;
				}		
			}
			return done;
		}
		else
		{
			return false;
		}
		
	}
	
	
	static public boolean updateTableMultiselect(Table cdesTable,List<? extends TableItem> res,String[] sortColumns,boolean[] sortAscending)
	{		
		BeanItemContainer mcInfos = (BeanItemContainer) cdesTable.getContainerDataSource();
		
		
		// Conservation de la valeur actuellement sélectionnée (si il y en a une)
		Set<TableItem> current = (Set<TableItem>) cdesTable.getValue();
		
		// Affacement de tous les elements
		mcInfos.removeAllItems();
				
		// Chargement des elements
		mcInfos.addAll(res);
				
		// Tris 
		mcInfos.sort(sortColumns,sortAscending);
				
		// Gestion de la selection
		if (current.size()==0)
		{
			return false;
		}
		
		
		Set<TableItem> newSelection = new TreeSet<TableItem>();
		for (TableItem dto : res)
		{
			if (wasSelected(dto.getId(),current))
			{
				newSelection.add(dto);
			}		
		}
		
		cdesTable.setValue(newSelection);
		return newSelection.size()>0;
		
		
	}


	private static boolean wasSelected(Long id, Set<TableItem> current)
	{
		for (TableItem tableItem : current)
		{
			if (tableItem.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}
	
	
}

	

