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
 package fr.amapj.service.engine.tools;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.common.GenericUtils.GetFieldTyped;
import fr.amapj.common.ObjectUtils;


/**
 * Cet outil est utile pour comparer 
 * 
 *  -> une liste d'element provenant de la base de données (dbList)
 *  -> une liste d'element provenant d'une saisie utilisateur (dtoList)
 *  
 *   dans le but de mettre à jour la base de données
 */

public class DtoToDb
{

	static public class ElementToAdd<DTO>
	{
		public DTO dto;
		public int index;
	}
	
	static public class ElementToUpdate<DB,DTO>
	{
		public DB db;
		public DTO dto;
		public int index;
	}
	
	static public class ListDiff<DB,DTO,KEY>
	{
		// Liste des élements à ajouter dans la base   
		// Ces elements proviennent de dtoList
		public List<ElementToAdd<DTO>> toAdd;
		
		// Liste des éléments à supprimer de la base 
		// Ces elements proviennent de dbList
		public List<DB> toSuppress;
		
		// Liste des éléments de la base à mettre à jour  
		// Ces elements proviennent de dbList
		public List<ElementToUpdate<DB,DTO>> toUpdate;
		
	}
	
	
	/**
	 * Permet de calculer la différence entre deux listes
	 * 
	 * 
	 * Retourne 
	 * la liste des éléments à ajouter 
	 * la liste des élements à supprimer
	 * la liste des elements à mettre à jour 
	 *  
	 * pour mettre à jour la base de données
	 * 
	 * 
	 * Cette méthode GERE l'ordre des elements dans la liste 
	 */
	public static <DB,DTO,KEY> ListDiff<DB,DTO,KEY> diffList(List<DB> dbList, List<DTO> dtoList,GetFieldTyped<DB,KEY> keyDb,GetFieldTyped<DTO,KEY> keyDto)
	{
		ListDiff<DB,DTO,KEY> res = new ListDiff<DB,DTO,KEY>();
		
	
		res.toAdd = new ArrayList<ElementToAdd<DTO>>();
		res.toSuppress = new ArrayList<DB>();
		res.toUpdate = new ArrayList<ElementToUpdate<DB,DTO>>();
	
		// Calcul des elements à supprimer 
		for (DB t : dbList)
		{
			KEY val = keyDb.getField(t);
			if (isNotIn(dtoList,val,keyDto))
			{
				res.toSuppress.add(t);
			}
		}
		
		for (int i = 0; i < dtoList.size(); i++)
		{
			DTO t = dtoList.get(i);
			KEY val = keyDto.getField(t);
			if (isNotIn(dbList,val,keyDb))
			{
				ElementToAdd<DTO> element = new ElementToAdd<DTO>();
				element.dto = t;
				element.index = i;
				res.toAdd.add(element);
			}
		}
		
		// Calcul de la liste des elements à updater
		for (DB t : dbList)
		{
			KEY val = keyDb.getField(t);
			ElementToUpdate<DB,DTO> match = find(dtoList,val,keyDto);
			
			if (match!=null)
			{
				match.db = t;
				res.toUpdate.add(match);
			}
		}
				
		return res;
	}
	

	private static <T,V> boolean isNotIn(List<T> ls, V val1 , GetFieldTyped<T,V> compare)
	{
		for (T t : ls)
		{
			V val2 = compare.getField(t);
			if (ObjectUtils.equals(val1, val2))
			{
				return false;
			}
		}
		return true;
	}
	
	
	private static <OLD,NEW,KEY> ElementToUpdate<OLD,NEW> find(List<NEW> dtos, KEY key,GetFieldTyped<NEW,KEY> compare)
	{
		for (int i = 0; i < dtos.size(); i++)
		{
			NEW dto = dtos.get(i);
			KEY key2 = compare.getField(dto);
			if (ObjectUtils.equals(key, key2))
			{
				ElementToUpdate<OLD, NEW> el = new ElementToUpdate<OLD, NEW>();
				el.dto = dto;
				el.index = i;
				return el;
			}
		}
		return null;
	}
	
	
}
