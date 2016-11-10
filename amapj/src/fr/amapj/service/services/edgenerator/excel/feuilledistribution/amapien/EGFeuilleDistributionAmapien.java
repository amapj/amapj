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
 package fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGGrilleTool;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;


/**
 * Permet l'impression de la feuille de distribution pour un amapien 
 * 
 *  
 *
 */
public class EGFeuilleDistributionAmapien  extends AbstractExcelGenerator
{
	
	Long contratId;
	
	public EGFeuilleDistributionAmapien(Long contratId)
	{
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
		Contrat c = em.find(Contrat.class, contratId);
		
		ModeleContrat mc = c.getModeleContrat();
		
		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = new GestionContratService().getAllProduit(em, mc);
		
		// Avec une sous requete, on obtient la liste de toutes les dates de livraison, trièes par ordre croissant 
		List<ModeleContratDate> dates = new GestionContratService().getAllDates(em, mc);

		// On charge ensuite le contrat
		ContratDTO contratDTO = new MesContratsService().loadContrat(mc.getId(), c.getId());
		
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
	    
	    String firstLine = "Feuille de distribution amapien de "+c.getUtilisateur().getPrenom()+" "+c.getUtilisateur().getNom();
	    int nbLine = dates.size();

		// Construction de l'entete
		new EGGrilleTool().contructEntete(et,mc,firstLine,titres,prods,nbLine,nbColGauche,"Dates","");
		
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		
		// Contruction d'une ligne pour chaque date
		for (int i = 0; i < dates.size(); i++)
		{			 
			// Construction de la ligne
			contructRow(et,contratDTO,nbColGauche,i,dates.get(i),df2,prods.size());
		}	
	}

	/**
	 * Construction des lignes 
	 * 
	 */
	private void contructRow(ExcelGeneratorTool et, ContratDTO contratDto, int nbColGauche, int dateIndex,ModeleContratDate date, SimpleDateFormat df2,int nbProd)
	{
		et.addRow();
		
		// Colonne 0  : la date
		et.setCell(0,df2.format(date.getDateLiv()),et.grasCentreBordure);
		
		// Colonne 1 - Vide
		et.setCell(1,"",et.grasGaucheNonWrappeBordure);

		// Colonne 2 - cumul pour cet utilisateur 
		et.setCellSumProdInRow(2, 3, nbProd, 7, et.prixCentreBordure);
		
		// Affectation des quantités
		int index =nbColGauche;
		
		// On itere sur les produits
		for (int j = 0; j < contratDto.contratColumns.size(); j++)
		{	
			et.setCellQte(index, contratDto.qte[dateIndex][j], et.nonGrasCentreBordure);
			index++;
		}
	}


	@Override
	public String getFileName(EntityManager em)
	{
		Contrat c = em.find(Contrat.class, contratId);
		Utilisateur u = c.getUtilisateur();
		return "distri-amapien-"+c.getModeleContrat().getNom()+"-"+u.getNom()+" "+u.getPrenom();
	}


	@Override
	public String getNameToDisplay(EntityManager em)
	{
		Contrat c = em.find(Contrat.class, contratId);
		Utilisateur u = c.getUtilisateur();
		return "la feuille de distribution amapien "+c.getModeleContrat().getNom()+" pour "+u.getNom()+" "+u.getPrenom();
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
	
	

	public static void main(String[] args) throws IOException
	{
		new EGFeuilleDistributionAmapien(8342L).test(); 
	}

}
