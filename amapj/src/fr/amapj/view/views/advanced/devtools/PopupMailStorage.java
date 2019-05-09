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

import java.io.IOException;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.services.mailer.MailerStorage;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de lister les mails du storage
 * 
 *
 */
public class PopupMailStorage extends FormPopup
{
	private ComplexTableBuilder<Message> builder;
	
	/**
	 * 
	 */
	public PopupMailStorage()
	{
		setWidth(80);	
		popupTitle = "Listes des mails envoyés dans le storage";
	}
	
	@Override
	protected void addFields()
	{		
		List<Message> messages = MailerStorage.getAllMessages();
		
		
		builder = new ComplexTableBuilder<Message>(messages);
		builder.setPageLength(7);
		
		builder.addString("Envoyé à", false, 300, e->getSendTo(e));
		builder.addString("Titre", false, 300,  e->getSubject(e));
		builder.addButton("Corps du message", 150, e->"Voir", e->openCorpsMessage(e));
		builder.addButton("Liste des pièces jointes", 150, e->getNbPiecesJointes(e), e->openPieceJointe(e));
		
		
		
		addComplexTable(builder);
	}



	private void openPieceJointe(Message message)
	{
		PopupAttachementDisplay.open(new PopupAttachementDisplay(message));
	}

	private String getNbPiecesJointes(Message e)
	{
		try
		{
			Multipart mp = (Multipart) e.getContent();
			int count =   mp.getCount();
			
			if (count==1)
			{
				return "Pas de pièce jointe";
			}
			else
			{
				return "Voir les "+(count-1)+" pièces";
			}
		} 
		catch (IOException | MessagingException e1)
		{
			throw new AmapjRuntimeException(e1);
		}
	}

	private void openCorpsMessage(Message e)
	{
		try
		{
			Multipart mp = (Multipart) e.getContent();
			MimeBodyPart part = (MimeBodyPart) mp.getBodyPart(0);
			String html = (String) part.getContent();
			
			MessagePopup.open(new MessagePopup("Corps du message",ColorStyle.GREEN,html));
		} 
		catch (IOException | MessagingException e1)
		{
			throw new AmapjRuntimeException(e1);
		}
	}

	private Object getSubject(Message e)
	{
		try
		{
			return e.getSubject();
		} 
		catch (MessagingException e1)
		{
			return "error"+e1.getMessage();
		}
	}

	private String getSendTo(Message e)
	{
		try
		{
			String str ="";
			Address[] addrs = e.getAllRecipients();
			for (int i = 0; i < addrs.length; i++)
			{
				str = str+addrs[i].toString()+";";
			}
			return str;
		} 
		catch (MessagingException e1)
		{
			return "error"+e1.getMessage();
		}
	}

	@Override
	protected void performSauvegarder() throws OnSaveException
	{	
		// Nothing to do 
	}
}
