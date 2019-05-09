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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.contrat.modele.ModeleContratDate;
import fr.amapj.model.models.contrat.modele.ModeleContratProduit;
import fr.amapj.model.models.fichierbase.Producteur;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.EGTotalLivraisonGrille;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;


/**
 * Permet la generation des feuilles de distribution producteur 
 * en tenant compte du parametrage demandé dans le producteur  
 * 
 *  
 *
 */
public class EGFeuilleDistributionProducteur extends AbstractExcelGenerator
{
	//
	Long modeleContratId;
	

	// Si ce champ est null, alors on prend en compte toutes les dates de livraisons
	Long modeleContratDateId; 
	
	public EGFeuilleDistributionProducteur(Long modeleContratId,Long modeleContratDateId)
	{
		this.modeleContratId = modeleContratId;
		this.modeleContratDateId = modeleContratDateId;
	}
	
	public EGFeuilleDistributionProducteur(Long modeleContratId)
	{
		this(modeleContratId,null);
	}
	
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		EGGrilleTool grilleTool = new EGGrilleTool();
		
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		
		// Avec une sous requete, on récupere la liste des produits
		List<ModeleContratProduit> prods = new GestionContratService().getAllProduit(em, mc);
		
		// Avec une sous requete, on obtient la liste de toutes les dates de livraison, trièes par ordre croissant 
		List<ModeleContratDate> dates = new GestionContratService().getAllDates(em, mc);

		// Avec une sous requete, on obtient la liste de tous les utilisateur ayant commandé au moins un produit
		List<Utilisateur> utilisateurs = new MesContratsService().getUtilisateur(em, mc);
		
		// On charge ensuite la liste de tous les contrats pour chaque utilisateur
		Map<Utilisateur, ContratDTO> contrats = grilleTool.loadContrat(em,utilisateurs,mc);

		// Nombre de colonnes fixe à gauche
		int nbColGauche = 3;

		//
		Producteur p = mc.getProducteur();
		
		SimpleDateFormat df = new SimpleDateFormat("dd MMMMM");
		SimpleDateFormat df3 = new SimpleDateFormat("dd MMMMM yyyy");
		
		// On itère sur chaque date, avec une feuille par date
		for (ModeleContratDate date : dates)
		{
			if (isAccordingDate(date,modeleContratDateId))
			{
				// Si besoin, ajout de la feuille en mode grille
				if (p.feuilleDistributionGrille==ChoixOuiNon.OUI)
				{
					addSheetGrilleMode(date,df3,df,et,mc,prods,utilisateurs,nbColGauche,contrats,grilleTool);
				}
				
				// Si besoin, on ajoute une feuille à plat
				if (p.feuilleDistributionListe==ChoixOuiNon.OUI)
				{
					new EGFeuilleDistributionProducteurListe(date.getId()).fillExcelFile(em, et);
				}
				
				// Si besoin, on ajoute une feuille avec les étiquettes 
				if (new EditionSpeService().needEtiquette(modeleContratId))
				{
					new EGEtiquetteDistribution(date.getId()).fillExcelFile(em, et);
				}
				
			}
		}
		
		// Génération d'une page de cumul si on fait le classeur avec toutes les feuilles de distribution 
		if  (modeleContratDateId==null) 
		{
			// si on a bien le mode grille activé
			if (p.feuilleDistributionGrille==ChoixOuiNon.OUI)
			{
				new EGTotalLivraisonGrille().fillExcelFile(em, et, prods, dates, mc);
			}
		}	
	}
	
	
	private void addSheetGrilleMode(ModeleContratDate date, SimpleDateFormat df3, SimpleDateFormat df, ExcelGeneratorTool et, ModeleContrat mc, List<ModeleContratProduit> prods, List<Utilisateur> utilisateurs, int nbColGauche, Map<Utilisateur, ContratDTO> contrats, EGGrilleTool grilleTool)
	{
		List<ModeleContratDate> ds = new ArrayList<>();
		ds.add(date);
		
		ParametresDTO param = new ParametresService().getParametres();
		
		String firstLine = param.nomAmap+" - Feuille de distribution producteur du "+df3.format(date.getDateLiv());
		String sheetName = df.format(date.getDateLiv());
		
		grilleTool.performSheet(et,firstLine,sheetName,mc,prods,ds,utilisateurs,nbColGauche,contrats);
		
		// Suppression de la colonne avec les prix 
		et.setColHidden(2, true);
		
		// Ajustement pour tenir sur une seule page
		et.adjustSheetForOnePage();
		
		// On repete les lignes produits conditionnement  à toutes les pages lors de l'impression
		et.setRepeatingRow(6, 9);
		
	}

	/**
	 * Retourne si cette date fait parties des dates qui doivent être présentes dans le document 
	 * @param date
	 * @param modeleContratDateId
	 * @return
	 */
	private boolean isAccordingDate(ModeleContratDate date, Long modeleContratDateId)
	{
		if (modeleContratDateId==null)
		{
			return true;
		}
		return date.getId().equals(modeleContratDateId);
	}

	
	
	
	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		if (modeleContratDateId==null)
		{
			return "distri-"+mc.getNom();
		}
		else
		{
			ModeleContratDate date = em.find(ModeleContratDate.class, modeleContratDateId);
			SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
			return "distri-"+mc.getNom()+"-"+df.format(date.getDateLiv());
		}
	}


	@Override
	public String getNameToDisplay(EntityManager em)
	{
		if (modeleContratDateId==null)
		{
			return "toutes les feuilles de distribution producteur";
		}
		else
		{
			ModeleContratDate date = em.find(ModeleContratDate.class, modeleContratDateId);
			SimpleDateFormat df = new SimpleDateFormat("dd MMMMM yyyy");
			return "la feuille de distribution producteur du "+df.format(date.getDateLiv());
		}
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
	
	
	public static void main(String[] args) throws IOException
	{
		//new EGFeuilleLivraison(4342L,4409L).test();
		new EGFeuilleDistributionProducteur(4342L).test();
	}


}
