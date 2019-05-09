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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.DateUtils;
import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
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
		Query q = em.createQuery("select pc from PermanenceCell pc where "
					+ " pc.periodePermanenceDate.periodePermanence.etat=:etat and " 
					+ " pc.periodePermanenceDate.datePerm>=:d1 and "
					+ " pc.periodePermanenceDate.datePerm<=:d2 and "
					+ " pc.dateNotification is null "
					+ " order by pc.periodePermanenceUtilisateur.utilisateur.nom,pc.periodePermanenceUtilisateur.utilisateur.prenom");
		
		Date d1 = DateUtils.getDate();
		Date d2 = DateUtils.addDays(d1, param.delaiMailRappelPermanence);
		
		// Un delta de 4 heures est retirée à d2 pour que le mail parte vers 4h du matin
		d2 = DateUtils.addHour(d2, -4);
		
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("d1", d1);
		q.setParameter("d2", d2);
		
		
		List<PermanenceCell> pcs = q.getResultList();
		for (PermanenceCell pc : pcs)
		{
			Utilisateur utilisateur = pc.periodePermanenceUtilisateur.utilisateur;
			if (UtilisateurUtil.canSendMailTo(utilisateur))
			{
				sendPermanenceNotification(pc,em,param);
			}
			else
			{
				memorize(em,pc.id);  
			}
		}
	}
	
	
	
	
	/**
	 * 
	 * @param u
	 * @param em
	 */
	private void sendPermanenceNotification(PermanenceCell pc, EntityManager em,ParametresDTO param)
	{
		
		// Construction du message
		MailerMessage message  = new MailerMessage();
		
		Utilisateur utilisateur = pc.periodePermanenceUtilisateur.utilisateur;
		
		String titre = replaceWithContext(param.titreMailRappelPermanence, pc, em,utilisateur,param);
		String content = replaceWithContext(param.contenuMailRappelPermanence, pc, em,utilisateur,param);
		
		message.setTitle(titre);
		message.setContent(content);
		message.setEmail(utilisateur.getEmail());
		sendMessageAndMemorize(message,pc.getId());
		
	}
	
	
	
	

	/**
	 * On réalise chaque envoi dans une transaction indépendante 
	 * 
	 * @param email
	 * @param title
	 * @param content
	 * @param generator
	 */
	private void sendMessageAndMemorize(final MailerMessage message, final Long idPermanenceCell)
	{
		NewTransaction.write(new Call()
		{
			@Override
			public Object executeInNewTransaction(EntityManager em)
			{
				sendMessageAndMemorize(em,message,idPermanenceCell);
				return null;
			}
		});
		
	}

	protected void sendMessageAndMemorize(EntityManager em, MailerMessage message, Long idPermanenceCell)
	{
		// On mémorise dans la base de données que l'on va envoyer le message
		memorize(em,idPermanenceCell);
		
		// On envoie le message
		new MailerService().sendHtmlMail(message);
	}



	
	private void memorize(EntityManager em, Long idPermanenceCell)
	{
		PermanenceCell pc = em.find(PermanenceCell.class, idPermanenceCell);
		pc.dateNotification = DateUtils.getDate();
	}


	private String replaceWithContext(String in,PermanenceCell dpu,EntityManager em,Utilisateur u,ParametresDTO param)
	{
		// Si le champ a été laissé vide, on positione une valeur par défaut 
		if (in==null || in.trim().length()==0)
		{
			in = "Permanence AMAP";
		}
		
		// Calcul du contexte
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		
		PeriodePermanenceDateDTO permanenceDTO = new PeriodePermanenceService().loadOneDatePermanence(dpu.periodePermanenceDate.id);
		String link = param.getUrl()+"?username="+u.getEmail();
		
		in = in.replaceAll("#NOM_AMAP#", param.nomAmap);
		in = in.replaceAll("#VILLE_AMAP#", param.villeAmap);
		in = in.replaceAll("#LINK#", link);
		
		in = in.replaceAll("#DATE_PERMANENCE#", df.format(dpu.periodePermanenceDate.datePerm));
		in = in.replaceAll("#PERSONNES#", permanenceDTO.getNomInscrit());
		
		return in;
		
	}
	
	public static void main(String[] args)
	{
		TestTools.init();
		PermanenceNotificationService service = new PermanenceNotificationService();
		service.sendPermanenceNotification();
	}


}
