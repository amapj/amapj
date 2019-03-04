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
 package fr.amapj.service.services.importdonnees;

import fr.amapj.view.engine.tools.TableItem;


/**
 * Permet la gestion des utilisateurs en masse
 * ou du changement de son état
 * 
 */
public class ImportProduitProducteurDTO implements TableItem
{
	
	public String producteur;
	public String produit;
	public String conditionnement;
	
	public Long getId()
	{
		return null;
	}

}
