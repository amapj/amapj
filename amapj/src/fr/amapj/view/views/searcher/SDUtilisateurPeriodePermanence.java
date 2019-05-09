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
 package fr.amapj.view.views.searcher;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.searcher.SearcherDefinition;


/**
 * Permet de lister tous les utilisateurs que l'on pourrait ajouter à cette période de permanence
 * 
 *  On peut prendre tous les utilisateurs actifs de la base, sauf ceux déjà présent dans la liste  
 */
public class SDUtilisateurPeriodePermanence implements SearcherDefinition
{
	
	private PeriodePermanenceDTO dto;
	
	private List<PeriodePermanenceUtilisateurDTO> toExclude;
	
	
	/**
	 * Ce searcher va présenter tous les utiisateurs actifs de la base, sauf ceux deja présent dans la liste <code>dto.utilisateurs</code> 
	 * et sauf ceux présent dans <code>toExclude</code> 
	 */
	public SDUtilisateurPeriodePermanence(PeriodePermanenceDTO dto,List<PeriodePermanenceUtilisateurDTO> toExclude)
	{
		this.dto = dto;
		this.toExclude = toExclude;
	}
	
	@Override
	public String getTitle()
	{
		return "Adhérent";
	}

	@Override
	public List<? extends Identifiable> getAllElements(Object params)
	{
		List<Utilisateur> res  = new ArrayList<Utilisateur>();
		List<Utilisateur> us = new UtilisateurService().getUtilisateurs(false);
		
		for (Utilisateur utilisateur : us)
		{
			if (isAlreadyIn(utilisateur.getId())==false)
			{
				res.add(utilisateur);
			}
		}

		return res;
	}


	private boolean isAlreadyIn(Long idUtilisateur)
	{
		for (PeriodePermanenceUtilisateurDTO detail : dto.utilisateurs)
		{
			if (detail.idUtilisateur==idUtilisateur)
			{
				return true;
			}
		}
		
		
		for (PeriodePermanenceUtilisateurDTO detail : toExclude)
		{
			if (detail.idUtilisateur==idUtilisateur)
			{
				return true;
			}
		}
		
		
		return false;
	}

	@Override
	public String toString(Identifiable identifiable)
	{
		Utilisateur u = (Utilisateur) identifiable;
		return u.getNom()+" "+u.getPrenom();
	}
	

	@Override
	public boolean needParams()
	{
		return false;
	}

}
