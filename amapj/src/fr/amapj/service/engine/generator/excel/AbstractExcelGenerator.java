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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.Workbook;

import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.service.engine.generator.CoreGeneratorService;


/**
 * Permet la gestion des extractions excels
 * 
 *  
 *
 */
abstract public class AbstractExcelGenerator implements CoreGenerator
{
	
	
	/**
	 * Permet de générer le fichier Excel pour un modele de contrat
	 * @return
	 */
	abstract public void fillExcelFile(EntityManager em,ExcelGeneratorTool et);
	
	abstract public String getFileName(EntityManager em);
	
	abstract public String getNameToDisplay(EntityManager em);
	
	abstract public ExcelFormat getFormat();
	
	
	private String nameToDisplaySuffix;
	
	/**
	 * Permet de positionner un suffixe au file name, par exemple pour préciser son format
	 * Ce suffixe est à la fin du nom de fichier, mais avant l'extension 
	 */
	public void setNameToDisplaySuffix(String nameToDisplaySuffix)
	{
		this.nameToDisplaySuffix = nameToDisplaySuffix;
	}	
	
	@Override
	public String getNameToDisplaySuffix()
	{
		return nameToDisplaySuffix;
	}
	



	public String getExtension()
	{
		return getFormat().toString().toLowerCase();
	}
	
	
	@Override
	public InputStream getContent()
	{
		
		Workbook workbook = new CoreGeneratorService().getFichierExcel(this);
		
		ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
	
		try
		{
			workbook.write(imagebuffer);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Erreur inattendue");
		}
		return new ByteArrayInputStream(imagebuffer.toByteArray());
	}
	
	
	public void test() throws IOException
	{
		TestTools.init();
		
		String filename = "test."+this.getFormat().name().toLowerCase();
		Workbook workbook = new CoreGeneratorService().getFichierExcel(this); 
		
		FileOutputStream fileOut = new FileOutputStream(filename);
		workbook.write(fileOut);
		fileOut.close();
		System.out.println("Your excel file has been generated!");
	}
	
	/**
	 * Pour les tests unitaires 
	 * 
	 */
	public UnitTestInfo unitTest() throws IOException
	{
		UnitTestInfo res = new UnitTestInfo();
		
		res.fileExtension = this.getFormat().name().toLowerCase();
		Workbook workbook = new CoreGeneratorService().getFichierExcel(this); 
		
		res.baos = new ByteArrayOutputStream();
		workbook.write(res.baos);
		
		return res;
	}
	
	
	public class UnitTestInfo
	{
		public ByteArrayOutputStream baos;
		public String fileExtension;
	}
}
