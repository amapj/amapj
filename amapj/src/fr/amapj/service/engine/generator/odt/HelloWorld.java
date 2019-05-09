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
 package fr.amapj.service.engine.generator.odt;
import java.net.URI;

import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;

public class HelloWorld {
	public static void main(String[] args) {
		TextDocument outputOdt;
		try {
			outputOdt = TextDocument.newTextDocument();
			
			// add image
			// outputOdt.newImage(new URI("odf-logo.png"));
			
			// add paragraph
			outputOdt.addParagraph("Hello World, Hello Simple ODF!");
			
			// add list
			outputOdt.addParagraph("The following is a list.");
			List list = outputOdt.addList();
			String[] items = {"item1", "item2", "item3"};
			list.addItems(items);
			
			// add table
			Table table = outputOdt.addTable(2, 2);
			Cell cell = table.getCellByPosition(0, 0);
			cell.setStringValue("Hello World!");
			
			outputOdt.save("HelloWorld.odt");
			
			
		} catch (Exception e) {
			System.err.println("ERROR: unable to create output file.");
		}
	}
}
