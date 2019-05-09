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
 package fr.amapj.view.views.editionspe.engagement;

import fr.amapj.model.engine.metadata.MetaDataEnum;
import fr.amapj.model.models.editionspe.PageFormat;

/**
 * Liste des templates pour les editions
 *
 */
public enum EngagementTemplate 
{
	//
	TRES_SIMPLE_PORTRAIT(PageFormat.A4_PORTRAIT) ,
	
	//
	SIMPLE_PORTRAIT(PageFormat.A4_PORTRAIT) ,
	
	//
	SIMPLE_SANS_RECU(PageFormat.A4_PORTRAIT) ,
	
	// 
	DETAILLE(PageFormat.A4_PORTRAIT) ,
	
	//
	VIERGE_PORTRAIT(PageFormat.A4_PORTRAIT),
	
	//
	VIERGE_PAYSAGE(PageFormat.A4_PAYSAGE),
	
	//
	DOCUMENTATION(PageFormat.A4_PORTRAIT);
	
	
	
	static public class MetaData extends MetaDataEnum
	{
		
		public void fill()
		{	
			add(TRES_SIMPLE_PORTRAIT,"Contrat très simple avec reçu - 1 page Portrait");
			add(SIMPLE_PORTRAIT,"Contrat simple  avec reçu - 1 page Portrait");
			add(SIMPLE_SANS_RECU,"Contrat simple  sans reçu - 1 page Portrait");
			add(DETAILLE,"Contrat détaillé - 2 pages Portrait");
			add(VIERGE_PORTRAIT,"Modèle vierge - Portrait");
			add(VIERGE_PAYSAGE,"Modèle vierge - Paysage");
			add(DOCUMENTATION,"Documentation - Liste des champs");
		}
	}	
	
	
	   
	private PageFormat pageFormat;
	  
	EngagementTemplate(PageFormat pageFormat) 
    {
        this.pageFormat = pageFormat;
    }

	public PageFormat getPageFormat()
	{
		return pageFormat;
	}
}
