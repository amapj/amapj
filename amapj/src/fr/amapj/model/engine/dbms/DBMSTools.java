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
 package fr.amapj.model.engine.dbms;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBMSTools
{

	/**
	 * Permet la lecture d'un result set et la transformation en tableau de String 
	 * @param resultset
	 * @return
	 * @throws SQLException
	 */
	static public List<List<String>> readResultSet(ResultSet resultset) throws SQLException
	{
		List<List<String>> result = new ArrayList<>();  
		ResultSetMetaData metadata = resultset.getMetaData();
		int numcols = metadata.getColumnCount();
		
		// Une ligne de titre
		 List<String> title = new ArrayList<>(numcols);
		for (int i = 1; i <= numcols; i++)
		{
			title.add(metadata.getTableName(i)+"."+metadata.getColumnLabel(i));	
		}
		result.add(title);
		
		
		while (resultset.next()) 
		{
		    List<String> row = new ArrayList<>(numcols); // new list per row
		    int i = 1;
		    while (i <= numcols) 
		    {  
		        row.add(resultset.getString(i++));
		    }
		    result.add(row); 
		}
		
		return result;
	}
	
	
}
