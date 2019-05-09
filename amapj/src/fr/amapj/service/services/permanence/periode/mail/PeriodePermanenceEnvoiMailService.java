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
 package fr.amapj.service.services.permanence.periode.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.services.edgenerator.excel.permanence.EGPlanningPermanence;
import fr.amapj.service.services.mailer.MailerAttachement;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;

/**
 * Permet d'envoyer des mails aux participants d'une permanence 
 * 
 */
public class PeriodePermanenceEnvoiMailService
{
	
	
	// PARTIE ENVOI DU PLANNING PAR MAIL
	
	/**
	 * Permet de faire un un envoi du planning  par mail 
	 */
	@DbRead
	public void sendMailAvecPlanning(String texte,Long idPeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();
		
		//
		Date startingDate = DateUtils.getDateWithNoTime();
		
		// Récuperation de tous les utilisateurs ayant une permanence dans le futur
		List<Utilisateur> utilisateurs = getUtilisateursPermanenceFuture(em,startingDate,idPeriodePermanence);
		
		// Calcul du fichier Excel des permanences à venir 
		AbstractExcelGenerator generator = new EGPlanningPermanence(idPeriodePermanence, startingDate);
		MailerAttachement attachement = new MailerAttachement(generator);
		
		// Pour chaque utilisateur, envoi de l'email
		for (Utilisateur utilisateur : utilisateurs)
		{
			sendEmail(em,utilisateur,attachement,texte);
		}
		
	}


	private List<Utilisateur> getUtilisateursPermanenceFuture(EntityManager em, Date startingDate, Long idPeriodePermanence)
	{
		// On recherche tous les utilisateurs qui ont une permanence dans le futur
		Query q = em.createQuery("select distinct(pc.periodePermanenceUtilisateur.utilisateur) from PermanenceCell pc where " +
								" pc.periodePermanenceDate.datePerm>=:d1  and "+
								" pc.periodePermanenceDate.periodePermanence.id=:id  "+
								" order by pc.periodePermanenceUtilisateur.utilisateur.nom , pc.periodePermanenceUtilisateur.utilisateur.prenom");
		q.setParameter("d1", startingDate);
		q.setParameter("id", idPeriodePermanence);
		
		
		List<Utilisateur> us = q.getResultList();
		return us;
	}


	private void sendEmail(EntityManager em, Utilisateur utilisateur, MailerAttachement attachement, String texte)
	{
		ParametresDTO param = new ParametresService().getParametres();
		
		//
		String email=utilisateur.getEmail();
		
		String subject = param.nomAmap+" - Planning des permanences";
		String htmlContent = texte;
		
		// Mise en place des <br/>
		htmlContent = htmlContent.replaceAll("\r\n", "<br/>");
		htmlContent = htmlContent.replaceAll("\n", "<br/>");
		htmlContent = htmlContent.replaceAll("\r", "<br/>");
		
		
		// Remplacement des zones de textes
		String link = "<a href=\""+param.getUrl()+"\">"+param.getUrl()+"</a>";
		htmlContent = htmlContent.replaceAll("#LINK#", link);
		
		String datePermanences = getDatePermanence(em,utilisateur);
		htmlContent = htmlContent.replaceAll("#DATES#", datePermanences);
		
		// Construction du message
		MailerMessage message  = new MailerMessage();
		message.setEmail(email);
		message.setTitle(subject);
		message.setContent(htmlContent);
		message.addAttachement(attachement);
		
		// Envoi du message
		new MailerService().sendHtmlMail(message);
	}


	private String getDatePermanence(EntityManager em, Utilisateur utilisateur)
	{
		List<PeriodePermanenceDateDTO> permanencesFutures = new MesPermanencesService().getDistributionsFutures(em, utilisateur);
		SimpleDateFormat df = FormatUtils.getFullDate();
		
		// Partie haute
		StringBuffer buf = new StringBuffer();
		buf.append("<ul>");
		for (PeriodePermanenceDateDTO distribution : permanencesFutures)
		{
			buf.append("<li>"+df.format(distribution.datePerm)+"</li>");	
		}
		buf.append("</ul><br/>");
		return buf.toString();
	}
		
	

}
