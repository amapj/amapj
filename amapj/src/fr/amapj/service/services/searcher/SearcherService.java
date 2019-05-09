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
 package fr.amapj.service.services.searcher;

import java.util.List;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.IdentifiableUtil;
import fr.amapj.model.engine.transaction.Call;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.engine.transaction.NewTransaction;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;


public class SearcherService
{
	public SearcherService()
	{
		
	}
	
	
	/**
	 * Permet de récuperer tous les elements d'une table pour le mettre dans le searcher
	 * Ceci est fait dans une transaction en lecture  
	 */
	
	@DbRead
	public List<Identifiable> getAllElements(Class clazz)
	{
		EntityManager em = TransactionHelper.getEm();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identifiable> cq = cb.createQuery(clazz);
		List<Identifiable> roles = em.createQuery(cq).getResultList();
		return roles;
	}
	
	
	/**
	 * Permet de récuperer tous les produits d'un producteur
	 * Ceci est fait dans une transaction en lecture  
	 */
	@DbRead
	public List<Produit> getAllProduits(Long idProducteur)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Producteur producteur = em.find(Producteur.class, idProducteur);
		
		Query q = em.createQuery("select p from Produit p WHERE p.producteur=:prod order by p.nom,p.conditionnement");
		q.setParameter("prod",producteur);
		List<Produit> us = q.getResultList();
		return us;
	}
	
	
	/**
	 * Permet de récuperer tous les produits de tous les producteurs, en prenant garde de bien charger 
	 * les caractéristiques du producteur 
	 * Ceci est fait dans une transaction en lecture  
	 */
	@DbRead
	public List<Produit> getAllProduits()
	{
		EntityManager em = TransactionHelper.getEm();
		
		
		Query q = em.createQuery("select p from Produit p order by p.producteur.nom, p.nom,p.conditionnement");
		
		List<Produit> us = q.getResultList();
		String str="";
		for (Produit produit : us)
		{
			// Permet de forcer le chargement du nom du producteur
			str = str+ produit.getProducteur().nom;
		}
		
		return us;
	}
	
	
	
	

}
