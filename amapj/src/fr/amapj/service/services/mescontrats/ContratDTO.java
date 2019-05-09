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
 package fr.amapj.service.services.mescontrats;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.amapj.model.models.contrat.modele.JokerMode;
import fr.amapj.model.models.contrat.modele.NatureContrat;

/**
 * Represente un contrat ou un modele de contrat 
 *
 */
public class ContratDTO
{
	public Long contratId;
	
	public Long modeleContratId;
	
	public String nom;
	
	public String description;

	public String nomProducteur;
	
	public Date dateFinInscription;
	
	public Date dateDebut;
	
	public Date dateFin;

	public int nbLivraison;
	
	// Caractéristiques des lignes
	public List<ContratLigDTO> contratLigs = new ArrayList<ContratLigDTO>();

	// Caractéristiques des colonnes
	public List<ContratColDTO> contratColumns = new ArrayList<ContratColDTO>();
	
	// Contient les quantites qte[numero_ligne][numero_colonne]
	public int[][] qte;
	
	// Contient toutes les cases qui sont exclues de la saisies
	// Si excluded est null : toutes les cases sont autorisées
	// Si une case est egale a true : alors la case est exclue
	public boolean[][] excluded;
	
	
	// Caractéristiques du paiement
	public InfoPaiementDTO paiement;
	
	// Nature du contrat 
	public NatureContrat nature;
	
	// Ce contrat est modifiable - null dans le cas d'un modele de contrat   
	public Boolean isModifiable;
	
	// Ce contrat est supprimable - null dans le cas d'un modele de contrat   
	public Boolean isSupprimable;
	
	// Ce contrat n'est pas modifiable mais comporte des jokers que l'utilisateur peut modifier - null dans le cas d'un modele de contrat
	public Boolean isJoker;
	
	// Champ chargé uniquement pour les cartes prépayées
	public CartePrepayeeDTO cartePrepayee = null;
	
	// Champs concernant uniquement les contrats de type abonnement avec joker  
	public int jokerNbMin = 0;
	public int jokerNbMax = 0;
	public JokerMode jokerMode;
	public int jokerDelai;
	
	
	/**
	 * @return true si toutes les quantités sont à zéro
	 */
	public boolean isEmpty()
	{
		for (int i = 0; i < contratLigs.size(); i++)
		{
			for (int j = 0; j < contratColumns.size(); j++)
			{
				if (qte[i][j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	

	public int getMontantTotal()
	{
		int mnt = 0;
		
		for (int j = 0; j < contratColumns.size(); j++)
		{
			int prix = contratColumns.get(j).prix;
		
			for (int i = 0; i < contratLigs.size(); i++)
			{
				mnt = mnt + qte[i][j] * prix;
			}
		}
		return mnt;
	}

	/**
	 * Return true si toute la ligne est exclue
	 */
	public boolean isFullExcludedLine(int lineNumber)
	{
		for (int j = 0; j <  contratColumns.size(); j++)
		{
			if (isExcluded(lineNumber,j)==false)
			{
				return false;
			}
		}
		return true;
	}
	


	/**
	 * Retourne true si cette cellule est exclue,false sinon 
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean isExcluded(int i, int j)
	{
		if (excluded==null)
		{
			return false;
		}
		return excluded[i][j];
	}
	

	/**
	 * Réalise le calcul de la matrice excluded, si celle ci est nulle 
	 */
	public void expandExcluded()
	{
		if (excluded==null)
		{
			int nbCol = contratColumns.size();
			int nbLig = contratLigs.size();
			
			excluded = new boolean[nbLig][nbCol];
			for (int i = 0; i < nbLig; i++)
			{
				for (int j = 0; j < nbCol; j++)
				{
					excluded[i][j] = false ;
				}
			}
		}
	}
	
	
}
