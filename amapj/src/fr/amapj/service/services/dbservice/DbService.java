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
 package fr.amapj.service.services.dbservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;

/**
 * Service generique pour des accès à la base 
 * 
 *
 */
public class DbService
{
	private final static Logger logger = LogManager.getLogger();
	
	
	public DbService()
	{
		
	}
	
	
	/**
	 * Permet de récuperer un element d'une table à partir de son id
	 * Ceci est fait dans une transaction en lecture  
	 */
	@DbRead
	public Identifiable getOneElement(Class clazz,Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		return (Identifiable) em.find(clazz, id);
	}
	
	
	
	/**
	 * Permet de supprimer un element d'une table à partir de son id
	 * Ceci est fait dans une transaction en écriture 
	 */
	@DbWrite
	public void deleteOneElement(Class clazz,Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		Identifiable identifiable = (Identifiable) em.find(clazz, id);
		
		em.remove(identifiable);
		
	}
	
	
	/**
	 * Permet de combien il y a de lignes dans une table avec cette colonne avec cette valeur
	 * Ceci est fait dans une transaction en lecture  
	 */
	@DbRead
	public int count(Class clazz,String property,String value,Long id)
	{
		EntityManager em = TransactionHelper.getEm();
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery cq = cb.createQuery(clazz);
		Root root = cq.from(clazz);

		// On ajoute la condition where
		cq.where(cb.and(cb.equal(root.get(property), value),cb.notEqual(root.get("id"), id)));
		
		return em.createQuery(cq).getResultList().size();
	}
	
	
	

}
