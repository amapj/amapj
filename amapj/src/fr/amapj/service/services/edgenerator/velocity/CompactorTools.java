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
 package fr.amapj.service.services.edgenerator.velocity;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.common.StringUtils;


/**
 * Permet de realiser un compactage d'une liste de couple (String,String)
 * Le compactage est fait sur la deuxième colonne : si un element existe déjà 
 * avec la même valeur dans la deuxième colonne, alors on ajoute dans la premiere colonne
 *    
 * 
 */
public class CompactorTools
{
	
	static public class Item
	{
		String part1;
		String part2;
		
		List<String> part1s = new ArrayList<String>();
		
		public Item(String part1, String part2)
		{
			this.part1 = part1;
			this.part2 = part2;
			
			part1s.add(part1);
		}
		
	}
	
	
	private List<Item> items = new ArrayList<Item>();
	
	private boolean enabledCompact;
	
	public CompactorTools(boolean enabledCompact)
	{
		this.enabledCompact = enabledCompact;
	}
	
	
	/**
	 * A noter : si s2 est null ou vide, alors le couple (s1,s2) n'est pas pris en compte  
	 * 
	 * 
	 * 
	 * @param s1
	 * @param s2
	 */
	public void addLine(String s1,String s2)
	{
		if (s2==null || s2.length()==0)
		{
			return;
		}
		
		if (enabledCompact==false)
		{
			items.add(new Item(s1,s2));
			return;
		}
		
		Item item = findMatchingItem(s2);
		if (item==null)
		{
			items.add(new Item(s1,s2));
			return;
		}
		
		item.part1s.add(s1);
		
		
	}

	private Item findMatchingItem(String s2)
	{
		for (Item item : items)
		{
			if (item.part2.equals(s2))
			{
				return item;
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * COnstruit le resultat sous la forme d'une liste de String 
	 * 
	 * Chaque String est constituée par le header1 ou header2 (header1 si 1 element part1, header2 si plusieurs elements part1) 
	 * 
	 *  suivi de la liste des part1 séparée par sep1
	 * puis suivi de sep2 puis suivi de part2 puis du trailer
	 * 
	 * @param header
	 * @param sep1
	 * @param sep2
	 * @param trailer
	 * @return
	 */
	public List<String> getResult(String header1,String header2,String sep1,String sep2,String trailer)
	{
		List<String> res = new ArrayList<String>();
		for (Item item : items)
		{
			String header = item.part1s.size()==1 ? header1 : header2;
			String s = header+StringUtils.asString(item.part1s, sep1)+sep2+item.part2+trailer;
			res.add(s);
		}		
		return res;
	}
	
	
	/**
	 * Retourne le resultat brut 
	 * @return
	 */
	public List<Item> getResult()
	{
		return items;
	}
	
	
	
	

}
