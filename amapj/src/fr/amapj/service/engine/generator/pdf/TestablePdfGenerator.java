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

import javax.persistence.EntityManager;

import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.AbstractPdfEditionSpeJson;


/**
 * Permet des realiser des generator PDF avec un mode test intégré 
 * 
 */
abstract public class TestablePdfGenerator extends AbstractPdfGenerator
{
	
	abstract public void fillPdfFile(EntityManager em, PdfGeneratorTool et, String htmlContent);
	
	abstract public String readDataInTestMode(EntityManager em,AbstractEditionSpeJson forTest);
	
	abstract public AbstractPdfEditionSpeJson getEditionInNormalMode(EntityManager em);
	
	abstract public String getFileNameStandard(EntityManager em);

	abstract public String getNameToDisplayStandard(EntityManager em);
	
	
		
	public  AbstractPdfEditionSpeJson forTest;
	
	/**
	 * 
	 */
	public TestablePdfGenerator(AbstractPdfEditionSpeJson forTest)
	{
		this.forTest = forTest;	
	}
	
	@Override
	final public void fillPdfFile(EntityManager em,PdfGeneratorTool et)
	{
		Data d = computeEngagementAndHtml(em);
		
		// Demarrage du document 
		String htmlContent = PdfHtmlUtils.extractBody(d.html);
		et.startDocument(d.engagement);
		
		// Gestion des erreurs eventuelles
		if (d.error!=null)
		{
			et.addContent(d.error);
			return;
		}
		
		fillPdfFile(em,et,htmlContent);
	}
	


	private class Data
	{
		public AbstractPdfEditionSpeJson engagement;
		public String html;
		public String error = null;
	}
	
	
	/**
	 * Permet de distinguer le mode normal du mode de test
	 * 
	 * @param em
	 * @return
	 */
	private Data computeEngagementAndHtml(EntityManager em)
	{
		// Si mode test
		if (forTest!=null)
		{
			Data d = new Data();
			d.engagement = forTest;
			d.html = forTest.getText(); 
			d.error = readDataInTestMode(em,forTest);
			return d;
		}
		
		// Si mode normal 
		AbstractPdfEditionSpeJson engagement = getEditionInNormalMode(em);
		
		Data d = new Data();
		d.engagement = engagement;
		d.html = engagement.getText(); 
		
		
		return d;
	}



	@Override
	final public String getFileName(EntityManager em)
	{
		if (forTest!=null)
		{
			return "test";
		}
		
		return getFileNameStandard(em);
		
	}

	@Override
	final public String getNameToDisplay(EntityManager em)
	{
		if (forTest!=null)
		{
			return "test";
		}
		
		return getNameToDisplayStandard(em);
	}

	
}
