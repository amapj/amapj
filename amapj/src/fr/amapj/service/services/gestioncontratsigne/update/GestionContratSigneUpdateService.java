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
 package fr.amapj.service.services.gestioncontratsigne.update;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.SQLUtils;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.service.services.gestioncontrat.DateModeleContratDTO;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.service.services.notification.DeleteNotificationService;

public class GestionContratSigneUpdateService
{
	@DbWrite
	public void addDates(ModeleContratDTO modeleContratDTO)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratDTO.id);
		
		for (DateModeleContratDTO date : modeleContratDTO.dateLivs)
		{
			addOneDateLiv(em, date.dateLiv, mc);
		}
	}
	
	
	public void addOneDateLiv(EntityManager em,Date datLiv,ModeleContrat mc)
	{
		ModeleContratDate md = new ModeleContratDate();
		md.setModeleContrat(mc);
		md.setDateLiv(datLiv);
		em.persist(md);
	}
	
	
	/**
	 * Cette méthode permet de supprimer une date de livraison d'un contrat 
	 * Cette date ne doit pas contenir de livraison pour un amapien 
	 *  
	 */
	@DbWrite
	public void suppressOneDateLiv(Long idModeleContratDate)
	{
		EntityManager em = TransactionHelper.getEm();
		
		ModeleContratDate mcd = em.find(ModeleContratDate.class, idModeleContratDate);
		
		// On efface les exclusions relatives à cette date
		deleteAllDateBarrees(em, mcd);
				
		// On efface aussi toutes les notification relatives à cette date   
		new DeleteNotificationService().deleteAllNotificationDoneModeleContratDate(em, mcd);

		// On supprime la date 
		em.remove(mcd);
	}


	private void deleteAllDateBarrees(EntityManager em, ModeleContratDate mcd)
	{
		Query q = em.createQuery("select mce from ModeleContratExclude mce WHERE mce.date=:mcd");
		q.setParameter("mcd",mcd);
		
		SQLUtils.deleteAll(em, q);
	}
	
	
	

}
