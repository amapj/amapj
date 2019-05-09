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
 package fr.amapj.service.services.listeproducteurreferent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.StringUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.service.services.producteur.ProducteurService;

/**
 * 
 * 
 *  
 *
 */
public class ListeProducteurReferentService
{

	public ListeProducteurReferentService()
	{

	}

	// PARTIE REQUETAGE POUR AVOIR LA LISTE DES PRODUCTEURS

	/**
	 * Retourne la liste des producteurs
	 */
	@DbRead
	public List<DetailProducteurDTO> getAllProducteurs()
	{
		EntityManager em = TransactionHelper.getEm();
		
		List<DetailProducteurDTO> res = new ArrayList<DetailProducteurDTO>();
		
		Query q = em.createQuery("select p from Producteur p order by p.nom");

		List<Producteur> ps = q.getResultList();
		
		for (Producteur producteur : ps)
		{
			DetailProducteurDTO dto = createDetailProducteurDTO(producteur,em);
			res.add(dto);
		}
		
		return res;

	}

	private DetailProducteurDTO createDetailProducteurDTO(Producteur producteur,EntityManager em)
	{
		DetailProducteurDTO dto = new DetailProducteurDTO();
		
		dto.nom = producteur.nom;
		
		dto.description = StringUtils.asHtml(producteur.description);
		
		dto.utilisateurs = new ProducteurService().getUtilisateur(em, producteur);
		
		dto.referents = new ProducteurService().getReferents(em, producteur);
		
		return dto;
	}

}
