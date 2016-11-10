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
 package fr.amapj.service.services.producteur;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.LongUtils;
import fr.amapj.model.engine.IdentifiableUtil;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.editionspe.EditionSpecifique;
import fr.amapj.model.models.fichierbase.EtatNotification;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.ProducteurReferent;
import fr.amapj.model.models.fichierbase.ProducteurUtilisateur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;

/**
 * Permet la gestion des producteurs
 * 
 */
public class ProducteurService
{
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PRODUCTEURS
	
	/**
	 * Permet de charger la liste de tous les producteurs
	 */
	@DbRead
	public List<ProducteurDTO> getAllProducteurs()
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<ProducteurDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select p from Producteur p");
			
		List<Producteur> ps = q.getResultList();
		for (Producteur p : ps)
		{
			ProducteurDTO dto = createProducteurDto(em,p);
			res.add(dto);
		}
		
		return res;
		
	}

	
	public ProducteurDTO createProducteurDto(EntityManager em, Producteur p)
	{
		ProducteurDTO dto = new ProducteurDTO();
		
		dto.id = p.id;
		dto.nom = p.nom;
		dto.description = p.description;
		
		dto.feuilleDistributionGrille = p.feuilleDistributionGrille;
		dto.feuilleDistributionListe = p.feuilleDistributionListe;
		dto.feuilleDistributionEtiquette = p.etiquette==null ? ChoixOuiNon.NON : ChoixOuiNon.OUI;
		dto.idEtiquette = IdentifiableUtil.getId(p.etiquette);
		
		dto.contratEngagement = p.engagement==null ? ChoixOuiNon.NON : ChoixOuiNon.OUI;
		dto.idEngagement = IdentifiableUtil.getId(p.engagement);
		dto.libContrat = p.libContrat;
		
		dto.delaiModifContrat = p.delaiModifContrat;
		
		
		dto.referents = getReferents(em,p);
		dto.utilisateurs = getUtilisateur(em,p);
		
		return dto;
	}


	public List<ProdUtilisateurDTO> getReferents(EntityManager em, Producteur p)
	{
		List<ProdUtilisateurDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select c from ProducteurReferent c WHERE c.producteur=:p order by c.indx");
		q.setParameter("p", p);
		List<ProducteurReferent> prs =  q.getResultList();
		for (ProducteurReferent pr : prs)
		{
			ProdUtilisateurDTO dto = new ProdUtilisateurDTO();
			dto.idUtilisateur = pr.getReferent().getId();
			dto.nom = pr.getReferent().getNom();
			dto.prenom = pr.getReferent().getPrenom();
			
			res.add(dto);
		}
		return res;
	}


	public List<ProdUtilisateurDTO> getUtilisateur(EntityManager em, Producteur p)
	{
		List<ProdUtilisateurDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select c from ProducteurUtilisateur c WHERE c.producteur=:p order by c.indx");
		q.setParameter("p", p);
		List<ProducteurUtilisateur> pus =  q.getResultList();
		for (ProducteurUtilisateur pu : pus)
		{
			ProdUtilisateurDTO dto = new ProdUtilisateurDTO();
			dto.idUtilisateur = pu.getUtilisateur().getId();
			dto.nom = pu.getUtilisateur().getNom();
			dto.prenom = pu.getUtilisateur().getPrenom();
			dto.etatNotification = pu.getNotification()==EtatNotification.AVEC_NOTIFICATION_MAIL;
			
			res.add(dto);
		}
		return res;
	}


	// PARTIE MISE A JOUR DES PRODUCTEURS
	@DbWrite
	public Long update(final ProducteurDTO dto,final boolean create)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Producteur p;
		
		if (create)
		{
			p = new Producteur();
		}
		else
		{
			p = em.find(Producteur.class, dto.id);
		}
		
		p.nom = dto.nom;
		p.description = dto.description;
		
		p.feuilleDistributionGrille = dto.feuilleDistributionGrille;
		p.feuilleDistributionListe = dto.feuilleDistributionListe;
		p.etiquette = IdentifiableUtil.findIdentifiableFromId(EditionSpecifique.class, dto.idEtiquette, em);
		
		p.engagement = IdentifiableUtil.findIdentifiableFromId(EditionSpecifique.class, dto.idEngagement, em);
		p.libContrat = dto.libContrat;
		
		p.delaiModifContrat = dto.delaiModifContrat;
		
		
		
		if (create)
		{
			em.persist(p);
		}
		
		// La liste des utilisateurs producteurs 
		updateUtilisateur(dto,em,p);
			
		
		// La liste des référents
		updateReferent(dto,em,p);
		
		return p.id;
		
	}

	
	private void updateUtilisateur(ProducteurDTO dto, EntityManager em, Producteur p)
	{
		// Suppression de tous les référents
		Query q = em.createQuery("select c from ProducteurUtilisateur c WHERE c.producteur=:p");
		q.setParameter("p", p);
		List<ProducteurUtilisateur> prs =  q.getResultList();
		for (ProducteurUtilisateur pr : prs)
		{
			em.remove(pr);
		}
		
		// On recree les nouveaux
		int indx = 0;
		for (ProdUtilisateurDTO util : dto.utilisateurs)
		{
			ProducteurUtilisateur pr = new ProducteurUtilisateur();
			pr.setProducteur(p);
			pr.setUtilisateur(em.find(Utilisateur.class, util.idUtilisateur));
			pr.setIndx(indx);
			if (util.etatNotification==true)
			{
				pr.setNotification(EtatNotification.AVEC_NOTIFICATION_MAIL);
			}
			else
			{
				pr.setNotification(EtatNotification.SANS_NOTIFICATION_MAIL);
			}
			
			em.persist(pr);
			indx++;
		}	
	}

	
	private void updateReferent(ProducteurDTO dto, EntityManager em, Producteur p)
	{
		// Suppression de tous les référents
		Query q = em.createQuery("select c from ProducteurReferent c WHERE c.producteur=:p");
		q.setParameter("p", p);
		List<ProducteurReferent> prs =  q.getResultList();
		for (ProducteurReferent pr : prs)
		{
			em.remove(pr);
		}
		
		// On recree les nouveaux
		int indx = 0;
		for (ProdUtilisateurDTO referent : dto.referents)
		{
			ProducteurReferent pr = new ProducteurReferent();
			pr.setProducteur(p);
			pr.setReferent(em.find(Utilisateur.class, referent.idUtilisateur));
			pr.setIndx(indx);
			
			em.persist(pr);
			indx++;
		}	
	}


	// PARTIE SUPPRESSION

	/**
	 * Permet de supprimer un producteur 
	 * Ceci est fait dans une transaction en ecriture
	 */
	@DbWrite
	public void delete(final Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Producteur p = em.find(Producteur.class, id);

		int r = countContrat(p,em);
		if (r>0)
		{
			throw new UnableToSuppressException("Cet producteur posséde "+r+" contrats.");
		}
		
		r = countProduit(p,em);
		if (r>0)
		{
			throw new UnableToSuppressException("Cet producteur posséde "+r+" produits. Vous devez d'abord les supprimer.");
		}
		
		// Il faut d'abord supprimer les referents et les utilisateurs producteurs 
		Query q = em.createQuery("select c from ProducteurReferent c WHERE c.producteur=:p");
		q.setParameter("p", p);
		List<ProducteurReferent> prs =  q.getResultList();
		for (ProducteurReferent pr : prs)
		{
			em.remove(pr);
		}
		
		
		q = em.createQuery("select c from ProducteurUtilisateur c WHERE c.producteur=:p");
		q.setParameter("p", p);
		List<ProducteurUtilisateur> pus =  q.getResultList();
		for (ProducteurUtilisateur pu : pus)
		{
			em.remove(pu);
		}
		
		
		// Puis on supprime le producteur
		em.remove(p);
	}


	private int countContrat(Producteur p, EntityManager em)
	{
		Query q = em.createQuery("select count(c) from Contrat c WHERE c.modeleContrat.producteur=:p");
		q.setParameter("p", p);
			
		return LongUtils.toInt(q.getSingleResult());
	}
	
	private int countProduit(Producteur p, EntityManager em)
	{
		Query q = em.createQuery("select count(c) from Produit c WHERE c.producteur=:p");
		q.setParameter("p", p);
			
		return LongUtils.toInt(q.getSingleResult());
	}
	

	

}
