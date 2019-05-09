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
 package fr.amapj.service.services.permanence.detailperiode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.CollectionUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceUtilisateur;
import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;

/**
 * Permet la gestion du detail des  periodes de permanences
 * 
 */
public class DetailPeriodePermanenceService
{
	
	// PARTIE MODIFICATION DES INSCRIPTIONS A UNE DATE DE PERMANENCE 
	
	@DbWrite
	public void updateDetailPeriodePermanence(PeriodePermanenceDateDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, dto.idPeriodePermanenceDate);
		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, dto.idPeriodePermanenceDate);
		
		// On met à jour chaque cellule
		for (PermanenceCellDTO pcDto : dto.permanenceCellDTOs)
		{
			PermanenceCell c = em.find(PermanenceCell.class, pcDto.idPermanenceCell);
			if (pcDto.idUtilisateur==null)
			{
				c.periodePermanenceUtilisateur = null;
			}
			else
			{
				c.periodePermanenceUtilisateur = findPeriodePermanenceUtilisateur(em,pcDto.idUtilisateur,ppd.periodePermanence);
			}
		}
	}

	public PeriodePermanenceUtilisateur findPeriodePermanenceUtilisateur(EntityManager em, Long idUtilisateur, PeriodePermanence p)
	{
		Query q = em.createQuery("select c from PeriodePermanenceUtilisateur c WHERE c.periodePermanence=:p AND c.utilisateur.id=:uid");
		q.setParameter("p",p);
		q.setParameter("uid",idUtilisateur);
		
		List<PeriodePermanenceUtilisateur> ppus = q.getResultList();
		if (ppus.size()==1)
		{
			return ppus.get(0);
		}
		throw new AmapjRuntimeException("size = "+ppus.size());
	}

	
	@DbRead
	public List<Utilisateur> computeAllowedUser(Long idDatePeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, idDatePeriodePermanence);
		
		PeriodePermanence p = ppd.periodePermanence;
		
		// 
		Query q = em.createQuery("select c.utilisateur from PeriodePermanenceUtilisateur c WHERE c.periodePermanence=:p order by c.utilisateur.nom, c.utilisateur.prenom");
		q.setParameter("p", p);
		
		List<Utilisateur> us = q.getResultList();
		
		return us;
	}
	
	// PARTIE SUPPRESSION DES INSCRIPTIONS SUR UN INTERVALLE DE DATE 
	
	/**
	 * Supprimer des inscriptions sur un intervalle de date 
	 */
	
	@DbRead
	public String getDeleteInscriptionInfo(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf = new StringBuffer();

		PeriodePermanence pp = em.find(PeriodePermanence.class, dto.id);

		// On selectionne toutes les dates de permanences
		Query q = em.createQuery("select d from PeriodePermanenceDate d where  d.periodePermanence=:pp and d.datePerm >= :debut and  d.datePerm <= :fin");

		q.setParameter("pp", pp);
		q.setParameter("debut", dto.dateDebut);
		q.setParameter("fin", dto.dateFin);

		List<PeriodePermanenceDate> mcds = q.getResultList();

		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		buf.append("Les inscriptions sur " + mcds.size() + " dates de permanences suivantes vont être supprimées:<br/>");
		for (PeriodePermanenceDate modeleContratDate : mcds)
		{
			buf.append(" - " + df.format(modeleContratDate.datePerm) + "<br/>");
		}
		buf.append("<br/>");

		q = em.createQuery("select distinct(c.periodePermanenceUtilisateur.utilisateur) from PermanenceCell c where "
				+ " c.periodePermanenceDate.periodePermanence=:pp and "
				+ " c.periodePermanenceDate.datePerm >= :debut and " 
				+ " c.periodePermanenceDate.datePerm <= :fin " 
				+ " order by c.periodePermanenceUtilisateur.utilisateur.nom,c.periodePermanenceUtilisateur.utilisateur.prenom");

		q.setParameter("pp", pp);
		q.setParameter("debut", dto.dateDebut);
		q.setParameter("fin", dto.dateFin);

		List<Utilisateur> utilisateurs = q.getResultList();

		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		return buf.toString();

	}

	@DbWrite
	public void performDeleteInscription(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodePermanence pp = em.find(PeriodePermanence.class, dto.id);

		// On selectionne toutes les cellules, puis on les supprime
		Query q = em.createQuery("select c from PermanenceCell c where " 
						+ " c.periodePermanenceDate.periodePermanence=:pp and " 
						+ " c.periodePermanenceDate.datePerm >= :debut and "
						+ " c.periodePermanenceDate.datePerm <= :fin");

		q.setParameter("pp", pp);
		q.setParameter("debut", dto.dateDebut);
		q.setParameter("fin", dto.dateFin);

		
		List<PermanenceCell> pcs = q.getResultList();
		for (PermanenceCell pc : pcs)
		{
			pc.periodePermanenceUtilisateur = null;
			pc.dateNotification = null;
		}
	}

	
	// MISE A JOUR DES ROLES ET DES UTILISATEURS SUR UNE DATE DE PERMANENCE

	@DbWrite
	public void updateRoleAndUtilisateur(PeriodePermanenceDateDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On verrouille la date 
		new PeriodePermanenceService().lockOneDate(em, dto.idPeriodePermanenceDate);
		
		PeriodePermanenceDate ppd = em.find(PeriodePermanenceDate.class, dto.idPeriodePermanenceDate);
		
		// On met à jour le nombre de place 
		ppd.nbPlace = dto.permanenceCellDTOs.size();
		
		// On efface les cellules existantes et on memorize juste les dates d'envoi
		Query q = em.createQuery("select c from PermanenceCell c WHERE c.periodePermanenceDate=:ppd");
		q.setParameter("ppd", ppd);
				
		List<PermanenceCell> pcs = q.getResultList();
		List<PermanenceCellDTO> oldCells = new ArrayList<PermanenceCellDTO>();
		for (int i = 0; i < pcs.size(); i++)
		{
			PermanenceCell pc = pcs.get(i);
			
			PermanenceCellDTO pcDto = new PeriodePermanenceService().createPermanenceCellDTO(pc);
			oldCells.add(pcDto);
			
			em.remove(pc);
		}
		
		
		// Création de toutes les cellules de permanences
		for (int i = 0; i < ppd.nbPlace; i++)
		{
			PermanenceCellDTO pcDto = dto.permanenceCellDTOs.get(i);
			
			PermanenceCell pc = new PermanenceCell();
			pc.periodePermanenceDate = ppd;
			pc.permanenceRole = em.find(PermanenceRole.class, pcDto.idRole);
			if (pcDto.idUtilisateur!=null)
			{
				pc.periodePermanenceUtilisateur = findPeriodePermanenceUtilisateur(em, pcDto.idUtilisateur, ppd.periodePermanence);
				pc.dateNotification = CollectionUtils.findMatching(oldCells, e->(e.idPeriodePermanenceUtilisateur==pcDto.idPeriodePermanenceUtilisateur && e.idRole==pcDto.idRole),e->e.dateNotification);
			}
			pc.indx = i;
			
			
			em.persist(pc);
		}
		
	}

	

}
