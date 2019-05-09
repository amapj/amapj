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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.amapj.common.DateUtils;
import fr.amapj.common.SQLUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.contrat.reel.ContratCell;
import fr.amapj.model.models.fichierbase.Produit;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;


/**
 * Permet la generation des feuilles de livraison en mode liste (et non en mode grille)
 * 
 * Pour chaque utilisateur, on a la liste de ses produits
 * 
 */
public class EGFeuilleDistributionProducteurListe 
{
	private static final char BULLET_CHARACTER = '\u2022';
	
	private Long modeleContratDateId;
	
	public EGFeuilleDistributionProducteurListe(Long modeleContratDateId)
	{
		this.modeleContratDateId = modeleContratDateId;
	}
	
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy");
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd MMMMM");
		
		ModeleContratDate mcd = em.find(ModeleContratDate.class, modeleContratDateId);
		ModeleContrat mc = mcd.getModeleContrat();

		// Il y a 1 colonne
		String sheetName = df2.format(mcd.getDateLiv())+"-Liste";
		et.addSheet(sheetName, 1, 100);
				
		// Ligne 1 à 5
		et.addRow("FEUILLE DE DISTRIBUTION PRODUCTEUR DU "+df.format(mcd.getDateLiv()),et.grasGaucheNonWrappe);
		et.addRow(mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow(mc.getDescription(),et.grasGaucheNonWrappe);
		et.addRow("Extrait le "+df1.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		
		
		// Ajout du cumul des quantités à livrer
		addCumul(em,et);
		
		// AJout du détail pour chaque amapien
		addDetailAmapien(em,et);
		
	}
	
	

	private void addCumul(EntityManager em, ExcelGeneratorTool et)
	{
		et.addRow("CUMUL DES QUANTITES A LIVRER",et.grasCentreBordureColor);
		et.addRow("",et.grasGaucheNonWrappe);
		
		ModeleContratDate mcd = em.find(ModeleContratDate.class, modeleContratDateId);
		
		Query q = em.createQuery("select c.modeleContratProduit , sum(c.qte) from ContratCell c " +
				" WHERE c.modeleContratDate=:mcd "+
				" group by c.modeleContratProduit "+
				" order by c.modeleContratProduit.indx ");
		
		q.setParameter("mcd", mcd);
		
		List<Object[]> lines = q.getResultList();
		
		for (Object[] line : lines)
		{
			ModeleContratProduit mcp = (ModeleContratProduit) line[0];
			int qte = SQLUtils.toInt(line[1]);
			
			Produit p = mcp.getProduit();
			et.addRow("  "+BULLET_CHARACTER+" "+qte+" "+p.getNom()+" ,"+p.getConditionnement(),et.nongrasGaucheWrappe);
		}
		
		
	}

	private void addDetailAmapien(EntityManager em, ExcelGeneratorTool et)
	{
		et.addRow("",et.grasGaucheNonWrappe);
		et.addRow("DETAIL PAR AMAPIEN",et.grasCentreBordureColor);
		
		Long user = 0L;
				
		ModeleContratDate mcDate = em.find(ModeleContratDate.class, modeleContratDateId);
		
		Query q = em.createQuery("select c from ContratCell c WHERE " +
				"c.modeleContratDate=:mcDate "+
				"order by c.contrat.utilisateur.nom , c.contrat.utilisateur.prenom , c.modeleContratProduit.indx");
		q.setParameter("mcDate", mcDate);
		
		List<ContratCell> cells = q.getResultList();
		for (ContratCell cell : cells)
		{
			int qte =  cell.getQte();
			Utilisateur u = cell.getContrat().getUtilisateur();
			Produit p = cell.getModeleContratProduit().getProduit();
			
			if (u.getId().equals(user)==false)
			{
				user = u.getId();
				et.addRow("",et.grasGaucheNonWrappe);
				et.addRow(u.getNom()+" "+u.getPrenom(),et.grasGaucheNonWrappe);
			}
			et.addRow("  "+BULLET_CHARACTER+" "+qte+" "+p.getNom()+" ,"+p.getConditionnement(),et.nongrasGaucheWrappe);
		}	
		
	}


}
