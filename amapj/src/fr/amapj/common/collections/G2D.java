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
 package fr.amapj.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.CollectionUtils;
import fr.amapj.common.GenericUtils.GetField;
import fr.amapj.common.GenericUtils.GetFieldTyped;
import fr.amapj.common.collections.ab.TwoGetFieldHolder;

/**
 * Permet les manipulations complexes de collections
 * 
 * Permet de tranformer une liste en un groupement 2D
 *
 */
public class G2D<LIG,COL,CONTENT>
{
	
	// Liste de départ 
	private List<CONTENT> items;
	
	// TRI pour les lignes  
	private List<TwoGetFieldHolder<LIG,CONTENT>> ligSortGetField = new ArrayList<TwoGetFieldHolder<LIG,CONTENT>>();
	
	private List<Boolean> ligSortAscendant = new ArrayList<Boolean>();
	
	// TRI pour les colonnes  
	private List<TwoGetFieldHolder<COL,CONTENT>> colSortGetField = new ArrayList<TwoGetFieldHolder<COL,CONTENT>>();
	
	private List<Boolean> colSortAscendant = new ArrayList<Boolean>();

	
	// TRI DES CELLULES 	
	
	private List<GetField<CONTENT>> cellSortGetField = new ArrayList<GetField<CONTENT>>();
	
	private List<Boolean> cellSortAscendant = new ArrayList<Boolean>();
	
	// CHAMP DE REGROUPEMENT pour les lignes
	private GetFieldTyped<CONTENT, LIG> groupByLig;
	
	// CHAMP DE REGROUPEMENT pour les colonnes
	private GetFieldTyped<CONTENT, COL> groupByCol;

	private boolean computeDone = false;
	
	// Contenu aprés traitement 
	// Correspond à une liste de lignes, une ligne étant une liste de cellules 
	private List<List<Cell2<LIG,COL,CONTENT>>> content = new ArrayList<List<Cell2<LIG,COL,CONTENT>>>();
	
	private List<LIG> enteteLigs;
	
	private List<COL> enteteCols;
	
	// TODO cette presentation pourrait être interessante dans certains cas, on pourrait la rendre accessible par un getter
	private List<InternalCell2<LIG, CONTENT>> fullEnteteLigs;  
	
	private List<InternalCell2<COL, CONTENT>> fullEnteteCols;

	
	
	static public class Cell2<LIG,COL,CONTENT>
	{
		public Cell2(LIG lig, COL col, List<CONTENT> values)
		{
			this.lig = lig;
			this.col = col;
			this.values = values;
		}

		public Cell2()
		{
			
		}

		public LIG lig;
		
		public COL col;
		
		public List<CONTENT> values;
	
	}
	
	
	static private class InternalCell2<A,B>
	{
		public A a;
		
		public B b;

		public InternalCell2(A a,B b)
		{
			this.a = a;
			this.b = b;
			
		}	
	}
	
	
	

	/**
	 * Clé permettant le group by 
	 */
	static public class Key2<LIG,COL> 
	{
		LIG ligVal;
		COL colVal;
		
		public Key2()
		{
			
		}

		public Key2(LIG ligVal, COL colVal)
		{
			super();
			this.ligVal = ligVal;
			this.colVal = colVal;
		}

		@Override
		public boolean equals(Object o)
		{
			Key2<LIG,COL> k = (Key2<LIG,COL>) o;
			return ligVal.equals(k.ligVal) && colVal.equals(k.colVal);
		}
		
		@Override
		public int hashCode()
		{
			return ligVal.hashCode()+colVal.hashCode();
		}
		
	}
	
	
	
	/**
	 *  
	 */
	public G2D()
	{
		
	}
	
	
	/**
	 * Permet de remplir la liste des objets à traiter 
	 */
	public G2D<LIG,COL,CONTENT> fill(List<CONTENT> items)
	{
		this.items = items;
		return this;
	}
	
	
	/**
	 * Permet de spécifier le critère de regroupement en ligne 
	 */
	public G2D<LIG,COL,CONTENT> groupByLig(GetFieldTyped<CONTENT, LIG> groupByLig)
	{
		this.groupByLig = groupByLig;
		return this;
	}
	
	/**
	 * Permet de spécifier le critère de regroupement en colonne 
	 */
	public G2D<LIG,COL,CONTENT> groupByCol(GetFieldTyped<CONTENT, COL> groupByCol)
	{
		this.groupByCol = groupByCol;
		return this;
	}

	
	
	
	/**
	 * Permet de spécifier un critère de tri pour les lignes 
	 */
	public void sortLig(GetField<LIG> f,boolean asc)
	{
		TwoGetFieldHolder<LIG,CONTENT> f1 = new TwoGetFieldHolder<LIG, CONTENT>();
		f1.initA(f);
		
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	/**
	 * Permet d'indiquer un tri naturel sur les lignes 
	 */
	public void sortLigNatural(boolean asc)
	{
		TwoGetFieldHolder<LIG,CONTENT> f1 = new TwoGetFieldHolder<LIG, CONTENT>();
		f1.initA(e->e);
		
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	/**
	 * Permet de spécifier un critère de tri pour l'entete de ligne, en se basant sur un champ d'une ligne CONTENT
	 * 
	 * Attention : le champ utilisé dans CONTENT doit dépendre uniquement de l'entete de ligne, sinon le tri sera inconsistant 
	 * 
	 * Attention : le champ CONTENT peut être null (cela se produit si la liste est vide, ce qui peut arriver avec les fixedLigs)
	 */
	public void sortLigAdvanced(GetField<CONTENT> f,boolean asc)
	{
		TwoGetFieldHolder<LIG,CONTENT> f1 = new TwoGetFieldHolder<LIG, CONTENT>();
		f1.initB(f);
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	
	
	
	/**
	 * Permet de spécifier un critère de tri pour les colonnes 
	 */
	public void sortCol(GetField<COL> f,boolean asc)
	{
		TwoGetFieldHolder<COL,CONTENT> f1 = new TwoGetFieldHolder<COL, CONTENT>();
		f1.initA(f);
		
		colSortGetField.add(f1);
		colSortAscendant.add(asc);
	}
	
	/**
	 * Permet d'indiquer un tri naturel sur les colonnes
	 */
	public void sortColNatural(boolean asc)
	{
		TwoGetFieldHolder<COL,CONTENT> f1 = new TwoGetFieldHolder<COL, CONTENT>();
		f1.initA(e->e);
		
		colSortGetField.add(f1);
		colSortAscendant.add(asc);
	}
	
	/**
	 * Permet de spécifier un critère de tri pour l'entete de colonne, en se basant sur un champ d'une ligne CONTENT
	 * 
	 * Attention : le champ utilisé dans CONTENT doit dépendre uniquement de l'entete de colonne, sinon le tri sera inconsistant 
	 * 
	 * Attention : le champ CONTENT peut être null (cela se produit si la liste est vide, ce qui peut arriver avec les fixedLigs)
	 */
	public void sortColAdvanced(GetField<CONTENT> f,boolean asc)
	{
		TwoGetFieldHolder<COL,CONTENT> f1 = new TwoGetFieldHolder<COL, CONTENT>();
		f1.initB(f);
		colSortGetField.add(f1);
		colSortAscendant.add(asc);
	}
	
	
	/**
	 * Permet de spécifier un critère de tri pour les cellules
	 */
	public void sortCell(GetField<CONTENT> f,boolean asc)
	{
		cellSortGetField.add(f);
		cellSortAscendant.add(asc);
	}

	/**
	 * Permet d'indiquer un tri naturel pour les cellules 
	 */
	public void sortCellNatural(boolean asc)
	{
		cellSortGetField.add(e->e);
		cellSortAscendant.add(asc);
	}

	
	
	
	// REALISATION DU CALCUL 
	
	
	
	/**
	 * Réalisation du calcul  
	 */
	public G2D<LIG,COL,CONTENT> compute()
	{	
		if (groupByLig==null)
		{
			throw new AmapjRuntimeException("Vous devez appeler d'abord la méthode groupByLig");
		}
		if (groupByCol==null)
		{
			throw new AmapjRuntimeException("Vous devez appeler d'abord la méthode groupByCol");
		}
		
		
		
		// On réalise d'abord le group by 
		Map<Key2<LIG,COL>, List<CONTENT>> map = items.stream().collect(Collectors.groupingBy(e->new Key2<LIG,COL>(groupByLig.getField(e),groupByCol.getField(e))));
		
		// Si nécessaire , on réalise le tri des cellules 
		if (cellSortGetField.size()>0)
		{
			ComparatorByField<CONTENT> comparator = new ComparatorByField<CONTENT>(cellSortGetField,cellSortAscendant);
			
			for (List<CONTENT> cells : map.values())
			{
				Collections.sort(cells, comparator);
			}
		}
		
		
		// On calcule les entetes de lignes avant tri 
		fullEnteteLigs = computeEnteteLig(map);  
		
		// Idem pour les colonnes
		fullEnteteCols = computeEnteteCol(map);
				
		
		// On réalise le tri des entetes de ligne si necessaire 	
		if (ligSortGetField.size()>0)
		{			
			ComparatorByField<InternalCell2<LIG, CONTENT>> comparatorLig = new ComparatorByField<InternalCell2<LIG, CONTENT>>();
			for (int i = 0; i < ligSortGetField.size(); i++)
			{
				TwoGetFieldHolder<LIG, CONTENT> holder = ligSortGetField.get(i);
				if (holder.isA())
				{	
					GetField<LIG> f = holder.getGetFieldA();
					boolean asc = ligSortAscendant.get(i);
				
					// Attention : comparateur sur la valeur de l'entete de ligne 
					comparatorLig.add(e->f.getField(e.a), asc);
				}
				else
				{
					GetField<CONTENT> f = holder.getGetFieldB();
					boolean asc = ligSortAscendant.get(i);
				
					// Attention : comparateur sur un CONTENT pour cette entete de ligne 
					comparatorLig.add(e->f.getField(e.b), asc);
				}
			}
			Collections.sort(fullEnteteLigs,comparatorLig);
		}	
		// On stocke le resultat final des entetes de lignes 
		enteteLigs = fullEnteteLigs.stream().map(e->e.a).collect(Collectors.toList());
		
		
		// On réalise le tri des entetes de colonnes si necessaire 	
		if (colSortGetField.size()>0)
		{			
			ComparatorByField<InternalCell2<COL, CONTENT>> comparatorCol = new ComparatorByField<InternalCell2<COL, CONTENT>>();
			for (int i = 0; i < colSortGetField.size(); i++)
			{
				TwoGetFieldHolder<COL, CONTENT> holder = colSortGetField.get(i);
				if (holder.isA())
				{	
					GetField<COL> f = holder.getGetFieldA();
					boolean asc = colSortAscendant.get(i);
				
					// Attention : comparateur sur la valeur de l'entete de colonne 
					comparatorCol.add(e->f.getField(e.a), asc);
				}
				else
				{
					GetField<CONTENT> f = holder.getGetFieldB();
					boolean asc = colSortAscendant.get(i);
				
					// Attention : comparateur sur un CONTENT pour cette entete de colonne 
					comparatorCol.add(e->f.getField(e.b), asc);
				}
			}
			Collections.sort(fullEnteteCols,comparatorCol);
		}	
		// On stocke le resultat final des entetes de colonnes 
		enteteCols = fullEnteteCols.stream().map(e->e.a).collect(Collectors.toList());
		
		// On crée le contenu final en mappant les valeurs
		for (LIG lig : enteteLigs)
		{
			List<Cell2<LIG,COL,CONTENT>> ligneCells = new ArrayList<Cell2<LIG,COL,CONTENT>>(enteteCols.size());
			for (COL col : enteteCols)
			{
				List<CONTENT> values = map.getOrDefault(new Key2<LIG,COL>(lig,col), Collections.emptyList());
				Cell2<LIG,COL,CONTENT> cell = new Cell2<LIG,COL,CONTENT>(lig, col, values);
				ligneCells.add(cell);
			}
			content.add(ligneCells);
		}
		
		//
		computeDone = true;
		
		//
		return this;
	}
	
	
	/**
	 * On calcule la liste des entetes de lignes
	 * en associant à chaque entete de ligne un content 
	 */
	private List<InternalCell2<LIG, CONTENT>> computeEnteteLig(Map<Key2<LIG, COL>, List<CONTENT>> map)
	{
		// Pour chaque entrée de la map,on prend les distinct ligVal 
		Stream<Entry<Key2<LIG, COL>, List<CONTENT>>> s = map.entrySet().stream().filter(CollectionUtils.distinctByKey(e->e.getKey().ligVal));
		
		// puis on convertit en une liste de InternalCell2<LIG, CONTENT>
		return s.map(e->new InternalCell2<LIG, CONTENT>(e.getKey().ligVal, CollectionUtils.getFirstOrNull(e.getValue()))).collect(Collectors.toList());	
		
		
	}
	
	
	/**
	 * On calcule la liste des entetes de colonnes
	 * en associant à chaque entete de colonne un content 
	 */
	private List<InternalCell2<COL, CONTENT>> computeEnteteCol(Map<Key2<LIG, COL>, List<CONTENT>> map)
	{
		// Pour chaque entrée de la map,on prend les distinct ligVal 
		Stream<Entry<Key2<LIG, COL>, List<CONTENT>>> s = map.entrySet().stream().filter(CollectionUtils.distinctByKey(e->e.getKey().colVal));
		
		// puis on convertit en une liste de InternalCell2<COL, CONTENT>
		return s.map(e->new InternalCell2<COL, CONTENT>(e.getKey().colVal, CollectionUtils.getFirstOrNull(e.getValue()))).collect(Collectors.toList());	
		
		
	}
	


	// RECUPERATION DES ELEMENTS 
	
	public List<COL> getCols()
	{
		checkComputeDone();
		return enteteCols;
	}
	
	public List<LIG> getLigs()
	{
		checkComputeDone();
		return enteteLigs;
	}

	public List<List<CONTENT>> getLine(int index)
	{
		checkComputeDone();
		return content.get(index).stream().map(e->e.values).collect(Collectors.toList());
	}

	
	public List<Cell2<LIG,COL,CONTENT>> getFullLine(int index)
	{
		checkComputeDone();
		return content.get(index);
	}
	
	/**
	 * Permet de retrouver le contenu d'une cellule à partir de l'index de la ligne et la valeur de la tete de la colonne
	 * 
	 * Si on ne trouve pas la colonne, alors retourne une liste vide 
	 * 
	 */
	public List<CONTENT> findCellContent(int ligIndex,COL col)
	{
		checkComputeDone();
		for (Cell2<LIG, COL, CONTENT> c : content.get(ligIndex))
		{
			if (c.col.equals(col))
			{
				return c.values;
			}
		}
		return Collections.emptyList();
	}
	
	
	
	
	private void checkComputeDone()
	{
		if (computeDone==false)
		{
			throw new AmapjRuntimeException("Vous devez d'abord appeler la méthode compute");
		}
		
	}
	
}
