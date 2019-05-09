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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.service.engine.generator.CoreGeneratorService;
import fr.amapj.service.engine.generator.FileInfoDTO;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup de telechargement
 *  
 */
@SuppressWarnings("serial")
public class TelechargerPopup extends CorePopup
{
	
	static private class Item
	{
		public CoreGenerator generator;
		public String label;
	}
	
	private List<Item> items = new ArrayList<>();

	
	public TelechargerPopup(String popupTitle)
	{	
		this.popupTitle = popupTitle;
	}
	
	/**
	 * 
	 * @param popupTitle
	 * @param width en pourcentage 
	 */
	public TelechargerPopup(String popupTitle,int width)
	{	
		this.popupTitle = popupTitle;
		setWidth(width);
	}
	
	
	public void addGenerator(CoreGenerator generator)
	{
		Item item = new Item();
		item.generator = generator;
		items.add(item);
	}
	
	public void addSeparator()
	{
		addLabel("----");
	}
	
	public void addLabel(String label)
	{
		Item item = new Item();
		item.label = label;
		items.add(item);
	}
	
	

	protected void createContent(VerticalLayout contentLayout)
	{
		
		contentLayout.addStyleName("popup-telecharger");
		
		Label l = new Label("Veuillez cliquer sur le lien du fichier que vous souhaitez télécharger");
		l.addStyleName("titre");
		contentLayout.addComponent(l);
		
		// Calcul des liens
		List<CoreGenerator> gs = new ArrayList<CoreGenerator>();
		for (Item item : items)
		{
			if (item.generator!=null)
			{
				gs.add(item.generator);
			}
		}
		List<FileInfoDTO> fileInfoDTOs = new CoreGeneratorService().getFileInfo(gs);
		
		// Affichage 
		int i=0;
		for (Item item : items)
		{
			if (item.generator!=null)
			{
				FileInfoDTO fileInfoDTO = fileInfoDTOs.get(i);
				addLink(contentLayout,fileInfoDTO);
				i++;
			}
			else
			{
				Label lab = new Label(item.label,ContentMode.HTML);
				lab.addStyleName("separateur");
				contentLayout.addComponent(lab);
			}	
		}
	}
	
	private void addLink(VerticalLayout contentLayout, FileInfoDTO fileInfoDTO)
	{
		String titre = fileInfoDTO.nameToDisplay;
		String fileName = fileInfoDTO.fileName;
		String extension = fileInfoDTO.extension;
		
		StreamResource streamResource = new StreamResource(new CoreResource(fileInfoDTO.generator), fileName+"."+extension);
		streamResource.setCacheTime(1000);
		
		Link extractFile = new Link(titre,streamResource);
		extractFile.setIcon(FontAwesome.DOWNLOAD);
		extractFile.setTargetName("_blank");
		
		contentLayout.addComponent(extractFile);
	}

	protected void createButtonBar()
	{		
		addButton("Quitter", new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleAnnuler();
			}
		});
	}
	

	protected void handleAnnuler()
	{
		close();
	}
	
}
