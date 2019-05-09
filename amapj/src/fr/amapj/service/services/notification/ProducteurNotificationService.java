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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.StackUtils;
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
import fr.amapj.service.engine.deamons.DeamonsContext;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGFeuilleDistributionProducteur;
import fr.amapj.service.services.mailer.MailerAttachement;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.mescontrats.ContratStatusService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.producteur.ProducteurService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;


public class ProducteurNotificationService 
{
	private final static Logger logger = LogManager.getLogger();
	
	
	@DbRead
	public void sendProducteurNotification(DeamonsContext deamonsContext)
	{		
		EntityManager em = TransactionHelper.getEm();
		
		// On recherche tous les producteurs qui ont un contrat dans le futur et à l'état actif 
		Query q = em.createQuery("select distinct(mcd.modeleContrat.producteur) from ModeleContratDate mcd " +
								"where mcd.dateLiv>=:d1 and mcd.modeleContrat.etat=:etat");
		q.setParameter("d1", DateUtils.getDate());
		q.setParameter("etat", EtatModeleContrat.ACTIF);
		
		//
		List<Producteur> prods = q.getResultList();
		
		// On filtre ensuite pour garder uniquement les producteurs qui ont demandé à être notifié
		ProducteurService service = new ProducteurService();
		prods = CollectionUtils.filter(prods, e->service.needNotification(e,em));
		
		
		for (Producteur producteur : prods)
		{
			logger.info("Debut de notification du producteur : "+producteur.nom);
			try
			{
				sendNotificationProducteur(producteur,em,deamonsContext);
			}
			catch(Exception e)
			{
				// En cas d'erreur, on intercepte l'exception pour permettre la notification des autres producteurs
				deamonsContext.nbError++;
				logger.info("Erreur pour le producteur "+producteur.nom+"\n"+StackUtils.asString(e));
			}
			logger.info("Fin de notification du producteur : "+producteur.nom);
		}
	}
	




	private void sendNotificationProducteur(Producteur producteur, EntityManager em, DeamonsContext deamonsContext)
	{
		Query q = em.createQuery("select mcd from ModeleContratDate mcd where mcd.dateLiv>=:d1 and mcd.dateLiv<=:d2 and mcd.modeleContrat.producteur=:p");
		
		Date d1 = DateUtils.getDate();
		Date d2 = DateUtils.addDays(d1, producteur.delaiModifContrat);
		
		// Un delta de 2 heures est retirée à d2 pour que le mail parte vers 2h du matin
		d2 = DateUtils.addHour(d2, -2);
		
		q.setParameter("d1", d1);
		q.setParameter("d2", d2);
		q.setParameter("p", producteur);
		
		
		ContratStatusService statusService = new ContratStatusService();
		List<ModeleContratDate> mcds = q.getResultList();
		for (ModeleContratDate modeleContratDate : mcds)
		{
			// On verifie d'abord si le contrat est encore modifiable pour cette date 
			if (statusService.isDateModifiable(modeleContratDate, em, d1))
			{
				SimpleDateFormat df = FormatUtils.getStdDate();
				logger.info("La notification du modele de contrat "+modeleContratDate.modeleContrat.nom+
							 " n'a pas été envoyé car la date de livraison "+df.format(modeleContratDate.dateLiv)+" est encore modifiable.");
			}
			else
			{
				// Le contrat n'est pas modifiable : on va pouvoir notifier 
				
				// On notifie d'abord les producteurs puis les référents
				List<Utilisateur> users = getUserToNotify(em, producteur,modeleContratDate);
				List<Utilisateur> referents = getReferentsToNotify(em, producteur,modeleContratDate);
				
				//
				List<Utilisateur> dests = new ArrayList<Utilisateur>();
				dests.addAll(users);
				dests.addAll(referents);
				
				// On supprime les utilisateurs dont l'email se termine par #
				dests = CollectionUtils.filter(dests, u->UtilisateurUtil.canSendMailTo(u.email)==true);
				
				// On réalise l'envoi 
				if (users.size()>0)
				{
					sendOneMessageNotificationProducteur(modeleContratDate,dests,em,deamonsContext);
				}
			}
		}	
	}
	
	/**
	 * Retourne la liste des utilisateurs de ce producteur à notifier et qui n'ont pas encore été notifié 
	 * pour cette date 
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
	
	
	/**
	 * Retourne la liste des référents de ce producteur à notifier et qui n'ont pas encore été notifié 
	 * pour cette date 
	 */
	private List<Utilisateur> getReferentsToNotify(EntityManager em, Producteur producteur, ModeleContratDate modeleContratDate)
	{
		// On recherche tous les utilisateurs de ce producteurs qui veulent être notifiés
		Query q = em.createQuery(   "select c.referent from ProducteurReferent c WHERE " +
									"c.producteur=:p AND c.notification=:etat " +
									"AND NOT EXISTS (select d from NotificationDone d where d.typNotificationDone=:typNotif and d.utilisateur=c.referent and d.modeleContratDate=:mcd) "+
									"order by c.referent.nom,c.referent.prenom");
		q.setParameter("p", producteur);
		q.setParameter("etat", EtatNotification.AVEC_NOTIFICATION_MAIL);
		q.setParameter("typNotif", TypNotificationDone.FEUILLE_LIVRAISON_PRODUCTEUR);
		q.setParameter("mcd", modeleContratDate);
		
		List<Utilisateur> us =  q.getResultList();
		return us;
	}


	private void sendOneMessageNotificationProducteur(ModeleContratDate modeleContratDate, List<Utilisateur> users,EntityManager em, DeamonsContext deamonsContext)
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
			try
			{
				message.setEmail(utilisateur.email);
				sendMessageAndMemorize(message,modeleContratDate.getId(),utilisateur.getId());
			}
			catch(Exception e)
			{
				// En cas d'erreur, on intercepte l'exception pour permettre la notification des autres destinatires
				deamonsContext.nbError++;
				logger.error("Erreur pour notifier  "+utilisateur.email+"\n"+StackUtils.asString(e));
			}
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
		notificationDone.typNotificationDone = TypNotificationDone.FEUILLE_LIVRAISON_PRODUCTEUR;
		notificationDone.modeleContratDate = em.find(ModeleContratDate.class, modeleContratDateId);
		notificationDone.utilisateur = em.find(Utilisateur.class, utilisateurId);
		notificationDone.dateEnvoi = DateUtils.getDate();
		em.persist(notificationDone);
		
		// On envoie le message
		new MailerService().sendHtmlMail(message);
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

}
