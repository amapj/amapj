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
 package fr.amapj.service.services.logview;

import java.util.Date;

import fr.amapj.view.engine.tools.TableItem;


/**
 * 
 */
public class StatAccessDTO implements TableItem
{
	
	public Date date;
	
	public int nbAcces;
	
	public int nbVisiteur;
	
	// En minutes
	public int tempsTotal;

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public int getNbAcces()
	{
		return nbAcces;
	}

	public void setNbAcces(int nbAcces)
	{
		this.nbAcces = nbAcces;
	}

	public int getNbVisiteur()
	{
		return nbVisiteur;
	}

	public void setNbVisiteur(int nbVisiteur)
	{
		this.nbVisiteur = nbVisiteur;
	}

	public int getTempsTotal()
	{
		return tempsTotal;
	}

	public void setTempsTotal(int tempsTotal)
	{
		this.tempsTotal = tempsTotal;
	}

	@Override
	public Long getId()
	{
		return new Long(this.hashCode());
	}
	
	
	
	
}
