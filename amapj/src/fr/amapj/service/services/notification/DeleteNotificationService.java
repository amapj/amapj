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
 package fr.amapj.service.services.notification;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.SQLUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.fichierbase.Utilisateur;

/**
 * Permet la suppression des notifications, indispensable pour pouvoir supprimer
 * les elements liés comme l'utilisateur, la date de permanence, ... 
 *
 */
public class DeleteNotificationService 
{
	
	/**
	 * Methode utilitaire permettant de supprimer toutes les notifications faites sur les dates d'un modele de contrat
	 * @param em
	 * @param mc
	 */
	public void deleteAllNotificationDoneModeleContrat(EntityManager em, ModeleContrat mc)
	{ 
		Query q = em.createQuery("select n from NotificationDone n WHERE n.modeleContratDate.modeleContrat=:mc");
		q.setParameter("mc",mc);
		
		SQLUtils.deleteAll(em, q);
	}
	
	
	/**
	 * Methode utilitaire permettant de supprimer toutes les notifications faites sur une  date de livraison d'un modele de contrat
	 */
	public void deleteAllNotificationDoneModeleContratDate(EntityManager em, ModeleContratDate mcd)
	{ 
		Query q = em.createQuery("select n from NotificationDone n WHERE n.modeleContratDate=:mcd");
		q.setParameter("mcd",mcd);
		
		SQLUtils.deleteAll(em, q);
	}

	
	
	
	/**
	 * Methode utilitaire permettant de supprimer toutes les notifications faites sur un utilisateur 
	 * 
	 */
	public void deleteAllNotificationDoneUtilisateur(EntityManager em, Utilisateur u)
	{
		Query q = em.createQuery("select n from NotificationDone n WHERE n.utilisateur=:u");
		q.setParameter("u",u);
		
		SQLUtils.deleteAll(em, q);
	}
		
}
