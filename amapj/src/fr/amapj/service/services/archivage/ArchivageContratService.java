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
 package fr.amapj.service.services.archivage;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.contrat.reel.Paiement;
import fr.amapj.model.models.remise.RemiseProducteur;
import fr.amapj.service.engine.tools.DbToDto;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.remiseproducteur.RemiseProducteurService;

/**
 * Permet la gestion des contrats archivés
 * 
 */
public class ArchivageContratService
{

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES CONTRATS

	/**
	 * Permet de charger la liste de tous les modeles de contrats en archive
	 * dans une transaction en lecture
	 */
	@DbRead
	public List<ModeleContratSummaryDTO> getAllContratsArchives()
	{
		EntityManager em = TransactionHelper.getEm();

		GestionContratService service = new GestionContratService();

		Query q = em.createQuery("select mc from ModeleContrat mc WHERE mc.etat=:etat");
		q.setParameter("etat", EtatModeleContrat.ARCHIVE);

		return DbToDto.transform(q, (ModeleContrat mc) -> service.createModeleContratInfo(em, mc));

	}

	/**
	 * Retourne null si un contrat est archivale, sinon retourne un texte
	 * explicatif
	 */
	@DbRead
	public String checkIfArchivable(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		// On vérifie tout d'abord que tout est bien livré
		Query q = em.createQuery("select count(distinct(c.modeleContratDate)) from ContratCell c WHERE " + "c.modeleContratDate.dateLiv>=:d AND "
				+ "c.modeleContratDate.modeleContrat.id=:mcid");

		q.setParameter("d", DateUtils.getDateWithNoTime());
		q.setParameter("mcid", idModeleContrat);

		int nbLiv = SQLUtils.toInt(q.getSingleResult());
		if (nbLiv != 0)
		{
			return "Il existe " + nbLiv
					+ " livraisons à réaliser aujourd'hui ou dans le futur. Veuillez attendre la fin du contrat ou alors annuler les livraisons à venir.";
		}

		// On verifie ensuite que les chèques ont bien été tous remis
		ModeleContrat modeleContrat = em.find(ModeleContrat.class, idModeleContrat);
		if (modeleContrat.getGestionPaiement() == GestionPaiement.GESTION_STANDARD)
		{
			q = em.createQuery("select count(p) from Paiement p WHERE " + "p.contrat.modeleContrat.id=:mcid AND " + "p.etat!=:etat");

			q.setParameter("mcid", idModeleContrat);
			q.setParameter("etat", EtatPaiement.PRODUCTEUR);

			int nbCheque = SQLUtils.toInt(q.getSingleResult());
			if (nbCheque != 0)
			{
				return "Il existe " + nbCheque
						+ " chèques qui n'ont pas été remis au producteur. Veuillez annuler ces chèques ou alors faire une remise au producteur.";
			}

		}

		// Fin des verifications
		return null;
	}

	// PARTIE SUPPRESSION D'UN MODELE DE CONTRAT ET DE TOUS LES CONTRATS
	// ASSOCIES

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
		List<RemiseProducteur> remises = getAllRemises(em, mc);
		for (RemiseProducteur remiseProducteur : remises)
		{
			new RemiseProducteurService().deleteRemise(remiseProducteur.getId());
		}

		// On supprime ensuite tous les paiements
		List<Paiement> paiements = getAllPaiements(em, mc);
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
		q.setParameter("mc", mc);
		List<Contrat> cs = q.getResultList();
		return cs;
	}

}
