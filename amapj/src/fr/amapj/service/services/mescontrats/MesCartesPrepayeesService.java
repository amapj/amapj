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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.NatureContrat;

/**
 * Partie spécifique aux cartes prépayées 
 *
 */
public class MesCartesPrepayeesService
{
	public MesCartesPrepayeesService()
	{

	}

	/**
	 * Permet de calculer les informations relatives à une carte prepayée 
	 * 
	 * @param mc
	 * @param em
	 * @param now
	 * @return
	 */
	public CartePrepayeeDTO computeCartePrepayee(ModeleContrat mc, EntityManager em,Date now)
	{
		if (mc.nature!=NatureContrat.CARTE_PREPAYEE)
		{
			return null;
		}
		
		
		CartePrepayeeDTO dto = new CartePrepayeeDTO();
		
		Query q = em.createQuery("select mcd from ModeleContratDate mcd WHERE mcd.modeleContrat=:mc order by mcd.dateLiv , mcd.id");
		q.setParameter("mc",mc);
		List<ModeleContratDate> datLivs = q.getResultList();
		
		
		dto.cartePrepayeeDelai = mc.cartePrepayeeDelai;
		dto.nbLigModifiable = getNbLigModifiable(datLivs,now,mc.cartePrepayeeDelai);
		dto.nextDateLiv = getNextDateLivraison(datLivs,now,mc.cartePrepayeeDelai);
		dto.nextDateLivModifiable = getNextDateLivModifiable(datLivs,now,mc.cartePrepayeeDelai);
		
		return dto;
		
	}

	
	
	/**
	 * Indique le nombre de lignes encore modifiables 
	 * @param cartePrepayeeDelai 
	 * @param now 
	 */
	private int getNbLigModifiable(List<ModeleContratDate> datLivs, Date now, int cartePrepayeeDelai)
	{
		int res = 0;
		for (ModeleContratDate lig : datLivs)
		{
			if (cartePrepayeeLigModifiable(lig,now,cartePrepayeeDelai))
			{
				res++;
			}
		}
		return res;
	}
	
	/** 
	 * Retourne la date de la prochaine livraison (aujourd'hui est une valeur possible)  
	 */
	private Date getNextDateLivraison(List<ModeleContratDate> datLivs, Date now, int cartePrepayeeDelai)
	{
		Date ref = DateUtils.addDays(now, -1);
		for (ModeleContratDate lig : datLivs)
		{
			if (ref.before(lig.getDateLiv()))
			{
				return lig.getDateLiv();
			}
		}
		return null;
	}
	
	

	
	public Date getNextDateLivModifiable(List<ModeleContratDate> datLivs, Date now, int cartePrepayeeDelai)
	{
		for (ModeleContratDate lig : datLivs)
		{
			if (cartePrepayeeLigModifiable(lig,now,cartePrepayeeDelai))
			{
				return lig.getDateLiv();
			}
		}
		return null;
	}
	
	
	
	
	public boolean cartePrepayeeLigModifiable(ModeleContratDate lig,Date now, int cartePrepayeeDelai)
	{
		Date d = DateUtils.addDays(lig.getDateLiv(), -cartePrepayeeDelai);
		return  d.after(now);
	}
	
}
