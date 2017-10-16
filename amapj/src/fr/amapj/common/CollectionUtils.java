/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.amapj.common.GenericUtils.GetField;
import fr.amapj.common.GenericUtils.ToBoolean;
import fr.amapj.common.GenericUtils.ToDate;

public class CollectionUtils
{

	/**
	 * Découpe la liste listIn en sous listes contenant au maximum nbElem
	 * 
	 * @param listIn
	 * @param nbElem
	 * @return
	 */
	static public <T> List<List<T>> cutInSubList(List<T> listIn, int nbElem)
	{
		if (nbElem <= 0)
		{
			throw new AmapjRuntimeException("Impossible de découper une liste avec nbElem=" + nbElem);
		}

		List<List<T>> res = new ArrayList<>();

		List<T> tmp = new ArrayList<>();
		int size = listIn.size();
		for (int i = 0; i < size; i++)
		{
			if ((i != 0) && ((i % nbElem) == 0))
			{
				res.add(tmp);
				tmp = new ArrayList<>();
			}
			tmp.add(listIn.get(i));
		}

		if (tmp.size() != 0)
		{
			res.add(tmp);
		}

		return res;
	}

	static public interface ToString<T>
	{
		public String toString(T t);
	}

	/**
	 * Convertit une liste d'objet en une String Exemple : ls = [ "Bob" , "Marc"
	 * , "Paul" ]
	 * 
	 * asString(ls,",") => "Bob,Marc,Paul"
	 * 
	 */
	public static <T> String asString(List<T> ls, String sep, ToString<T> f)
	{
		if (ls.size() == 0)
		{
			return "";
		}

		StringBuffer str = new StringBuffer();
		for (int i = 0; i < ls.size() - 1; i++)
		{
			T l = ls.get(i);
			if (l != null)
			{
				str.append(f.toString(l));
			} else
			{
				str.append("null");
			}
			str.append(sep);
		}

		T l = ls.get(ls.size() - 1);
		if (l != null)
		{
			str.append(f.toString(l));
		} else
		{
			str.append("null");
		}

		return str.toString();
	}

	/**
	 * Convertit une liste d'objet en une String Exemple : ls = [ "Bob" , "Marc"
	 * , "Paul" ]
	 * 
	 * asString(ls,",") => "Bob,Marc,Paul"
	 * 
	 */
	public static <T> String asString(List<T> ls, String sep)
	{
		ToString<T> f = new CollectionUtils.ToString<T>()
		{
			public String toString(T t)
			{
				return t.toString();
			}
		};
		return asString(ls, sep, f);
	}

	/**
	 * Convertit une liste d'objet en une String Exemple : ls = [ "Bob" , "Marc"
	 * , "Paul" ]
	 * 
	 * asString(ls) => "(Bob,Marc,Paul)"
	 * 
	 */
	public static <T> String asStdString(List<T> ls, ToString<T> f)
	{
		return "(" + asString(ls, ",", f) + ")";
	}

	/**
	 * Convertit une liste d'objet en une String , EN CONSERVANT LE SEPARATEUR
	 * FINAL POUR LE DERNIER ELEMENT
	 * 
	 * Exemple : ls = [ "Bob" , "Marc" , "Paul" ]
	 * 
	 * asString(ls,",") => "Bob,Marc,Paul,"
	 * 
	 * Si la liste est vide, retourne la chaine vide
	 * 
	 */
	public static <T> String asStringFinalSep(List<T> ls, String sep, ToString<T> f)
	{
		if (ls.size() == 0)
		{
			return "";
		}

		StringBuffer str = new StringBuffer();
		for (int i = 0; i < ls.size(); i++)
		{
			T l = ls.get(i);
			if (l != null)
			{
				str.append(f.toString(l));
			} else
			{
				str.append("null");
			}
			str.append(sep);
		}
		return str.toString();
	}

	/**
	 * Permet de trier des objets suivant un champ de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1)
	{
		sortInternal(ls, new GetField[] { f1 }, new boolean[] { true });
	}

	public static <T> void sort(List<T> ls, GetField<T> f1, boolean asc1)
	{
		sortInternal(ls, new GetField[] { f1 }, new boolean[] { asc1 });
	}

	/**
	 * Permet de trier des objets suivant 2 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, GetField<T> f2)
	{
		sortInternal(ls, new GetField[] { f1, f2 }, new boolean[] { true, true });
	}

	/**
	 * Permet de trier des objets suivant 2 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, boolean asc1, GetField<T> f2, boolean asc2)
	{
		sortInternal(ls, new GetField[] { f1, f2 }, new boolean[] { asc1, asc2 });
	}

	/**
	 * Permet de trier des objets suivant 3 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, GetField<T> f2, GetField<T> f3)
	{
		sortInternal(ls, new GetField[] { f1, f2, f3 }, new boolean[] { true, true, true });
	}

	/**
	 * Permet de trier des objets suivant 3 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, boolean asc1, GetField<T> f2, boolean asc2, GetField<T> f3, boolean asc3)
	{
		sortInternal(ls, new GetField[] { f1, f2, f3 }, new boolean[] { asc1, asc2, asc3 });
	}

	/**
	 * Permet de trier des objets suivant 4 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, GetField<T> f2, GetField<T> f3, GetField<T> f4)
	{
		sortInternal(ls, new GetField[] { f1, f2, f3, f4 }, new boolean[] { true, true, true, true });
	}

	/**
	 * Permet de trier des objets suivant 3 champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	public static <T> void sort(List<T> ls, GetField<T> f1, boolean asc1, GetField<T> f2, boolean asc2, GetField<T> f3, boolean asc3, GetField<T> f4,
			boolean asc4)
	{
		sortInternal(ls, new GetField[] { f1, f2, f3, f4 }, new boolean[] { asc1, asc2, asc3, asc4 });
	}

	/**
	 * Permet de trier des objets suivant plusieurs champs de l'objet
	 * 
	 * Les objets avec le champ null sont en premier (les plus petits)
	 */
	private static <T> void sortInternal(List<T> ls, GetField<T>[] fs, boolean[] asc)
	{
		Comparator<T> c = new Comparator<T>()
		{
			@Override
			public int compare(T t1, T t2)
			{
				for (int i = 0; i < fs.length; i++)
				{
					GetField<T> f = fs[i];

					int a = doCompare(f, t1, t2);
					if (asc[i] == false)
					{
						a = -a;
					}

					if (a != 0)
					{
						return a;
					}
				}
				return 0;
			}

			public int doCompare(GetField<T> f, T t1, T t2)
			{
				Comparable o1 = (Comparable) f.getField(t1);
				Comparable o2 = (Comparable) f.getField(t2);

				if ((o1 == null) && (o2 == null))
				{
					return 0;
				}
				if (o1 == null)
				{
					return -1;
				}
				if (o2 == null)
				{
					return +1;
				}
				return o1.compareTo(o2);
			}
		};

		Collections.sort(ls, c);
	}

	// FIND MATCHING
	
	
	/**
	 * Permet d'extraire un element d'une liste , qui matche un certain nombre de critere 
	 */
	public static <T> T findMatching(List<T> ls, ToBoolean<T> f1)
	{
		for (T t : ls)
		{
			if (f1.toBoolean(t))
			{
				return t;
			}
		}
		return null;
	}
	
	
	/**
	 * Permet d'extraire un element d'une liste , qui matche un certain nombre de critere, 
	 * puis d'extraire un champ date de cet element
	 * 
	 * Retourne null si il n'y a pas d'element qui match 
	 */
	public static <T> Date findMatching(List<T> ls, ToBoolean<T> f1,ToDate<T> field)
	{
		T t = findMatching(ls, f1);
		if (t!=null)
		{
			return field.toDate(t);
		}
		return null;
	}
	
	
	
	// SELECT 
	public static <T> List<Object> selectDistinct(List<T> ls, GetField<T> f1)
	{
		List<Object> res = new ArrayList<Object>();
		
		for (T t : ls)
		{
			Object field = f1.getField(t);
			if (res.contains(field)==false)
			{
				res.add(field);
			}
		}
		return res;
	}
	
	
	// FILTER
	
	/**
	 * On retourne une nouvelle liste contenant uniquement les élements dont la fonction retourne true 
	 * @param ls
	 * @param f1
	 * @return
	 */
	public static <T> List<T> filter(List<T> ls, ToBoolean<T> f1)
	{
		List<T> res = new ArrayList<T>();
		
		for (T t : ls)
		{
			if (f1.toBoolean(t)==true)
			{
				res.add(t);
			}
		}
		return res;
	}
	
	
	

}
