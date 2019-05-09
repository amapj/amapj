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
 * Permet de tranformer une liste en un groupement 1D  
 * 
 * INITIAL est le type des objets de liste initiale 
 * 
 * LIG correspond à l'entete des lignes resultantes
 *
 */
public class G1D<LIG,INITIAL>
{
	
	// Liste de départ 
	private List<INITIAL> items;
	
	// Contenu aprés traitement 
	private List<Cell1<LIG,INITIAL>> content = new ArrayList<Cell1<LIG,INITIAL>>();
	
	// TRI DE LA CLE 
	// Cette liste peut contenir soit des objets de type GetField<LIG> , soit de type GetField<INITIAL> 
	private List<TwoGetFieldHolder<LIG,INITIAL>> ligSortGetField = new ArrayList<TwoGetFieldHolder<LIG,INITIAL>>();
	
	private List<Boolean> ligSortAscendant = new ArrayList<Boolean>();
	
	// TRI DES CELLULES 	
	
	private List<GetField<INITIAL>> cellSortGetField = new ArrayList<GetField<INITIAL>>();
	
	private List<Boolean> cellSortAscendant = new ArrayList<Boolean>();
	
	// CHAMP DE REGROUPEMENT 
	private GetFieldTyped<INITIAL, LIG> groupBy;
	
	private boolean computeDone = false;
	
	// Liste des entete de lignes , dans le cas des entete de lignes fixées
	private List<LIG> fixedLigs = null;
	
	
	static public class Cell1<LIG,INITIAL>
	{
		public Cell1(LIG lig, List<INITIAL> values)
		{
			this.lig = lig;
			this.values = values;
		}
		
		public Cell1()
		{
			
		}

		public LIG lig;
		
		public List<INITIAL> values = new ArrayList<>();
	
	}
	
	
	
	/**
	 *  
	 */
	public G1D()
	{
		
	}
	
	
	/**
	 * Permet de remplir la liste des objets à traiter 
	 */
	public G1D<LIG,INITIAL> fill(List<INITIAL> items)
	{
		this.items = items;
		return this;
	}
	
	
	/**
	 * Permet de spécifier le critère de regroupement 
	 */
	public G1D<LIG,INITIAL> groupBy(GetFieldTyped<INITIAL, LIG> groupBy)
	{
		this.groupBy = groupBy;
		return this;
	}
	
	/**
	 * Optionnel : permet de fixer la liste des entetes de lignes 
	 * 
	 * La liste des entetes de lignes  sera retriée ensuite si on indique un tri avec sortKey  
	 * 
	 * A noter : si il existe des elements V qui ont des clés qui ne sont pas dans fixedLigs, alors ils sont exclus du resultat
	 */
	public G1D<LIG,INITIAL> fixedLigs(List<LIG> fixedLigs)
	{
		this.fixedLigs = fixedLigs;
		return this;
	}
	
	
	/**
	 * Permet de spécifier un critère de tri pour l'entete de ligne, en se basant sur l'entete de ligne seulement
	 */
	public void sortLig(GetField<LIG> f,boolean asc)
	{
		TwoGetFieldHolder<LIG,INITIAL> f1 = new TwoGetFieldHolder<LIG, INITIAL>();
		f1.initA(f);
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	/**
	 * Permet d'indiquer un tri naturel sur la valeur de l'entete de ligne 
	 */
	public void sortLigNatural(boolean asc)
	{
		TwoGetFieldHolder<LIG,INITIAL> f1 = new TwoGetFieldHolder<LIG, INITIAL>();
		f1.initA(e->e);
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	/**
	 * Permet de spécifier un critère de tri pour l'entete de ligne, en se basant sur un champ d'une ligne INITIAL
	 * 
	 * Attention : le champ utilisé dans INITIAL doit dépendre uniquement de l'entete de ligne, sinon le tri sera inconsistant 
	 * 
	 * Attention : le champ INITIAL peut être null (cela se produit si la liste est vide, ce qui peut arriver avec les fixedLigs)
	 */
	public void sortLigAdvanced(GetField<INITIAL> f,boolean asc)
	{
		TwoGetFieldHolder<LIG,INITIAL> f1 = new TwoGetFieldHolder<LIG, INITIAL>();
		f1.initB(f);
		ligSortGetField.add(f1);
		ligSortAscendant.add(asc);
	}
	
	
	/**
	 * Permet de spécifier un critère de tri pour les cellules
	 */
	public void sortCell(GetField<INITIAL> f,boolean asc)
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
	public G1D<LIG,INITIAL> compute()
	{
		if (groupBy==null)
		{
			throw new AmapjRuntimeException("Vous devez appeler d'abord la méthode groupBy");
		}
		
		// On réaliser d'abord le group by 
		Map<LIG, List<INITIAL>> map = items.stream().collect(Collectors.groupingBy(e->groupBy.getField(e)));
		
		// On met le tout en  stream   
		Stream<Cell1<LIG,INITIAL>> s = null;
		if (fixedLigs==null)
		{
			s =	map.entrySet().stream().map(e->new Cell1<LIG,INITIAL>(e.getKey(),e.getValue()));
		}
		else
		{
			// Pour chaque clé de fixedKeys, on associe la liste en provenance de la map 
			s = fixedLigs.stream().map(e->new Cell1<LIG,INITIAL>(e,map.getOrDefault(e, new ArrayList<INITIAL>())));

		}
		
		// On réalise le tri suivant les clés si nécessaire  
		if (ligSortGetField.size()>0)
		{
			ComparatorByField<Cell1<LIG,INITIAL>> comparator = new ComparatorByField<Cell1<LIG,INITIAL>>();
			for (int i = 0; i < ligSortGetField.size(); i++)
			{
				TwoGetFieldHolder<LIG, INITIAL> holder = ligSortGetField.get(i);
				if (holder.isA())
				{	
					GetField<LIG> f = holder.getGetFieldA();
					boolean asc = ligSortAscendant.get(i);
				
					// Attention : comparateur sur la clé 
					comparator.add(e->f.getField(e.lig), asc);
				}
				else
				{
					GetField<INITIAL> f = holder.getGetFieldB();
					boolean asc = ligSortAscendant.get(i);
				
					// Attention : comparateur sur le premier element de la liste des items pour cette entete de ligne 
					comparator.add(e->f.getField(CollectionUtils.getFirstOrNull(e.values)), asc);
				}
			}
		
			s = s.sorted(comparator);
		}
		
		// On met le tout en liste 
		content = s.collect(Collectors.toList());
 
		
		// Si nécessaire , on réalise le tri des cellules 
		if (cellSortGetField.size()>0)
		{
			ComparatorByField<INITIAL> comparator = new ComparatorByField<INITIAL>(cellSortGetField,cellSortAscendant);
			
			for (Cell1<LIG, INITIAL> cell : content)
			{
				Collections.sort(cell.values, comparator);
			}
		}
		
		//
		computeDone = true;
		
		//
		return this;
	}
	
	


	// RECUPERATION DES ELEMENTS 
	
	public List<LIG> getKeys()
	{
		checkComputeDone();
		return content.stream().map(e->e.lig).collect(Collectors.toList());
	}
	
	public List<Cell1<LIG, INITIAL>> getFullCells()
	{
		checkComputeDone();
		return content;
	}
	
	public Cell1<LIG, INITIAL> getFullCell(int index)
	{
		checkComputeDone();
		return content.get(index);
	}
	
	
	public List<List<INITIAL>> getCells()
	{
		checkComputeDone();
		return content.stream().map(e->e.values).collect(Collectors.toList());
	}
	
	public List<INITIAL> getCell(int index)
	{
		return content.get(index).values;
	}
	
	private void checkComputeDone()
	{
		if (computeDone==false)
		{
			throw new AmapjRuntimeException("Vous devez d'abord appeler la méthode compute");
		}
		
	}
	
}
