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
 package fr.amapj.view.views.saisiecontrat;

import java.text.SimpleDateFormat;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.engine.grid.integergrid.PopupIntegerGrid;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.SaisieContratData;

/**
 * Popup pour la saisie des quantites pour une carte prépayée 
 *  
 */
public class PopupSaisieQteCartePrepayee extends PopupIntegerGrid
{	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private ContratDTO contratDTO;
	
	private SaisieContratData data;
	
	// Index de la première ligne que l'on va pouvoir modifier 
	private int indexFirstLine;
	
	
	
	/**
	 * 
	 */
	public PopupSaisieQteCartePrepayee(SaisieContratData data)
	{
		super();
		this.data = data;
		this.contratDTO = data.contratDTO;
		
		//
		popupTitle = "Mon contrat "+contratDTO.nom;
		
		// 
		param.readOnly = (data.modeSaisie==ModeSaisie.READ_ONLY);
		param.messageSpecifique = data.messageSpecifique;
		
	}
	
	
	
	public void loadParam()
	{
		int nbLigRestant = contratDTO.cartePrepayee.nbLigModifiable;
		if (nbLigRestant==0)
		{
			throw new AmapjRuntimeException("Pas de ligne restante à afficher");
		}
		indexFirstLine = contratDTO.contratLigs.size()-nbLigRestant; 
		
		//
		param.nbLig = nbLigRestant;
		param.nbCol = contratDTO.contratColumns.size();
		param.qte = computeQte(indexFirstLine);
		param.excluded = computeExcluded(indexFirstLine);
		param.allowedEmpty = computeAllowedEmpty();
		
		// tableau des prix
		param.prix = computePrix(indexFirstLine); 
		param.libPrixTotal = "Prix des livraisons à venir";
		
		// Largeur des colonnes
		param.largeurCol = 110;
		
				
		// Construction du header 1
		GridHeaderLine line1  =new GridHeaderLine();
		line1.styleName = "tete";
		line1.cells.add("Produit");
				
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			line1.cells.add(col.nomProduit);
		}
		GridSizeCalculator.autoSize(line1,param.largeurCol,"Arial",16);
		
	
		// Construction du header 2
		GridHeaderLine line2  =new GridHeaderLine();
		line2.styleName = "prix";
		line2.cells.add("prix unitaire");
				
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			line2.cells.add(new CurrencyTextFieldConverter().convertToString(col.prix));
		}

		// Construction du header 3
		GridHeaderLine line3  =new GridHeaderLine();
		line3.styleName = "tete";
		line3.cells.add("Dates");
				
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			line3.cells.add(col.condtionnementProduit);
		}
		GridSizeCalculator.autoSize(line3,param.largeurCol,"Arial",16);
		
		param.headerLines.add(line1);
		param.headerLines.add(line2);
		param.headerLines.add(line3);
		
		
		// Partie gauche de chaque ligne
		param.leftPartLineLargeur = 110;
		param.leftPartLineStyle = "date-saisie";
		for (int i = 0; i < param.nbLig; i++)
		{
			ContratLigDTO lig = contratDTO.contratLigs.get(i+indexFirstLine);
			param.leftPartLine.add(df.format(lig.date));
		}	
	}
	

	/**
	 * On est autorisé à laisser tout vide uniquement si il y a dèjà des quantités sur la partie qui est cachée
	 */
	private boolean computeAllowedEmpty()
	{
		for (int i = 0; i < indexFirstLine; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				if (contratDTO.qte[i][j]!=0)
				{
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * On réalise une simple copie de la partie qui nous concerne
	 * 
	 */
	private int[][] computeQte(int indexFirstLine)
	{
		int[][] res = new int [param.nbLig][param.nbCol];
		
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				res[i][j] = contratDTO.qte[i+indexFirstLine][j];
			}
		}
		return res;
	}
	
	
	/**
	 * On réalise une simple copie de la partie qui nous concerne
	 * 
	 */
	private boolean[][] computeExcluded(int indexFirstLine)
	{
		if (contratDTO.excluded==null)
		{
			return null;
		}
		
		boolean[][] res = new boolean [param.nbLig][param.nbCol];
		
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				res[i][j] = contratDTO.excluded[i+indexFirstLine][j];
			}
		}
		return res;
	}
	
	
	/**
	 * On réalise une simple copie de la partie qui nous concerne
	 * 
	 */
	private int[][] computePrix(int indexFirstLine)
	{	
		int[][] res = new int [param.nbLig][param.nbCol];
		
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				ContratColDTO col = contratDTO.contratColumns.get(j);
				res[i][j] =  col.prix;
			}
		}
		return res;
	}
	
	
	
	@Override
	protected void handleContinuer()
	{
		data.validate();
		close();
	}

	@Override
	public boolean performSauvegarder()
	{
		// On recopie les données dans le contratDto, uniquement la partie qui nous concerne 
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				contratDTO.qte[i+indexFirstLine][j] = param.qte[i][j];
			}
		}
		
		
		// 
		data.validate();
		
		return true;
	}



	@Override
	public int getLineHeight(boolean readOnly)
	{
		// Une ligne fait 32 en mode edition , sinon 26
		return readOnly ? 26 : 32;
	}
	
	@Override
	public int getHeaderHeight()
	{
		// On cacule la place consommée par les headers, boutons, ...
		// 365 : nombre de pixel mesurée pour les haeders, les boutons, ... en mode normal, 270 en mode compact
		return BaseUiTools.isCompactMode() ? 270 : 365;

	}
	
}
