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

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.ui.AppConfiguration;

/**
 * Permet d'envoyer des mails
 * 
 *
 */
public class MailerService
{
	private final static Logger logger = LogManager.getLogger();


	public MailerService()
	{

	}
	
	private Message initMail(ParametresDTO param, String recipient, String subject) throws AddressException, MessagingException, UnsupportedEncodingException
	{
		
		
		Session session;
		switch (param.smtpType)
		{
		case GMAIL:
			session = getGmailSession(param);
			break;
		
		case POSTFIX_LOCAL :
			session = getPostfixLocalSession(param);
			break;

		default:
			throw new AmapjRuntimeException();
		}
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(param.sendingMailUsername,param.nomAmap));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		if ( (param.mailCopyTo!=null) && (param.mailCopyTo.length()>0))
		{
			message.addRecipient(Message.RecipientType.BCC, InternetAddress.parse(param.mailCopyTo)[0]);
			message.setReplyTo(InternetAddress.parse(param.mailCopyTo));
		}
		message.setSubject(subject);
		
		return message;
	}
	
	
	
	private Session getPostfixLocalSession(ParametresDTO param)
	{
		final String username = param.getSendingMailUsername();
		
		if ( (username==null) || (username.length()==0))
		{
			throw new AmapjRuntimeException("Le service mail n'est pas paramétré : absence du nom de l'utilisateur");
		}
		
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "127.0.0.1");
		Session	session = Session.getInstance(props);

		return session;
	}

	private Session getGmailSession(ParametresDTO param)
	{
		final String username = param.getSendingMailUsername();
		final String password = param.getSendingMailPassword();
		
		if ( (username==null) || (username.length()==0))
		{
			throw new AmapjRuntimeException("Le service mail n'est pas paramétré correctement: absence du nom de l'utilisateur");
		}
		
		if ( (password==null) || (password.length()==0))
		{
			throw new AmapjRuntimeException("Le service mail n'est pas paramétré correctement: absence du mot de passe");
		}
		
		Properties props = new Properties();
		
	
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
			
		Session session = Session.getInstance(props, new javax.mail.Authenticator()
			{
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(username, password);
				}
			});
		
		return session;
	}

	/**
	 *  Envoi d'un mail 
	 */
	public void sendHtmlMail(MailerMessage mailerMessage)
	{
		ParametresDTO param = new ParametresService().getParametres();
		
		if (MailerCounter.isAllowed(param)==false)	
		{
			throw new AmapjRuntimeException("Impossible d'envoyer un mail car le quota par jour est dépassé (quota = "+param.sendingMailNbMax+" )");
		}
		
		try
		{
			Message message = initMail(param,mailerMessage.getEmail(), mailerMessage.getTitle());
			
			Multipart mp = new MimeMultipart();

	        MimeBodyPart htmlPart = new MimeBodyPart();
	        htmlPart.setText(mailerMessage.getContent(), "UTF-8", "html");
	        mp.addBodyPart(htmlPart);

	        for (MailerAttachement attachement : mailerMessage.getAttachements())
			{
		        MimeBodyPart attachment = new MimeBodyPart();
		        attachment.setFileName(attachement.getName());
		        attachment.setDataHandler(new DataHandler(attachement.getDataSource()));
		        mp.addBodyPart(attachment);
	        }
			
			message.setContent(mp);
			
			if (AppConfiguration.getConf().isAllowMailControl()==false)
			{
				// Le cas standard : le mail est envoyé 
				Transport.send(message);
				logger.info("Envoi d'un message a : "+mailerMessage.getEmail());
			}
			else
			{
				// Le cas debug : le mail est stocké 
				MailerStorage.store(message);
				logger.info("STOCKAGE d'un message destiné a : "+mailerMessage.getEmail());
			}
		}
		catch (MessagingException | UnsupportedEncodingException e)
		{
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	
	
	public static void main(String[] args)
	{
		TestTools.init();
		
		MailerMessage message = new MailerMessage("essai@gmail.com", "essai", "<h1>This is actual message</h1>");
		
		new MailerService().sendHtmlMail(message);
	}
}
