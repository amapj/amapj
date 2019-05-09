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

import java.util.ArrayList;
import java.util.List;


/**
 * Permet de stocker un message à envoyer par mail
 * 
 *
 */
public class MailerMessage
{

	// Destinataires du message
	private String email;
	
	// Titre du message
	private String title;
	
	// Contenu du message en html
	private String content;
	
	// Liste des pièces jointes
	private List<MailerAttachement> attachements = new ArrayList<>();

	public MailerMessage()
	{
		
	}
	
	public MailerMessage(String email, String title, String content)
	{
		super();
		this.email = email;
		this.title = title;
		this.content = content;
	}
	
	
	public void addAttachement(MailerAttachement attachement)
	{
		attachements.add(attachement);
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public List<MailerAttachement> getAttachements()
	{
		return attachements;
	}

	public void setAttachements(List<MailerAttachement> attachements)
	{
		this.attachements = attachements;
	}

	
	
}
