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
 package fr.amapj.view.engine.grid.integergrid;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.view.engine.grid.GridHeaderLine;



/**
 * Liste des parametres pour un PopupIntegerGrid
 *
 */
public class IntegerGridParam
{
	//
	public int nbLig;
	
	//
	public int nbCol;
	
	// Contient les prix de chaque cellule prix[numero_ligne][numero_colonne]
	public int[][] prix;
	
	// Contient les quantites qte[numero_ligne][numero_colonne]
	// Attention : ceci doit être un nouveau tableau, car il est modifié 
	public int[][] qte;
	
	// Largeur des colonnes de saisie  en pixel, exemple 110
	public int largeurCol;
	
	// Grille en lecture seule
	public boolean readOnly;
	
	// Message specifique a afficher en haut de popup
	public String messageSpecifique;
	
	// Libellé utilisé pour le prix total 
	public String libPrixTotal="";
	
	// Message specifique a afficher en bas de popup
	public String messageSpecifiqueBottom;
	
	public List<GridHeaderLine> headerLines = new ArrayList<>();
	
	// Largeur de la colonne de gauche en pixel
	public int leftPartLineLargeur;
	
	// Style de la colonne de gauche 
	public String leftPartLineStyle;
	
	// Contenu de la colonne de gauche 
	public List<String> leftPartLine = new ArrayList<>();
	
	// Contient toutes les cases qui sont exclues de la saisies
	// Si excluded est null : toutes les cases sont autorisées
	// Si une case est egale a true : alors la case est exclue
	public boolean[][] excluded;
	
	// si true, il est autorisé de saisir une grille avec uniquement des 0
	// si false, il est impossible de saisir une grille avec uniquement des 0
	public boolean allowedEmpty = false;
	
	
	// Si true,présence d'un boutton copier la première ligne  
	public boolean buttonCopyFirstLine = true;
	
	/*
	 * Partie calculée automatiquement et initialisée par la méthode initialize
	 */
	
	// Montant total du contrat en centimes
	private int montantTotal;
	
	// Montant total des lignes
	private int montantLig[];
	
	// Montant total des colonnes
	private int montantCol[];
			
	
	/**
	 * 
	 */
	public void initialize()
	{
		// Montant total des colonnes et montant total total
		montantCol = new int[nbCol];
		montantTotal = 0;
		
		for (int j = 0; j < nbCol; j++)
		{
			montantCol[j] = 0;
			for (int i = 0; i < nbLig; i++)
			{
				montantCol[j] = montantCol[j] +qte[i][j]*prix[i][j];
				montantTotal = montantTotal +qte[i][j]*prix[i][j];
			}
		}
		
		
		// Montant total des lignes
		montantLig = new int[nbLig];
		for (int i = 0; i < nbLig; i++)
		{
			montantLig[i] = 0;
			for (int j = 0; j < nbCol; j++)
			{
				montantLig[i] = montantLig[i] +qte[i][j]*prix[i][j];
			}
		}
	}
	
	
	/**
	 * Cette méthode met à jour à la fois la quantité et le montant total
	 * 
	 */
	public void updateQte(int lig,int col,int newQte)
	{
		int delta = (newQte-qte[lig][col])*prix[lig][col];
		
		qte[lig][col] = newQte;
		montantLig[lig] = montantLig[lig]+ delta;
		montantCol[col] = montantCol[col]+ delta;
		montantTotal = montantTotal+delta;
	}
	
	/**
	 * Cette fonction doit être utilisée uniquement par PopupIntegerGrid
	 * @return
	 */
	public int getMontantTotal()
	{
		return montantTotal;
	}
	
}
