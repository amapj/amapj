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
 package fr.amapj.service.services.importdonnees;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.param.ChoixOuiNon;

/**
 * Service pour l'import des données
 *  
 *
 */
public class ImportDonneesService
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public ImportDonneesService()
	{
		
	}

	/*
	 * Produits et producteurs
	 */
	
	@DbWrite
	public void insertDataProduits(List<ImportProduitProducteurDTO> prods)
	{
		EntityManager em = TransactionHelper.getEm();
		
		for (ImportProduitProducteurDTO importProduitProducteurDTO : prods)
		{
			insertDataProduits(em,importProduitProducteurDTO);
		}
	}

	private void insertDataProduits(EntityManager em, ImportProduitProducteurDTO dto)
	{
		Query q = em.createQuery("select p from Producteur p WHERE p.nom LIKE :nom");
		q.setParameter("nom",dto.producteur);
		
		List<Producteur> prods = q.getResultList();
		Producteur p = null;
		if (prods.size()==0)
		{
			p = new Producteur();
			p.nom = dto.producteur;
			p.delaiModifContrat = 3;
			p.feuilleDistributionGrille = ChoixOuiNon.OUI;
			p.feuilleDistributionListe = ChoixOuiNon.NON;
			em.persist(p);
		}
		else if (prods.size()==1)
		{
			p = prods.get(0);
		}
		else
		{
			throw new RuntimeException("Deux producteurs avec le même nom");
		}
		
		
		Produit pr = new Produit();
		pr.setConditionnement(dto.conditionnement);
		pr.setNom(dto.produit);
		pr.setProducteur(p);
		em.persist(pr);
		
	}

	@DbRead
	public List<ImportProduitProducteurDTO> getAllProduits()
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<ImportProduitProducteurDTO> res = new ArrayList<>();
		
		Query q = em.createQuery("select p from Produit p order by p.producteur.nom");
		List<Produit> prods = q.getResultList();
		
		for (Produit prod : prods)
		{
			ImportProduitProducteurDTO dto = new ImportProduitProducteurDTO();
			dto.producteur = prod.getProducteur().nom;
			dto.produit = prod.getNom();
			dto.conditionnement = prod.getConditionnement();
			
			res.add(dto);
		}
		
		
		return res;
	}
}
