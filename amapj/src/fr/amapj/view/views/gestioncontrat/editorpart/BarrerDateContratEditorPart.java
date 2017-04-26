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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.ContratColDTO;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.booleangrid.PopupBooleanGrid;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour la saisie des quantites pour un contrat
 *  
 */
public class BarrerDateContratEditorPart extends PopupBooleanGrid
{
	private final static Logger logger = LogManager.getLogger();
	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private ContratDTO contratDTO;
	
	private Long idModeleContrat;
	
	/**
	 * 
	 */
	public BarrerDateContratEditorPart(Long idModeleContrat)
	{
		super();
		this.idModeleContrat = idModeleContrat;
	}
	
	
	
	public void loadParam()
	{
		// Chargement de l'objet  à modifier
		contratDTO = new MesContratsService().loadContrat(idModeleContrat,null);
		
		//
		popupTitle = "Barrer des dates pour le contrat "+contratDTO.nom;
		setWidth(90);
		
		//
		param.messageSpecifique = null;
		
		param.nbCol = contratDTO.contratColumns.size();
		param.nbLig = contratDTO.contratLigs.size();
		if (contratDTO.excluded==null)
		{
			contratDTO.excluded = new boolean[param.nbLig][param.nbCol];
			for (int i = 0; i < param.nbLig; i++)
			{
				for (int j = 0; j < param.nbCol; j++)
				{
					contratDTO.excluded[i][j] = false ;
				}
			}
		}
		param.box = contratDTO.excluded;
		
		param.largeurCol = 110;
		param.espaceInterCol = 3;
		
				
		// Construction du header 1
		GridHeaderLine line1  =new GridHeaderLine();
		line1.height = 70;
		line1.styleName = "tete";
		line1.cells.add("Produit");
				
		for (ContratColDTO col : contratDTO.contratColumns)
		{
			line1.cells.add(col.nomProduit);
		}
		
	
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
		
		new GestionContratService().updateDateBarreesModeleContrat(contratDTO);
	}
	
	/**
	 * Vérifie si il n'y a pas déjà des contrats signés, qui vont empecher de modifier les disponibilites
	 */
	@Override
	protected String checkInitialCondition()
	{
		int nbInscrits = new GestionContratService().getNbInscrits(idModeleContrat);
		if (nbInscrits!=0)
		{
			String str = "Vous ne pouvez plus barrer certaines dates ou produits pour ce contrat<br/>"+
						 "car "+nbInscrits+" adhérents ont déjà souscrits à ce contrat<br/>."+
						 "Une seule solution est possible :<br/><ul>"+
						 "<li>Supprimez les contrats signés par les adhérents, si ce sont des données de test</li>"+
						 "</ul>";
			return str;
		}
		
		return null;
	}
	
}
