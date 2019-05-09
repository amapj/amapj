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
 package fr.amapj.service.engine.generator.excel.samples;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Row;

import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelCellAutoSize;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;



/**
 * Permet de generer un fichier pour tester le fon fonctionnement du calcul 
 * de la hauteur de ligne
 * 
 */
public class EGTestAutoSize extends AbstractExcelGenerator
{
	
	int from;
	int to;
	
	public EGTestAutoSize(int from,int to)
	{
		this.from = from;
		this.to = to;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		
		for (int i = from; i <=to; i++)
		{
			addOneSheet(i,et);
		}
	}

	private void addOneSheet(int largeur, ExcelGeneratorTool et)
	{
		
		// 
		et.addSheet("Largeur"+largeur, 3, largeur);
				

		// Création de la ligne titre des colonnes
		et.addRow();
		et.setCell(0,"Nom",et.grasCentreBordure);
		et.setCell(1,"Prénom",et.grasCentreBordure);
		et.setCell(2,"Telephone",et.grasCentreBordure);
		
		
		for (int i = 1; i < 4*largeur; i++)
		{
			String cellValue = generate(i);
			addRow(et,cellValue+"1");
		}		
	}

	private String generate(int len)
	{
		StringBuffer buf  = new StringBuffer();
		for (int i = 0; i < len; i++)
		{
			if (i%4==0)
			{
				buf.append('a');
			}
			else if (i%4==1)
			{
				buf.append('b');
			}
			else if (i%4==2)
			{
				buf.append('c');
			}
			else
			{
				buf.append('D');
			}
		}
		
		
		return buf.toString();
	}

	private void addRow( ExcelGeneratorTool et, String cellValue)
	{
		Row currentRow =  et.addRow();
		
		
		et.setCell(0,cellValue,et.nonGrasCentreBordure);
		et.setCell(1,"bob",et.grasCentreBordure);
		et.setCell(2,"0 1",et.grasCentreBordure);
		
		// Sizing 
		ExcelCellAutoSize info = new ExcelCellAutoSize(0);
		info.addCell(et.getColumnWidthInPoints(0), "Arial", 10);
		info.addLine(cellValue);
		info.autosize(currentRow);
		
	}


	@Override
	public String getFileName(EntityManager em)
	{
		return "essai";
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "essai";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	public static void main(String[] args) throws IOException
	{
		new EGTestAutoSize(10,50).test();
	}

}
