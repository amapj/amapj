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
 package fr.amapj.view.engine.grid.currencyvector;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.view.engine.grid.GridHeaderLine;



/**
 * Liste des parametres pour un PopupCurrencyVector
 *
 */
public class CurrencyVectorParam
{
	//
	public int nbLig;
	
	
	// Contient le montant Cible
	public int montantCible;
	
	// Contient le montant de l'avoir initial
	public int avoirInitial;
	
	// Contient les montants reels
	public int[] montant;
	
	// Indique si la dernière ligne doit être calculé automatiquement
	public boolean computeLastLine = true;
	
	
	// Largeur de la colonne en pixel, exemple 110
	public int largeurCol;
	
	// Escpace entre les colonnes en pixel, exemple 3 
	public int espaceInterCol;

	// Grille en lecture seule
	public boolean readOnly;
	
	// Message specifique a afficher en haut de popup
	public String messageSpecifique;
	
	// Message specifique a afficher en haut de popup, aprés le message spécifique 1
	public String messageSpecifique2;
	
	// Message specifique a afficher en haut de popup, aprés le message spécifique 2
	public String messageSpecifique3;

	
	public List<GridHeaderLine> headerLines = new ArrayList<>();
	
	public List<String> leftPartLine = new ArrayList<>();
	
	// Contient toutes les cases qui sont exclues de la saisies
	// Si excluded est null : toutes les cases sont autorisées
	// Si une case est egale a true : alors la case est exclue
	public boolean[] excluded;
	
	// Contient le nombre de lignes exclues
	public int nbExcluded =0;
	
	
	
	
}
