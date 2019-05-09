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
 package fr.amapj.service.services.edgenerator.excel.livraison;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import fr.amapj.common.CollectionUtils;
import fr.amapj.common.DateUtils;
import fr.amapj.common.FormatUtils;
import fr.amapj.common.StringUtils;
import fr.amapj.common.collections.G1D.Cell1;
import fr.amapj.common.collections.M2;
import fr.amapj.common.collections.M2.Pair;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PeriodePermanenceDate;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelCellAutoSize;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.view.engine.tools.BaseUiTools;


/**
 * Permet la generation d'une feuille de livraison au format liste à destination de l'amapien
 */
public class EGLivraisonAmapien extends AbstractExcelGenerator
{
	private static final char BULLET_CHARACTER = '\u2022';
	
	private Date endDate;
	
	private Date startDate;
	
	private Long idUtilisateur;

	private TypPeriode typPeriode;
	
	/**
	 * Deux modes sont possibles : 
	 * les livraisons d'un amapien sur une periode (idUtilisateur non null)
	 * les livraisons de tous les amapiens sur une periode (idUtilisateur = null)
	 * 
	 * @param startDate inclusive
	 * @param endDate inclusive 
	 */
	public EGLivraisonAmapien(TypPeriode typPeriode,Date startDate,Date endDate,Long idUtilisateur)
	{
		this.typPeriode = typPeriode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.idUtilisateur = idUtilisateur;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{	
		List<Utilisateur> utilisateurs = new LivraisonAmapienCommon().getUtilisateurs(em,idUtilisateur,startDate,endDate);
		for (Utilisateur utilisateur : utilisateurs)
		{
			addOneSheet(em,utilisateur,et);
		}
		
		// On positionne un message d'avertissement si besoin 
		if(utilisateurs.size()==0)
		{
			et.addSheet("AMAP", 1, 50);
			et.addRow("Il y a aucune livraison pour aucun utilisateur !!",et.grasGaucheWrappe);
		}
	}
	
	
	
	
	private void addOneSheet(EntityManager em, Utilisateur u, ExcelGeneratorTool et)
	{
		SimpleDateFormat df1 = FormatUtils.getFullDate();
		SimpleDateFormat df2 = FormatUtils.getTimeStd();
		
		String nomUtilisateur = u.nom+" "+u.prenom;
		
		// Création de la feuille 
		et.addSheet(nomUtilisateur, 2, 15);
		et.setColumnWidth(1, 80);
		
		// Affichage des informations d'entête
		
		// 3 lignes de titre
		ParametresDTO param = new ParametresService().getParametres();
		et.addRow(param.nomAmap, et.grasCentre);
		et.mergeCellsRight(0,2);
		
		ExcelCellAutoSize as = new ExcelCellAutoSize(0);
		Row currentRow = et.addRow();
		String lib = getTitre(nomUtilisateur);
		et.setCell(0, lib, et.grasGaucheWrappe);
		et.mergeCellsRight(0,2);
		as.addCell(et.getColumnWidthInPointsForMergedCell(0, 2), "Arial", 10,true);
		as.addLine(lib);
		
		// Calcul de la hauteur de ligne optimale 
		as.autosize(currentRow);
		
		// 
		et.addRow("Extrait le "+df2.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
		
		
		
		// Une ligne vide
		et.addRow();
		
		// L'entete du tableau 
		et.addRow();
		et.setRowHeigth(2);
		et.setCell(0, "Date de livraison", et.grasCentreBordureGray);
		et.setCell(1, "Produits", et.grasCentreBordureGray);
 
		
		// On calcule la liste des blocs à afficher
		List<Pair<Date, List<ContratCell>, List<PeriodePermanenceDate>>> dateLivs = new LivraisonAmapienCommon().computeListDateLiv(em, u, startDate, endDate);
		
		// Pour chaque bloc, on l'affiche 
		for (Pair<Date, List<ContratCell>, List<PeriodePermanenceDate>> dateLiv : dateLivs)
		{
			drawBlocDate(em,et,dateLiv.key,dateLiv.v1,dateLiv.v2,df1);
		}	
	}
	

	private String getTitre(String nomUtilisateur)
	{
		return "Livraisons pour "+nomUtilisateur+" pour "+new LivraisonAmapienCommon().getDescriptionPeriode(typPeriode, startDate, endDate);
	}

	

	private void drawBlocDate(EntityManager em, ExcelGeneratorTool et, Date datLiv, List<ContratCell> contratCells, List<PeriodePermanenceDate> perms, SimpleDateFormat df1)
	{
		// Calcul du libellé sur les livraisons
		String libLivraison = computeLibLivraison(contratCells);
		
		// Calcul du libellé sur les permances
		String libPermanence = CollectionUtils.asString(new LivraisonAmapienCommon().getInfoPermanence(perms),"\n");
			
		// Calcul du libellé global de la colonne Produit, en supprimant le dernier /n
		String libColProduit = StringUtils.removeLast(libLivraison+libPermanence, "\n");				
		
		// Calcul du libelle de la colonne date
		String libColDate = df1.format(datLiv);
		CellStyle dateStyle = et.nonGrasCentreBordure;
		boolean isBold = false;
		if (perms.size()!=0)
		{
			libColDate = libColDate+"\n\nPERMANENCE";
			dateStyle = et.grasCentreBordure;
			isBold = true;
		}
		
		
		// Construction de la ligne  
		ExcelCellAutoSize as = new ExcelCellAutoSize(5);
		Row currentRow = et.addRow();
	
		et.setCell(0, libColDate, dateStyle);
		as.addCell(et.getColumnWidthInPoints(0), "Arial", 10 ,isBold); 
		as.addLine(libColDate);
		
		
		et.setCell(1, libColProduit, et.nonGrasGaucheBordure);
		as.addCell(et.getColumnWidthInPoints(1), "Arial", 10);
		as.addLine(libColProduit);
		
		// Calcul de la hauteur de ligne optimale 
		as.autosize(currentRow);
		
	}

	private String computeLibLivraison(List<ContratCell> contratCells)
	{
		// On calcule la liste des contrats à afficher
		List<Cell1<ModeleContrat, ContratCell>> livs = new LivraisonAmapienCommon().computeBlocDate(contratCells);
		
		// Pour chaque contrat, on calcule la chaîne à afficher
		StringBuilder sb= new StringBuilder();
		for (Cell1<ModeleContrat, ContratCell> liv : livs)
		{
			sb.append(getListeProduit(liv));
		}
		return sb.toString();
	}

	private StringBuilder getListeProduit(Cell1<ModeleContrat, ContratCell> liv)
	{
		// Nom du contrat 
		StringBuilder buf  = new StringBuilder();
		buf.append(liv.lig.getNom());
		buf.append("\n");
			
		// Liste de produits commandés
		for (ContratCell cell : liv.values)
		{
			Produit p = cell.getModeleContratProduit().getProduit();
			String content = cell.getQte()+" x "+p.getNom()+" , "+p.getConditionnement();
			buf.append(" "+BULLET_CHARACTER+" "+content+"\n");
		}
		
		// Une ligne vide
		buf.append("\n");
		
		return buf;
	}
	

	
	
	@Override
	public String getFileName(EntityManager em)
	{
		return new LivraisonAmapienCommon().getFileName(typPeriode, startDate, endDate);
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		String nomUtilisateur;
		if (idUtilisateur==null)
		{
			nomUtilisateur = "tous les amapiens";
		}
		else
		{
			Utilisateur u = em.find(Utilisateur.class, idUtilisateur);
			nomUtilisateur = u.nom+" "+u.prenom;		
		}
		return getTitre(nomUtilisateur);
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
	
}
