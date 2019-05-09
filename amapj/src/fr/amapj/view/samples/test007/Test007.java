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
 package fr.amapj.view.samples.test007;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.samples.VaadinTest;

/**
 * Upload de fichier
 * 
 *  
 * 
 */
public class Test007 implements VaadinTest, Receiver, SucceededListener
{
	public File file;

	public OutputStream receiveUpload(String filename, String mimeType)
	{
		// Create upload stream
		FileOutputStream fos = null; // Stream to write to
		try
		{
			// Open the file for writing.
			file = new File("/home/amapj/uploads/" + filename);
			fos = new FileOutputStream(file);
		} 
		catch (final java.io.FileNotFoundException e)
		{
			new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		}
		return fos; // Return the output stream to write to
	}

	public void uploadSucceeded(SucceededEvent event)
	{
		new Notification("Fichier chargé",  Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
	}

	public void buildView(VaadinRequest request, UI ui)
	{
		VerticalLayout layout = new VerticalLayout();

		// Create the upload with a caption and set receiver later
		Upload upload = new Upload("", this);
		upload.addSucceededListener(this);

		layout.addComponent(new Label("Bonjour Merci de choisir votre fichier, puis de cliquer sur le bouton upload pour l'envoyer"));
		layout.addComponent(upload);

		ui.setContent(layout);

	}
}
