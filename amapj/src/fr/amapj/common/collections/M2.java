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
 package fr.amapj.common.collections;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.amapj.common.AmapjRuntimeException;

/**
 * Classe utilitaire generique permettant de fusionner deux listes 
 * et de trier suivant une clé 
 * 
 * (merge de 2 listes)
 * 
 *
 */
public class M2<L1,L2,KEY extends Comparable,VAL1,VAL2>
{
	private List<L2> l2;
	private List<L1> l1;
	
	private Function<L1, KEY> key1;
	private Function<L2, KEY> key2;
	
	private Function<L1, VAL1> val1;
	private Function<L2, VAL2> val2;
	
	private Function<KEY,VAL1> defaultVal1;
	private Function<KEY,VAL2> defaultVal2;

	public M2()
	{
	}
	
	public void setL1(List<L1> l1,Function<L1,KEY> key1,Function<L1, VAL1> val1,Function<KEY,VAL1> defaultVal1)
	{
		this.l1 = l1;
		this.key1 = key1;
		this.val1 = val1;
		this.defaultVal1 = defaultVal1;
	}
	
	public void setL2(List<L2> l2,Function<L2,KEY> key2,Function<L2, VAL2> val2,Function<KEY,VAL2> defaultVal2)
	{
		this.l2 = l2;
		this.key2 = key2;
		this.val2 = val2;
		this.defaultVal2 = defaultVal2;
	}
	
	
	public List<Pair<KEY,VAL1,VAL2>> get()
	{
		Stream<Pair<KEY,VAL1,VAL2>> s1 = l1.stream().map(e->createPairFromL1(e));
		Stream<Pair<KEY,VAL1,VAL2>> s2 = l2.stream().map(e->createPairFromL2(e));
		
		// On agrege les deux streams en un seul 
		Set<Entry<KEY, Pair<KEY,VAL1,VAL2>>> vals = Stream.concat(s1, s2).collect(Collectors.toMap(e->e.key,Function.identity(),(p1,p2)->merge(p1,p2))).entrySet();
		
		// On trie suivant la clé et on retourne le resultat sous forme d'une liste  
		return vals.stream().sorted((k1,k2)->k1.getKey().compareTo(k2.getKey())).map(e->map(e.getValue())).collect(Collectors.toList());
	}

	
	
	private Pair<KEY,VAL1,VAL2> createPairFromL1(L1 l1)
	{
		Pair<KEY,VAL1,VAL2> p = new Pair<KEY,VAL1,VAL2>();
		p.key = key1.apply(l1);
		p.v1 = val1.apply(l1);
		p.v1filled = true;
		p.v2filled = false;
		
		return p;
	}
	
	private Pair<KEY,VAL1,VAL2> createPairFromL2(L2 l2)
	{
		Pair<KEY,VAL1,VAL2> p = new Pair<KEY,VAL1,VAL2>();
		p.key = key2.apply(l2);
		p.v2 = val2.apply(l2);
		p.v1filled = false;
		p.v2filled = true;
	
		return p;
	}

	
	
	/**
	 * Permet le merge 
	 */
	private Pair<KEY,VAL1,VAL2> merge(Pair<KEY,VAL1,VAL2> p1,Pair<KEY,VAL1,VAL2> p2)
	{
		// Detection des incoherences 
		if (p1.v1filled && p2.v1filled) 
		{
			throw new AmapjRuntimeException("Dans la liste L1, il y a des elements différents avec la même clé");
		}
		if (p1.v2filled && p2.v2filled) 
		{
			throw new AmapjRuntimeException("Dans la liste L2, il y a des elements différents avec la même clé");
		}
			
		if (p1.v1filled)
		{
			p1.v2 = p2.v2;
			p1.v2filled = true;
		}
		else
		{
			p1.v1 = p2.v1;
			p1.v1filled = true;
		}
		
		return p1;
	}
	
	

	/**
	 * Si nécesaire, on complete les Pair en mettant une valeur par défaut pour les elements non remplis 
	 * @param pair
	 * @return
	 */
	private Pair<KEY,VAL1,VAL2> map(Pair<KEY,VAL1,VAL2> pair)
	{
		if (pair.v1filled ==false && defaultVal1!=null)
		{
			pair.v1 = defaultVal1.apply(pair.key);
		}
		
		if (pair.v2filled ==false && defaultVal2!=null)
		{
			pair.v2 = defaultVal2.apply(pair.key);
		}
		return pair;
	}


	static public class Pair<KEY extends Comparable,VAL1,VAL2>
	{
		// La valeur 1 a été remplie
		public boolean v1filled;
		
		// La valeur 2 a été remplie
		public boolean v2filled;
		
		public KEY key;
		
		public VAL1 v1;
		
		public VAL2 v2;
			
	}
	
	// methodes statiques pour simplifier l'accés 
	
	public static <L1,L2,KEY extends Comparable,VAL1,VAL2> M2<L1,L2,KEY,VAL1,VAL2> merge(
			List<L1> l1,Function<L1,KEY> key1,Function<L1, VAL1> val1,Function<KEY,VAL1> defaultVal1,
			List<L2> l2,Function<L2,KEY> key2,Function<L2, VAL2> val2,Function<KEY,VAL2> defaultVal2)
	{
		M2<L1,L2,KEY,VAL1,VAL2> m2 = new M2<L1,L2,KEY,VAL1,VAL2>();
		m2.setL1(l1, key1, val1, defaultVal1);
		m2.setL2(l2, key2, val2, defaultVal2);
		return m2;
	}

}


