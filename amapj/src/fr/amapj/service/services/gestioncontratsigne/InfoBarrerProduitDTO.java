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
 package fr.amapj.service.services.gestioncontratsigne;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.collections.G2D;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.fichierbase.Produit;

/**
 * Permet de savoir les produits barrés et les produits débarrés
 *
 */
public class InfoBarrerProduitDTO 
{
	static public enum DiffState
	{
		// le produit était dispo avant et maintenant il ne l'est plus
		NO_MORE_DISPO,
		
		// le produit n'était pas dispo avant et maintenant il l'est
		NOW_DISPO;
	}
	
	static public class CellChange
	{
		public DiffState state;
		
		public ModeleContratDate modeleContratDate;
		
		public ModeleContratProduit modeleContratProduit;

		public CellChange(DiffState state, ModeleContratDate modeleContratDate, ModeleContratProduit modeleContratProduit)
		{
			super();
			this.state = state;
			this.modeleContratDate = modeleContratDate;
			this.modeleContratProduit = modeleContratProduit;
		}
		

		
	}
	
	public List<CellChange> cellChanges = new ArrayList<InfoBarrerProduitDTO.CellChange>();

	public void addCellChange(DiffState state, ModeleContratDate modeleContratDate, ModeleContratProduit modeleContratProduit)
	{
		cellChanges.add(new CellChange(state, modeleContratDate, modeleContratProduit));
	}
	
	
	/**
	 * Permet un affichage lisible de la liste des modifications qui vont être réalisées
	 */
	public String computeStringInfo()
	{
		// On réalise une projection 2D de ces cellChanges
		// En colonne, l'état DiffState state
		// En ligne , les produits 
		G2D<Produit,DiffState,CellChange> c1 = new G2D<Produit, DiffState, CellChange>();
		
		// 
		c1.fill(cellChanges);
		c1.groupByLig(e->e.modeleContratProduit.getProduit());
		c1.groupByCol(e->e.state);
		
		// Tri par index du produit dans le contrat
		c1.sortLigAdvanced(e->e.modeleContratProduit.getIndx(),true);
		
		// Tri des colonnes dans l'ordre de declaration, donc NO_MORE_DISPO en premier 
		c1.sortCol(e->e,true);
		
		// tri sur les cellules par dates croissantes
		c1.sortCell(e->e.modeleContratDate.getDateLiv(), true);
		
		//
		c1.compute();
		
		
		// On transforme ensuite cette grille en une chaine de caractères lisibles facilement 
		StringBuilder buf = new StringBuilder();
		List<Produit> produits = c1.getLigs();
		
		buf.append(produits.size() + " produits sont impactés : <br/><ul>");
		for (int i = 0; i < produits.size(); i++)
		{
			Produit produit = produits.get(i);
			
			List<CellChange> noMoreDispo = c1.findCellContent(i, DiffState.NO_MORE_DISPO);
			List<CellChange> nowDispo = c1.findCellContent(i, DiffState.NOW_DISPO);
				
			if (noMoreDispo.size()>0)
			{
				List<Date> noMoreDispoDates = CollectionUtils.convert(noMoreDispo, e->e.modeleContratDate.getDateLiv());
				buf.append("<li>" + produit.getNom() + "," + produit.getConditionnement() + " n'est plus disponible "+FormatUtils.listeDate(noMoreDispoDates)+"</li>");
			}
			
			if (nowDispo.size()>0)
			{
				List<Date> nowDispoDates = CollectionUtils.convert(nowDispo, e->e.modeleContratDate.getDateLiv());
				buf.append("<li>" + produit.getNom() + "," + produit.getConditionnement() + " est maintenant disponible "+FormatUtils.listeDate(nowDispoDates)+"</li>");
			}
		}
		
		buf.append("</ul>");
		
		return buf.toString();
	}
	
	
	
	
	
	
	
	
}
