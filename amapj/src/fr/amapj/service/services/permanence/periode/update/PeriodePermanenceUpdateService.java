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
 package fr.amapj.service.services.permanence.periode.update;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;

/**
 * Permet la gestion des modifiactions des periodes de permanences
 * 
 */
public class PeriodePermanenceUpdateService
{
	
	/**
	 * Chargement de la periode à modifier 
	 */

	public PeriodePermanenceDTO loadPeriodePermanence(Long id)
	{
		return null;
	}
	
	
	/**
	 * Modification de l'entete 
	 * @param dto
	 */
	@DbWrite
	public void updateEntete(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm(); 
		PeriodePermanence pp = em.find(PeriodePermanence.class,dto.id);
		
		pp.nom = dto.nom;
		pp.description = dto.description;
		pp.dateFinInscription = dto.dateFinInscription;
		pp.flottantDelai = dto.flottantDelai;
		
	}

	
	/**
	 * Modification des regles d'inscription 
	 * @param dto
	 */
	@DbWrite
	public void updateRegleInscription(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm(); 
		PeriodePermanence pp = em.find(PeriodePermanence.class,dto.id);
		
		pp.regleInscription = dto.regleInscription;
		
	}
	
	

	/**
	 * Ajouter des dates 
	 * 
	 */
	@DbWrite
	public void addDates(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanence p = em.find(PeriodePermanence.class, dto.id);

		// Recuperation du role par defaut 
		PermanenceRole defaultRole = new PermanenceRoleService().getOrCreateDefaultRole(em);
		
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			PeriodePermanenceDate md = new PeriodePermanenceDate();
			md.periodePermanence = p;
			md.datePerm = date.datePerm;
			md.nbPlace = date.nbPlace;
			em.persist(md);
		
			// Création de toutes les cellules de permanences
			for (int i = 0; i < date.nbPlace; i++)
			{
				PermanenceCell pc = new PermanenceCell();
				pc.periodePermanenceDate = md;
				pc.permanenceRole = defaultRole;
				pc.indx = i;
				
				em.persist(pc);
			}
		}
	}
	
	
	/**
	 * Supprimer des dates
	 */
	
	@DbRead
	public String getDeleteDateInfo(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer buf = new StringBuffer();

		PeriodePermanence pp = em.find(PeriodePermanence.class, dto.id);

		// On selectionne toutes les dates de permanences
		Query q = em.createQuery("select d from PeriodePermanenceDate d where  d.periodePermanence=:pp and d.datePerm >= :debut and  d.datePerm <= :fin ORDER BY d.datePerm");

		q.setParameter("pp", pp);
		q.setParameter("debut", dto.dateDebut);
		q.setParameter("fin", dto.dateFin);

		List<PeriodePermanenceDate> mcds = q.getResultList();

		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
		buf.append("Les " + mcds.size() + " dates de permanences suivantes vont être supprimées:<br/>");
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
	public void performDeleteDatePermanence(PeriodePermanenceDTO dto)
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
			em.remove(pc);
		}
		
		// On supprime ensuite les dates 
		q = em.createQuery("select d from PeriodePermanenceDate d where  d.periodePermanence=:pp and d.datePerm >= :debut and  d.datePerm <= :fin");
		q.setParameter("pp", pp);
		q.setParameter("debut", dto.dateDebut);
		q.setParameter("fin", dto.dateFin);
		
		
		List<PeriodePermanenceDate> ppds = q.getResultList();
		for (PeriodePermanenceDate ppd : ppds)
		{
			em.remove(ppd);
		}
	}


	/**
	 * Ajouter des utilisateurs 
	 * 
	 */
	@DbWrite
	public void addUtilisateurs(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodePermanence p = em.find(PeriodePermanence.class, dto.id);
		
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			Utilisateur utilisateur = em.find(Utilisateur.class, detail.idUtilisateur);
			
			PeriodePermanenceUtilisateur ppu = new PeriodePermanenceUtilisateur();
			ppu.nbParticipation = detail.nbParticipation;
			ppu.utilisateur = utilisateur;
			ppu.periodePermanence = p;
			
			em.persist(ppu);
		}
	}
	
	
	
	/**
	 * Modifier le nombre de participations
	 * @param existingUtilisateurs 
	 */
	@DbRead
	public String getUpdateNbParticipationInfo(PeriodePermanenceDTO dto, List<PeriodePermanenceUtilisateurDTO> existingUtilisateurs)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer info = new StringBuffer();
		List<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
		
		
		// On vérifie chaque changement et on évalue son impact
		for (int i = 0; i < existingUtilisateurs.size(); i++)
		{
			PeriodePermanenceUtilisateurDTO oldDetail = existingUtilisateurs.get(i);
			PeriodePermanenceUtilisateurDTO newDetail = dto.utilisateurs.get(i);
			
			if (newDetail.nbParticipation!=oldDetail.nbParticipation)
			{
				Utilisateur u = em.find(Utilisateur.class, newDetail.idUtilisateur);
				utilisateurs.add(u);
				
				info.append(getInfos(newDetail,oldDetail,em,u,dto.id));
				info.append("<br/>");
			}
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		buf.append("<br/>Détail des modifications:<br/>");
		buf.append(info);
		
		return buf.toString();

	}

	private String getInfos(PeriodePermanenceUtilisateurDTO newDetail, PeriodePermanenceUtilisateurDTO oldDetail, EntityManager em, Utilisateur u,Long idPeriodePermanence)
	{
		if (newDetail.nbParticipation>oldDetail.nbParticipation)
		{
			return "L'utilisateur "+u.getNom() + " " + u.getPrenom()+" doit faire "+(newDetail.nbParticipation-oldDetail.nbParticipation)+" participations supplémentaires";
		}
		
		
		String str = "L'utilisateur "+u.getNom() + " " + u.getPrenom()+" doit faire "+(oldDetail.nbParticipation-newDetail.nbParticipation)+" participations en moins.";
		
		UnePeriodePermanenceDTO cpts = new MesPermanencesService().loadCompteurPeriodePermanence(idPeriodePermanence, u.getId());
		
		if (cpts.nbInscription<=newDetail.nbParticipation)
		{
			str = str+" Il est inscrit "+cpts.nbInscription+" et doit maintenant participer "+newDetail.nbParticipation+", donc il n'est pas impacté.";
		}
		else
		{
			str = str+" Il est inscrit "+cpts.nbInscription+" et doit maintenant participer "+newDetail.nbParticipation+", donc il doit se desincrire sur "+(cpts.nbInscription-newDetail.nbParticipation)+" dates.";
		}
		return str;
	}


	@DbWrite
	public void performUpdateNbParticipation(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodePermanence pp = em.find(PeriodePermanence.class, dto.id);
		
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			PeriodePermanenceUtilisateur ppu = new DetailPeriodePermanenceService().findPeriodePermanenceUtilisateur(em, detail.idUtilisateur, pp);
			ppu.nbParticipation = detail.nbParticipation;
		}
	}

	
	
	/**
	 * Enlever des participants  
	 */
	@DbRead
	public String getDeleteUtilisateurInfo(List<PeriodePermanenceUtilisateurDTO> utilisateurToSuppress, Long idPeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();

		StringBuffer info = new StringBuffer();
		List<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
		
		
		// On vérifie chaque changement et on évalue son impact
		for (PeriodePermanenceUtilisateurDTO detail : utilisateurToSuppress)
		{
			Utilisateur u = em.find(Utilisateur.class, detail.idUtilisateur);
			utilisateurs.add(u);
				
			info.append(getDeleteUtilisateurInfos(detail,em,u,idPeriodePermanence));
			info.append("<br/>");
			
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("Les utilisateurs suivants vont être enlevés de cette période de permanence<br/>");
		buf.append(UtilisateurUtil.getUtilisateurImpactes(utilisateurs));
		buf.append("<br/><br/>Détail des inscriptions de ces utilisateurs :<br/>");
		buf.append(info);
		
		return buf.toString();

	}

	
	/***
	 * 
	 * 
	 */
	private String getDeleteUtilisateurInfos(PeriodePermanenceUtilisateurDTO detail, EntityManager em, Utilisateur u,Long idPeriodePermanence)
	{
		List<PeriodePermanenceDate> dates = getDateUtilisateur(em,u,idPeriodePermanence);
		
		if (dates.size()==0)
		{
			return "L'utilisateur "+u.getNom() + " " + u.getPrenom()+" n'était pas inscrit.";
		}
		else
		{
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
			
			String str = "L'utilisateur "+u.getNom() + " " + u.getPrenom()+" était inscrit "+dates.size()+" fois :";
			str = str +CollectionUtils.asStdString(dates, e->df.format(e.datePerm));
			str = str+". Ces incriptions vont être effacées.";
			
			return str;
		}
		
	}


	private List<PeriodePermanenceDate> getDateUtilisateur(EntityManager em, Utilisateur u, Long idPeriodePermanence)
	{
		Query q = em.createQuery("select c.periodePermanenceDate from PermanenceCell c WHERE "
				+ " c.periodePermanenceDate.periodePermanence.id=:id and "
				+ " c.periodePermanenceUtilisateur.utilisateur = :u "
				+ " order by c.periodePermanenceDate.datePerm");
		
		q.setParameter("id", idPeriodePermanence);
		q.setParameter("u", u);
		
		List<PeriodePermanenceDate> pcs = q.getResultList();
		return pcs;
	}


	@DbWrite
	public void performDeleteUtilisateur(Long idPeriodePermanence,List<PeriodePermanenceUtilisateurDTO> utilisateurToSuppress)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodePermanence pp = em.find(PeriodePermanence.class, idPeriodePermanence);
		
		for (PeriodePermanenceUtilisateurDTO detail : utilisateurToSuppress)
		{
			performDeleteOneUtilisateur(em,detail,pp);
		}
		
	}
	
	
	private void performDeleteOneUtilisateur(EntityManager em, PeriodePermanenceUtilisateurDTO detail, PeriodePermanence pp)
	{
		PeriodePermanenceUtilisateur ppu = new DetailPeriodePermanenceService().findPeriodePermanenceUtilisateur(em, detail.idUtilisateur, pp);
		
		// On selectionne toutes les cellules, puis on les supprime
		Query q = em.createQuery("select c from PermanenceCell c where c.periodePermanenceUtilisateur=:ppu");

		q.setParameter("ppu", ppu);
	
		List<PermanenceCell> pcs = q.getResultList();
		for (PermanenceCell pc : pcs)
		{
			em.remove(pc);
		}
		
		// On supprime ensuite le ppu
		em.remove(ppu);
	}

}
