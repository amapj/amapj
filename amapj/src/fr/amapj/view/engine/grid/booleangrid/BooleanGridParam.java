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
 package fr.amapj.view.engine.grid.booleangrid;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.view.engine.grid.GridHeaderLine;



/**
 * Liste des parametres pour un PopupBooleanGrid
 *
 */
public class BooleanGridParam
{
	//
	public int nbLig;
	
	//
	public int nbCol;
	
	// Contient les boolean qte[numero_ligne][numero_colonne]
	public boolean[][] box;
	
	// Largeur de la colonne en pixel, exemple 110
	public int largeurCol;

	// Message specifique a afficher en haut de popup
	public String messageSpecifique;
	
	public List<GridHeaderLine> headerLines = new ArrayList<>();
	
	public List<String> leftPartLine = new ArrayList<>();	
	
	public List<String> leftPartLine2 = null;
}
