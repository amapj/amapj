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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.grille;

import java.text.SimpleDateFormat;

import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.engine.grid.booleangrid.PopupBooleanGrid;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.grille.PopupBarrerProduit.BarrerData;

/**
 * Popup pour barrer debarrer des dates ou des produits sur un contrat signé 
 *  
 */
public class GrilleBarrerDateProduit extends PopupBooleanGrid
{
	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private ContratDTO contratDTO;
	
	private Long idModeleContrat;
	
	private BarrerData barrerData;
	
	/**
	 * 
	 */
	public GrilleBarrerDateProduit(BarrerData barrerData)
	{
		super();
		this.barrerData = barrerData;
		this.idModeleContrat = barrerData.idModeleContrat;
		
		this.saveButtonTitle = "Continuer ...";
	}
	
	
	
	public void loadParam()
	{
		// Chargement de l'objet  à modifier
		contratDTO = new MesContratsService().loadContrat(idModeleContrat,null);
		contratDTO.expandExcluded();
		
		//
		popupTitle = "Barrer des dates pour le contrat "+contratDTO.nom;
		setWidth(90);
		
		//
		param.messageSpecifique = null;
		
		param.nbCol = contratDTO.contratColumns.size();
		param.nbLig = contratDTO.contratLigs.size();
		param.box = contratDTO.excluded;
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
		for (ContratLigDTO lig : contratDTO.contratLigs)
		{
			param.leftPartLine.add(df.format(lig.date));
		}	
	}
	
	public void performSauvegarder()
	{
		barrerData.validate();
		barrerData.contratDTO = contratDTO;	
		barrerData.resBarrerProduit = new GestionContratSigneService().getBarrerProduitInfo(contratDTO);
	}

}
