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
import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.service.services.searcher.SearcherService;
import fr.amapj.view.engine.searcher.SearcherDefinition;


/**
 * 
 * 
 */
public class SDRoleAllowed implements SearcherDefinition
{
	
	private List<Long> allowed;
	
	public SDRoleAllowed(List<Long> allowed)
	{
		this.allowed = allowed;
	}
	
	@Override
	public String getTitle()
	{
		return "Rôle";
	}

	@Override
	public List<? extends Identifiable> getAllElements(Object params)
	{
		List<Identifiable> res = new ArrayList<Identifiable>();
		
		List<Identifiable> ls = new SearcherService().getAllElements(PermanenceRole.class);
		
		for (Identifiable l : ls)
		{
			if (allowed.contains(l.getId()))
			{
				res.add(l);
			}
		}
		
		return res;
	}

	@Override
	public String toString(Identifiable identifiable)
	{
		PermanenceRole u = (PermanenceRole) identifiable;
		return u.nom;
	}
	

	@Override
	public boolean needParams()
	{
		return false;
	}

}
