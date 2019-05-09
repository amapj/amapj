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
 package fr.amapj.service.services.permanence.mespermanences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceUtilisateur;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService.DateInfo;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.service.services.permanence.periode.SmallPeriodePermanenceDTO;

public class MesPermanencesService
{

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PERMANENCES POUR UN UTILISATEUR 

	/**
	 * Retourne la liste des permanences sur lesquelles l'utilisateur courant
	 * doit s'inscrire
	 */
	@DbRead
	public MesPermanenceDTO getMesPermanenceDTO(Long userId)
	{
		Date nowNoTime = DateUtils.getDateWithNoTime();
		EntityManager em = TransactionHelper.getEm();

		MesPermanenceDTO res = new MesPermanenceDTO();

		Utilisateur user = em.find(Utilisateur.class, userId);
		

		// On récupère d'abord la liste des permanences avec date de fin des
		// inscriptions fixes
		Query q = em.createQuery("select ppu from PeriodePermanenceUtilisateur ppu WHERE "
				+ " ppu.periodePermanence.etat=:etat and "
				+ " :d<=ppu.periodePermanence.dateFinInscription and "
				+ " ppu.periodePermanence.nature=:t1 and "
				+ " ppu.utilisateur.id = :uid "
				+ " order by ppu.periodePermanence.dateFinInscription asc , ppu.periodePermanence.nom , ppu.periodePermanence.id");
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("t1", NaturePeriodePermanence.INSCRIPTION_LIBRE_AVEC_DATE_LIMITE);
		q.setParameter("d", nowNoTime);
		q.setParameter("uid", userId);
		
		

		List<PeriodePermanenceUtilisateur> pps1 = q.getResultList();

		// On récupère ensuite la liste de permanences avec date de fin des
		// inscriptions flottantes qui ont des dates dans le future
		q = em.createQuery("select ppu from PeriodePermanenceUtilisateur ppu WHERE "
				+ " ppu.periodePermanence.etat=:etat and "
				+ " EXISTS (select ppd from PeriodePermanenceDate ppd where ppd.periodePermanence.id=ppu.periodePermanence.id and ppd.datePerm>=:d) and "
				+ " ppu.periodePermanence.nature=:t1 and "
				+ " ppu.utilisateur.id = :uid "
				+ " order by ppu.periodePermanence.dateFinInscription asc , ppu.periodePermanence.nom , ppu.periodePermanence.id");
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("t1", NaturePeriodePermanence.INSCRIPTION_LIBRE_FLOTTANT);
		q.setParameter("d", nowNoTime);
		q.setParameter("uid", userId);
		
		
		List<PeriodePermanenceUtilisateur> pps2 = q.getResultList();
		// On filtre ensuite pour tenir compte du délai 
		pps2 = filterFlottant(pps2,em,nowNoTime);
		
		
		List<PeriodePermanenceUtilisateur> pps = new ArrayList<PeriodePermanenceUtilisateur>();
		pps.addAll(pps1);
		pps.addAll(pps2);

		for (PeriodePermanenceUtilisateur periodePermanenceUtilisateur : pps)
		{
			PeriodePermanence periodePermanence = periodePermanenceUtilisateur.periodePermanence;
			UnePeriodePermanenceDTO up = createUnePeriodePermanenceDTO(em, periodePermanence, periodePermanenceUtilisateur,nowNoTime);
			res.mesPeriodesPermanences.add(up);

		}
		
		// On charge ensuite les permanences dans le futur pour cet utilisateur 
		res.mesPermanencesFutures = getDistributionsFutures(em, user);
		
		return res;

	}
	
	
	/**
	 * On conserve uniquement les periodes de permanence qui ont des permanences dans le futur et qui sont modifiables
	 * en tenant compte du délai de modification (période de permanence de type flottant uniquement)
	 * 
	 */
	private List<PeriodePermanenceUtilisateur> filterFlottant(List<PeriodePermanenceUtilisateur> pps, EntityManager em,Date now)
	{
		List<PeriodePermanenceUtilisateur> res = new ArrayList<PeriodePermanenceUtilisateur>();
		for (PeriodePermanenceUtilisateur ppu : pps)
		{
			if (isModifiable(ppu.periodePermanence,em,now))
			{
				res.add(ppu);
			}
		}
		return res;
	}
	
	/**
	 * On conserve uniquement les periodes de permanence qui ont des permanences dans le futur et qui sont modifiables
	 * en tenant compte du délai de modification (période de permanence de type flottant uniquement)
	 * @param now 
	 * 
	 */
	private boolean isModifiable(PeriodePermanence periodePermanence, EntityManager em, Date now)
	{
		int delai = periodePermanence.flottantDelai;
		Date ref = DateUtils.addDays(now, delai);
		
		Query q = em.createQuery("select count(ppd) from PeriodePermanenceDate ppd where ppd.periodePermanence=:pp and ppd.datePerm>:ref");
				
		q.setParameter("pp", periodePermanence);
		q.setParameter("ref", ref);
		
		return SQLUtils.toInt(q.getSingleResult()) >=1;
	}


	/**
	 * 
	 */
	public List<PeriodePermanenceDateDTO> getDistributionsFutures(EntityManager em, Utilisateur user )
	{
		Date dateDebut = DateUtils.firstMonday(DateUtils.getDate());
		
		Query q = em.createQuery("select pc.periodePermanenceDate from PermanenceCell pc WHERE " +
				"pc.periodePermanenceDate.periodePermanence.etat=:etat and " +
				"pc.periodePermanenceDate.datePerm>=:deb and " +
				"pc.periodePermanenceUtilisateur.utilisateur=:user " +
				"order by pc.periodePermanenceDate.datePerm");
		
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("deb", dateDebut, TemporalType.DATE);
		q.setParameter("user", user);
		
		List<PeriodePermanenceDate> ppds = q.getResultList();
		List<PeriodePermanenceDateDTO> res = new ArrayList<PeriodePermanenceDateDTO>();
		
		for (PeriodePermanenceDate ppd : ppds)
		{
			PeriodePermanenceDateDTO dto = new PeriodePermanenceService().loadOneDatePermanence(ppd.id);
			res.add(dto);
		}
		
		return res;
	}
	
	
	
	

	private UnePeriodePermanenceDTO createUnePeriodePermanenceDTO(EntityManager em, PeriodePermanence p, PeriodePermanenceUtilisateur ppu, Date nowNoTime)
	{
		UnePeriodePermanenceDTO dto = new UnePeriodePermanenceDTO();
		DateInfo di = new PeriodePermanenceService().getDateDebutFin(em, p);

		dto.idPeriodePermanence = p.id;
		dto.nature = p.nature;
		dto.nom = p.nom;
		dto.description = p.description;

		dto.dateFinInscription = p.dateFinInscription;

		dto.dateDebut = di.dateDebut;
		dto.dateFin = di.dateFin;
		dto.nbDatePermanence = di.nbDatePerm;

		

		dto.nbSouhaite = ppu.nbParticipation;

		Query q = em.createQuery("select count(c) from PermanenceCell c WHERE c.periodePermanenceUtilisateur=:ppu");
		q.setParameter("ppu", ppu);
		dto.nbInscription = SQLUtils.toInt(q.getSingleResult());

		if (p.nature==NaturePeriodePermanence.INSCRIPTION_LIBRE_FLOTTANT)
		{
			dto.firstDateModifiable = computeFirstDateModifiable(p,em,nowNoTime);
		}
		
		dto.regleInscription = p.regleInscription;
		
		return dto;
	}

	
	private Date computeFirstDateModifiable(PeriodePermanence p, EntityManager em, Date nowNoTime)
	{
		int delai = p.flottantDelai;
		Date ref = DateUtils.addDays(nowNoTime, delai);
		
		Query q = em.createQuery("select ppd from PeriodePermanenceDate ppd where ppd.periodePermanence=:pp and ppd.datePerm>:ref order by ppd.datePerm");
				
		q.setParameter("pp", p);
		q.setParameter("ref", ref);
		
		List<PeriodePermanenceDate> ppds = q.getResultList();
		if (ppds.size()==0)
		{
			return null;
		}
		else
		{
			return ppds.get(0).datePerm;
		}
	}

	
	
	
	
	
	

	/**
	 * Permet de charger les compteurs pour un adherent particulier 
	 * @param idPeriodePermanence
	 * @param userId
	 * @return
	 */
	@DbRead
	public UnePeriodePermanenceDTO loadCompteurPeriodePermanence(Long idPeriodePermanence , Long userId)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodePermanence p = em.find(PeriodePermanence.class, idPeriodePermanence);
		PeriodePermanenceUtilisateur ppu = new DetailPeriodePermanenceService().findPeriodePermanenceUtilisateur(em, userId, p);
		
		return createUnePeriodePermanenceDTO(em, p, ppu,DateUtils.getDateWithNoTime());
		
	}
	
	

	/**
	 * Permet à un utilisateur de s'inscrire pour une permanence
	 */
	
	public enum InscriptionMessage
	{
		DEJA_INSCRIT_CETTE_DATE,
		NOMBRE_SUFFISANT,
		PAS_DE_PLACE_CETTE_DATE,
		
		// Correspond au choix precis d'une place, et celle ci n'est plus disponible 
		PLACE_NON_DISPONIBLE;
	}
	
	
	/**
	 * Permet à un utilisateur de s'inscrire pour une permanence, avec la regle RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE 
	 * ou la regle RegleInscriptionPeriodePermanence.MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT
	 */
	@DbWrite
	public InscriptionMessage inscription(Long userId, Long idPeriodePermanenceDate,Long idRole,RegleInscriptionPeriodePermanence regle)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, idPeriodePermanenceDate);

		//
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idPeriodePermanenceDate);
		
		// On recharge d'abord les anciennes valeurs  
		Query q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate=:ppd order by c.indx");
		q.setParameter("ppd", ppd);
		
		List<PermanenceCell> pcs = q.getResultList();
		
		// On recherche le ppu 
		PeriodePermanenceUtilisateur ppu = new DetailPeriodePermanenceService().findPeriodePermanenceUtilisateur(em,userId,ppd.periodePermanence);
		if (ppu==null)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas vous inscrire à cette période");
		}
		
		
		// On verifie d'abord que l'utilisateur ne soit pas déjà inscrit à cette date
		if (regle==RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE)
		{
			for (PermanenceCell pc : pcs)
			{
				if (pc.periodePermanenceUtilisateur!=null && pc.periodePermanenceUtilisateur.utilisateur.getId().equals(userId))
				{
					return InscriptionMessage.DEJA_INSCRIT_CETTE_DATE;
				}
			}
		}
		else
		{
			for (PermanenceCell pc : pcs)
			{
				if (pc.periodePermanenceUtilisateur!=null && pc.periodePermanenceUtilisateur.utilisateur.getId().equals(userId) && pc.permanenceRole.id.equals(idRole))
				{
					return InscriptionMessage.DEJA_INSCRIT_CETTE_DATE;
				}
			}
		}
			
		
		// On vérifie ensuite que l'utilisateur n'a pas dépassé son quota d'inscription sur la période
		q = em.createQuery("select count(c) from PermanenceCell c WHERE c.periodePermanenceUtilisateur=:ppu");
		q.setParameter("ppu", ppu);
		int nbInscriptionReel = SQLUtils.toInt(q.getSingleResult());
		if (nbInscriptionReel>=ppu.nbParticipation)
		{
			return InscriptionMessage.NOMBRE_SUFFISANT;
		}
		
		// On cherche ensuite une place disponible
		PermanenceCell pc = findPlaceDisponible(pcs,idRole);
		
		if (pc==null)
		{
			return InscriptionMessage.PAS_DE_PLACE_CETTE_DATE;
		}
		
		// On ajoute le nouvel participant
		pc.periodePermanenceUtilisateur = ppu;
			
		return null;
	}
	
	
	

	private PermanenceCell findPlaceDisponible(List<PermanenceCell> pcs, Long idRole)
	{
		for (PermanenceCell pc : pcs)
		{
			if (   (pc.periodePermanenceUtilisateur==null) && (pc.permanenceRole.id.equals(idRole)) )
			{
				return pc;
			}
		}
		return null;
	}
	


	/**
	 * Permet à un utilisateur de supprimer une inscription pour une permanence , avec la regle RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE,
	 * 
	 * @param userId
	 * @param idPeriodePermanenceDate
	 */
	@DbWrite
	public void deleteInscription(Long userId, Long idPeriodePermanenceDate)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, idPeriodePermanenceDate);

		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idPeriodePermanenceDate);
		
		// On recharge d'abord les anciennes valeurs  
		Query q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate=:ppd");
		q.setParameter("ppd", ppd);
		
		List<PermanenceCell> pcs = q.getResultList();
		
		// On supprime les inscriptions de cet utilisateur
		for (PermanenceCell pc : pcs)
		{
			if (pc.periodePermanenceUtilisateur!=null && pc.periodePermanenceUtilisateur.utilisateur.getId().equals(userId))
			{
				pc.dateNotification = null;
				pc.periodePermanenceUtilisateur = null;
			}
		}		
	}
	
	
	
	/**
	 * Permet à un utilisateur de supprimer une inscription pour une permanence , avec la regle RegleInscriptionPeriodePermanence.MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT,
	 * 
	 * @param userId
	 * @param idPeriodePermanenceDate
	 */
	@DbWrite
	public void deleteInscriptionRoleDifferent(Long userId, Long idPeriodePermanenceDate,Long idRole)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, idPeriodePermanenceDate);

		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idPeriodePermanenceDate);
		
		// On recharge d'abord les anciennes valeurs  
		Query q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate=:ppd");
		q.setParameter("ppd", ppd);
		
		List<PermanenceCell> pcs = q.getResultList();
		
		// On supprime les inscriptions de cet utilisateur
		for (PermanenceCell pc : pcs)
		{
			if (pc.periodePermanenceUtilisateur!=null && pc.periodePermanenceUtilisateur.utilisateur.getId().equals(userId) && pc.permanenceRole.id.equals(idRole))
			{
				pc.dateNotification = null;
				pc.periodePermanenceUtilisateur = null;
			}
		}		
	}
	
	
	/**
	 * Permet à un utilisateur de s'inscrire pour une permanence, avec la regle RegleInscriptionPeriodePermanence.TOUT_AUTORISE,
	 */
	@DbWrite
	public InscriptionMessage inscriptionToutAutorise(Long userId, Long idPeriodePermanenceCell,Long idPeriodePermanenceDate)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, idPeriodePermanenceDate);

		// On recherche le ppu
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idPeriodePermanenceDate);
		PeriodePermanenceUtilisateur ppu = new DetailPeriodePermanenceService().findPeriodePermanenceUtilisateur(em,userId,ppd.periodePermanence);
		if (ppu==null)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas vous inscrire à cette période");
		}
		
		// On vérifie ensuite que l'utilisateur n'a pas dépassé son quota d'inscription sur la période
		Query q = em.createQuery("select count(c) from PermanenceCell c WHERE c.periodePermanenceUtilisateur=:ppu");
		q.setParameter("ppu", ppu);
		int nbInscriptionReel = SQLUtils.toInt(q.getSingleResult());
		if (nbInscriptionReel>=ppu.nbParticipation)
		{
			return InscriptionMessage.NOMBRE_SUFFISANT;
		}
		
		PermanenceCell cell = em.find(PermanenceCell.class, idPeriodePermanenceCell);
		
		// On verifie que personne ne soit inscrit entre temps 
		if (cell.periodePermanenceUtilisateur!=null)
		{
			return InscriptionMessage.PLACE_NON_DISPONIBLE;
		}
		
		// On inscrit l'utilisateur
		cell.periodePermanenceUtilisateur = ppu;
		
		return null;
	}
	
	
	/**
	 * Permet à un utilisateur de supprimer une inscription pour une permanence , avec la regle RegleInscriptionPeriodePermanence.TOUT_AUTORISE,
	 * 
	 * @param userId
	 * @param idPeriodePermanenceDate
	 */
	@DbWrite
	public void deleteInscriptionToutAutorise(Long userId, Long idPeriodePermanenceCell,Long idPeriodePermanenceDate)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, idPeriodePermanenceDate);

		PermanenceCell cell = em.find(PermanenceCell.class, idPeriodePermanenceCell);
		
		// On verifie que l'on est toujours bien inscrit 
		if (cell.periodePermanenceUtilisateur==null)
		{
			// Quelqu'un nous a desincrit entre temps
			return;
		}
		if (cell.periodePermanenceUtilisateur.utilisateur.getId().equals(userId)==false)
		{
			// Quelqu'un nous a desincrit entre temps et un autre s'est inscrit 
			return;
		}
		
		cell.dateNotification = null;
		cell.periodePermanenceUtilisateur = null;	
	}
	
	
	
	

	/**
	 * Permet de connaitre toutes les periodes de permanence avec des dates dans le futur et qui sont actives 
	 * 
	 * Attention : on ne charge que le id et le nom de periode 
	 *  
	 * @return
	 */
	@DbRead
	public List<SmallPeriodePermanenceDTO> getAllPeriodeInFuture()
	{
		EntityManager em = TransactionHelper.getEm();
		
		Date dateDebut = DateUtils.getDateWithNoTime();
		
		Query q = em.createQuery("select distinct(pc.periodePermanence) from PeriodePermanenceDate pc WHERE " +
				"pc.periodePermanence.etat=:etat and "+
				"pc.datePerm>=:deb " +
				"order by pc.periodePermanence.id");
		
		q.setParameter("etat", EtatPeriodePermanence.ACTIF);
		q.setParameter("deb", dateDebut, TemporalType.DATE);
				
		List<PeriodePermanence> ppds = q.getResultList();
		
		List<SmallPeriodePermanenceDTO> dtos = new ArrayList<SmallPeriodePermanenceDTO>();
		
		for (PeriodePermanence ppd : ppds)
		{
			SmallPeriodePermanenceDTO dto = new SmallPeriodePermanenceDTO();
			dto.id = ppd.id;
			dto.nom = ppd.nom;
			
			dtos.add(dto);
		}
		
		return dtos;	
	}
	
	
	
	
	
	
}
