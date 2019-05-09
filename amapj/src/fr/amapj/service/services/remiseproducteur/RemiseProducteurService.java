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
 package fr.amapj.service.services.remiseproducteur;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDatePaiement;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.remise.RemiseProducteur;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Permet la gestion des remises aux producteurs
 * 
 * 
 * 
 */
public class RemiseProducteurService
{

	public RemiseProducteurService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES REMISES DEJA FAITES

	/**
	 * Permet de charger la liste de tous les remises dans une transaction en lecture
	 * 
	 * Retourne la liste des remises, ordonnée par ordre de date
	 */
	@DbRead
	public List<RemiseDTO> getAllRemise(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		List<RemiseDTO> res = new ArrayList<RemiseDTO>();

		Query q = em.createQuery("select r from RemiseProducteur r " + "WHERE r.datePaiement.modeleContrat=:mc " + "ORDER BY r.datePaiement.datePaiement");
		q.setParameter("mc", mc);

		List<RemiseProducteur> remises = q.getResultList();

		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
		for (RemiseProducteur remise : remises)
		{
			RemiseDTO mcInfo = createRemiseInfo(em, remise, df);
			res.add(mcInfo);
		}

		return res;

	}

	/**
	 * 
	 * TODO on pourrait vérifier à ce niveau si il y a un écart entre la somme des montants des cheques et le montant global de la remise
	 * 
	 * @param em
	 * @param remise
	 * @param df
	 * @return
	 */
	public RemiseDTO createRemiseInfo(EntityManager em, RemiseProducteur remise, SimpleDateFormat df)
	{
		RemiseDTO info = new RemiseDTO();

		info.idModeleContrat = remise.getDatePaiement().getModeleContrat().getId();
		info.dateCreation = remise.getDateCreation();
		info.dateReelleRemise = remise.getDateRemise();
		info.dateTheoRemise = remise.getDatePaiement().getDatePaiement();
		info.id = remise.getId();
		info.mnt = remise.getMontant();
		info.moisRemise = df.format(info.dateTheoRemise);
		info.nbCheque = getNbCheque(em, remise);

		return info;
	}

	private int getNbCheque(EntityManager em, RemiseProducteur remise)
	{
		Query q = em.createQuery("select count(p) from Paiement p WHERE p.remise=:r");
		q.setParameter("r", remise);

		return ((Long) q.getSingleResult()).intValue();
	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES REMISES A VENIR

	/**
	 * 
	 */
	@DbRead
	public List<RemiseDTO> getNextRemise(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		List<ModeleContratDatePaiement> dates = getDatePaiementSansRemise(em, mc);
		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");

		List<RemiseDTO> res = new ArrayList<RemiseDTO>();
		for (ModeleContratDatePaiement modeleContratDatePaiement : dates)
		{
			RemiseDTO dto = createVirtualRemise(modeleContratDatePaiement, em, idModeleContrat, df);
			res.add(dto);
		}
		return res;
	}

	private RemiseDTO createVirtualRemise(ModeleContratDatePaiement datePaiement, EntityManager em, Long idModeleContrat, SimpleDateFormat df)
	{
		RemiseDTO info = new RemiseDTO();
		info.idModeleContratDatePaiement = datePaiement.getId();
		info.dateTheoRemise = datePaiement.getDatePaiement();
		info.idModeleContrat = idModeleContrat;
		info.moisRemise = df.format(info.dateTheoRemise);

		List<Paiement> paiements = getPaiement(em, datePaiement);
		info.nbCheque = paiements.size();
		info.mnt = 0;

		for (Paiement paiement : paiements)
		{
			info.mnt = info.mnt + paiement.getMontant();

			PaiementRemiseDTO dto = new PaiementRemiseDTO();
			dto.idPaiement = paiement.getId();
			dto.montant = paiement.getMontant();
			dto.nomUtilisateur = paiement.getContrat().getUtilisateur().getNom();
			dto.prenomUtilisateur = paiement.getContrat().getUtilisateur().getPrenom();
			dto.etatPaiement = paiement.getEtat();
			dto.commentaire1 = paiement.getCommentaire1();
			dto.commentaire2 = paiement.getCommentaire2();

			info.paiements.add(dto);
		}

		return info;
	}

	// PREPARATION DE LA REMISE A VENIR

	@DbRead
	public RemiseDTO prepareRemise(Long idModeleContrat)
	{
		List<RemiseDTO> nextRemises = getNextRemise(idModeleContrat);

		//
		if (nextRemises.size() == 0)
		{
			RemiseDTO dto = new RemiseDTO();
			dto.preparationRemiseFailed = true;
			dto.messageRemiseFailed = "Il n'y a plus de remise à faire pour ce contrat";
			return dto;
		}

		RemiseDTO nextRemise = nextRemises.get(0);

		List<String> chequesManquants = getChequesManquants(nextRemise);

		if (chequesManquants.size() != 0)
		{
			nextRemise.preparationRemiseFailed = true;
			nextRemise.messageRemiseFailed = "Il n'est pas possible de réaliser la remise. Il manque les chèques de : <br/>"
					+ StringUtils.asString(chequesManquants, "<br/>") + getInfo();
			return nextRemise;
		}

		nextRemise.preparationRemiseFailed = false;
		return nextRemise;
	}

	private String getInfo()
	{
		return "<br/><br/>Pour résoudre le problème, vous devez aller dans Réception des chèques, et supprimer les chèques qui vous manquent, ou alors les reporter sur un autre mois"; 
	}

	private List<String> getChequesManquants(RemiseDTO nextRemise)
	{
		List<String> strs = new ArrayList<String>();
		// On vérifie que tous les paiements sont bons
		for (PaiementRemiseDTO paiement : nextRemise.paiements)
		{
			if (paiement.etatPaiement != EtatPaiement.AMAP)
			{
				strs.add(paiement.nomUtilisateur + " " + paiement.prenomUtilisateur + " - "
						+ new CurrencyTextFieldConverter().convertToString(paiement.montant) + " €");
			}
		}
		return strs;
	}

	/**
	 * Retourne la liste des date de paiement sans remise
	 * 
	 * @return
	 */
	public List<ModeleContratDatePaiement> getDatePaiementSansRemise(EntityManager em, ModeleContrat mc)
	{
		Query q = em.createQuery("select d from ModeleContratDatePaiement d WHERE" + " d.modeleContrat=:mc and "
				+ " NOT EXISTS (select r from RemiseProducteur r where r.datePaiement = d) " + "order by d.datePaiement");

		q.setParameter("mc", mc);
		List<ModeleContratDatePaiement> us = q.getResultList();
		return us;
	}

	/**
	 * Retourne la liste des paiements pour une date, ordonnée par nom et prénom des utilisateurs
	 * 
	 * @param em
	 * @param datePaiement
	 * @return
	 */
	private List<Paiement> getPaiement(EntityManager em, ModeleContratDatePaiement datePaiement)
	{
		Query q = em.createQuery("select p from Paiement p WHERE p.modeleContratDatePaiement=:d "
				+ "ORDER BY p.contrat.utilisateur.nom , p.contrat.utilisateur.prenom");
		q.setParameter("d", datePaiement);

		return ((List<Paiement>) q.getResultList());
	}

	@DbWrite
	public void performRemise(RemiseDTO remiseDTO)
	{
		EntityManager em = TransactionHelper.getEm();

		RemiseProducteur remiseProducteur = new RemiseProducteur();
		remiseProducteur.setDateCreation(DateUtils.getDate());
		remiseProducteur.setDatePaiement(em.find(ModeleContratDatePaiement.class, remiseDTO.idModeleContratDatePaiement));
		remiseProducteur.setDateRemise(remiseDTO.dateReelleRemise);
		remiseProducteur.setMontant(remiseDTO.mnt);
		em.persist(remiseProducteur);

		for (PaiementRemiseDTO paiement : remiseDTO.paiements)
		{
			Paiement p = em.find(Paiement.class, paiement.idPaiement);
			p.setEtat(EtatPaiement.PRODUCTEUR);
			p.setRemise(remiseProducteur);
		}
	}

	// PARTIE EFFACEMENT
	@DbWrite
	public void deleteRemise(Long idItemToSuppress)
	{
		EntityManager em = TransactionHelper.getEm();

		RemiseProducteur remiseProducteur = em.find(RemiseProducteur.class, idItemToSuppress);

		List<Paiement> paiements = getPaiement(em, remiseProducteur);
		for (Paiement p : paiements)
		{
			p.setEtat(EtatPaiement.AMAP);
			p.setRemise(null);
		}
		em.remove(remiseProducteur);

	}

	/**
	 * Retourne la liste des paiements ordonnés par nom d'utilisateur
	 * 
	 * @param em
	 * @param remise
	 * @return
	 */
	private List<Paiement> getPaiement(EntityManager em, RemiseProducteur remise)
	{
		Query q = em.createQuery("select p from Paiement p " + "WHERE p.remise=:r " + "ORDER BY p.contrat.utilisateur.nom, p.contrat.utilisateur.prenom");
		q.setParameter("r", remise);

		return ((List<Paiement>) q.getResultList());
	}

	// PARTIE CHARGEMENT
	@DbRead
	public RemiseDTO loadRemise(Long idRemise)
	{
		EntityManager em = TransactionHelper.getEm();

		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
		RemiseProducteur remiseProducteur = em.find(RemiseProducteur.class, idRemise);
		RemiseDTO res = createRemiseInfo(em, remiseProducteur, df);

		List<Paiement> paiements = getPaiement(em, remiseProducteur);
		for (Paiement paiement : paiements)
		{
			PaiementRemiseDTO dto = new PaiementRemiseDTO();
			dto.idPaiement = paiement.getId();
			dto.etatPaiement = paiement.getEtat();
			dto.montant = paiement.getMontant();
			dto.nomUtilisateur = paiement.getContrat().getUtilisateur().getNom();
			dto.prenomUtilisateur = paiement.getContrat().getUtilisateur().getPrenom();
			dto.commentaire1 = paiement.getCommentaire1();
			dto.commentaire2 = paiement.getCommentaire2();
			
			res.paiements.add(dto);

			if (paiement.getEtat() != EtatPaiement.PRODUCTEUR)
			{
				throw new RuntimeException("Erreur pour " + paiement.getContrat().getUtilisateur().getNom()
						+ paiement.getContrat().getUtilisateur().getPrenom() + " le cheque n'a pas le bon état");
			}

		}

		return res;

	}

}
