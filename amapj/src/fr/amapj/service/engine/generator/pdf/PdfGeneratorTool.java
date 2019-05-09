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

import fr.amapj.model.models.editionspe.Imprimable;


/**
 * Permet la génération facile des fichiers PDF/HTML 
 * 
 */
public class PdfGeneratorTool
{
	//
	private StringBuffer buf = null;
	
	
	//
	public PdfGeneratorTool()
	{
		
	}
	

	public void startDocument(Imprimable imprimable)
	{
		buf = new StringBuffer();
		buf.append(PdfHtmlUtils.generateHeaderAndBodyLineForCKEditor(imprimable));
	}

	/**
	 * Permet l'ajout d'un saut de page
	 */
	public void addSautPage()
	{		
		buf.append("<div style=\"page-break-after: always\"><span style=\"display:none\">&nbsp;</span></div>");

	}
	
	/**
	 * Permet d'ajouter du contenu html
	 * 
	 * Ne doit pas contenir les tag <html> ou <body>
	 *   
	 * @param content
	 */
	public void addContent(String content)
	{  
		buf.append(content);
	}
	
	
	/**
	 * Ferme le document et retourne son contenu 
	 * @return
	 */
	public String getFinalDoc()
	{
		buf.append("</body>");
		buf.append("</html>");
		return buf.toString();
	}
	
	
	public String getParameterForCommandLine()
	{
		return " --disable-smart-shrinking -T 0 -B 0 -L 0 -R 0 ";
	}
	
}
