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
 package fr.amapj.service.services.mescontrats;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.models.contrat.modele.JokerMode;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.model.models.contrat.reel.Contrat;


/**
 * Permet de determiner si 
 * 
 * -> un modele de contrat est modifiable par un adherent 
 * 
 * -> une date d'un modele de contrat est modifiable par un adherent
 * 
 * -> un contrat peut être supprimé par un adhérent 
 * 
 * -> un contrat est en historique 
 *
 */
public class ContratStatusService
{

	/*
	 * PARTIE MODELE DE CONTRAT
	 */
	
	
	/**
	 * Indique si ce modele de contrat est modifiable par un adhérent à cette date now 
	 * 
	 * Si la fonction retourne true alors 
	 *    - un adherent peut s'inscrire 
	 *     ou 
	 *    - un adherent peut modifier son contrat existant 
	 *       
	 */
	public boolean isModifiable(ModeleContrat mc,EntityManager em,CartePrepayeeDTO cartePrepayeeDTO,Date now)
	{
		NatureContrat nature = mc.nature;
		
		// Cas de la carte prépayée
		if (nature==NatureContrat.CARTE_PREPAYEE)
		{
			return cartePrepayeeDTO.nbLigModifiable>0;
		}
		// Autre cas
		else
		{
			return isInscriptionNonTerminee(mc, now);
		}
	}
	
	

	
	
	/**
	 * Indique si cette date de modele de contrat est modifiable par un adherent quelconque à la date now 
	 * 
	 */
	public boolean isDateModifiable(ModeleContratDate mcd,EntityManager em,Date now)
	{
		NatureContrat nature = mcd.modeleContrat.nature;
		
		// Cas de la carte prépayée
		if (nature==NatureContrat.CARTE_PREPAYEE)
		{
			MesCartesPrepayeesService service = new MesCartesPrepayeesService();
			int cartePrepayeeDelai = mcd.modeleContrat.cartePrepayeeDelai;
			return service.cartePrepayeeLigModifiable(mcd, now, cartePrepayeeDelai);
		}
		// Cas abonnement 
		else if (nature==NatureContrat.ABONNEMENT)
		{
			boolean inscriptionNonTerminee = isInscriptionNonTerminee(mcd.modeleContrat, now);
			
			if (mcd.modeleContrat.jokerMode==JokerMode.INSCRIPTION)
			{
				return inscriptionNonTerminee;
			}
			else
			{
				if (inscriptionNonTerminee==true)
				{
					return inscriptionNonTerminee;
				}
				
				LocalDate limit = DateUtils.asLocalDate(mcd.dateLiv);
				limit = limit.plusDays(-mcd.modeleContrat.jokerDelai);
				return DateUtils.asLocalDate(now).isBefore(limit);
			}
		} 
		// Cas LIBRE
		else
		{
			return isInscriptionNonTerminee(mcd.modeleContrat, now);
		}
	}
	
	/**
	 * Retourne true si les inscriptions ne sont pas terminées pour ce contrat 
	 * 
	 * A utiliser uniquement avec les contrats de type ABONNEMENT et LIBRE 
	 */
	private boolean isInscriptionNonTerminee(ModeleContrat modeleContrat,Date now)
	{
		Date dateFinInscription = modeleContrat.getDateFinInscription();
		Date d = DateUtils.addHour(dateFinInscription,23);
		d = DateUtils.addMinute(d, 59);
		return  d.after(now);
	}
	
	
	/*
	 * PARTIE CONTRAT
	 */

	
	
	/**
	 * Indique si le contrat est supprimable par l'adhérent 
	 */
	public boolean isSupprimable(Contrat contrat,EntityManager em,CartePrepayeeDTO cartePrepayeeDTO,Date now,boolean isModifiable)
	{
		// Contrat peut être null dans certains cas 
		if (contrat==null)
		{
			return false;
		}

		
		NatureContrat nature = contrat.getModeleContrat().nature;
	
		// Cas de la carte prépayée 
		if (nature==NatureContrat.CARTE_PREPAYEE)
		{
			// On verifie si il y a des lignes non modifiables avec des quantités non nulles
			Date d = DateUtils.suppressTime(now);
			d = DateUtils.addDays(d, contrat.getModeleContrat().cartePrepayeeDelai);
			
			Query q = em.createQuery("select count(cc) from ContratCell cc  WHERE cc.contrat=:c and cc.modeleContratDate.dateLiv<=:d");
			q.setParameter("c",contrat);
			q.setParameter("d",d);
			
			int count = SQLUtils.toInt(q.getSingleResult());
			return (count==0);
		}
		// Autre cas
		else
		{
			return isModifiable;
		}
	}
	
	
	/**
	 * Un contrat est historique si la date de la dernière livraison 
	 * est passée de plus de 5 jours et si il n'est plus possible de s'inscrire
	 */
	public boolean isHistorique(Contrat contrat,EntityManager em,Date now,boolean isModifiable)
	{
		// Si on peut modifier ce contrat, il n'est pas en historique 
		if (isModifiable)
		{
			return false;
		}
		
		// Cas standard
		Query q = em.createQuery("select count(cc) from ContratCell cc " +
				"WHERE cc.contrat=:c and cc.modeleContratDate.dateLiv>=:d");

		q.setParameter("c",contrat);
		
		Date d = DateUtils.suppressTime(now);
		d = DateUtils.addDays(d, -5);
		q.setParameter("d",d);
		
		// On obtient le nombre de livraisons restantes
		long count = (Long) q.getSingleResult();
		
		return (count==0);
	}
	
}
