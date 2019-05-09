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
 package fr.amapj.service.services.gestioncontrat;

import javax.persistence.EntityManager;

import com.google.gson.Gson;

import fr.amapj.common.DebugUtil;
import fr.amapj.common.GzipUtils;
import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.extendparam.MiseEnFormeGraphique;
import fr.amapj.model.models.extendedparam.ExtendedParam;


/**
 * Gestion des paramètres etendus du modele de contrat 
 *
 */
public class ExtPModeleContratService
{

	@DbRead
	public MiseEnFormeGraphique loadMiseEnFormeGraphique(Long idModeleContrat)
	{
		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);
		return getMiseEnFormeGraphique(mc.miseEnFormeGraphique);
	}
	
	@DbWrite
	public void saveMiseEnFormeGraphique(Long idModeleContrat,MiseEnFormeGraphique pm)
	{
		EntityManager em = TransactionHelper.getEm();
		ModeleContrat mc = em.find(ModeleContrat.class, idModeleContrat);
		setMiseEnFormeGraphique(em,mc,pm);
	}
	
	
	/**
	 * Permet de récuperer les parametres etendus d'un modele de contrat 
	 */
	private MiseEnFormeGraphique getMiseEnFormeGraphique(ExtendedParam extendedParam)
	{
		if (extendedParam==null)
		{
			MiseEnFormeGraphique pm = new MiseEnFormeGraphique();
			pm.setDefault();
			return pm;
		}
		
		
		MiseEnFormeGraphique res = (MiseEnFormeGraphique) new Gson().fromJson(GzipUtils.uncompress(extendedParam.content), MiseEnFormeGraphique.class);
		res.setDefault();
		return res;
	}
	
	
	/**
	 *  Permet de sauvegarder les parametres etendus d'un modele de contrat 
	 * @param em 
	 */
	private void setMiseEnFormeGraphique(EntityManager em, ModeleContrat mc,MiseEnFormeGraphique pm)
	{
		Gson gson = new Gson();
		
		String str = gson.toJson(pm);
		
		MiseEnFormeGraphique def = new MiseEnFormeGraphique();
		def.setDefault();
		String ref = gson.toJson(def);
		
		DebugUtil.trace("-----------------------------");
		DebugUtil.trace("str="+str);
		DebugUtil.trace("-----------------------------");
		DebugUtil.trace("ref="+ref);
		DebugUtil.trace("-----------------------------");
		
		// Si les parametres sont les paramètres par défaut, on sauvegarde null pour gagner de l'espace
		if (ref.equals(str))
		{
			if (mc.miseEnFormeGraphique!=null)
			{
				em.remove(mc.miseEnFormeGraphique);
			}
			mc.miseEnFormeGraphique = null;
		}
		else
		{
			if (mc.miseEnFormeGraphique==null)
			{
				mc.miseEnFormeGraphique = new ExtendedParam();
				em.persist(mc.miseEnFormeGraphique);
			}
			
			mc.miseEnFormeGraphique.content = GzipUtils.compress(str);
		}
	}
	
}
