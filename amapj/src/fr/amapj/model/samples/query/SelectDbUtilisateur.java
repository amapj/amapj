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
 package fr.amapj.model.samples.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;

/**
 * 
 * Exemple de base sur des requetes dans la base de données avec les outils Criteria
 * 
 * Cette classe permet de selctionner les utilisateurs dans la base de donnees
 */
public class SelectDbUtilisateur
{

	/**
	 * Permet de lister simplement tous les utilisteurs
	 */
	@DbRead
	public void listAllUser()
	{
		EntityManager em = TransactionHelper.getEm();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Utilisateur> cq = cb.createQuery(Utilisateur.class);
		Root<Utilisateur> root = cq.from(Utilisateur.class);
		
		List<Utilisateur> us = em.createQuery(cq).getResultList();
		for (Utilisateur u : us)
		{
			System.out.println("Utilisateur: Nom ="+u.getNom()+" Prenom ="+u.getPrenom());
		}

	}
	
	/**
	 * Permet de lister tous les utilisteurs ayant pour nom AA
	 */
	@DbRead
	public void listUserWithNameAA()
	{
		EntityManager em = TransactionHelper.getEm();
	
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Utilisateur> cq = cb.createQuery(Utilisateur.class);
		Root<Utilisateur> root = cq.from(Utilisateur.class);
		
		// On ajoute la condition where 
		cq.where(cb.equal(root.get("nom"),"AA"));
		
		List<Utilisateur> us = em.createQuery(cq).getResultList();
		for (Utilisateur u : us)
		{
			System.out.println("Utilisateur: Nom ="+u.getNom()+" Prenom ="+u.getPrenom());
		}

	}
	
	

	

	public static void main(String[] args)
	{
		TestTools.init();
		
		SelectDbUtilisateur selectUtilisateur = new SelectDbUtilisateur();
		System.out.println("Requete dans la base ..");
		selectUtilisateur.listUserWithNameAA();
		System.out.println("Fin de la requete");

	}

}
