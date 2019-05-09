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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFConditionalFormatting;
import org.apache.poi.hssf.usermodel.HSSFConditionalFormattingRule;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSheetConditionalFormatting;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;


/**
 * Recherche pour la mise en place d'une diagonale 
 *
 */
public class DiagonalBorder
{
	public static void main(String[] args) throws Exception
	{
		DiagonalBorder diagonalBorder = new DiagonalBorder();
		diagonalBorder.execute();
	}
	
	/**
	 * Le code pour la mise en place d'une diagonale a ete faite par reverse
	 * 
	 *  Le detail des operations est ci dessous 
	 *   
	 */
	private void execute() throws Exception
	{
		// 1 - Generation d'un fichier basique 
		//generateBasicFile();
		
		// 2 - Modification du fichier à la main avec Excel ou Libre Office 
		// Do it !! 
		
		// 3 - Relecture du fichier pour voir le contenu des cases 
		// readExcelFile();
		
		// 4 - Generation d'un fichier excel avec des cases barrées 
		generateFileWithDiagonal();
		
	}


	/**
	 * Permet la generation d'un fichier basique avec 3 lignes et 3 colonnes
	 */
	private void generateBasicFile() throws IOException
	{
		HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet =  workbook.createSheet("Amap");  
         
        HSSFCellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        
        addRow(sheet,style, (short) 0);
        addRow(sheet,style, (short) 1);
        addRow(sheet,style, (short) 2);    
        
        
        FileOutputStream fos = new FileOutputStream("test1.xls");
        workbook.write(fos);
        fos.flush();
        fos.close();
        
        System.out.println("OK !");
        
	}

	private HSSFRow addRow(HSSFSheet sheet, HSSFCellStyle style, short rowNumber)
	{
        //
        HSSFRow row =   sheet.createRow(rowNumber);   
      
    
        HSSFCell c = row.createCell(0);
        c.setCellStyle(style);
        c.setCellValue(4.0);
        
        c = row.createCell(1);
        c.setCellStyle(style);
        c.setCellValue(4.0);
        
        c = row.createCell(2);
        c.setCellStyle(style);
        c.setCellValue("");
        
        return row;
	}
		
	/**
	 * Permet la lecture d'un fichier excel et l'affichage des propriétés de style 
	 */
	
	private void readExcelFile() throws Exception
	{
		 
		
		FileInputStream file = new FileInputStream(new File("test1.xls"));

		HSSFWorkbook workbook = new HSSFWorkbook(file);

		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		// Iterate through each rows from first sheet
		Iterator<Row> rowIterator = sheet.iterator();
        
		int i=1;
		while (rowIterator.hasNext())
		{

			Row row = rowIterator.next();
			
			Cell cell = row.getCell(0);
			
			System.out.println("Affichage des infos de la cellule 1 de la ligne "+i);
			displayCell(cell);
			
			i++;
		}

		file.close();
	}

	private void displayCell(Cell cell) throws Exception
	{
		
		CellStyle style = cell.getCellStyle();
		
		Field f = HSSFCellStyle.class.getDeclaredField("_format");
	    f.setAccessible(true);
	        
	        
	    ExtendedFormatRecord efr = (ExtendedFormatRecord) f.get(style);
	       
	    System.out.println("getAdtlDiag="+efr.getAdtlDiag());
	    System.out.println("getDiag="+efr.getDiag());
	    System.out.println("getAdtlDiagLineStyle="+efr.getAdtlDiagLineStyle());
	    System.out.println("isIndentNotParentBorder="+efr.isIndentNotParentBorder());
	}
        
	
	
	/**
	 * Permet la generation d'un fichier basique avec 3 lignes et 3 colonnes
	 */
	private void generateFileWithDiagonal() throws Exception
	{
		HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet =  workbook.createSheet("Amap");  
         
        HSSFCellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        
        
        Field f = HSSFCellStyle.class.getDeclaredField("_format");
        f.setAccessible(true);
        
        
        ExtendedFormatRecord efr = (ExtendedFormatRecord) f.get(style);
        
        efr.setIndentNotParentBorder(true);
        efr.setDiag((short)3);
        
        // 8 et 64 semble marcher de facon identique  
        efr.setAdtlDiag((short) 64);
        efr.setAdtlDiagLineStyle((short) 1);
        
        
        addRow(sheet,style, (short) 0);
        addRow(sheet,style, (short) 1);
        addRow(sheet,style, (short) 2);    
        
        
        FileOutputStream fos = new FileOutputStream("test2.xls");
        workbook.write(fos);
        fos.flush();
        fos.close();
        
        System.out.println("OK !");
        
       
        
	}
        
     
}
