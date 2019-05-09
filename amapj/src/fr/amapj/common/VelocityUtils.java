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

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import fr.amapj.service.services.edgenerator.velocity.VCAmapien;


public class VelocityUtils
{
	
	
	public static String evaluate(VelocityContext ctx,String in)
	{				
		StringReader reader = new StringReader(in);
		StringWriter writer = new StringWriter();

		boolean ret = Velocity.evaluate(ctx, writer, "velocity", reader);
		if (ret==false)
		{
			throw new AmapjRuntimeException("Impossible d'évaluer le contexte");
		}
		
		return writer.toString();
		
	}
	
	
	

	public static void main(String[] args)
	{
		Velocity.init();

		VelocityContext context = new VelocityContext();

		VCAmapien a = new VCAmapien();
		a.nom = "toto";
		context.put( "a", a);

				
		StringReader reader = new StringReader("essai $a.nom");
		StringWriter writer = new StringWriter();

		Velocity.evaluate(context, writer, "xx", reader);
		
		System.out.println("res="+writer.toString());
	
	}
	
	

}
