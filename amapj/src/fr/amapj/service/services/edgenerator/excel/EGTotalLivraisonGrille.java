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
 package fr.amapj.service.services.edgenerator.excel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGGrilleTool;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;


/**
 * Génration de la page de cumul des livraisons 
 * pour un contrat
 */
public class EGTotalLivraisonGrille  
{
	
	
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et,List<ModeleContratProduit> prods,List<ModeleContratDate> dates,
			ModeleContrat mc) 
	{			
		// Nombre de colonnes fixe à gauche
		int nbColGauche = 3;
			
		// Calcul du nombre de colonnes 
		int nbColTotal = nbColGauche+prods.size();
		
		// Construction de la feuille et largeur des colonnes
		et.addSheet("Total livraison", nbColTotal, 10);
		et.setColumnWidth(0, 20);
	    et.setColumnWidth(1, 2);
	    
		// On place ensuite cette page en tete
		et.setSheetFirst();
	    
	    
	    // Génération du titre 
	    List<String> titres = new ArrayList<>();
	    titres.add("");
	    
	    ParametresDTO param = new ParametresService().getParametres();
	    
	    String firstLine = param.nomAmap+" - TOTAL DES LIVRAISONS";
	    int nbLine = dates.size();

		// Construction de l'entete
		new EGGrilleTool().contructEntete(et,mc,firstLine,titres,prods,nbLine,nbColGauche,"Dates","");
		
		SimpleDateFormat df1 = new SimpleDateFormat("dd MMMMM");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		
		
		// Contruction d'une ligne pour chaque date
		for (int i = 0; i < dates.size(); i++)
		{			 
			// Construction de la ligne
			contructRow(et,nbColGauche,dates.get(i),df1,df2,prods.size());
		}	
		
		
		
	}

		


	/**
	 * Construction des lignes 
	 * 
	 */
	private void contructRow(ExcelGeneratorTool et, int nbColGauche, ModeleContratDate date, SimpleDateFormat df1,SimpleDateFormat df2,int nbProd)
	{
		et.addRow();
		
		// Colonne 0  : la date
		et.setCell(0,df2.format(date.getDateLiv()),et.grasCentreBordure);
		
		// Colonne 1 - Vide
		et.setCell(1,"",et.grasGaucheNonWrappeBordure);

		// Colonne 2 - cumul pour cette date
		et.setCellSumProdInRow(2, 3, nbProd, 7, et.prixCentreBordure);
		
		String sheetName = df1.format(date.getDateLiv());
		
		// Affectation des quantités
		int index =nbColGauche;
		
		// On itere sur les produits
		for (int j = 0; j < nbProd; j++)
		{	
			String formula = "'"+sheetName+"'!"+et.getCellLabel(10, index);
			et.setCellFormula(index, formula, et.nonGrasCentreBordure);
			index++;
		}
	}
}
