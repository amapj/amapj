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
 package fr.amapj.service.services.editionspe;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.GzipUtils;
import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.editionspe.AbstractEditionSpeJson;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.model.models.editionspe.adhesion.BulletinAdhesionJson;
import fr.amapj.model.models.editionspe.bilanlivraison.BilanLivraisonJson;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.editionspe.engagement.EngagementJson;
import fr.amapj.model.models.editionspe.etiquette.EtiquetteProducteurJson;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;

/**
 * Permet la gestion des éditions spécifiques
 */
public class EditionSpeService
{
	
	/**
	 * Permet de savoir si il y a des feuilles d'émargements
	 */
	@DbRead
	public boolean needPlanningMensuel()
	{
		EntityManager em = TransactionHelper.getEm();
		
		Query q = em.createQuery("select count(p) from EditionSpecifique p WHERE p.typEditionSpecifique=:e");
		q.setParameter("e", TypEditionSpecifique.FEUILLE_EMARGEMENT);

		return LongUtils.toInt(q.getSingleResult())>0;
		
	}
	
	
	
	

	/**
	 * Permet de savoir si ce contrat a besoin d'étiquette
	 */
	@DbRead
	public boolean needEtiquette(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		if (mc.getProducteur().etiquette == null)
		{
			return false;
		}

		return true;
	}
	
	
	
	/**
	 * Permet de savoir si ce contrat a besoin d'un contrat d'engagement
	 */
	@DbRead
	public boolean needEngagement(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();

		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);

		if (mc.getProducteur().engagement == null)
		{
			return false;
		}

		return true;
	}
	
	
	/**
	 * Permet de savoir si il est possible de saisir les étiquettes dans la fiche producteur
	 * 
	 * Si il y a au moins une étiquette définie dans les éditions spécifiques, alors
	 * on active la saisie du champ dans la fiche producteur
	 */
	@DbRead
	public boolean ficheProducteurNeedEtiquette()
	{
		EntityManager em = TransactionHelper.getEm();
	
		Query q = em.createQuery("select count(p) from EditionSpecifique p WHERE p.typEditionSpecifique=:e");
		q.setParameter("e", TypEditionSpecifique.ETIQUETTE_PRODUCTEUR);

		return LongUtils.toInt(q.getSingleResult())>0;
	}
	
	
	/**
	 * Permet de savoir si il est possible de saisir les engagements dans la fiche producteur
	 * 
	 * Si il y a au moins un engagement défini dans les éditions spécifiques, alors
	 * on active la saisie du champ dans la fiche producteur
	 */
	@DbRead
	public boolean ficheProducteurNeedEngagement()
	{
		EntityManager em = TransactionHelper.getEm();

		Query q = em.createQuery("select count(p) from EditionSpecifique p WHERE p.typEditionSpecifique=:e");
		q.setParameter("e", TypEditionSpecifique.CONTRAT_ENGAGEMENT);

		return LongUtils.toInt(q.getSingleResult())>0;
	}
	
	
	/**
	 * Permet de savoir si il est possible de saisir les bulletins d'adhesion dans la fiche PeriodeCostisation
	 * 
	 * Si il y a au moins un engagement défini dans les éditions spécifiques, alors
	 * on active la saisie du champ dans la fiche producteur
	 */
	@DbRead
	public boolean fichePeriodeNeedBulletinAdhesion()
	{
		EntityManager em = TransactionHelper.getEm();
	
		Query q = em.createQuery("select count(p) from EditionSpecifique p WHERE p.typEditionSpecifique=:e");
		q.setParameter("e", TypEditionSpecifique.BULLETIN_ADHESION);

		return LongUtils.toInt(q.getSingleResult())>0;
	}
	
	
	

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES EDITIONS SPECIFIQUES

	/**
	 * Permet de charger la liste de toutes les editions specifiques
	 */
	@DbRead
	public List<EditionSpeDTO> getAllEtiquettes()
	{
		EntityManager em = TransactionHelper.getEm();

		List<EditionSpeDTO> res = new ArrayList<>();

		Query q = em.createQuery("select p from EditionSpecifique p");

		List<EditionSpecifique> ps = q.getResultList();
		for (EditionSpecifique p : ps)
		{
			EditionSpeDTO dto = createEtiquetteDTO(em, p);
			res.add(dto);
		}

		return res;

	}

	private EditionSpeDTO createEtiquetteDTO(EntityManager em, EditionSpecifique p)
	{
		EditionSpeDTO dto = new EditionSpeDTO();

		dto.id = p.getId();
		dto.nom = p.nom;
		dto.typEditionSpecifique = p.typEditionSpecifique;

		return dto;
	}

	

	// PARTIE MISE A JOUR DES ETIQUETTES
	@DbWrite
	public Long update(AbstractEditionSpeJson speJson, boolean create)
	{
		EntityManager em = TransactionHelper.getEm();

		EditionSpecifique p;

		if (create)
		{
			TypEditionSpecifique typEditionSpecifique = findTypEditionSpecifique(speJson);

			p = new EditionSpecifique();
			p.typEditionSpecifique = typEditionSpecifique;
		} 
		else
		{
			p = em.find(EditionSpecifique.class, speJson.getId());
		}

		p.nom = speJson.getNom();
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String content = gson.toJson(speJson);
		p.content = GzipUtils.compress(content); 
		

		if (create)
		{
			em.persist(p);
		}
		
		return p.id;

	}


	





	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer une étiquette Ceci est fait dans une transaction en ecriture
	 */
	@DbWrite
	public void delete(final Long id)
	{
		EntityManager em = TransactionHelper.getEm();

		EditionSpecifique p = em.find(EditionSpecifique.class, id);

		int r = countProducteur(p, em);
		if (r > 0)
		{
			throw new UnableToSuppressException("Cette édition spécifique  est utilisée par " + r + " producteurs.");
		}

		em.remove(p);
	}

	private int countProducteur(EditionSpecifique e, EntityManager em)
	{
		Query q = em.createQuery("select count(p) from Producteur p WHERE p.etiquette=:e");
		q.setParameter("e", e);

		return LongUtils.toInt(q.getSingleResult());
	}
	
	
	/**
	 * Permet de charger la liste de toutes les editions specifiques
	 */
	@DbRead
	public List<EditionSpecifique> getEtiquetteByType(TypEditionSpecifique typEditionSpecifique)
	{
		EntityManager em = TransactionHelper.getEm();

		Query q = em.createQuery("select p from EditionSpecifique p where p.typEditionSpecifique=:e ");
		q.setParameter("e", typEditionSpecifique);

		List<EditionSpecifique> ps = q.getResultList();
		
		return ps;

	}


	// DUPLICATION 
	@DbWrite
	public void dupliquer(EditionSpeDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
		
		// On conserve le nom qui a été saisi
		EditionSpecifique to = new EditionSpecifique();
		to.nom = dto.nom;
		
		// On charge le reste par rapport à l'enregistrement en base
		EditionSpecifique from = em.find(EditionSpecifique.class, dto.id);
		
		to.content = from.content;
		to.typEditionSpecifique = from.typEditionSpecifique;
		
		em.persist(to);
		
	}
	
	
	// Chargement 
	
	/**
	 * Permet de charger un objet JSON à partir d'un id de EditionSpecifique  
	 */
	@DbRead
	public AbstractEditionSpeJson load(Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		EditionSpecifique editionSpecifique = em.find(EditionSpecifique.class, id);
		
		Class<? extends AbstractEditionSpeJson> clazz = findClazz(editionSpecifique.typEditionSpecifique);
	
		String content  = GzipUtils.uncompress(editionSpecifique.content);
		
		AbstractEditionSpeJson etiquetteDTO = (AbstractEditionSpeJson) new Gson().fromJson(content, clazz); 
		etiquetteDTO.setId(editionSpecifique.id);
		etiquetteDTO.setNom(editionSpecifique.nom);
			
		return etiquetteDTO;
	}
	
	
	// Conversion entre  TypEditionSpecifique et les classes 
	
	private Class<? extends AbstractEditionSpeJson> findClazz(TypEditionSpecifique typ)
	{
		switch (typ)
		{
		case ETIQUETTE_PRODUCTEUR:
			return EtiquetteProducteurJson.class;
			
		case FEUILLE_EMARGEMENT:
			return FeuilleEmargementJson.class;
			
		case CONTRAT_ENGAGEMENT:
			return EngagementJson.class;
			
		case BULLETIN_ADHESION:
			return BulletinAdhesionJson.class;
		
		case BILAN_LIVRAISON:
			return BilanLivraisonJson.class;
		
		default:
			throw new AmapjRuntimeException("Type non pris en compte");
		}
	}
	
	
	private TypEditionSpecifique findTypEditionSpecifique(AbstractEditionSpeJson speJson)
	{
		if (speJson instanceof EtiquetteProducteurJson)
		{
			return TypEditionSpecifique.ETIQUETTE_PRODUCTEUR;
		}
		else if (speJson instanceof FeuilleEmargementJson)
		{
			return TypEditionSpecifique.FEUILLE_EMARGEMENT;
		} 
		else if (speJson instanceof EngagementJson)
		{
			return TypEditionSpecifique.CONTRAT_ENGAGEMENT;
		}
		else if (speJson instanceof BulletinAdhesionJson)
		{
			return TypEditionSpecifique.BULLETIN_ADHESION;
		}
		else if (speJson instanceof BilanLivraisonJson)
		{
			return TypEditionSpecifique.BILAN_LIVRAISON;
		}
		
		throw new AmapjRuntimeException();
	}

}
