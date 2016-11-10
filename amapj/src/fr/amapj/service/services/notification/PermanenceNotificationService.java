/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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

import java.text.SimpleDateFormat;
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
import fr.amapj.model.models.distribution.DatePermanenceUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.stats.NotificationDone;
import fr.amapj.model.models.stats.TypNotificationDone;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;

/**
 * Notification pour les permanences
 *
 */
public class PermanenceNotificationService 
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public PermanenceNotificationService()
	{
		
	}
	
	
	@DbRead
	public void sendPermanenceNotification()
	{		
		EntityManager em = TransactionHelper.getEm();
		ParametresDTO param = new ParametresService().getParametres();
		
		if (param.envoiMailRappelPermanence==ChoixOuiNon.NON)
		{
			return ;
		}
		
		
		// On recherche toutes les dates de permanence dans les x jours qui viennent
		// et qui n'ont pas encore été notifiées
		Query q = em.createQuery("select dpu from DatePermanenceUtilisateur dpu where "
					+ " dpu.datePermanence.datePermanence>=:d1 and "
					+ " dpu.datePermanence.datePermanence<=:d2 and "
					+ " NOT EXISTS (select d from NotificationDone d where d.typNotificationDone=:typNotif and d.datePermanenceUtilisateur=dpu) "
					+ " order by dpu.utilisateur.nom,dpu.utilisateur.prenom");
		
		Date d1 = DateUtils.getDate();
		Date d2 = DateUtils.addDays(d1, param.delaiMailRappelPermanence);
		
		// Un delta de 4 heures est retirée à d2 pour que le mail parte vers 4h du matin
		d2 = DateUtils.addHour(d2, -4);
		
		q.setParameter("d1", d1);
		q.setParameter("d2", d2);
		q.setParameter("typNotif", TypNotificationDone.RAPPEL_PERMANENCE);
		
		
		List<DatePermanenceUtilisateur> dpus = q.getResultList();
		for (DatePermanenceUtilisateur dpu : dpus)
		{
			if (UtilisateurUtil.canSendMailTo(dpu.getUtilisateur()))
			{
				sendPermanenceNotification(dpu,em,param);
			}
		}
	}
	
	
	
	
	/**
	 * 
	 * @param u
	 * @param em
	 */
	private void sendPermanenceNotification(DatePermanenceUtilisateur dpu, EntityManager em,ParametresDTO param)
	{
		
		// Construction du message
		MailerMessage message  = new MailerMessage();
		
		String titre = replaceWithContext(param.titreMailRappelPermanence, dpu, em,dpu.getUtilisateur(),param);
		String content = replaceWithContext(param.contenuMailRappelPermanence, dpu, em,dpu.getUtilisateur(),param);
		
		message.setTitle(titre);
		message.setContent(content);
		message.setEmail(dpu.getUtilisateur().getEmail());
		sendMessageAndMemorize(message,dpu.getId(),dpu.getUtilisateur().getId());
		
	}
	
	
	
	

	/**
	 * On réalise chaque envoi dans une transaction indépendante 
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @param generator
	 */
	private void sendMessageAndMemorize(final MailerMessage message, final Long dpuId, final Long utilisateurId)
	{
		NewTransaction.write(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				sendMessageAndMemorize(em,message,dpuId,utilisateurId);
				return null;
			}
		});
		
	}

	protected void sendMessageAndMemorize(EntityManager em, MailerMessage message, Long dpuId, Long utilisateurId)
	{
		// On mémorise dans la base de données que l'on va envoyer le message
		NotificationDone notificationDone = new NotificationDone();
		notificationDone.setTypNotificationDone(TypNotificationDone.RAPPEL_PERMANENCE);
		notificationDone.setDatePermanenceUtilisateur(em.find(DatePermanenceUtilisateur.class, dpuId));
		notificationDone.setUtilisateur(em.find(Utilisateur.class, utilisateurId));
		notificationDone.setDateEnvoi(DateUtils.getDate());
		em.persist(notificationDone);
		
		// On envoie le message
		new MailerService().sendHtmlMail(message);
		//System.out.println("titre="+message.getTitle()+" email="+message.getEmail());
		//System.out.println("content="+message.getContent());
	}



	
	private String replaceWithContext(String in,DatePermanenceUtilisateur dpu,EntityManager em,Utilisateur u,ParametresDTO param)
	{
		// Calcul du contexte
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		
		PermanenceDTO permanenceDTO = new PermanenceService().createDistributionDTO(em, dpu.getDatePermanence());
		String link = param.getUrl()+"?username="+u.getEmail();
		
		in = in.replaceAll("#NOM_AMAP#", param.nomAmap);
		in = in.replaceAll("#VILLE_AMAP#", param.villeAmap);
		in = in.replaceAll("#LINK#", link);
		
		in = in.replaceAll("#DATE_PERMANENCE#", df.format(dpu.getDatePermanence().getDatePermanence()));
		in = in.replaceAll("#PERSONNES#", permanenceDTO.getUtilisateurs());
		
		return in;
		
	}
	
	public static void main(String[] args)
	{
		TestTools.init();
		PermanenceNotificationService service = new PermanenceNotificationService();
		service.sendPermanenceNotification();
	}


}
