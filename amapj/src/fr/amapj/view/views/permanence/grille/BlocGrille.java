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
 package fr.amapj.view.views.permanence.grille;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;


/**
 * Represente un bloc dans une grille
 *
 */
public class BlocGrille
{
	
	public PeriodePermanenceDateDTO date;
	
	public String titre;
	
	public String styleTitre;
	
	public List<BlocGrilleLine> lines = new ArrayList<BlocGrille.BlocGrilleLine>();
	
	
	static public class BlocGrilleLine
	{
		public String col1;
		public String styleCol1;
		public String col2;
		public String styleCol2;
	}
}
