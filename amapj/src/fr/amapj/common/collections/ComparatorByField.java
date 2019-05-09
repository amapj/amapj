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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.amapj.common.GenericUtils.GetField;

public class  ComparatorByField<T> implements Comparator<T>
{
	private List<GetField<T>> fs = new ArrayList<GetField<T>>();
	
	private List<Boolean> ascendant = new ArrayList<Boolean>();
	
	
	public ComparatorByField()
	{
		
	}
	
	public ComparatorByField(GetField<T>... fs)
	{
		for (int i = 0; i < fs.length; i++)
		{
			add(fs[i],true);
		}	
	}
	
	public ComparatorByField(GetField<T> f1,boolean asc1)
	{
		add(f1,asc1);
	}
	
	public ComparatorByField(GetField<T> f1,boolean asc1,GetField<T> f2,boolean asc2)
	{
		add(f1,asc1);
		add(f2,asc2);
	}
	
	
	public ComparatorByField(List<GetField<T>> fs, List<Boolean> ascendant)
	{
		for (int i = 0; i < fs.size(); i++)
		{
			add(fs.get(i), ascendant.get(i));
		}
	}

	public void add(GetField<T> f, boolean ascendant)
	{
		this.fs.add(f);
		this.ascendant.add(true);		
	}

	

	@Override
	public int compare(T t1, T t2)
	{
		for (int i = 0; i < fs.size(); i++)
		{
			GetField<T> f = fs.get(i);
		

			int a = doCompare(f, t1, t2);
			if (ascendant.get(i) == false)
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
}
