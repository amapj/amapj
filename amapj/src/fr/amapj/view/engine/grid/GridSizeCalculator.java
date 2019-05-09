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

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedString;



public class GridSizeCalculator
{
	/**
	 * Permet de calculer le nombre de lignes dans une cellule, à partir 
	 * de la chaîne contenue dans la cellule et du format de la cellule 
	 * 
	 * @param cellValue
	 * @param cellWidth
	 * @param fontName
	 * @param fontSize
	 * @return
	 */
	public int getHeight(String cellValue, int cellWidth , String fontName,int fontSize)
	{
		if (cellValue==null || cellValue.length()==0) 
		{
			return 1;
		}
		
		// Create Font object with Font attribute (e.g. Font family, Font size, etc)
	
		java.awt.Font currFont = new java.awt.Font(fontName, 0, fontSize);
		AttributedString attrStr = new AttributedString(cellValue);
		attrStr.addAttribute(TextAttribute.FONT, currFont);

		// Use LineBreakMeasurer to count number of lines needed for the text
		FontRenderContext frc = new FontRenderContext(null, true, true);
		LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(),frc);
		
		int nextPos = 0;
		int lineCnt = 0;
		while (measurer.getPosition() < cellValue.length())
		{
		    nextPos = measurer.nextOffset(cellWidth); // mergedCellWidth is the max width of each line
		    lineCnt++;
		    measurer.setPosition(nextPos);
		}
		
		return lineCnt;
	}

	/**
	 * Calcule automatiquement la hauteur de la ligne en fonction de son contenu 
	 * @param line
	 * @param fontName
	 * @param fontSize
	 */
	public static void autoSize(GridHeaderLine line, int cellWidth,String fontName,int fontSize)
	{
		int nbLine = 1;
		GridSizeCalculator cal = new GridSizeCalculator();
		
		for (String cell : line.cells)
		{
			if (cell!=null)
			{
				nbLine = Math.max(nbLine, cal.getHeight(cell, cellWidth, fontName, fontSize));
			}
		}
		
		// La taille d'une ligne est la hauteur de la police + une marge de 6 pixels par ligne (vérifié pour la fonte 16)
		int margin = 6;
		int height = fontSize+margin;
		
		// Il faut encore ajouter deux pixels pour lepaisseur du cadre gris
		line.height= height*nbLine+2;
		
	}

}
