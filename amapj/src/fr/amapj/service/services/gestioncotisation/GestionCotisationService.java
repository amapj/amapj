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
 package fr.amapj.service.services.gestioncotisation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.IdentifiableUtil;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.cotisation.EtatPaiementAdhesion;
import fr.amapj.model.models.cotisation.PeriodeCotisation;
import fr.amapj.model.models.cotisation.PeriodeCotisationUtilisateur;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.mescontrats.AdhesionDTO;
import fr.amapj.service.services.mescontrats.AdhesionDTO.AffichageOnly;
import fr.amapj.service.services.mescontrats.MesContratsDTO;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;

/**
 * Permet la gestion des cotisations
 * 
 */
public class GestionCotisationService
{
	
	private final static Logger logger = LogManager.getLogger();

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DE TOUTES LES PERIODES DE COTISATIONS

	/**
	 * Permet de charger la liste de toutes les periodes de cotisations
	 */
	@DbRead
	public List<PeriodeCotisationDTO> getAll()
	{
		EntityManager em = TransactionHelper.getEm();

		List<PeriodeCotisationDTO> res = new ArrayList<>();

		Query q = em.createQuery("select a from PeriodeCotisation a");

		List<PeriodeCotisation> ps = q.getResultList();
		for (PeriodeCotisation p : ps)
		{
			PeriodeCotisationDTO dto = createPeriodeCotisationDto(em, p);
			res.add(dto);
		}

		return res;

	}

	public PeriodeCotisationDTO createPeriodeCotisationDto(EntityManager em, PeriodeCotisation a)
	{
		PeriodeCotisationDTO dto = new PeriodeCotisationDTO();
		
		dto.id = a.getId();
		dto.nom = a.getNom();
		dto.montantMini = a.getMontantMini();
		dto.montantConseille = a.getMontantConseille();
		dto.dateDebutInscription = a.getDateDebutInscription();
		dto.dateFinInscription = a.getDateFinInscription();
		dto.textPaiement = a.getTextPaiement();
		dto.libCheque = a.getLibCheque();
		dto.dateRemiseCheque =a.getDateRemiseCheque();
		dto.dateDebut = a.getDateDebut();
		dto.dateFin = a.getDateFin();
		dto.idBulletinAdhesion = IdentifiableUtil.getId(a.getBulletinAdhesion());
		
		// Champs calculés
		dto.nbAdhesion = getNbAdhesion(em,a);
		dto.mntTotalAdhesion = getMntTotalAdhesion(em,a);
		dto.nbPaiementDonnes = getNbPaiement(em,a,EtatPaiementAdhesion.ENCAISSE);
		dto.nbPaiementARecuperer = getNbPaiement(em,a,EtatPaiementAdhesion.A_FOURNIR);
		
		return dto;
	}

	
	private PeriodeCotisationUtilisateurDTO createPeriodeCotisationUtilisateurDto(EntityManager em, PeriodeCotisationUtilisateur a)
	{
		PeriodeCotisationUtilisateurDTO dto = new PeriodeCotisationUtilisateurDTO();

		dto.dateAdhesion = a.getDateAdhesion();
		dto.dateReceptionCheque = a.getDateReceptionCheque();
		dto.etatPaiementAdhesion = a.getEtatPaiementAdhesion();
		dto.id = a.getId();
		dto.idPeriodeCotisation = a.getPeriodeCotisation().getId();
		dto.idUtilisateur = a.getUtilisateur().getId();
		dto.nomUtilisateur = a.getUtilisateur().getNom();
		dto.prenomUtilisateur = a.getUtilisateur().getPrenom();
		dto.montantAdhesion = a.getMontantAdhesion();
		dto.typePaiementAdhesion = a.getTypePaiementAdhesion();
		
		return dto;
	}

	private int getNbAdhesion(EntityManager em, PeriodeCotisation pc)
	{
		Query q = em.createQuery("select count(p.id) from PeriodeCotisationUtilisateur p WHERE p.periodeCotisation=:pc");
		q.setParameter("pc", pc);
		return LongUtils.toInt(q.getSingleResult());
	}

	private int getMntTotalAdhesion(EntityManager em, PeriodeCotisation pc)
	{
		Query q = em.createQuery("select sum(p.montantAdhesion) from PeriodeCotisationUtilisateur p WHERE p.periodeCotisation=:pc");
		q.setParameter("pc", pc);
		return LongUtils.toInt(q.getSingleResult());
	}

	private int getNbPaiement(EntityManager em, PeriodeCotisation pc,EtatPaiementAdhesion epc)
	{
		Query q = em.createQuery("select count(p.id) from PeriodeCotisationUtilisateur p WHERE p.periodeCotisation=:pc and p.etatPaiementAdhesion=:epc");
		q.setParameter("pc", pc);
		q.setParameter("epc", epc);
		return LongUtils.toInt(q.getSingleResult());
	}

	

	// CREATION D'UNE PERIODE DE COTISATION
	@DbWrite
	public void createOrUpdate(PeriodeCotisationDTO dto) throws OnSaveException
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodeCotisation a;
		if (dto.id==null)
		{
			a = new PeriodeCotisation();
		}
		else
		{
			a = em.find(PeriodeCotisation.class, dto.id);
		}

		a.setNom(dto.nom);
		a.setMontantMini(dto.montantMini);
		a.setMontantConseille(dto.montantConseille);
		a.setDateDebutInscription(dto.dateDebutInscription);
		a.setDateFinInscription(dto.dateFinInscription);
		a.setTextPaiement(dto.textPaiement);
		a.setLibCheque(dto.libCheque);
		a.setDateRemiseCheque(dto.dateRemiseCheque);
		a.setDateDebut(dto.dateDebut);
		a.setDateFin(dto.dateFin);
		a.setBulletinAdhesion(IdentifiableUtil.findIdentifiableFromId(EditionSpecifique.class, dto.idBulletinAdhesion, em));
				
		if (dto.id==null)
		{
			em.persist(a);
		}

	}



	// PARTIE SUPPRESSION  D'UNE PERIODE DE COTISATION

	/**
	 * Permet de supprimer une periode de cotisation Ceci est fait dans une transaction en ecriture
	 */
	@DbWrite
	public void delete(final Long id)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodeCotisation a = em.find(PeriodeCotisation.class, id);

		em.remove(a);
	}
	

	/*
	 * PARTIE GESTION DES ADHESIONS D'UN UTILISATEUR
	 */

	
	
	public void computeAdhesionInfo(EntityManager em, MesContratsDTO res, Utilisateur user)
	{
		res.adhesionDTO.idUtilisateur = user.getId();
		
		// Récupération de la liste des cotisations
		Query q = em.createQuery("select p from PeriodeCotisation p " +
				"WHERE p.dateDebutInscription<=:d and p.dateFinInscription>=:d");

		Date d = DateUtils.getDateWithNoTime();
		q.setParameter("d",d);
		

		List<PeriodeCotisation> periodeCotisations = q.getResultList();
		
		logger.debug("Nombre de periodes de cotisation = {} ",periodeCotisations.size());
		
		// Cas pas de gestion des cotisations
		if (periodeCotisations.size()==0)
		{
			res.adhesionDTO.periodeCotisationDTO = null;
			
			// On recherche si on s'est inscrit récemment  (moins de 30 jours), pour affichage uniquement 
			res.adhesionDTO.affichageOnly = findAdhesionRecente(em,user);
			
			return;
		}
		
		// Cas erreur de paramétrage
		if (periodeCotisations.size()>1)
		{
			throw new AmapjRuntimeException("Erreur dans le paramétrage : il y a deux périodes de cotisations pour la date du jour");
		}
		
		PeriodeCotisation periodeCotisation = periodeCotisations.get(0);
		res.adhesionDTO.periodeCotisationDTO = createPeriodeCotisationDto(em, periodeCotisation);
		
		// Récupération de la cotisation
		q = em.createQuery("select pu from PeriodeCotisationUtilisateur pu " +
						"WHERE pu.periodeCotisation=:p and pu.utilisateur=:u");
		q.setParameter("p",periodeCotisation);
		q.setParameter("u",user);
		
		List<PeriodeCotisationUtilisateur> periodeCotisationUtilisateurs = q.getResultList();
		
		// L'utilisateur n'a pas adhéré
		if (periodeCotisationUtilisateurs.size()==0)
		{
			res.adhesionDTO.periodeCotisationUtilisateurDTO = null;
			return;
		}
		
		// Cas erreur
		if (periodeCotisationUtilisateurs.size()>1)
		{
			throw new AmapjRuntimeException("Il y a deux adhesions pour la même personne");
		}
		
		PeriodeCotisationUtilisateur periodeCotisationUtilisateur = periodeCotisationUtilisateurs.get(0);
		res.adhesionDTO.periodeCotisationUtilisateurDTO = createPeriodeCotisationUtilisateurDto(em, periodeCotisationUtilisateur);
			
	}
	
	/**
	 * Les adhesions sont terminées, mais on cherche une adhesion récente pour affichage uniquement 
	 * (moins de 30 jours)
	 * 
	 * @param em
	 * @param user
	 * @return
	 */
	private AffichageOnly findAdhesionRecente(EntityManager em, Utilisateur user)
	{
		// Récupération de la cotisation
		Query q = em.createQuery("select pu from PeriodeCotisationUtilisateur pu " +
						"WHERE pu.utilisateur=:u "+
						"ORDER BY pu.periodeCotisation.dateFinInscription desc");
		
		q.setParameter("u",user);
		
		List<PeriodeCotisationUtilisateur> pcus = q.getResultList();
		
		if (pcus.size()==0)
		{
			return null;
		}
		
		PeriodeCotisationUtilisateur pcu = pcus.get(0);
	
		// SI c'est passé de plus de 30 jours, on oublie
		Date dateRef = DateUtils.getDateWithNoTime();
		dateRef = DateUtils.addDays(dateRef, -30);
		if (pcu.getPeriodeCotisation().getDateFinInscription().before(dateRef))
		{
			return null;
		}
		
		AffichageOnly lien = new AffichageOnly();
		lien.idPeriode = pcu.getPeriodeCotisation().getId();
		lien.idPeriodeUtilisateur = pcu.getId();
		lien.nomPeriode = pcu.getPeriodeCotisation().getNom();
		lien.idBulletin = IdentifiableUtil.getId(pcu.getPeriodeCotisation().getBulletinAdhesion());
		lien.montantAdhesion = pcu.getMontantAdhesion();
		
		return lien;
	}

	@DbWrite
	public void createOrUpdateAdhesion(AdhesionDTO dto, int montant)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodeCotisationUtilisateur pcu;
		if (dto.isCotisant())
		{
			pcu = em.find(PeriodeCotisationUtilisateur.class, dto.periodeCotisationUtilisateurDTO.id); 
		}
		else
		{
			pcu = new PeriodeCotisationUtilisateur();
			pcu.setPeriodeCotisation(em.find(PeriodeCotisation.class, dto.periodeCotisationDTO.id));
			pcu.setUtilisateur(em.find(Utilisateur.class, dto.idUtilisateur));
		}
		
		pcu.setDateAdhesion(DateUtils.getDate());
		pcu.setMontantAdhesion(montant);
		
		if (dto.isCotisant()==false)
		{
			em.persist(pcu);
		}
		
	}

	@DbWrite
	public void deleteAdhesion(Long idItemToSuppress)
	{
		EntityManager em = TransactionHelper.getEm();

		PeriodeCotisationUtilisateur a = em.find(PeriodeCotisationUtilisateur.class, idItemToSuppress);

		em.remove(a);
		
	}
	
	/*
	 * PARTIE BILAN GLOBAL POUR UNE PERIODE
	 */
	@DbRead
	public BilanAdhesionDTO loadBilanAdhesion(Long idPeriodeCotisation)
	{
		BilanAdhesionDTO res = new BilanAdhesionDTO();
		EntityManager em = TransactionHelper.getEm();

		PeriodeCotisation p = em.find(PeriodeCotisation.class, idPeriodeCotisation);

		res.periodeCotisationDTO = createPeriodeCotisationDto(em, p);
		
		
		// Récupération de la cotisation
		Query q = em.createQuery("select pu from PeriodeCotisationUtilisateur pu " +
								"WHERE pu.periodeCotisation=:p order by pu.utilisateur.nom, pu.utilisateur.prenom");
		q.setParameter("p",p);
		
		List<PeriodeCotisationUtilisateur> periodeCotisationUtilisateurs = q.getResultList();
		for (PeriodeCotisationUtilisateur pcu : periodeCotisationUtilisateurs)
		{
			PeriodeCotisationUtilisateurDTO pcuDTO = createPeriodeCotisationUtilisateurDto(em, pcu);
			res.utilisateurDTOs.add(pcuDTO);
		}
		
		return res;
	}
	
	@DbWrite
	public void receptionMasseAdhesion(List<PeriodeCotisationUtilisateurDTO> dtos)
	{
		EntityManager em = TransactionHelper.getEm();
		
		for (PeriodeCotisationUtilisateurDTO pcuDto : dtos)
		{
			PeriodeCotisationUtilisateur pcu = em.find(PeriodeCotisationUtilisateur.class, pcuDto.id);
			pcu.setDateReceptionCheque(pcuDto.dateReceptionCheque);
			pcu.setEtatPaiementAdhesion(pcuDto.etatPaiementAdhesion);
		}
	}
	
	
	/**
	 * Récupère la liste de tous les utilisateurs qui ne sont pas adherents sur la periode
	 * indiquée et qui sont actifs 
	 * 
	 * @param idPeriodeCotisation
	 * @return
	 */
	@DbRead
	public List<Utilisateur> getAllUtilisateurSansAdhesion(Long idPeriodeCotisation)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Query q = em.createQuery("select u from Utilisateur u where "
				+ " u.etatUtilisateur=:etat and "
				+ " u.id not in (select p.utilisateur.id from PeriodeCotisationUtilisateur p where p.periodeCotisation.id=:idPeriode) "
				+ " order by u.nom,u.prenom");
		q.setParameter("etat", EtatUtilisateur.ACTIF);
		q.setParameter("idPeriode", idPeriodeCotisation);
		List<Utilisateur> us = q.getResultList();
		
		
		return us;
	}
	
	
	/**
	 * Récupère la liste de tous les utilisateurs qui sont adherents sur la periode
	 * indiquée et qui sont actifs 
	 * 
	 * @param idPeriodeCotisation
	 * @return
	 */
	@DbRead
	public List<Utilisateur> getAllUtilisateurAvecAdhesion(Long idPeriodeCotisation)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodeCotisation p = em.find(PeriodeCotisation.class, idPeriodeCotisation);
		
		Query q = em.createQuery("select pu.utilisateur from PeriodeCotisationUtilisateur pu " 
									+ "WHERE pu.periodeCotisation=:p  AND "
									+ " pu.utilisateur.etatUtilisateur=:etat "
									+ " order by pu.utilisateur.nom, pu.utilisateur.prenom");
		q.setParameter("p",p);
		q.setParameter("etat", EtatUtilisateur.ACTIF);
		
		List<Utilisateur> us = q.getResultList();
		
		return us;
	}
	
	
	

	
	/**
	 * Permet l'ajout ou la mise à jour d'une cotisation dans l'écran de gestion des cotisations
	 * @param dto
	 */
	@DbWrite
	public void createOrUpdateCotisation(boolean create, PeriodeCotisationUtilisateurDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		PeriodeCotisationUtilisateur pcu;
		if (create==false)
		{
			pcu = em.find(PeriodeCotisationUtilisateur.class, dto.id); 
		}
		else
		{
			pcu = new PeriodeCotisationUtilisateur();
			pcu.setPeriodeCotisation(em.find(PeriodeCotisation.class, dto.idPeriodeCotisation));
			pcu.setUtilisateur(em.find(Utilisateur.class, dto.idUtilisateur));
			pcu.setDateAdhesion(DateUtils.getDate());
		}
		
		pcu.setEtatPaiementAdhesion(dto.etatPaiementAdhesion);
		pcu.setMontantAdhesion(dto.montantAdhesion);
		pcu.setTypePaiementAdhesion(dto.typePaiementAdhesion);
		
		// On met à jour la date de réception du chèque si elle n'est pas connu et que l'état est ENCAISSE
		if (pcu.getDateReceptionCheque()==null && dto.etatPaiementAdhesion==EtatPaiementAdhesion.ENCAISSE)
		{
			pcu.setDateReceptionCheque(DateUtils.getDate());
		}
		
		
		if (create==true)
		{
			em.persist(pcu);
		}	
	}
}
