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
 package fr.amapj.common;

import java.util.Date;


public class GenericUtils
{
	static public interface ToString<T>
	{
		public String toString(T t);
	}
	
	static public interface ToDate<T>
	{
		public Date toDate(T t);
	}
	
	static public interface ToBoolean<T>
	{
		public boolean toBoolean(T t);
	}
	
	static public interface ToLong<T>
	{
		public boolean toLong(T t);
	}
	
	
	
	static public interface GetField<T>
	{
		public Object getField(T t);
	}
	
	static public interface GetFieldTyped<T,V>
	{
		public V getField(T t);
	}
	
	
	static public interface SetField<T>
	{
		public void setField(T t,Object val);
	}
	
	
	static public interface VoidAction
	{
		public void action();
	}
	
	static public interface VoidActionException
	{
		public void action() throws Exception;
	}
	
	
	static public interface StringAction
	{
		public String action();
	}

	
}
