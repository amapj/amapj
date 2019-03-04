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
 package fr.amapj.view.views.saisiecontrat;

import java.text.SimpleDateFormat;

import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.engine.grid.integergrid.PopupIntegerGrid;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour le detail des dates pour les contrats de type abonnement 
 *  
 */
public class PopupDetailDate extends PopupIntegerGrid
{	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private ContratDTO contratDTO;
	
	/**
	 * 
	 */
	public PopupDetailDate(ContratDTO contratDTO,int[][] qte)
	{
		super();
		this.contratDTO = contratDTO;
		
		//
		popupTitle = "Détails des dates";
		
		// 
		param.readOnly = true;
		param.messageSpecifique = "";
		param.qte = qte;
		
	}
	
	
	
	public void loadParam()
	{
		//
		param.nbLig = contratDTO.contratLigs.size();
		param.nbCol = contratDTO.contratColumns.size();
		param.excluded = contratDTO.excluded;
		
		// tableau des prix
		param.prix = new int[param.nbLig][param.nbCol];
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				ContratColDTO col = contratDTO.contratColumns.get(j);
				param.prix[i][j] = col.prix;
			}
		}
		param.libPrixTotal = "Prix total";
		
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
		for (ContratLigDTO lig : contratDTO.contratLigs)
		{
			param.leftPartLine.add(df.format(lig.date));
		}	
	}
	
	@Override
	public boolean performSauvegarder()
	{
		// Do nothing - ne sera pas appele
		return false;
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


	protected void createButtonBar()
	{
		addDefaultButton("OK", e->close());
	}

	
}
