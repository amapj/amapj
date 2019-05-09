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

import java.util.List;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.cotisation.PeriodeCotisation;
import fr.amapj.service.services.searcher.SearcherService;
import fr.amapj.view.engine.searcher.SearcherDefinition;

public class SDPeriodeCotisation implements SearcherDefinition
{
	@Override
	public String getTitle()
	{
		return "Période de cotisation";
	}

	@Override
	public List<? extends Identifiable> getAllElements(Object params)
	{
		return new SearcherService().getAllElements(PeriodeCotisation.class);
	}


	@Override
	public String toString(Identifiable identifiable)
	{
		PeriodeCotisation u = (PeriodeCotisation) identifiable;
		return u.getNom();
	}
	

	@Override
	public boolean needParams()
	{
		return false;
	}

}
