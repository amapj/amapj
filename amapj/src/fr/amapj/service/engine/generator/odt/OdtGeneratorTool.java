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
 package fr.amapj.service.engine.generator.odt;

import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.odftoolkit.simple.text.list.ListItem;
import org.odftoolkit.simple.text.list.ListDecorator.ListType;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.StringUtils;

/**
 * Permet la génération facile des fichiers PDF
 * 
 */
public class OdtGeneratorTool
{
	TextDocument doc;

	public OdtGeneratorTool()
	{
		try
		{
			doc = TextDocument.newTextDocument();
			
			
			// TODO voir ce qui il y a la dedans 
			// http://stackoverflow.com/questions/18108452/how-can-the-page-size-page-orientation-and-page-margins-of-an-ods-spreadsheet
			doc.getOfficeMasterStyles().getMasterPages();
		} 
		catch (Exception e)
		{
			throw new AmapjRuntimeException(e);
		}

	
	}

	/**
	 * Permet l'ajout d'un saut de page
	 */
	public void addSautPage()
	{
		// TODO document.getPageNumber()

		// document.a

	}

	public void addWikiContent(String wikiInput)
	{
		java.util.List<String> lines = StringUtils.asList(wikiInput);

		for (String line : lines)
		{
			processLine(line);
		}
	}

	static private final String TITRE1 = "<titre1>";
	static private final String TITRE2 = "<titre2>";
	static private final String CHAPITRE = "<chapitre>";
	static private final String TIRET = "-";

	private void processLine(String line)
	{
		if (line.startsWith(TITRE1))
		{
			line = line.substring(TITRE1.length());
			addTitre1(line);
		} 
		else if (line.startsWith(TITRE2))
		{
			line = line.substring(TITRE1.length());
			addTitre2(line);
		} 
		else if (line.startsWith(CHAPITRE))
		{
			line = line.substring(CHAPITRE.length());
			addChapitre(line);
		} 
		else if (line.startsWith(TIRET))
		{
			line = line.substring(TIRET.length());
			addBullet(line);
		}
		else
		{
			addStandardText(line);
		}

	}

	// Le titre du document
	public void addTitre1(String titre)
	{
		add(titre, HorizontalAlignmentType.CENTER, true, 14);
	}

	// Le sous titre du document
	public void addTitre2(String titre)
	{
		add(titre, HorizontalAlignmentType.CENTER, false, 12);
	}

	// Le titre d'un chapitre
	public void addChapitre(String titre)
	{
		add(titre, HorizontalAlignmentType.LEFT, true, 12);
	}

	// Le texte standard
	public void addStandardText(String titre)
	{
		add(titre, HorizontalAlignmentType.LEFT, false, 10);
	}
	
	// Une liste de bulles
	public void addBullet(String titre)
	{
		List list = doc.addList(new AmapjBulletDecorator(doc));
		String[] items = {titre};
		list.addItems(items);
		
	}



	private Paragraph add(String content, HorizontalAlignmentType align, boolean bold, int fontSize)
	{
		Paragraph paragraph = doc.addParagraph(content);
		
		paragraph.setHorizontalAlignment(align);
		FontStyle fontStyle = bold ? FontStyle.BOLD : FontStyle.REGULAR;
		paragraph.setFont(new Font("Arial", fontStyle, fontSize));
		
		return paragraph;
	}

	public TextDocument getDoc()
	{
		return doc;
	}

}
