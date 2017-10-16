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
 package fr.amapj.service.services.maintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.GzipUtils;
import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbUtil;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.distribution.DatePermanence;
import fr.amapj.model.models.distribution.DatePermanenceUtilisateur;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.model.models.permanence.periode.PeriodePermanence;
import fr.amapj.model.models.remise.RemiseProducteur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.remiseproducteur.RemiseProducteurService;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceUtilisateurDTO;

/**
 * Permet la gestion des contrats
 * 
 *  
 *
 */
public class MaintenanceService
{
	
	private final static Logger logger = LogManager.getLogger();
	
	public MaintenanceService()
	{

	}



	// PARTIE SUPPRESSION D'UN MODELE DE CONTRAT ET DE TOUS LES CONTRATS ASSOCIES

	/**
	 * Permet de supprimer un modele de contrat et TOUS les contrats associées
	 * Ceci est fait dans une transaction en ecriture  
	 */
	@DbWrite
	public void deleteModeleContratAndContrats(Long modeleContratId)
	{
		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		
		// On supprime d'abord toutes les remises
		List<RemiseProducteur> remises = getAllRemises(em,mc);
		for (RemiseProducteur remiseProducteur : remises)
		{
			new RemiseProducteurService().deleteRemise(remiseProducteur.getId());
		}
		
		// On supprime ensuite tous les paiements
		List<Paiement> paiements = getAllPaiements(em,mc);
		for (Paiement paiement : paiements)
		{
			em.remove(paiement);
		}
		
		
		// On supprime ensuite tous les contrats
		List<Contrat> cs = getAllContrats(em, mc);
		for (Contrat contrat : cs)
		{
			new MesContratsService().deleteContrat(contrat.getId());
		}
		
		// On supprime ensuite le modele de contrat
		new GestionContratService().deleteContrat(modeleContratId);
		
	}

	
	



	private List<RemiseProducteur> getAllRemises(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select r from RemiseProducteur r  WHERE r.datePaiement.modeleContrat=:mc ORDER BY r.datePaiement.datePaiement desc");
		q.setParameter("mc", mc);
		
		List<RemiseProducteur> rps = q.getResultList();
		return rps;
	}

	
	private List<Paiement> getAllPaiements(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select p from Paiement p  WHERE p.contrat.modeleContrat=:mc");
		q.setParameter("mc", mc);
		List<Paiement> rps = q.getResultList();
		return rps;
	}


	/**
	 * 
	 */
	private List<Contrat> getAllContrats(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select c from Contrat c WHERE c.modeleContrat=:mc");
		q.setParameter("mc",mc);
		List<Contrat> cs = q.getResultList();
		return cs;
	}
	

	
	/**
	 * Permet de vider le cache de la base
	 * Ceci est fait dans une transaction en ecriture  
	 * obligatoire après requete SQL manuelle
	 */
	@DbWrite
	public void resetDatabaseCache()
	{
		EntityManager em = TransactionHelper.getEm();
		em.getEntityManagerFactory().getCache().evictAll();
	}


//	/**
//	 * Application du patch V019
//	 */
//	public String applyPatchV019()
//	{
//		StringBuffer str = new StringBuffer();
//		SpecificDbUtils.executeInAllDb(()->patch(str),false);
//		return str.toString();
//	}
//	
//	@DbWrite
//	private Void patch(StringBuffer str)
//	{
//		EntityManager em = TransactionHelper.getEm();
//		
//		String dbName = DbUtil.getCurrentDb().getDbName();
//		
//		Query q = em.createQuery("select p from EditionSpecifique p");
//
//		List<EditionSpecifique> ps = q.getResultList();
//		for (EditionSpecifique p : ps)
//		{
//			zipContent(p);
//		}
//		
//		str.append("ok pour "+dbName+"<br/>");
//		
//		return null;
//	}
//
//
//	/**
//	 * On zippe uniquement si cela n'a pas déja été fait 
//	 * @param p
//	 */
//	private void zipContent(EditionSpecifique p)
//	{
//		if (p.content==null)
//		{
//			return ;
//		}
//		if (p.content.startsWith("{")==false)
//		{
//			return;
//		}
//		
//		p.content = GzipUtils.compress(p.content);
//	}

	
	/**
	 * Application du patch V020
	 */
	public String applyPatchV020()
	{
		StringBuffer str = new StringBuffer();
		SpecificDbUtils.executeInAllDb(()->patch(str),false);
		return str.toString();
	}
	
	@DbWrite
	private Void patch(StringBuffer str)
	{
		EntityManager em = TransactionHelper.getEm();
		
		String dbName = DbUtil.getCurrentDb().getDbName();
		
		int nb= transferPermanenceData(em);
		
		str.append("Nombre de données transférées="+nb+" - ok pour "+dbName+"<br/>");
		
		logger.info("Nombre de données transférées="+nb+" - ok pour "+dbName);
		
		return null;
	}

	


	/**
	 * 
	 */
	private int transferPermanenceData(EntityManager em)
	{
		Query q = em.createQuery("select distinct(d.datePermanence) from DatePermanence d order by d.datePermanence");
			
		List<Date> ds = q.getResultList();
		
		if (ds.size()==0)
		{
			return 0; 
		}
		
		// ETAPE 1 : On crée la période de permanence 
		
		PeriodePermanenceDTO dto = new PeriodePermanenceDTO();
		
		dto.nom = "Planning de permanence";
		dto.description = "Planning de permanence";
		dto.dateFinInscription = null;
		dto.flottantDelai = 0;
		dto.nature = NaturePeriodePermanence.INSCRIPTION_NON_LIBRE;
		
		for (Date d : ds)
		{
			PeriodePermanenceDateDTO date = new PeriodePermanenceDateDTO();
			date.datePerm = d;
			date.nbPlace = countNbPlace(em,d);
			
			dto.datePerms.add(date);
		}
		
		List<Utilisateur> utilisateurs = getUtilisateurs(em);
		for (Utilisateur utilisateur : utilisateurs)
		{
			PeriodePermanenceUtilisateurDTO detail = new PeriodePermanenceUtilisateurDTO();
			detail.idUtilisateur = utilisateur.getId();
			detail.nbParticipation = countNbParticipation(em,utilisateur);
			
			dto.utilisateurs.add(detail);
		}
		
		Long idPeriodePermanence = new PeriodePermanenceService().create(dto);
			
		
		// ETAPE 2 - ON TRANSFERE LES INSCRIPTIONS
		int nb=0;
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			List<Utilisateur> us = getUtilisateurDate(date.datePerm,em);
			nb = nb+us.size();
			for (int i = 0; i < us.size(); i++)
			{
				Utilisateur utilisateur = us.get(i);
				date.permanenceCellDTOs.get(i).idUtilisateur = utilisateur.getId();
			}
			
			new DetailPeriodePermanenceService().updateDetailPeriodePermanence(date);
		}
		
		
		// ETAPE 3 - ON EFFACE LES DONNEES DE LA BASE 
		q = em.createQuery("select du from DatePermanenceUtilisateur du");
		List<DatePermanenceUtilisateur> us = q.getResultList();
		for (DatePermanenceUtilisateur u : us)
		{
			em.remove(u);
		}
		
		q = em.createQuery("select d from DatePermanence d");
		List<DatePermanence> dds = q.getResultList();
		for (DatePermanence dd : dds)
		{
			em.remove(dd);
		}
		
		// ETAPE 4 - ON REND LA PERIODE ACTIVE 
		new PeriodePermanenceService().updateEtat(EtatPeriodePermanence.ACTIF, idPeriodePermanence);
				
		return nb;
	}

	
	private List<Utilisateur> getUtilisateurDate(Date datePerm, EntityManager em)
	{
		Query q = em.createQuery("select distinct(du.utilisateur) from DatePermanenceUtilisateur du "
				+ "WHERE du.datePermanence.datePermanence=:d "
				+ "ORDER BY du.utilisateur.nom,du.utilisateur.prenom");
		q.setParameter("d", datePerm);

		List<Utilisateur> us = q.getResultList();
		return us;
	}



	private int countNbParticipation(EntityManager em, Utilisateur utilisateur)
	{
		Query q = em.createQuery("select du from DatePermanenceUtilisateur du "
				+ "WHERE du.utilisateur=:u ");
		q.setParameter("u", utilisateur);

		List<DatePermanenceUtilisateur> us = q.getResultList();
		return us.size();
	}



	private List<Utilisateur> getUtilisateurs(EntityManager em)
	{
		Query q = em.createQuery("select distinct(du.utilisateur) from DatePermanenceUtilisateur du "
				+ "ORDER BY du.utilisateur.nom,du.utilisateur.prenom");

		List<Utilisateur> us = q.getResultList();
		return us;
	}



	private int countNbPlace(EntityManager em, Date d)
	{
		Query q = em.createQuery("select distinct(du.utilisateur) from DatePermanenceUtilisateur du "
				+ "WHERE du.datePermanence.datePermanence=:d "
				+ "ORDER BY du.utilisateur.nom,du.utilisateur.prenom");
		q.setParameter("d", d);

		List<Utilisateur> us = q.getResultList();
		return us.size();
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

		
	
}
