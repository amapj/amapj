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
 package fr.amapj.model.engine;

import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerConfiguration;

public class TestServer
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException
	{
		HsqlProperties argProps = new HsqlProperties();

        argProps.setProperty("server.database.0","file:c:\\amapj\\workspace\\amapj\\db\\data\\amapj");
        argProps.setProperty("server.dbname.0","amapj");
        argProps.setProperty("server.no_system_exit","false");
        
      
        ServerConfiguration.translateAddressProperty(argProps);

        // finished setting up properties;
        Server server = new Server();

        try 
        {
            server.setProperties(argProps);
        } 
        catch (Exception e) 
        {
            //server.printError("Failed to set properties");
            //server.printStackTrace(e);
        	// TODO
        	e.printStackTrace();
            return;
        }

        // now messages go to the channel specified in properties
        System.out.println("Startup sequence initiated from main() method");

        

        server.start();
        
        
        while (true)
		{
			System.out.println("Tomcat running");
			Thread.sleep(2000);
			
		}
        

	}

}
