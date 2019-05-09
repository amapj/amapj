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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.reel.Contrat;
import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;


/**
 * Permet la generation des feuilles de collecte de cheques
 * 
 *  
 *
 */
public class EGBilanCompletCheque extends AbstractExcelGenerator
{
	Long modeleContratId;
	
	public EGBilanCompletCheque(Long modeleContratId)
	{
		this.modeleContratId = modeleContratId;
	}
	
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");

		// Charge des informations sur le modele
		ContratDTO contratDTO = new MesContratsService().loadContrat(mc.getId(),null);
		
		// Calcul du nombre de colonnes :  Nom + prénom + 1 montant commandé + 1 espace + promis + recu + remis + 1 espace + 1 solde+ 1 vide + 1 avoir 
		// + 3 colonnes par mois 
		int numColFirstMonth= 11;
		int nbMois = contratDTO.paiement.datePaiements.size();
		int nbCol = numColFirstMonth + 3*nbMois;
		
		et.addSheet("Amap", nbCol, 9);
		et.setColumnWidth(0, 14);
		et.setColumnWidth(1, 12);
		et.setColumnWidth(2, 12);
				
		
		et.addRow("Bilan des chèques",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		et.addRow("Nom du contrat : "+mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow("Nom du producteur : "+mc.getProducteur().nom,et.grasGaucheNonWrappe);
		et.addRow("Ordre des chèques : "+mc.getLibCheque(),et.grasGaucheNonWrappe);
		

		
		
		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc);
		et.addRow(utilisateurs.size()+" adhérents pour ce contrat",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		

		// Création de la ligne titre des colonnes
		et.addRow();
		et.setCell(0,"Nom",et.grasCentreBordure);
		et.setCell(1,"Prénom",et.grasCentreBordure);
		et.setCell(2,"Montant commandé",et.grasCentreBordure);
		
		et.setCell(4,"Chèques",et.grasCentreBordure);
		et.mergeCellsRight(4, 3);
		
		et.setCell(8,"Solde final",et.grasCentreBordure);
		
		et.setCell(10,"Avoir initial",et.grasCentreBordure);
		
		for (int i = 0; i < nbMois; i++)
		{
			// TODO gestion du colorize
			CellStyle st = et.grasCentreBordure;
			if ((i %2)==0)
			{
				st = et.grasCentreBordureColor;
			}
			
			
			Date d = contratDTO.paiement.datePaiements.get(i).datePaiement;
			String libMois = df.format(d);
			int numCol = numColFirstMonth+i*3; 
			et.setCell(numCol, libMois,st);
			et.mergeCellsRight(numCol, 3);
		}
		
		// Sous ligne de titre
		et.addRow();
		
		et.setCell(4,"promis",et.grasCentreBordure);
		et.setCell(5,"reçu",et.grasCentreBordure);
		et.setCell(6,"remis",et.grasCentreBordure);
		
		for (int i = 0; i < nbMois; i++)
		{
			// TODO gestion du colorize
			CellStyle st = et.grasCentreBordure;
			if ((i %2)==0)
			{
				st = et.grasCentreBordureColor;
			}
		
			et.setCell(numColFirstMonth+i*3, "promis",st);
			et.setCell(numColFirstMonth+i*3+1, "reçu",st);
			et.setCell(numColFirstMonth+i*3+2, "remis",st);
				
		}
		
		// Merge des cellules du titre
		et.mergeCellsUp(0,2);
		et.mergeCellsUp(1,2);
		et.mergeCellsUp(2,2);
		et.mergeCellsUp(8,2);
		et.mergeCellsUp(10,2);
				
		// Une ligne vide
		et.addRow("",et.grasGaucheNonWrappe);
		
		// Une ligne pour le cumul
		addRowCumul(et,nbMois,utilisateurs,numColFirstMonth);
		
		// Une ligne vide
		et.addRow("",et.grasGaucheNonWrappe);
		
		// Une ligne pour chaque utilisateur 
		for (Utilisateur utilisateur : utilisateurs)
		{
			addRow(utilisateur,et,mc,em,nbMois,numColFirstMonth);
		}
		
	}

	
	
	
	private void addRowCumul(ExcelGeneratorTool et, int nbMois, List<Utilisateur> utilisateurs, int numColFirstMonth)
	{
		et.addRow();
		
		int nbUser = utilisateurs.size();
		
		et.setCell(0,"Cumul",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		
		
		et.setCellSumInColDown(2, 2, nbUser, et.prixCentreBordure);
		et.setCellSumInColDown(4, 2, nbUser, et.prixCentreBordure);
		et.setCellSumInColDown(5, 2, nbUser, et.prixCentreBordure);
		et.setCellSumInColDown(6, 2, nbUser, et.prixCentreBordure);
		et.setCellSumInColDown(8, 2, nbUser, et.prixCentreBordure);
		et.setCellSumInColDown(10, 2, nbUser, et.prixCentreBordure);
		
		et.setCellSumInColDown(6, 2, nbUser, et.prixCentreBordure);
		
		for (int i = 0; i < nbMois; i++)
		{
			// TODO gestion du colorize
			CellStyle st = et.prixCentreBordure;
			if ((i %2)==0)
			{
				st = et.prixCentreBordureColor;
			}
			
			et.setCellSumInColDown(numColFirstMonth+i*3, 2, nbUser, st);
			et.setCellSumInColDown(numColFirstMonth+i*3+1, 2, nbUser, st);
			et.setCellSumInColDown(numColFirstMonth+i*3+2, 2, nbUser, st);
		}
		
	}




	private void addRow(Utilisateur utilisateur, ExcelGeneratorTool et, ModeleContrat mc, EntityManager em, int nbMois, int numColFirstMonth)
	{
		Contrat c = new MesContratsService().getContrat(mc.getId(),em,utilisateur);
		ContratDTO contratDTO = new MesContratsService().loadContrat(mc.getId(), c.getId());
		int montantDu = new GestionContratSigneService().getMontant(em, c);
		
		
		et.addRow();
		et.setCell(0,utilisateur.getNom(),et.grasGaucheNonWrappeBordure);
		et.setCell(1,utilisateur.getPrenom(),et.nonGrasGaucheBordure);
		
		
		et.setCellPrix(2,montantDu,et.prixCentreBordure);
		et.setCellSumInRow(4, numColFirstMonth, 3, nbMois, null , et.prixCentreBordure);
		et.setCellSumInRow(5, numColFirstMonth+1, 3, nbMois, null , et.prixCentreBordure);
		et.setCellSumInRow(6, numColFirstMonth+2, 3, nbMois, null , et.prixCentreBordure);
		
		et.setCellBasicFormulaInRow(8,new int[] { 6 , 10 }, new int[] { 2 } , et.prixCentreBordure);
		
		et.setCellPrix(10, contratDTO.paiement.avoirInitial,et.prixCentreBordure);
		
		for (int i = 0; i < nbMois; i++)
		{
			DatePaiementDTO dateDto=contratDTO.paiement.datePaiements.get(i);
			
			// TODO gestion du colorize
			CellStyle st = et.prixCentreBordure;
			if ((i %2)==0)
			{
				st = et.prixCentreBordureColor;
			}
			
			et.setCellPrix(numColFirstMonth+i*3, getPromis(dateDto),st);
			et.setCellPrix(numColFirstMonth+i*3+1, getRecu(dateDto),st);
			et.setCellPrix(numColFirstMonth+i*3+2, getRemis(dateDto),st);
		}
	}




	private int getRemis(DatePaiementDTO dateDto)
	{
		if ( dateDto.etatPaiement==EtatPaiement.PRODUCTEUR) 
		{
			return dateDto.montant;
		}
		else
		{
			return 0;
		}
	}




	private int getRecu(DatePaiementDTO dateDto)
	{
		if ( (dateDto.etatPaiement==EtatPaiement.AMAP) || (dateDto.etatPaiement==EtatPaiement.PRODUCTEUR)) 
		{
			return dateDto.montant;
		}
		else
		{
			return 0;
		}
	}




	private int getPromis(DatePaiementDTO dateDto)
	{
		return dateDto.montant;
	}
	

	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		return "bilan-cheque-"+mc.getNom();
	}


	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "le bilan complet chèques";
	}


	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}



}
