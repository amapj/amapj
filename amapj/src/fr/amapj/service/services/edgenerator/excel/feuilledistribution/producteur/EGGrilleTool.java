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
 package fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.poi.ss.usermodel.CellStyle;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.ContratLigDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;


/**
 * Permet la création de grilles Excel avec les produits en colonnes
 * 
 * Outil utilisé par les feuilles de distribution producteur et amapien , et la synthese du contrat en 1 page
 *
 */
public class EGGrilleTool 
{
	public EGGrilleTool()
	{		
	}
	

	public void performSheet(ExcelGeneratorTool et, String firstLine,String nomFeuille, ModeleContrat mc, List<ModeleContratProduit> prods, List<ModeleContratDate> dates, List<Utilisateur> utilisateurs,
			int nbColGauche, Map<Utilisateur, ContratDTO> contrats)
	{
			
		// Calcul du nombre de colonnes :  (date / produit)
		int nbColDateProd = dates.size()*prods.size();
		
		int nbColTotal = nbColGauche+nbColDateProd;
		
		
		// Construction de la feuille et largeur des colonnes
		et.addSheet(nomFeuille, nbColTotal, 10);
	    et.setColumnWidth(0, 16);
	    et.setColumnWidth(1, 16);
	    
	    
	    // Génération du titre de chaque bloc (un bloc = une date)
	    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
	    List<String> titres = new ArrayList<>();
	    for (ModeleContratDate date : dates)
		{
			titres.add(df2.format(date.getDateLiv()));
		}
	    

		// Construction de l'entete
		contructEntete(et,mc,firstLine,titres,prods,utilisateurs.size(),nbColGauche,"Nom","Prénom");
		
		// Contruction d'une ligne pour chaque Utilisateur
		for (int i = 0; i < utilisateurs.size(); i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
			
			// 
			ContratDTO contratDto = contrats.get(utilisateur); 
			
			// Construction de la ligne
			contructRow(et,contratDto,utilisateur,nbColDateProd,nbColGauche,dates);
		}	
	}



	public void contructEntete(ExcelGeneratorTool et, ModeleContrat mc, String firstLine,List<String> titres, List<ModeleContratProduit> prods, int nbClient, int nbColGauche,String c1,String c2)
	{
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		
		// Ligne 1 à 5
		et.addRow(firstLine,et.grasGaucheNonWrappe);
		et.addRow(mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow(mc.getDescription(),et.grasGaucheNonWrappe);
		et.addRow("Extrait le "+df1.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);

				
		// Ligne 6 avec les dates
		et.addRow();
		et.setCell(0,c1,et.grasCentreBordure);
		et.setCell(1,c2,et.grasCentreBordure);
		et.setCell(2,"Montant total",et.grasCentreBordure);
		
		for (int i = 0; i < titres.size(); i++)
		{
			String titre = titres.get(i);
			int index = nbColGauche+i*prods.size(); 
			et.setCell(index,titre,et.switchColor(et.grasCentreBordure,i));
			et.mergeCellsRight(index, prods.size());
		}
		
		// Ligne 7 avec le nom du produit
		et.addRow();
		et.setRowHeigth(4);
		int i =0;
		for (int k = 0; k < titres.size(); k++)
		{
			for (ModeleContratProduit prod : prods)
			{
				int index = nbColGauche+i;
				et.setCell(index,prod.getProduit().getNom(),et.switchColor(et.grasCentreBordure,k));
				i++;
			}
		}
		
		// Ligne 8 avec le prix du produit
		et.addRow();
		i =0;
		for (int k = 0; k < titres.size(); k++)
		{
			for (ModeleContratProduit prod : prods)
			{
				int index = nbColGauche+i;
				et.setCellPrix(index,prod.getPrix(),et.switchColor(et.prixCentreBordure,k));
				i++;
			}
		}
		
		// Ligne 9 avec le conditionnement du produit
		et.addRow();
		et.setRowHeigth(6);
		et.mergeCellsUp(0, 4);
		et.mergeCellsUp(1, 4);
		et.mergeCellsUp(2, 4);
		i =0;
		for (int k = 0; k < titres.size(); k++)
		{
			for (ModeleContratProduit prod : prods)
			{
				int index = nbColGauche+i;
				et.setCell(index,prod.getProduit().getConditionnement(),et.switchColor(et.grasCentreBordure,k));
				i++;
			}
		}
			
		// Ligne 10 vide
		et.addRow();
		
		// Ligne 11 avec le cumul 
		et.addRow();
		et.setCell(0, "Cumul", et.grasGaucheNonWrappeBordure);
		et.setCell(1, "", et.grasGaucheNonWrappeBordure);
		et.setCellSumInColDown(2, 2, nbClient, et.prixCentreBordure);
				
		//
		i=0;
		for (int k = 0; k < titres.size(); k++)
		{
			for (ModeleContratProduit prod : prods)
			{
				int index=nbColGauche+i;
				et.setCellSumInColDown(index, 2, nbClient, et.switchColor(et.grasCentreBordure,k));
				i++;
			}
		}
		
		// Ligne 12 vide
		et.addRow();
	}

		


	/**
	 * Construction des lignes utilisateurs 
	 * 
	 * @param et
	 * @param contratDto
	 * @param utilisateur
	 * @param nbColDateProd
	 * @param nbColGauche
	 */
	private void contructRow(ExcelGeneratorTool et, ContratDTO contratDto, Utilisateur utilisateur,int nbColDateProd,int nbColGauche, List<ModeleContratDate> dates)
	{
		et.addRow();
		
		// Colonne 0 et 1 : le nom et prenom 
		et.setCell(0,utilisateur.getNom(),et.grasGaucheNonWrappeBordure);
		et.setCell(1,utilisateur.getPrenom(),et.nonGrasGaucheBordure);
		
		// Colonne 3 - cumul pour cet utilisateur 
		et.setCellSumProdInRow(2, 3, nbColDateProd, 7, et.prixCentreBordure);
		
		// Affectation des quantités
		int index =nbColGauche;
		
		// On itere sur les dates
		for (int k = 0; k < dates.size(); k++)
		{
			ModeleContratDate date = dates.get(k);
			int i = findIndex(date,contratDto.contratLigs);
			
			// On itere sur les produits
			for (int j = 0; j < contratDto.contratColumns.size(); j++)
			{	
				CellStyle style = et.nonGrasCentreBordure;
				if (contratDto.isExcluded(i, j))
				{
					style = et.nonGrasCentreBordureDiagonal;
				}
				
				
				et.setCellQte(index, contratDto.qte[i][j], et.switchColor(style, k));
				index++;
			}
		}
	}

	private int findIndex(ModeleContratDate date, List<ContratLigDTO> contratLigs)
	{
		for (int i = 0; i < contratLigs.size(); i++)
		{
			ContratLigDTO contratLigDTO = contratLigs.get(i);
			if (contratLigDTO.modeleContratDateId.equals(date.getId()))
			{
				return i;
			}
		}
		return -1;
	}
	
	
	private ContratDTO findContrat(EntityManager em,Utilisateur utilisateur,ModeleContrat mc)
	{
		
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Contrat> cq = cb.createQuery(Contrat.class);
		Root<Contrat> root = cq.from(Contrat.class);

		// On ajoute la condition where
		cq.where(cb.and(cb.equal(root.get(Contrat.P.UTILISATEUR.prop()), utilisateur),cb.equal(root.get(Contrat.P.MODELECONTRAT.prop()), mc)));
		
		List<Contrat> contrats = em.createQuery(cq).getResultList();
		if (contrats.size()==0)
		{
			throw new RuntimeException("Erreur inattendue");
		}
		if (contrats.size()>1)
		{
			throw new RuntimeException("L'utilisateur "+utilisateur.getNom()+" posséde plusieurs contrats !!");
		}
		
		Contrat contrat = contrats.get(0);
		
		return new MesContratsService().loadContrat(contrat.getModeleContrat().getId(), contrat.getId());
		
	}
	
	
	public Map<Utilisateur, ContratDTO> loadContrat(EntityManager em, List<Utilisateur> utilisateurs, ModeleContrat mc)
	{
		Map<Utilisateur, ContratDTO> res = new HashMap<>();
		for (Utilisateur utilisateur : utilisateurs)
		{
			ContratDTO dto = findContrat(em, utilisateur, mc);
			res.put(utilisateur, dto);
		}
		return res;
	}
	
}
