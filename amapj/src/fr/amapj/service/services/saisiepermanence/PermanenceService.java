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
 package fr.amapj.service.services.saisiepermanence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;

import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.models.distribution.DatePermanence;
import fr.amapj.model.models.distribution.DatePermanenceUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.stats.NotificationDone;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.services.edgenerator.excel.EGPlanningPermanence;
import fr.amapj.service.services.mailer.MailerAttachement;
import fr.amapj.service.services.mailer.MailerMessage;
import fr.amapj.service.services.mailer.MailerService;
import fr.amapj.service.services.notification.DeleteNotificationService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;

/**
 * Permet la saisie des distributions 
 * 
 */
public class PermanenceService
{
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES DISTRIBUTIONS
	
	/**
	 * Permet de charger la liste de tous les distributions
	 * dans une transaction en lecture
	 */
	@DbRead
	public List<PermanenceDTO> getAllDistributions()
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<PermanenceDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select d from DatePermanence d order by d.datePermanence");
			
		List<DatePermanence> ds = q.getResultList();
		for (DatePermanence d : ds)
		{
			PermanenceDTO dto = createDistributionDTO(em,d);
			res.add(dto);
		}
		
		return res;
	}

	
	public PermanenceDTO createDistributionDTO(EntityManager em, DatePermanence d)
	{
		List<PermanenceUtilisateurDTO> idUtilisateurs = new ArrayList<>();
		
		List<DatePermanenceUtilisateur> dus = getAllDateDistriUtilisateur(em,d);
		for (DatePermanenceUtilisateur du : dus)
		{
			Utilisateur u = du.getUtilisateur();
		
			PermanenceUtilisateurDTO distriUtilisateurDTO = new PermanenceUtilisateurDTO();
			distriUtilisateurDTO.idUtilisateur = u.getId();
			distriUtilisateurDTO.nom = u.getNom();
			distriUtilisateurDTO.prenom = u.getPrenom();
			distriUtilisateurDTO.numSession = du.getNumSession();
			idUtilisateurs.add(distriUtilisateurDTO);
		}
		
		
		PermanenceDTO dto = new PermanenceDTO();
		
		dto.id = d.getId();
		dto.datePermanence = d.getDatePermanence();
		dto.permanenceUtilisateurs = idUtilisateurs;
		return dto;
	}


	private List<DatePermanenceUtilisateur> getAllDateDistriUtilisateur(EntityManager em,DatePermanence d)
	{
		Query q = em.createQuery("select du from DatePermanenceUtilisateur du "
								+ "WHERE du.datePermanence=:d "
								+ "ORDER BY du.utilisateur.nom,du.utilisateur.prenom");
		q.setParameter("d", d);
		
		List<DatePermanenceUtilisateur> us = q.getResultList();
		return us;
	}


	// PARTIE CREATION OU MISE A JOUR D'UNE DISTRIBUTION
	
	@DbWrite
	public void updateorCreateDistribution(PermanenceDTO dto,boolean create)
	{
		EntityManager em = TransactionHelper.getEm();
		
		DatePermanence d=null;
		
		if (create)
		{
			d = new DatePermanence();
			d.setDatePermanence(dto.datePermanence);
			em.persist(d);
		}
		else
		{
			d = em.find(DatePermanence.class, dto.id);
			
			List<DatePermanenceUtilisateur> dus = getAllDateDistriUtilisateur(em,d);
			for (DatePermanenceUtilisateur du : dus)
			{
				em.remove(du);
			}
		}
		
		
		for (PermanenceUtilisateurDTO distriUtilisateur : dto.permanenceUtilisateurs)
		{
			DatePermanenceUtilisateur du = new DatePermanenceUtilisateur();
			du.setDatePermanence(d);
			du.setUtilisateur(em.find(Utilisateur.class, distriUtilisateur.idUtilisateur));
			du.setNumSession(distriUtilisateur.numSession);
			em.persist(du);
		}
		
		
	}

	
	

	
	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer une distribution
	 * Ceci est fait dans une transaction en ecriture
	 */
	@DbWrite
	public void deleteDistribution(final Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		DatePermanence d = em.find(DatePermanence.class, id);
		
		List<DatePermanenceUtilisateur> dus = getAllDateDistriUtilisateur(em,d);
		for (DatePermanenceUtilisateur du : dus)
		{
			new DeleteNotificationService().deleteAllNotificationDoneDatePermanenceUtilisateur(em, du);
			em.remove(du);
		}
		
		em.remove(d);
	}
	
	
	// PARTIE VISUALISATION DES DISTRIBUTIONS PAR L'UTILISATEUR FINAL

	/**
	 * Permet de charger le planning des distributions pour un utilisateur final
	 */
	@DbRead
	public MesPermanencesDTO getMesDistributions(Date d)
	{
		EntityManager em = TransactionHelper.getEm();

		MesPermanencesDTO res = new MesPermanencesDTO();

		Utilisateur user = em.find(Utilisateur.class, SessionManager.getUserId());

		res.dateDebut = fr.amapj.common.DateUtils.firstMonday(d);
		res.dateFin = DateUtils.addDays(res.dateDebut,6);
		
		// On récupère ensuite la liste de toutes les distributions dans cet intervalle
		res.permanencesSemaine = getAllDistributions(em, res.dateDebut,res.dateFin);
		
		// On récupère la liste des distributions de cet utilisateur dans le futur
		res.permanencesFutures = getDistributionsFutures(em, user);
		
		return res;

	}

	
	/**
	 * 
	 */
	private List<PermanenceDTO> getDistributionsFutures(EntityManager em, Utilisateur user )
	{
		Date dateDebut = fr.amapj.common.DateUtils.firstMonday(fr.amapj.common.DateUtils.getDate());
		
		Query q = em.createQuery("select distinct(du.datePermanence) from DatePermanenceUtilisateur du WHERE " +
				"du.datePermanence.datePermanence>=:deb and " +
				"du.utilisateur=:user " +
				"order by du.datePermanence.datePermanence");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("user", user);
		
		List<DatePermanence> dds = q.getResultList();
		List<PermanenceDTO> res = new ArrayList<PermanenceDTO>();
		
		for (DatePermanence dd : dds)
		{
			PermanenceDTO dto = createDistributionDTO(em, dd);
			res.add(dto);
		}
		
		return res;
	}
	
	

	/**
	 * 
	 */
	public List<PermanenceDTO> getAllDistributions(EntityManager em, Date dateDebut, Date dateFin)
	{
		Query q = em.createQuery("select d from DatePermanence d WHERE " +
				"d.datePermanence>=:deb AND " +
				"d.datePermanence<=:fin " +
				"order by d.datePermanence, d.id");
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("fin", dateFin, TemporalType.DATE);
		
		List<DatePermanence> dds = q.getResultList();
		List<PermanenceDTO> res = new ArrayList<PermanenceDTO>();
		
		for (DatePermanence dd : dds)
		{
			PermanenceDTO dto = createDistributionDTO(em, dd);
			res.add(dto);
		}
		
		return res;
	}
	
	// PARTIE RAPPEL PAR MAIL
	
	/**
	 * Permet de faire un rappel par mail 
	 */
	@DbRead
	public void performRappel(String texte)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// Récuperation de tous les utilisateurs ayant une permanence dans le futur
		List<Utilisateur> utilisateurs = getUtilisateursPermanenceFuture(em);
		
		// Calcul du fichier Excel des permanences à venir 
		AbstractExcelGenerator generator = new EGPlanningPermanence(fr.amapj.common.DateUtils.getDate());
		MailerAttachement attachement = new MailerAttachement(generator);
		
		// Pour chaque utilisateur, envoi de l'email
		for (Utilisateur utilisateur : utilisateurs)
		{
			sendEmail(em,utilisateur,attachement,texte);
		}
		
	}


	private List<Utilisateur> getUtilisateursPermanenceFuture(EntityManager em)
	{
		// On recherche tous les utilisateurs qui ont une permanence dans le futur
		Query q = em.createQuery("select distinct(dpu.utilisateur) from DatePermanenceUtilisateur dpu " +
								"where dpu.datePermanence.datePermanence>=:d1 ");
		q.setParameter("d1", fr.amapj.common.DateUtils.getDate());
		
		
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
		//System.out.println("titre="+message.getTitle());
		//System.out.println("email="+message.getEmail());
		//System.out.println("content="+message.getContent());
		
	}


	private String getDatePermanence(EntityManager em, Utilisateur utilisateur)
	{
		List<PermanenceDTO> permanencesFutures = getDistributionsFutures(em, utilisateur);
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		
		// Partie haute
		StringBuffer buf = new StringBuffer();
		buf.append("<ul>");
		for (PermanenceDTO distribution : permanencesFutures)
		{
			buf.append("<li>"+df.format(distribution.datePermanence)+"</li>");	
		}
		buf.append("</ul><br/>");
		return buf.toString();
	}
		
	

}
