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
 package fr.amapj.view.engine.grid;


/**
 * Object immatble contenant une position i,j
 *  
 *
 */
public class GridIJData
{
	private int i;
	private int j;
	
	
	public GridIJData(int i, int j)
	{
		this.i = i;
		this.j = j;
	}
	
	public int i()
	{
		return i;
	}
		
	public int j()
	{
		return j;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		GridIJData o = (GridIJData) obj;
		return ( (o.i==i) && (o.j==j));
	}

	@Override
	public int hashCode()
	{
		return i+107*j;
	}
	
}
