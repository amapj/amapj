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

import fr.amapj.common.AmapjRuntimeException;

/**
 * Liste des lignes dans la table
 * 
 */
public class RowList
{
	
	// La liste des objects graphiques permettant l'édition
	private List<Row> rows;
	
	// Compteur sur les lignes totales, y compris les lignes supprimées
	private int nbLignes = 0;
	
	public RowList()
	{
		rows = new ArrayList<Row>();
	}
	
	public void add(Row row, Object idBeanInfo)
	{
		Integer itemId = new Integer(nbLignes);
		row.setItemId(itemId);
		row.setIdBeanInfo(idBeanInfo);
		rows.add(row);
		nbLignes++;
	}
	
	
	
	/**
	 * Supprime une ligne 
	 * et retourne la prochaine ligne à sélectionner 
	 *  
	 * 
	 * @param itemId
	 * @return
	 */
	public Object remove(Object itemId)
	{
		int s = rows.size();
		
		for (int i = 0; i < s; i++)
		{
			Row line = rows.get(i);
			if (line.getItemId()==itemId)
			{
				rows.remove(line);
				
				// La table ne contenait qu'un element
				if (s==1)
				{
					return null;
				}
				// On etait sur la derniere ligne
				else if (i==(s-1))
				{
					return rows.get(s-2).getItemId();
				}
				// Cas standard
				else
				{
					return rows.get(i).getItemId();
				}
			}
		}
		
		// On aurait du trouver la ligne
		throw new AmapjRuntimeException();
	}

	public List<Row> getRows()
	{
		return rows;
	}

	/**
	 * Retourne l'index de la ligne identifiée par itemId, 
	 * c'est à dire le numero de la ligne 
	 * 
	 * -1 si la ligne n'est pas trouvée
	 * 
	 */
	public int getIndex(Object itemId)
	{
		int i=0;
		for (Row line : rows)
		{
			if (line.getItemId()==itemId)
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Peux t on déplacer vers la base cette ligne ? 
	 * @param index
	 * @return
	 */
	public boolean canDown(int index)
	{
		if (index==rows.size()-1)
		{
			return false;
		}
		return true;
	}
	
	

	/**
	 * Retourne le itemId de la ligne qui a été descendue
	 * @param index
	 * @return
	 */
	public Object downRow(int index)
	{
		Row r1 = rows.get(index);
		Row r2 = rows.get(index+1);
		
		int s = r1.getNbFields();
		for (int col = 0; col < s; col++)
		{
			Object val1 = r1.getFieldValue(col);
			Object val2 = r2.getFieldValue(col);
			
			r1.setFieldValue(col, val2);
			r2.setFieldValue(col, val1);
		}
		
		switchIdBeanInfo(r1,r2);
		
		return r2.getItemId();
		
		
	}
	
	
	/**
	 * Peux t on déplacer vers le haut cette ligne ? 
	 * @param index
	 * @return
	 */
	public boolean canUp(int index)
	{
		if (index==0)
		{
			return false;
		}
		return true;
	}
	
	
	/**
	 * Retourne le itemId de la ligne qui a été montée
	 * @param index
	 * @param idToPreserveInfo 
	 * @return
	 */
	public Object upRow(int index)
	{
		Row r1 = rows.get(index);
		Row r2 = rows.get(index-1);
		
		int s = r1.getNbFields();
		for (int col = 0; col < s; col++)
		{
			Object val1 = r1.getFieldValue(col);
			Object val2 = r2.getFieldValue(col);
			
			r1.setFieldValue(col, val2);
			r2.setFieldValue(col, val1);
		}
		
		switchIdBeanInfo(r1,r2);
		
		return r2.getItemId();
		
		
	}

	/**
	 * Realise la permutation des idBeanInfo
	 */
	private void switchIdBeanInfo(Row r1, Row r2)
	{
		Object idBeanInfo1 = r1.getIdBeanInfo();
		Object idBeanInfo2 = r2.getIdBeanInfo();
		
		r1.setIdBeanInfo(idBeanInfo2);
		r2.setIdBeanInfo(idBeanInfo1);
	}
	
}
