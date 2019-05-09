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
import fr.amapj.model.models.editionspe.PageFormat;
import fr.amapj.service.engine.generator.MiseEnPageUtils;


/**
 * Permet la gestion du HTML servant à generer les PDF 
 * 
 */
public class PdfHtmlUtils
{

	/**
	 * Permet d'extraire le contenu du body depuis une page HTML complete
	 */
	static public String extractBody(String content)
	{  
		int startBody1 = content.indexOf("<body");
		int endBody1 = content.indexOf(">",startBody1);
		
		int startBody2 = content.indexOf("</body>");
		
		return content.substring(endBody1+1, startBody2);	
	}	
	
	
	/**
	 * 
	 * @param text
	 * @param imprimable
	 * @return
	 */
	public static String updateHeaderAndBodyLineForCKEditor(String text,Imprimable imprimable)
	{
		String body = extractBody(text);
		return generateHeaderAndBodyLineForCKEditor(imprimable)+body+"</body></html>";
	}
	
	
	static public String generateHeaderAndBodyLineForCKEditor(Imprimable imprimable)
	{
		// Dans les documents PDF, tout est géré en points 
		
		// On ne met que les marges droite et gauche
		int marginLeft = MiseEnPageUtils.toPoints(imprimable.getMargeGauche());
		int marginRight = MiseEnPageUtils.toPoints(imprimable.getMargeDroite());
		
		int fullWidthInMm = imprimable.getPageFormat()==PageFormat.A4_PORTRAIT ? 210 : 297;
		int widthInMm = fullWidthInMm-imprimable.getMargeGauche()-imprimable.getMargeDroite();
		int width = MiseEnPageUtils.toPoints(widthInMm);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<!DOCTYPE html>");
		buf.append("<html>");
		buf.append("<head>");
		buf.append("<meta charset=\"utf-8\">");
		buf.append("<title></title>");
		buf.append("</head>");
		buf.append("<body style=\"width:"+width+"pt;margin-right:"+marginRight+"pt;margin-left:"+marginLeft+"pt;font-family:sans-serif;font-size:10pt;\">");
		
		return buf.toString();
	}

	
	
	public static void main(String[] args)
	{
		System.out.println("debut");
		String content = "<!DOCTYPE html>"+
						"<html>"+
						"<head>"+
						"<meta charset=\"utf-8\"/>"+
						"<title></title>"+
						"</head>"+
						"<body style=\"width:538pt; margin:28pt;font-family:liberation sans;font-size:10pt;\">"+
						"<p>CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE CECI EST UN TEXTE</p>"+
						"<p></p>"+
						"</body>"+
						"</html>";
		
		String res = PdfHtmlUtils.extractBody(content);
		System.out.println(res);
	}



}
