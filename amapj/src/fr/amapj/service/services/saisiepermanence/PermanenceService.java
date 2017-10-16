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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.distribution.DatePermanence;
import fr.amapj.model.models.distribution.DatePermanenceUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;

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

	
	private PermanenceDTO createDistributionDTO(EntityManager em, DatePermanence d)
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
			// new DeleteNotificationService().deleteAllNotificationDoneDatePermanenceUtilisateur(em, du);
			em.remove(du);
		}
		
		em.remove(d);
	}
	
}
