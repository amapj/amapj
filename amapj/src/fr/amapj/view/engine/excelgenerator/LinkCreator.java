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
 package fr.amapj.view.engine.excelgenerator;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Link;

import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.service.engine.generator.CoreGeneratorService;
import fr.amapj.service.engine.generator.FileInfoDTO;

/**
 * Outil pour créer facilement des liens 
 *  
 */
public class LinkCreator 
{
	
	static public Link createLink(CoreGenerator generator)
	{
		return createLink(generator, true);
	}
	
	
	/**
	 * 
	 * @param generator
	 * @param addPrefixTelecharger : si true, on ajoute le libellé "Telecharger" devant le nom du fichier
	 * @return
	 */
	static public Link createLink(CoreGenerator generator,boolean addPrefixTelecharger)
	{
		FileInfoDTO fileInfoDTO = new CoreGeneratorService().getFileInfo(generator);
		
		String titre = fileInfoDTO.nameToDisplay;
		String fileName = fileInfoDTO.fileName;
		String extension = fileInfoDTO.extension;
		
		StreamResource streamResource = new StreamResource(new CoreResource(fileInfoDTO.generator), fileName+"."+extension);
		streamResource.setCacheTime(1000);
		
		
		String lien = addPrefixTelecharger ? "Télécharger "+titre : titre;
		Link extractFile = new Link(lien,streamResource);
		extractFile.setIcon(FontAwesome.DOWNLOAD);
		extractFile.setTargetName("_blank");
		
		return extractFile;
	}
	
}
