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
 package fr.amapj.service.services.edgenerator.excel.emargement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Row;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.common.collections.G1D;
import fr.amapj.common.collections.G1D.Cell1;
import fr.amapj.common.collections.G2D;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.editionspe.emargement.FeuilleEmargementJson;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.ProducteurUtilisateur;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.engine.generator.excel.ExcelCellAutoSize;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.emargement.EGFeuilleEmargement.LibInfo;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.producteur.ProducteurService;


/**
 * Permet la generation d'une feuille d'émargement (hebdomadaire ou mensuelle)
 * au format liste
 */
public class EGFeuilleEmargementListe
{
	private static final char BULLET_CHARACTER = '\u2022';
	
	public EGFeuilleEmargementListe()
	{
	}
	
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et,FeuilleEmargementJson planningJson, LibInfo libInfo)
	{
		//
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM");
		
		// On recherche tout d'abord la liste des livraisons concernées
		List<ContratCell> cells = getContratCell(em, libInfo, planningJson);
		
		// On réalise une projection 2D de ces livraisons
		// En colonne, les dates de livraison, en ligne les utilisateurs 
		G2D<Utilisateur,Date,ContratCell> c1 = new G2D<Utilisateur,Date,ContratCell>();
		
		// 
		c1.fill(cells);
		c1.groupByLig(e->e.getContrat().getUtilisateur());
		c1.groupByCol(e->e.getModeleContratDate().getDateLiv());
		
		// Tri par nom prenom des lignes
		c1.sortLig(e->e.getNom(),true);
		c1.sortLig(e->e.getPrenom(),true);
		
		// Tri des dates croissantes
		c1.sortCol(e->e,true);
		
		// Pas de tri sur les cellules
		c1.compute();
		
		
		// On en deduit la liste des titre de lignes et de colonnes
		List<Utilisateur> utilisateurs = c1.getLigs();
		List<Date> dateLivs = c1.getCols();
		
		// On calcule ensuite l'entete (ajout des infos de permanence) 
		Entete entete = getEntetePlanning(dateLivs,em,libInfo);		
		
		
		// Les colonnes en + sont le nom, prenom et telephone1 et telephone 2 et commentaire
		int nbCol =  entete.dateCols.size()*2+5;
		et.addSheet("Feuille émargement "+libInfo.lib2+" "+libInfo.lib1, nbCol, 25);
		et.setMarginAndPageFormat(planningJson);
		
		
		//Positionnement de toutes les largeurs
		et.setColumnWidthInMm(0, planningJson.getLgColNom());
		et.setColumnWidthInMm(1, planningJson.getLgColPrenom());
				
		int index = 2;
		for (DateColonne prodCol : entete.dateCols)
		{
			// Colonne 1 avec les produits
			et.setColumnWidthInMm(index, planningJson.getLgColProduits());
			index++;
			
			// Colonne 2 avec la signature
			et.setColumnWidthInMm(index, planningJson.getLgColPresence());
			index++;
		}
		
		et.setColumnWidthInMm(index, planningJson.getLgColnumTel1());
		index++;

		et.setColumnWidthInMm(index, planningJson.getLgColnumTel2());
		index++;
		
		et.setColumnWidthInMm(index, planningJson.getLgColCommentaire());
		
		
		
		
		// Ecriture de la ligne des dates
		et.addRow();
		et.setCell(0, "DISTRIBUTIONS "+libInfo.lib1.toUpperCase(), et.grasCentreBordure);
		et.mergeCellsRight(0, 2);
		
		index = 2;
		for (DateColonne dateCol : entete.dateCols)
		{
			et.setCell(index, df.format(dateCol.date), et.grasCentreBordure);
			et.mergeCellsRight(index, 2);
			index = index + 2;
		}
		
		et.setNCell(index, 3,"", et.grasCentreBordure);
		
		
		// Ecriture de la ligne des responsables de la distribution
		et.addRow();
		et.setRowHeigth(3);
		et.setCell(0, "Responsable de distribution", et.nonGrasGaucheBordure);
		et.mergeCellsRight(0, 2);
		
		index = 2;
		for (DateColonne dateCol : entete.dateCols)
		{
			et.setCell(index, dateCol.permanence, et.nonGrasCentreBordure);
			et.mergeCellsRight(index, 2);
			index = index + 2;
		}
		
		et.setNCell(index, 3,"", et.grasCentreBordure);
		
		// Ligne vide 
		et.addRow();
		
		// Si besoin : ecriture d'un cumul par producteur 
		if (planningJson.getListeAffichageCumulProducteur()==ChoixOuiNon.OUI)
		{
			addCumulParProducteur(cells,et,planningJson,entete,em);
		}
		
		
		// Ecriture de la ligne avec les noms des produits
		et.addRow();
		et.setCell(0, "Nom", et.grasCentreBordure);
		et.setCell(1, "Prénom", et.grasCentreBordure);

		index = 2;
		for (DateColonne prodCol : entete.dateCols)
		{
			// Colonne 1 avec les produits
			et.setCell(index, "Produits", et.grasCentreBordure);
			index++;
			
			// Colonne 2 avec la signature
			et.setCell(index, "Présence", et.grasCentreBordureColorPetit);
			index++;
		}
		
		et.setCell(index, "Téléphone 1 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Téléphone 2 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Commentaire ", et.grasCentreBordure);
		
		//
		for (int i = 0; i < utilisateurs.size(); i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
			List<List<ContratCell>> ligne = c1.getLine(i);
			addRowUtilisateur(et,utilisateur,ligne,entete,i,planningJson);
		}
		
	}
	

	private void addRowUtilisateur(ExcelGeneratorTool et, Utilisateur utilisateur, List<List<ContratCell>> ligne, Entete entete,int numLigne,FeuilleEmargementJson feuilleEmargementJson)
	{	
		ExcelCellAutoSize as = new ExcelCellAutoSize(5);
		
		Row currentRow = et.addRow();
		
		et.setCell(0, utilisateur.getNom(), et.switchGray(et.grasGaucheWrappeBordure,numLigne));
		et.setCell(1, utilisateur.getPrenom(), et.switchGray(et.nonGrasGaucheBordure,numLigne));
		
		int index = 2;
		List<DateColonne> dateCols = entete.dateCols;
		for (int i = 0; i < dateCols.size(); i++)
		{
			DateColonne prodCol = dateCols.get(i);
			List<ContratCell> userCells = ligne.get(i);
					
			
			String listeProduits = getListeProduit(prodCol,utilisateur,userCells,feuilleEmargementJson);
			
			// Colonne 1 avec les produits
			et.setCell(index, listeProduits, et.switchGray(et.nonGrasGaucheBordure,numLigne));
			
			as.addCell(et.getColumnWidthInPoints(index), "Arial", 10);
			as.addLine(listeProduits);
			
			index++;
			
			// Colonne 2 avec la signature
			et.setCell(index, "", et.grasCentreBordureColor);
			index++;
		}
		
		
		// Numéro de telephone 1
		et.setCell(index, utilisateur.getNumTel1(), et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Numéro de telephone 2
		index++;
		et.setCell(index, utilisateur.getNumTel2(), et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Commentaire
		index++;
		et.setCell(index, "", et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Calcul de la hauteur de ligne optimale 
		as.autosize(currentRow);
	}
	

	

	private String getListeProduit(DateColonne prodCol, Utilisateur utilisateur,List<ContratCell> cells,FeuilleEmargementJson feuilleEmargementJson)
	{
		// On réalise un eclatement 1D des ContratCell par modele de contrat 
		// Avec un tri de la clé par nom du producteur puis nom du contrat
		// et un tri des cellules par indx produit 
		G1D<ModeleContrat, ContratCell> c1 = new G1D<ModeleContrat, ContratCell>();
		
		c1.fill(cells);
		c1.groupBy(e->e.getModeleContratDate().getModeleContrat());
		
		c1.sortLig(e->e.getProducteur().nom,true);
		c1.sortLig(e->e.getNom(),true);
		
		c1.sortCell(e->e.getModeleContratProduit().getIndx(), true);
		
		c1.compute();
		
		List<Cell1<ModeleContrat, ContratCell>> livs = c1.getFullCells();
		
		// On réalise ensuite le rendu de chaque livraison pour cet utilisateur
		StringBuffer buf = new StringBuffer();
		for (Cell1<ModeleContrat, ContratCell> liv : livs)
		{
			if (feuilleEmargementJson.getNomDuProducteur()==ChoixOuiNon.OUI)
			{
				buf.append(liv.lig.getProducteur().nom);
				buf.append("\n");
			}
			
			if (feuilleEmargementJson.getNomDuContrat()==ChoixOuiNon.OUI)
			{
				buf.append(liv.lig.getNom());
				buf.append("\n");
			}
			
			if (feuilleEmargementJson.getDetailProduits()==ChoixOuiNon.OUI)
			{
				for (ContratCell cell : liv.values)
				{
					Produit p = cell.getModeleContratProduit().getProduit();
					String content = cell.getQte()+" "+p.getNom()+" , "+p.getConditionnement();
					buf.append(" "+BULLET_CHARACTER+" "+content+"\n");
				}
			}
		}	
		
		// On supprime le dernier /n
		return StringUtils.removeLast(buf.toString(), "\n");
	}


	/**
	 * Calcul de l'entête du planning mensuel
	 * @param em
	 * @param planningJson
	 * @return
	 */
	private Entete getEntetePlanning(List<Date> dateLivs,EntityManager em,LibInfo libInfo)
	{
		Entete entete = new Entete();
		
		// Recherche des permanences dans ce mois
		List<PeriodePermanenceDateDTO> permanenceDTOs = getPermanence(em,libInfo);
				
		// 
		for (Date dateLiv : dateLivs)
		{
			DateColonne dateCol = new DateColonne();
			dateCol.date = dateLiv;
			dateCol.permanence = findPermanence(permanenceDTOs,dateLiv);
			
			entete.dateCols.add(dateCol);
		}
		
		// 
		return entete;
	}
	
	private List<PeriodePermanenceDateDTO> getPermanence(EntityManager em,LibInfo libInfo)
	{
		return new PeriodePermanenceService().getAllDistributionsActif(em, libInfo.debut, libInfo.fin);
	}
	
	
	
	/**
	 * 
	 */
	private String findPermanence(List<PeriodePermanenceDateDTO> permanenceDTOs, Date dateLiv)
	{
		for (PeriodePermanenceDateDTO permanenceDTO : permanenceDTOs)
		{
			if (permanenceDTO.datePerm.equals(dateLiv))
			{
				return permanenceDTO.getNomInscrit("\n");
			}
		}
		
		return "";
	}
	
	
	static public class DateColonne
	{
		public Date date;
		
		public String permanence;
	}
	
	
	static public class Entete
	{
		public List<DateColonne> dateCols = new ArrayList<>();
		
	}
	
	
	// PARTIE CUMUL PAR PRODUCTEUR 
	
	private void addCumulParProducteur(List<ContratCell> cells, ExcelGeneratorTool et, FeuilleEmargementJson planningJson, Entete entete, EntityManager em)
	{
		// On réalise une projection 2D de ces livraisons
		// En colonne, les dates de livraison, en ligne les producteurs 
		G2D<Producteur,Date,ContratCell> c1 = new G2D<Producteur,Date,ContratCell>();
		
		// 
		c1.fill(cells);
		c1.groupByLig(e->e.getModeleContratDate().getModeleContrat().getProducteur());
		c1.groupByCol(e->e.getModeleContratDate().getDateLiv());
		
		// Tri par nom des lignes (producteur)
		c1.sortLig(e->e.nom,true);
		
		// Tri des dates croissantes
		c1.sortCol(e->e,true);
		
		// Pas de tri sur les cellules
		c1.compute();
		
		
		// On en deduit la liste des titre de lignes 
		List<Producteur> producteurs = c1.getLigs();
		
		//
		et.addRow("Cumul des quantités par producteur",et.grasGaucheNonWrappe);
		
		// Ligne de titre
		// Ecriture de la ligne avec les noms des produits
		et.addRow();
		et.setCell(0, "Nom du producteur", et.grasCentreBordure);
		et.mergeCellsRight(0, 2);

		int index = 2;
		for (DateColonne prodCol : entete.dateCols)
		{
			// Colonne 1 avec les produits
			et.setCell(index, "Produits", et.grasCentreBordure);
			et.mergeCellsRight(index, 2);
			index=index+2;
		}
		
		et.setCell(index, "Téléphone 1 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Téléphone 2 ", et.grasCentreBordure);
		index++;
		et.setCell(index, "Commentaire ", et.grasCentreBordure);
		
		

		for (int i = 0; i < producteurs.size(); i++)
		{
			Producteur producteur = producteurs.get(i);
			List<List<ContratCell>> ligne = c1.getLine(i);
			addRowCumulProducteur(et,entete,ligne,producteur,i,planningJson,em);
		}
		
		
		// On conclue par une ligne vide
		et.addRow();
		
		// et une ligne pour introduire les quantites amapiens
		et.addRow("Quantités par amapien",et.grasGaucheNonWrappe);

	}

	private void addRowCumulProducteur(ExcelGeneratorTool et, Entete entete, List<List<ContratCell>> ligne, Producteur producteur,int numLigne, FeuilleEmargementJson feuilleEmargementJson, EntityManager em)
	{
		ExcelCellAutoSize as = new ExcelCellAutoSize(5);
		
		Row currentRow = et.addRow();
		
		et.setCell(0, producteur.nom, et.grasGaucheWrappeBordure);
		et.mergeCellsRight(0, 2);
		
		int index = 2;
		List<DateColonne> dateCols = entete.dateCols;
		for (int i = 0; i < dateCols.size(); i++)
		{
			DateColonne prodCol = dateCols.get(i);
			List<ContratCell> cells = ligne.get(i);
			String listeProduits = getListeProduitProducteur(prodCol,cells,feuilleEmargementJson);
			
			// Colonne 1 avec les produits
			et.setCell(index, listeProduits, et.switchGray(et.nonGrasGaucheBordure,numLigne));
			
			// On prend les deux cellules index et index +1, étant donnés que l'on va réaliser un merge 
			as.addCell(et.getColumnWidthInPoints(index)+et.getColumnWidthInPoints(index+1), "Arial", 10);
			as.addLine(listeProduits);
			
			// La colonne 2 avec la signature est mergé avec la colonne précédente, car elle n'est pas utile ici 			
			et.mergeCellsRight(index, 2);
			index = index+2;
		}
		
		
		// Numéro de telephone 1 + 2 + Commentaire 
		List<ProducteurUtilisateur> us = new ProducteurService().getProducteurUtilisateur(em, producteur);
		String tel1 =CollectionUtils.asString(us, "\n", e->e.getUtilisateur().getNumTel1(),true);
		String tel2 =CollectionUtils.asString(us, "\n", e->e.getUtilisateur().getNumTel2(),true);
		
		
		// Numéro de telephone 1
		et.setCell(index, tel1, et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Numéro de telephone 2
		index++;
		et.setCell(index, tel2, et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Commentaire
		index++;
		et.setCell(index, "", et.switchGray(et.nonGrasCentreBordure,numLigne));
		
		// Calcul de la hauteur de ligne optimale 
		as.autosize(currentRow);
	}
	
	/**
	 * On va presenter chaque modele de contrat, et pour chaque modele de contrat les quantités cumulées à livrer 
	 */
	private String getListeProduitProducteur(DateColonne prodCol, List<ContratCell> cells,FeuilleEmargementJson feuilleEmargementJson)
	{
		// On réalise un eclatement 1D des ContratCell par modele de contrat    
		// Avec un tri des lignes par nom du contrat
		G1D<ModeleContrat, ContratCell> c1 = new G1D<ModeleContrat, ContratCell>();
		
		c1.fill(cells);
		
		c1.groupBy(e->e.getModeleContratDate().getModeleContrat());
		c1.sortLig(e->e.getNom(),true);
				
		// Pas de tri sur les cellules
		// Puis calcul du tout
		c1.compute();

		
		StringBuffer buf = new StringBuffer();
		
		// On boucle sur les modeles de contrats
		List<ModeleContrat> modeleContrats = c1.getKeys();
		for (int i = 0; i < modeleContrats.size(); i++)
		{
			ModeleContrat modeleContrat = modeleContrats.get(i);
			
			buf.append(modeleContrat.getNom());
			buf.append("\n");
			
			List<ContratCell> pcells = c1.getCell(i);
			buf.append(computeListeProduit(pcells));
		}
		
		// On supprime le dernier /n
		return StringUtils.removeLast(buf.toString(), "\n");
	}
	
	
	
	private String computeListeProduit(List<ContratCell> pcells)
	{
		// On réalise un eclatement 1D des ContratCell par produits
		// Avec un tri des lignes par index du produit dans le contrat
		G1D<Produit, ContratCell> c1 = new G1D<Produit, ContratCell>();
		
		c1.fill(pcells);
		
		c1.groupBy(e->e.getModeleContratProduit().getProduit());
		c1.sortLigAdvanced(e->e.getModeleContratProduit().getIndx(), true);
				
		// Pas de tri sur les cellules
		// Puis calcul du tout
		c1.compute();
		
		StringBuffer buf = new StringBuffer();
		
		// On boucle sur les produits
		List<Produit> produits = c1.getKeys();
		for (int i = 0; i < produits.size(); i++)
		{
			Produit p = produits.get(i);
			List<ContratCell> cells = c1.getCell(i);
			
			int qte = CollectionUtils.accumulateInt(cells, e->e.getQte());
			
			String content = qte+" "+p.getNom()+" , "+p.getConditionnement();
			buf.append(" "+BULLET_CHARACTER+" "+content+"\n");
		}
		
		return buf.toString();
	}

	/**
	 * Retourne la liste de toutes les livraisons concernées
	 * 
	 * @param em
	 * @return
	 */
	private List<ContratCell> getContratCell(EntityManager em,LibInfo libInfo, FeuilleEmargementJson planningJson)
	{
		// 
		Query q = em.createQuery("select c from ContratCell c WHERE "
				+ " c.modeleContratDate.dateLiv >= :d1 AND c.modeleContratDate.dateLiv<:d2  ");
				
		
		q.setParameter("d1",libInfo.debut);
		q.setParameter("d2",libInfo.fin);
		
		List<ContratCell> us = q.getResultList();
		return us;
	}
	
}
