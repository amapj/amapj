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
 package fr.amapj.model.engine.ddl;

import java.text.DecimalFormat;


/**
 * Cette classe permet de cr�er des roles dans la base de donn�es
 */
public class GeneratePersistenceXml
{

	public void createData()
	{
		DecimalFormat df = new DecimalFormat("00");
		
		for (int i = 0; i < 100; i++)
		{	
			System.out.println("<persistence-unit name=\"pu"+df.format(i)+"\"> <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>  <exclude-unlisted-classes>false</exclude-unlisted-classes>  <properties> 	</properties> 	</persistence-unit> ");
		}

		
		
	}

	public static void main(String[] args)
	{
		GeneratePersistenceXml generateSqlSchema = new GeneratePersistenceXml();
		generateSqlSchema.createData();

	}

}
