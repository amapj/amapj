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
 package fr.amapj.view.views.editionspe.bilanlivraison;

import fr.amapj.model.models.editionspe.PageFormat;



/**
 * Liste des templates pour les editions
 *
 */
public enum BilanLivraisonTemplate
{
		
	//
	SIMPLE_PORTRAIT("Bilan de livraison",PageFormat.A4_PORTRAIT) ,
	
		//
	DOCUMENTATION("Documentation - Liste des champs",PageFormat.A4_PORTRAIT);
	
	private String title;   
	private PageFormat pageFormat;
	
	   
	BilanLivraisonTemplate(String title,PageFormat pageFormat) 
    {
        this.title = title;
        this.pageFormat = pageFormat;
    }


	public String getTitle()
	{
		return title;
	}


	public PageFormat getPageFormat()
	{
		return pageFormat;
	}
	
	
	
	

}
