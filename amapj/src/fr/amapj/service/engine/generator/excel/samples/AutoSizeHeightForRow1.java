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

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * La mise à la hauteur automatique des lignes est un problème complexe avec 
 * apache poi
 * 
 * Voici un premier exemple fonctionnel, dans un cas de base 
 * 
 * Exemple extrait de 
 * http://stackoverflow.com/questions/19145628/auto-size-height-for-rows-in-apache-poi
 *
 */
public class AutoSizeHeightForRow1
{
	public static void main(String[] args) throws IOException
	{
		HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet =  workbook.createSheet("Amap");  
        
        // sheet.autoSizeColumn(0);
        
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

	private static HSSFRow addRow(HSSFSheet sheet, HSSFCellStyle style, short rowNumber)
	{
        //
        HSSFRow row =   sheet.createRow(rowNumber);   
        // row.setRowStyle(style);     -- has no effect
        // row.setHeight((short)-1);   -- has no effect
    
        HSSFCell c = row.createCell(0);
        c.setCellStyle(style);
        c.setCellValue("x");
        
        c = row.createCell(1);
        c.setCellStyle(style);
        c.setCellValue( "a\nb\nc");
        
        c = row.createCell(2);
        c.setCellStyle(style);
        c.setCellValue( "y");
            
        return row;
        
	}
}
