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
 package fr.amapj.service.services.produit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.CollectionUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;

/**
 * Permet la gestion des producteurs
 * 
 */
public class ProduitService
{
	
	
	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PRODUITS
	
	
	/**
	 * Permet de charger la liste de tous les produits
	 * dans une transaction en lecture
	 */
	@DbRead
	public List<ProduitDTO> getAllProduitDTO(Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<ProduitDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select p from Produit p " +
					"where p.producteur.id=:prd " +
					"order by p.nom,p.conditionnement");
		
		q.setParameter("prd", idProducteur);
		List<Produit> ps = q.getResultList();
		for (Produit p : ps)
		{
			ProduitDTO dto = createProduitDto(p,em);
			res.add(dto);
		}	
		return res;
		
	}
	
	
	
	/**
	 * Permet de charger un produit
	 */
	private  ProduitDTO createProduitDto(Produit p,EntityManager em)
	{			
		ProduitDTO dto = new ProduitDTO();
		
		dto.id = p.getId();
		dto.nom = p.getNom();
		dto.conditionnement = p.getConditionnement();
		dto.producteurId = p.getProducteur().getId();
		
		return dto;
		
	}

	
	

	/**
	 * Mise à jour ou création d'un produit
	 * @param dto
	 * @param create
	 */
	@DbWrite
	public Long update(ProduitDTO dto,boolean create)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Produit p;
		
		if (create)
		{
			p = new Produit();
		}
		else
		{
			p = em.find(Produit.class, dto.id);
		}
		
		p.setNom(dto.nom);
		p.setConditionnement(dto.conditionnement);
		p.setProducteur(em.find(Producteur.class, dto.producteurId));
		
		if (create)
		{
			em.persist(p);
		}
		
		return p.getId();
	}



	/**
	 * Permet la suppression d'un produit
	 */
	@DbWrite
	public void deleteProduit(Long idItemToSuppress) throws UnableToSuppressException
	{
		EntityManager em = TransactionHelper.getEm();
		Produit p = em.find(Produit.class, idItemToSuppress);
		
		// 
		verifContrat(p,em);
		
		em.remove(p);
	}
	
	
	private void verifContrat(Produit p, EntityManager em) throws UnableToSuppressException
	{
		Query q = em.createQuery("select distinct(c.modeleContrat) from ModeleContratProduit c WHERE c.produit=:p");
		q.setParameter("p", p);
			
		List<ModeleContrat> mcs = q.getResultList();

		if (mcs.size()>0)
		{
			String str = CollectionUtils.asStdString(mcs, t -> t.getNom());
			throw new UnableToSuppressException("Cet produit est présent dans "+mcs.size()+" contrats : "+str);
		}
	}
	
	/**
	 *
	 */
	@DbRead
	public String prettyString(Long idProduit)
	{
		EntityManager em = TransactionHelper.getEm();
		
		if (idProduit==null)
		{
			return "";
		}
		
		Produit p = em.find(Produit.class, idProduit);
		return p.getNom()+","+p.getConditionnement();
		
	}
	
	
}
