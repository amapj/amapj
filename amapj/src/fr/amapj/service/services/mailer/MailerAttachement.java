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
 package fr.amapj.service.services.mailer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.engine.generator.CoreGeneratorService;
import fr.amapj.service.engine.generator.FileInfoDTO;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;

/**
 * Permet de stocker une piece jointe 
 * 
 *
 */
public class MailerAttachement
{

	private DataSource dataSource;
	
	private String name;
	
	
	public MailerAttachement()
	{
		
	}
	
	/**
	 * Permet de construire une pièce jointe à partir d'un fichier local
	 */
	public MailerAttachement(File file)
	{
		dataSource = new FileDataSource(file);
		name = file.getName();	
	}
	
	/**
	 * Permet de construire une pièce jointe à partir d'un fichier excel 
	 */	
	public MailerAttachement(AbstractExcelGenerator generator)
	{
		CoreGeneratorService excelGeneratorService = new CoreGeneratorService();
		
		FileInfoDTO fileInfo = excelGeneratorService.getFileInfo(generator);
		InputStream content = generator.getContent();

		try
		{
			dataSource = new ByteArrayDataSource(content, "application/vnd.ms-excel");
		} 
		catch (IOException e)
		{
			throw new AmapjRuntimeException("",e);
		}
		name = fileInfo.fileName+"."+fileInfo.extension;
	}
	
	
	
	public DataSource getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	
	
	
	
	
	
}
