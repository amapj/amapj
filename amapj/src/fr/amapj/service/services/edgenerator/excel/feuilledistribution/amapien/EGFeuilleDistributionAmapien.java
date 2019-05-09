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
 package fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.CellStyle;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGGrilleTool;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;


/**
 * Permet l'impression de la feuille de distribution pour un amapien 
 * 
 *  
 *
 */
public class EGFeuilleDistributionAmapien  extends AbstractExcelGenerator
{
	
	private Long contratId;
	
	private Long modeleContratId;
	
	private EGMode mode;
	
	public enum EGMode
	{
		STD , 
		UN_VIERGE
	}
	
	
	/**
	 * Deux cas sont possibles 
	 * 
	 * STD,not null,not null
	 * 
	 * UN_VIERGE,not null,null
	 * 
	 * @param mode
	 * @param modeleContratId
	 * @param contratId
	 */
	public EGFeuilleDistributionAmapien(EGMode mode,Long modeleContratId,Long contratId)
	{
		this.mode = mode;
		this.modeleContratId = modeleContratId;
		this.contratId = contratId;
		
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{	
		addOnePage(em,et,"AMAP");
	}

		
	/**
	 * Creéation d'une page pour un utilisateur
	 * @param em
	 * @param et
	 * @param nomPage
	 */
	public void addOnePage(EntityManager em, ExcelGeneratorTool et,String nomPage)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		
		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = new GestionContratService().getAllProduit(em, mc);

		// On charge le contrat
		ContratDTO contratDTO = new MesContratsService().loadContrat(mc.getId(), contratId);
		List<ContratLigDTO> dates = contratDTO.contratLigs;
		
		// Nombre de colonnes fixe à gauche
		int nbColGauche = 3;
			
		// Calcul du nombre de colonnes 
		int nbColTotal = nbColGauche+prods.size();
		
		
		// Construction de la feuille et largeur des colonnes
		et.addSheet(nomPage, nbColTotal, 10);
	    et.setColumnWidth(0, 20);
	    et.setColumnWidth(1, 2);
	    
	    // Génération du titre 
	    List<String> titres = new ArrayList<>();
	    titres.add("");
	    
	    String firstLine = "Feuille de distribution amapien de ";
	    if (mode==EGMode.STD)
	    {
	    	Utilisateur u = em.find(Contrat.class, contratId).getUtilisateur();
	    	firstLine = firstLine+u.getPrenom()+" "+u.getNom();
	    }
	    
	    
	    int nbLine = dates.size();

		// Construction de l'entete
		new EGGrilleTool().contructEntete(et,mc,firstLine,titres,prods,nbLine,nbColGauche,"Dates","");
		
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		
		// Contruction d'une ligne pour chaque date
		for (int i = 0; i < dates.size(); i++)
		{			 
			// Construction de la ligne
			contructRow(et,contratDTO,nbColGauche,i,dates.get(i).date,df2,prods.size());
		}	
		
		// Si on est un vierge et qu'il y a au moins une case exclue et qu'il y a au moins un produit
		// On ajoute un texte explicatfi sur la signification de la croix barrée
		if ( (mode==EGMode.UN_VIERGE) && (contratDTO.excluded!=null) && (nbColTotal>=4) )
	    {
			et.addRow();
			
			et.addRow();
			
			// Colonne 0  : vide
			et.setCell(0, "", et.grasGaucheNonWrappe);
			
			// Colonne 1 - Vide
			et.setCell(1, "", et.grasGaucheNonWrappe);

			// Colonne 2 - une croix  
			et.setCell(2, "", et.nonGrasCentreBordureDiagonal);
			
			// Colonne 3 : un texte explicatif 
			et.setCell(3, "produit non disponible", et.grasGaucheNonWrappe);
	    }
		
	}

	/**
	 * Construction des lignes 
	 * 
	 */
	private void contructRow(ExcelGeneratorTool et, ContratDTO contratDto, int nbColGauche, int dateIndex,Date date, SimpleDateFormat df2,int nbProd)
	{
		et.addRow();
		
		// Colonne 0  : la date
		et.setCell(0,df2.format(date),et.grasCentreBordure);
		
		// Colonne 1 - Vide
		et.setCell(1,"",et.grasGaucheNonWrappeBordure);

		// Colonne 2 - cumul pour cet utilisateur 
		et.setCellSumProdInRow(2, 3, nbProd, 7, et.prixCentreBordure);
		
		// Affectation des quantités
		int index =nbColGauche;
		
		// On itere sur les produits
		for (int j = 0; j < contratDto.contratColumns.size(); j++)
		{	
			CellStyle style = et.nonGrasCentreBordure;
			if (contratDto.isExcluded(dateIndex, j))
			{
				style = et.nonGrasCentreBordureDiagonal;
			}
			
			
			et.setCellQte(index, contratDto.qte[dateIndex][j], style);
			index++;
		}
	}


	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		String str = "distri-amapien-"+mc.getNom();
		
		if (mode==EGMode.UN_VIERGE)
		{
			return str+"-vierge";
		}
		else
		{
			Utilisateur u = em.find(Contrat.class, contratId).getUtilisateur();
			return str+"-"+u.getNom()+" "+u.getPrenom();
		}
	}


	@Override
	public String getNameToDisplay(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		String str = "la feuille de distribution amapien "+mc.getNom();
		
		if (mode==EGMode.UN_VIERGE)
		{
			return str+" vierge";
		}
		else
		{
			Utilisateur u = em.find(Contrat.class, contratId).getUtilisateur();
			return str+" pour "+u.getNom()+" "+u.getPrenom();
		}
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
	
	

	public static void main(String[] args) throws IOException
	{
		new EGFeuilleDistributionAmapien(EGMode.STD,null,null).test(); 
	}

}
