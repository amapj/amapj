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
 package fr.amapj.service.engine.generator.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DeleteOnCloseFileInputStream;
import fr.amapj.common.RuntimeUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.service.engine.generator.CoreGeneratorService;
import fr.amapj.view.engine.ui.AppConfiguration;
import fr.amapj.view.engine.ui.AppTempConfiguration;


/**
 * Permet la gestion des extractions au format PDF
 * 
 *  
 *
 */
abstract public class AbstractPdfGenerator implements CoreGenerator
{
	
	private final static Logger logger = LogManager.getLogger();
	
	/**
	 * Permet de générer le fichier Excel pour un modele de contrat
	 * @return
	 */
	abstract public void fillPdfFile(EntityManager em,PdfGeneratorTool et);
	
	abstract public String getFileName(EntityManager em);
	
	abstract public String getNameToDisplay(EntityManager em);
	
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
		return "pdf";
	}
	
	/**
	 * Cette méthode doit être utilisée pour les tests unitaires uniquement
	 * Retourne le contenu HTML du document 
	 */
	public String getHtmlContentForTest()
	{
		PdfGeneratorTool pdfGeneratorTool = new CoreGeneratorService().getFichierPdf(this);
		String html = pdfGeneratorTool.getFinalDoc();
		return html;
	}
	
	@Override
	public InputStream getContent()
	{
		boolean deleteFileOnClose = AppTempConfiguration.getTempConf().getEffacerFichierTempPDF()==ChoixOuiNon.OUI;
		
		PdfGeneratorTool pdfGeneratorTool = new CoreGeneratorService().getFichierPdf(this);
		String html = pdfGeneratorTool.getFinalDoc();
		
		String addCmdLine = pdfGeneratorTool.getParameterForCommandLine();
		
		return convertHtmlToPdf(html,addCmdLine,deleteFileOnClose);
	}
	
	
	static public InputStream convertHtmlToPdf(String html, String addCmdLine, boolean deleteFileOnClose)
	{
		try
		{
			File in = File.createTempFile("inpdf", ".html"); 
			File out = File.createTempFile("outpdf", ".pdf");
			
			FileOutputStream fos = new FileOutputStream(in);
			fos.write(html.getBytes());
			fos.flush();
			fos.close();
						
			String wkhtmltopdfCommand = AppConfiguration.getConf().getWkhtmltopdfCommand();
			if (wkhtmltopdfCommand==null)
			{
				throw new AmapjRuntimeException("Impossible de generer le fichier PDF car  wkhtmltopdf n'est pas installé ou son chemin d'accès n'est pas configuré");
			}
			
			
			String fullCommand = wkhtmltopdfCommand+" "+addCmdLine+" "+in.getCanonicalPath()+" "+out.getCanonicalPath();
			
			logger.info("Lancement de la commande="+fullCommand);
			
			RuntimeUtils.executeCommandLine(fullCommand, 50000);
			
			// On efface tout de suite le fichier HTML
			if (deleteFileOnClose)
			{
				in.delete();
			}
			
			InputStream res = new DeleteOnCloseFileInputStream(out, deleteFileOnClose);
			
			return res;
			
		} 
		catch (IOException | InterruptedException | TimeoutException e)
		{
			throw new AmapjRuntimeException(e);
		}
	}
	
	
	
	public void test() throws Exception
	{
		TestTools.init();
		
		String filename = "test.pdf";
		
		FileOutputStream fos = new FileOutputStream(filename);
		IOUtils.copy(getContent(), fos);
		fos.close();
		

		System.out.println("Your pdf file has been generated!");
	}
	
	
}
