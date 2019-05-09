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
 package fr.amapj.view.views.advanced.devtools;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Link;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;

/**
 * Permet d'afficher les pièces jointes d'un mail
 * 
 *
 */
public class PopupAttachementDisplay extends FormPopup
{

	
	private Message message;

	/**
	 * 
	 */
	public PopupAttachementDisplay(Message message)
	{
		this.message = message;
		setWidth(80);	
		popupTitle = "Listes des pièces jointes";
	}
	
	@Override
	protected void addFields()
	{		
		try
		{
			Multipart mp = (Multipart) message.getContent();
			int count = mp.getCount();
			if (count==1)
			{
				addLabel("Pas de pièces jointes", ContentMode.HTML);
				return;
			}
			
			
			for (int i = 1; i < count; i++)
			{
				MimeBodyPart part = (MimeBodyPart) mp.getBodyPart(i);
				String fileName = part.getFileName();
				Object content = part.getContent();
				
				if (content instanceof ByteArrayInputStream)
				{
					Link l = createLink(i, (ByteArrayInputStream) content,fileName);
					form.addComponent(l);
				}
				else
				{
					addLabel("Format de la pièce "+i+" inconnu FileName ="+fileName, ContentMode.HTML);
				}
				
				
			}
			
			
		} 
		catch (IOException | MessagingException e1)
		{
			throw new AmapjRuntimeException(e1);
		}
	}

	public Link createLink(int pieceNumber,ByteArrayInputStream bais,String fileName)
	{
		 StreamResource streamResource = new StreamResource(()->bais, fileName);
		 streamResource.setCacheTime(1000);
	
		 Link extractFile = new Link("Pièce "+pieceNumber+" - "+fileName,streamResource);
		 extractFile.setIcon(FontAwesome.DOWNLOAD);
		 extractFile.setTargetName("_blank");
	
		 return extractFile;
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{	
		// Nothing to do 
	}
}
