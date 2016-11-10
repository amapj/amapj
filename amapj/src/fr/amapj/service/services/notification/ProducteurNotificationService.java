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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fr.amapj.common.DateUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.fichierbase.EtatNotification;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.stats.NotificationDone;
import fr.amapj.model.models.stats.TypNotificationDone;
import fr.amapj.service.engine.deamons.DeamonsImpl;
import fr.amapj.service.engine.deamons.DeamonsUtils;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGFeuilleDistributionProducteur;
import fr.amapj.service.services.mailer.MailerAttachement;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;


public class ProducteurNotificationService 
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public ProducteurNotificationService()
	{
		
	}
	
	
	@DbRead
	public void sendProducteurNotification()
	{		
		EntityManager em = TransactionHelper.getEm();
		
		// On recherche tous les producteurs qui ont un contrat dans le futur
		Query q = em.createQuery("select distinct(mcd.modeleContrat.producteur) from ModeleContratDate mcd " +
								"where mcd.dateLiv>=:d1 and mcd.modeleContrat.etat=:etat");
		q.setParameter("d1", DateUtils.getDate());
		q.setParameter("etat", EtatModeleContrat.ACTIF);
		
		List<Producteur> prods = q.getResultList();
		for (Producteur producteur : prods)
		{
			sendNotificationProducteur(producteur,em);
		}
	}
	
	
	
	

	private void sendNotificationProducteur(Producteur producteur, EntityManager em)
	{
		Query q = em.createQuery("select mcd from ModeleContratDate mcd where mcd.dateLiv>=:d1 and mcd.dateLiv<=:d2 and mcd.modeleContrat.producteur=:p");
		
		Date d1 = DateUtils.getDate();
		Date d2 = DateUtils.addDays(d1, producteur.delaiModifContrat);
		
		// Un delta de 2 heures est retirée à d2 pour que le mail parte vers 2h du matin
		d2 = DateUtils.addHour(d2, -2);
		
		q.setParameter("d1", d1);
		q.setParameter("d2", d2);
		q.setParameter("p", producteur);
		
		
		List<ModeleContratDate> mcds = q.getResultList();
		for (ModeleContratDate modeleContratDate : mcds)
		{
			List<Utilisateur> users = getUserToNotify(em, producteur,modeleContratDate);
			if (users.size()>0)
			{
				sendOneMessageNotificationProducteur(modeleContratDate,users,em);
			}
		}	
	}
	
	/**
	 * Retourne la liste des utilisateurs de ce producteur à notifier et qui n'ont pas encore été notifié 
	 * pour cette date 
	 * 
	 * @param em
	 * @param producteur
	 * @param modeleContratDate
	 * @return
	 */
	private List<Utilisateur> getUserToNotify(EntityManager em, Producteur producteur, ModeleContratDate modeleContratDate)
	{
		// On recherche tous les utilisateurs de ce producteurs qui veulent être notifiés
		Query q = em.createQuery(   "select c.utilisateur from ProducteurUtilisateur c WHERE " +
									"c.producteur=:p AND c.notification=:etat " +
									"AND NOT EXISTS (select d from NotificationDone d where d.typNotificationDone=:typNotif and d.utilisateur=c.utilisateur and d.modeleContratDate=:mcd) "+
									"order by c.utilisateur.nom,c.utilisateur.prenom");
		q.setParameter("p", producteur);
		q.setParameter("etat", EtatNotification.AVEC_NOTIFICATION_MAIL);
		q.setParameter("typNotif", TypNotificationDone.FEUILLE_LIVRAISON_PRODUCTEUR);
		q.setParameter("mcd", modeleContratDate);
		
		List<Utilisateur> us =  q.getResultList();
		return us;
	}


	private void sendOneMessageNotificationProducteur(ModeleContratDate modeleContratDate, List<Utilisateur> users,EntityManager em)
	{
		// Construction du message
		MailerMessage message  = new MailerMessage();
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		ParametresDTO param = new ParametresService().getParametres();
	
		message.setTitle(param.nomAmap+" - Feuille de livraison du "+df.format(modeleContratDate.getDateLiv()));
		message.setContent(getMessageContent(modeleContratDate,param,df));
		AbstractExcelGenerator generator = new EGFeuilleDistributionProducteur(modeleContratDate.getModeleContrat().getId(), modeleContratDate.getId());
		message.addAttachement(new MailerAttachement(generator));

		for (Utilisateur utilisateur : users)
		{
			message.setEmail(utilisateur.getEmail());
			sendMessageAndMemorize(message,modeleContratDate.getId(),utilisateur.getId());
		}
	}

	/**
	 * On réalise chaque envoi dans une transaction indépendante 
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @param generator
	 */
	private void sendMessageAndMemorize(final MailerMessage message, final Long modeleContratDateId, final Long utilisateurId)
	{
		NewTransaction.write(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				sendMessageAndMemorize(em,message,modeleContratDateId,utilisateurId);
				return null;
			}
		});
		
	}

	protected void sendMessageAndMemorize(EntityManager em, MailerMessage message, Long modeleContratDateId, Long utilisateurId)
	{
		// On mémorise dans la base de données que l'on va envoyer le message
		NotificationDone notificationDone = new NotificationDone();
		notificationDone.setTypNotificationDone(TypNotificationDone.FEUILLE_LIVRAISON_PRODUCTEUR);
		notificationDone.setModeleContratDate(em.find(ModeleContratDate.class, modeleContratDateId));
		notificationDone.setUtilisateur(em.find(Utilisateur.class, utilisateurId));
		notificationDone.setDateEnvoi(DateUtils.getDate());
		em.persist(notificationDone);
		
		// On envoie le message
		new MailerService().sendHtmlMail(message);
		//System.out.println("titre="+message.getTitle()+" email="+message.getEmail());
	}

	private String getMessageContent(ModeleContratDate modeleContratDate, ParametresDTO param, SimpleDateFormat df)
	{
		String link = param.getUrl();
		
		StringBuffer buf = new StringBuffer();
		buf.append("<h3>"+param.nomAmap+"-"+param.villeAmap+"</h3><br/>");
		buf.append("Bonjour ,");
		buf.append("<br/>");
		buf.append("<br/>");
		buf.append("Vous trouverez ci joint la feuille de livraison pour le "+df.format(modeleContratDate.getDateLiv()));
		buf.append("<br/>");
		buf.append("<br/>");
		buf.append("Nom du contrat : "+modeleContratDate.getModeleContrat().getNom());
		buf.append("<br/>");
		buf.append("Nom du producteur : "+modeleContratDate.getModeleContrat().getProducteur().nom);
		buf.append("<br/>");
		buf.append("Si vous souhaitez accéder à l'application : <a href=\""+link+"\">Cliquez ici </a>");
		buf.append("<br/>");
		buf.append("<br/>");
		buf.append("Bonne journée !");
		buf.append("<br/>");
		buf.append("<br/>");
		
		return buf.toString();
	}

	public static void main(String[] args)
	{
		TestTools.init();
		ProducteurNotificationService service = new ProducteurNotificationService();
		service.sendProducteurNotification();
	}



}
