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
 package fr.amapj.service.services.notification;

import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.stats.NotificationDone;
import fr.amapj.model.models.stats.TypNotificationDone;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;

/**
 * Notification pour les permanences
 *
 */
public class PeriodiqueNotificationService 
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public PeriodiqueNotificationService()
	{
		
	}
	
	
	@DbRead
	public void sendPermanenceNotification()
	{		
		EntityManager em = TransactionHelper.getEm();
		ParametresDTO param = new ParametresService().getParametres();
		
		if (param.envoiMailPeriodique==ChoixOuiNon.NON)
		{
			return ;
		}
		
		// On recherche la date de la dernière notification (dans le passé ou le jour même)
		Date d = getLastNotificationDate(param);
		
		if (d==null)
		{
			return;
		}
		
		// On recherche la liste des utilisateurs qui n'ont pas eu de notification ce jour là et qui sont actif
		List<Utilisateur> utilisateurs = getUtilisateursActifWithNoNotification(d,em);
		
		for (Utilisateur utilisateur : utilisateurs)
		{
			if (UtilisateurUtil.canSendMailTo(utilisateur))
			{
				sendMail(utilisateur,d,em,param);
			}
		}
	}
	
	
	
	private List<Utilisateur> getUtilisateursActifWithNoNotification(Date d, EntityManager em)
	{
		Query q = em.createQuery("select u from Utilisateur u where "
				+ "	u.etatUtilisateur=:etat and "
				+ " NOT EXISTS (select d from NotificationDone d where d.typNotificationDone=:typNotif and d.dateMailPeriodique=:d and d.utilisateur=u) "
				+ " order by u.nom,u.prenom");
		
		q.setParameter("etat", EtatUtilisateur.ACTIF);
		q.setParameter("d", d);
		q.setParameter("typNotif", TypNotificationDone.MAIL_PERIODIQUE);

		List<Utilisateur> us = q.getResultList();
		
		return us;
	}


	/**
	 * On prend les dates sur les 7 jours précédant le jour courant 
	 * et on vérifie si cette date est une date pour la notification periodique
	 * 
	 * @param param
	 * @return
	 */
	private Date getLastNotificationDate(ParametresDTO param)
	{
		Date ref = DateUtils.getDateWithNoTime();
		
		for (int i = 0; i < 7; i++)
		{
			Date d = DateUtils.addDays(ref, -i);
			
			if (DateUtils.getDayInMonth(d)==param.numJourDansMois)
			{
				return d;
			}
		}
		
		return null;
	}


	private void sendMail(Utilisateur utilisateur, Date d, EntityManager em, ParametresDTO param)
	{	
		// Construction du message
		MailerMessage message  = new MailerMessage();
		
		String titre = replaceWithContext(param.titreMailPeriodique, em, d, utilisateur,param);
		String content = replaceWithContext(param.contenuMailPeriodique, em, d, utilisateur,param);
		
		message.setTitle(titre);
		message.setContent(content);
		message.setEmail(utilisateur.getEmail());
		sendMessageAndMemorize(message,d,utilisateur.getId());
		
	}
	
	
	
	

	/**
	 * On réalise chaque envoi dans une transaction indépendante 
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @param generator
	 */
	private void sendMessageAndMemorize(final MailerMessage message, final Date d, final Long utilisateurId)
	{
		NewTransaction.write(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				sendMessageAndMemorize(em,message,d,utilisateurId);
				return null;
			}
		});
		
	}

	protected void sendMessageAndMemorize(EntityManager em, MailerMessage message, Date d, Long utilisateurId)
	{
		// On mémorise dans la base de données que l'on va envoyer le message
		NotificationDone notificationDone = new NotificationDone();
		notificationDone.typNotificationDone = TypNotificationDone.MAIL_PERIODIQUE;
		notificationDone.dateMailPeriodique = d;
		notificationDone.utilisateur = em.find(Utilisateur.class, utilisateurId);
		notificationDone.dateEnvoi = DateUtils.getDate();
		em.persist(notificationDone);
		
		// On envoie le message
		new MailerService().sendHtmlMail(message);
	}



	
	private String replaceWithContext(String in,EntityManager em,Date d,Utilisateur u,ParametresDTO param)
	{
		// Calcul du contexte
		String link = param.getUrl()+"?username="+u.getEmail();
		
		in = in.replaceAll("#NOM_AMAP#", param.nomAmap);
		in = in.replaceAll("#VILLE_AMAP#", param.villeAmap);
		in = in.replaceAll("#LINK#", link);
		
		
		return in;
		
	}
	
	public static void main(String[] args)
	{
		TestTools.init();
		PeriodiqueNotificationService service = new PeriodiqueNotificationService();
		service.sendPermanenceNotification();
	}


}
