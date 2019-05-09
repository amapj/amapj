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
 package fr.amapj.service.engine.generator.excel;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;

import fr.amapj.common.StringUtils;


/**
 *  Permet le calcul de la hauteur d'une ligne d'une feuille Excel
 *  
 *  Attention : ici tout est géré en points (pas de pixel) 
 *
 */
public class ExcelCellAutoSize
{
	
	static public class ExcelSize
	{
		List<String> lines;
		int cellWidth;
		String fontName;
		int fontSize;
		boolean isBold;
	}
	
	private List<ExcelSize> cells;
	
	private int additionalSpace;
	
	
	/**
	 * 
	 * @param additionalSpace hauteur additionnelle exprimée en points, qui sera ajoutée après le calcul 
	 * de la hauteur de ligne optimale  
	 */
	public ExcelCellAutoSize(int additionalSpace)
	{
		this.additionalSpace = additionalSpace;
		cells = new ArrayList<ExcelSize>();
	}
	
	
	private ExcelSize current;
	
	
	public void addCell(int cellWidth,String fontName,int fontSize)
	{
		addCell(cellWidth, fontName, fontSize, false);
	}
	
	public void addCell(int cellWidth,String fontName,int fontSize,boolean isBold)
	{
		current = new ExcelSize();
		
		current.cellWidth = cellWidth;
		current.fontName = fontName;
		current.fontSize = fontSize;
		current.isBold = isBold;
		
		
		current.lines =  new ArrayList<String>();
		
		cells.add(current);
	}
	
	/**
	 * Permet d'ajouter une ligne
	 * La ligne est splittée en plusieurs lignes si elle contient des \n
	 * 
	 * @param line
	 */
	public void addLine(String line)
	{
		current.lines.addAll(StringUtils.asList(line));
	}
	
	
	public void autosize(Row currentRow)
	{
		// Contient la hauteur de la ligne en point
		// le minimum de hauteur est de une ligne fonte 10
		double height = getRealHeight(10);
		
		
		for (ExcelSize cell : cells)
		{
			double currentHeight = getLineCount(cell)*getRealHeight(cell.fontSize);
			
			height = Math.max(height,currentHeight);
		}
		
		currentRow.setHeight( (short) (20*(height+additionalSpace)) );
	}
	
	
	
	/**
	 * Suite à mesure dans Excel, voici la table de correspondance entre la fonte de la ligne 
	 * et la hauteur de la ligne en points 
	 * 
	 * 8 <=> 10.2
	 * 9 <=> 11.4
	 * 10 <=> 13.2
	 * 12 <=> 15
	 * 14 <=> 17.5 
	 * 24 <=> 30
	 * 48 <=> 60
	 *  
	 * On considére la fonte Arial en normal (non gras, non italique)  
	 * 
	 * @param height
	 * @return
	 */
	private double getRealHeight(int height)
	{
		if (height==8)
		{
			return 10.2;
		}
		else if (height==9)
		{
			return 11.4;
		} 
		else if (height==10)
		{
			return 13.2;
		}
		else
		{
			return height*1.25;
		}
	}


	/**
	 * Calcule le nombre de ligne dans une cellule, en sommant le nombre de lignes 
	 * de chacun des textes de la cellule
	 */
	private int getLineCount(ExcelSize cell)
	{
		int lineCnt = 0;
		for (String line : cell.lines)
		{
			lineCnt = lineCnt + getLineCount(line, cell.cellWidth, cell.fontName, cell.fontSize,cell.isBold);
		}
		return lineCnt;
	}
	
	
	/**
	 * Permet de calculer le nombre de lignes d'un texte 
	 * @param isBold 
	 * 
	 */
	private int getLineCount(String cellValue,int cellWidth,String fontName,int fontSize, boolean isBold)
	{
		if (cellValue.length()==0)
		{
			return 1;
		}
		
		// Create Font object with Font attribute (e.g. Font family, Font size, etc)
		java.awt.Font currFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, fontSize);
		
		
		AttributedString attrStr = new AttributedString(cellValue);
		attrStr.addAttribute(TextAttribute.FONT, currFont);
		if (isBold)
		{
			attrStr.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}

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
		
		/*System.out.println("CellValue="+cellValue);
		System.out.println("CellWidht="+cellWidth);
		System.out.println("Font="+fontName);
		System.out.println("Size="+fontSize);
		
		System.out.println("Line conunt="+lineCnt);*/
		
		return lineCnt;
	}



}
