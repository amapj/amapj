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
 package fr.amapj.service.services.permanence.detailperiode.calculauto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.common.DebugUtil;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceUtilisateur;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;

/**
 * Permet le calcul automatique des inscriptions sur une periode de permanence 
 * 
 */
public class CalculAutoPeriodePermanenceService
{
	
	
	/**
	 * Permet le calcul automatique des permanences
	 * 
	 * Le debut de l'algorithme est toujours le même
	 *  1/ On met à jour les utilisateurs pour enlever les fois ou ils sont dejà placés
	 *  2/ On crée une liste de UtilisateurInfo , avec pour chaque utilisateur la liste de ces dates de livraison et qui correspondent à cette période de permanence 
	 * 
	 * 
	 * Ensuite 2 algorithmes sont possibles
	 * 
	 *  STANDARD :  
	 *   On trie les UtilisateurInfo par (nombre de fois à placer , nom , prenom) 
	 *   On itère ensuite sur chaque couple (utilisateur, numero du placement) 
	 * 	     -> on calcule une date ref = date random comprise dans la période  
	 *       -> on trie toutes les places disponibles suivant (proximité avec la date ref)
	 *       -> on prend les places une par une, et on voit si on peut l'affecter à l'utilisateur (il faut que l'utilisateur ne soit pas deja inscrit à cette date et qu'il reste de la place)
	 * 
	 *  UNIQUEMENT_SI_COMMANDE
	 *    
	 *   On trie les utilisateurs par (nombre de fois commande croissant , nom , prenom) 
	 *   On itère ensuite sur chaque couple (utilisateur, numero du placement) 
	 * 	     -> on calcule une date random comprise dans la période  
	 *       -> on trie toutes les places disponibles suivant (utilisateur a commandé , proximité avec la date ref)
	 *       -> on prend les places une par une, et on voit si on peut l'affecter à l'utilisateur (il faut que l'utilisateur ne soit pas deja inscrit à cette date et qu'il reste de la place)
	 * 
	 */
	@DbWrite
	public String performPlanification(Long idPeriodePermanence)
	{
		EntityManager em = TransactionHelper.getEm();
		
		StringBuilder sb = new StringBuilder();
		
		// Chargement des données 
		PeriodePermanenceDTO dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);		
		
		// On met à jour les utilisateurs pour enlever les fois ou ils sont dejà placés 
		updateWithExistingInscription(em,dto);
		
		// On met à jour les utilisateurs pour calculer les dates de livraisons
		List<UtilisateurInfo> uInfos = createUtilisateurInfo(em,dto);

		// On fait ensuite l'affectation automatique  
		performAffectationStandard(em,uInfos,sb,dto);
		
		// On sauvegarde ensuite tout ca en base
		for (PeriodePermanenceDateDTO datePerm : dto.datePerms)
		{
			for (PermanenceCellDTO cell : datePerm.permanenceCellDTOs)
			{
				if (cell.idPeriodePermanenceUtilisateur!=null)
				{
					PermanenceCell pc = em.find(PermanenceCell.class, cell.idPermanenceCell);
					pc.periodePermanenceUtilisateur = em.find(PeriodePermanenceUtilisateur.class, cell.idPeriodePermanenceUtilisateur);
				}
			}
		}
		
		return sb.toString();
	}
	

	/**
	 * On met à jour les utilisateurs pour enlever les fois ou ils sont dejà placés 
	 */
	private void updateWithExistingInscription(EntityManager em, PeriodePermanenceDTO dto)
	{
		for (PeriodePermanenceDateDTO datePerm : dto.datePerms)
		{
			for (PermanenceCellDTO cell : datePerm.permanenceCellDTOs)
			{
				if (cell.idUtilisateur!=null)
				{
					PeriodePermanenceUtilisateurDTO util = findPeriodePermanenceUtilisateurDTO(dto,cell.idUtilisateur);
					util.nbParticipation--;
				}
			}
		}
	}
	

	private PeriodePermanenceUtilisateurDTO findPeriodePermanenceUtilisateurDTO(PeriodePermanenceDTO dto, Long idUtilisateur)
	{
		for (PeriodePermanenceUtilisateurDTO utilisateur : dto.utilisateurs)
		{
			if (utilisateur.idUtilisateur==idUtilisateur)
			{
				return utilisateur;
			}
		}
		throw new AmapjRuntimeException();
	}
	
	
	/**
	 * On met à jour les utilisateurs avec leur info de commande
	 * @param em
	 * @param dto
	 * @return 
	 */
	private List<UtilisateurInfo> createUtilisateurInfo(EntityManager em, PeriodePermanenceDTO dto)
	{
		//
		List<UtilisateurInfo> res = new ArrayList<CalculAutoPeriodePermanenceService.UtilisateurInfo>();
		
		// On extrait la liste des dates
		List<Date> dates = extractDate(dto.datePerms);
		
		// Pour chaque utilisateur, on calcule ses dates de livraison
		for (PeriodePermanenceUtilisateurDTO utilisateur : dto.utilisateurs)
		{
			List<Date> datLivs =  geDateLivraison(utilisateur.idUtilisateur,dates,em);
			
			// On calcule ensuite chaque UtilisateurInfo et on l'ajoute dans la liste 
			for (int i = 0; i < utilisateur.nbParticipation; i++)
			{
				UtilisateurInfo uInfo = new UtilisateurInfo(i, utilisateur, datLivs);
				
				res.add(uInfo);
			}
		}
		return res;
	}
	
	
	private List<Date> extractDate(List<PeriodePermanenceDateDTO> dates)
	{
		List<Date> res  = new ArrayList<>();
		for (PeriodePermanenceDateDTO date : dates)
		{
			res.add(date.datePerm);
			
		}
		return res;
	}
	
	
	private List<Date> geDateLivraison(Long idUtilisateur, List<Date> dates, EntityManager em)
	{
		Query q = em.createQuery("select distinct(c.modeleContratDate.dateLiv) from ContratCell c WHERE " +
				"c.contrat.utilisateur=:u and " +
				"c.modeleContratDate.dateLiv in :dates " +
				"order by c.modeleContratDate.dateLiv");
		q.setParameter("u", em.find(Utilisateur.class, idUtilisateur));
		q.setParameter("dates", dates);
		
		List<Date> ds = q.getResultList();
		return ds;
	}
	
	

	/**
	 * Affectation automatique avec l'algorithme standard  
	 *
	 */
	
	private void performAffectationStandard(EntityManager em, List<UtilisateurInfo> uInfos, StringBuilder sb,PeriodePermanenceDTO dto)
	{
		// On trie les utilisateurs  par (numero de placement decroissant, nombre de date de livraison croissant , nom , prenom) 
		CollectionUtils.sort(uInfos , e->e.numPlacement,false,e->e.datLivs.size() , true, e->e.utilisateur.nom,true,e->e.utilisateur.prenom,true);
		
		//
		Random random = new Random(0);
	
		// Pour chaque UtilisateurInfo, on le place 
		for (UtilisateurInfo uInfo : uInfos)
		{	
			placeUtilisateur(uInfo,sb,dto,random);
		}
	}

	/**
	 * on calcule le milieu de période 
	 * 
	 * on trie toutes les places disponibles suivant ( la date appartient à la période / utilisateur a commandé / proximité avec la date de reference)
	 * 
	 * on prend les places une par une, et on voit si on peut l'affecter à l'utilisateur (il faut que l'utilisateur ne soit pas deja inscrit à cette date et qu'il reste de la place
	 * @param random 
	 * 
	 * @param datLivs 
	 *  
	 */
	private void placeUtilisateur(UtilisateurInfo uInfo, StringBuilder sb,PeriodePermanenceDTO dto, Random random)
	{
		int numPlacement = uInfo.numPlacement;
		PeriodePermanenceUtilisateurDTO utilisateur = uInfo.utilisateur;
		
		//
		DateInfo di = computeDateRandomInPeriode(dto,numPlacement,utilisateur.nbParticipation,random);
		 
		// On trie ensuite 
		CollectionUtils.sort(dto.datePerms,e->appartientPeriode(e,di),e->hasOrder(e,uInfo.datLivs),e->deltaFromRef(e,di));
		
		// On affecte ensuite 
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			PermanenceCellDTO cell = canAffect(date,utilisateur);
			if (cell!=null)
			{
				cell.idPeriodePermanenceUtilisateur = utilisateur.idPeriodePermanenceUtilisateur;
				cell.idUtilisateur = utilisateur.idUtilisateur;
				return;
			}
		}	
		
		// On n'est pas arrivé à placer l'utilisateur 
		sb.append("Impossible de placer l'utilisateur "+utilisateur.nom+" "+utilisateur.prenom+" pour sa session numéro "+numPlacement+"<br/>");
	}

	private int appartientPeriode(PeriodePermanenceDateDTO e, DateInfo di)
	{
		// Si dans la periode , on retourne 0 (sera donc en premiere position)  
		if (DateUtils.isInIntervalle(e.datePerm,di.debut,di.fin))
		{
			return 0;
		}
		return 1;
	}
	
	private int hasOrder(PeriodePermanenceDateDTO e, List<Date> datLivs)
	{
		// Si il a commandé , on retourne 0 (sera donc en premiere position)  
		if (datLivs.contains(e.datePerm))
		{
			return 0;
		}
		return 1;
	}
	
	private int deltaFromRef(PeriodePermanenceDateDTO e, DateInfo di)
	{
		return Math.abs(DateUtils.getDeltaDay(e.datePerm,di.ref));
	}

	
	private PermanenceCellDTO canAffect(PeriodePermanenceDateDTO date, PeriodePermanenceUtilisateurDTO utilisateur)
	{
		// Est ce que l'utilisateur est déjà inscrit ? 
		if (date.isInscrit(utilisateur.idUtilisateur))
		{
			return null;
		}
		
		// Est ce que c'est complet ? 
		if (date.isDateComplete())
		{
			return null;
		}
		
		// On cherche une place
		for (PermanenceCellDTO cell : date.permanenceCellDTOs)
		{
			if (cell.idUtilisateur==null)
			{
				return cell;
			}
		}
		
		throw new AmapjRuntimeException();
	}
	

	/**
	 * Calcule une date random dans la période , le debut et la fin 
	 * 
	 */
	private DateInfo computeDateRandomInPeriode(PeriodePermanenceDTO dto, int numPlacement, int nbTotalPlacement,Random random)
	{
		// Calcul de la distance entre la periode
		double delta = DateUtils.getDeltaDay(dto.dateDebut, dto.dateFin);
		
		// Longueur de une période 
		double pas = delta / nbTotalPlacement;
		
		DateInfo res = new DateInfo();
		
		res.debut = DateUtils.addDays(dto.dateDebut, (int) ((     0             +numPlacement)*pas));
		res.ref =   DateUtils.addDays(dto.dateDebut, (int) ((random.nextDouble()+numPlacement)*pas)); 
		res.fin =   DateUtils.addDays(dto.dateDebut, (int) ((     1             +numPlacement)*pas));
		
		
		
		return res;
	}
	
	public class DateInfo
	{
		public Date debut;
		public Date fin;
		public Date ref;
	}
	

	/**
	 * Represente une participation d'un utilisateur, qu'il faudra placer  
	 *
	 */
	public class UtilisateurInfo 
	{
		// 
		public int numPlacement;
		
		public PeriodePermanenceUtilisateurDTO utilisateur;
		
		public List<Date> datLivs;

		public UtilisateurInfo(int numPlacement, PeriodePermanenceUtilisateurDTO utilisateur, List<Date> datLivs)
		{	
			this.numPlacement = numPlacement;
			this.utilisateur = utilisateur;
			this.datLivs = datLivs;
		}
		
	}
	
	
	
}
