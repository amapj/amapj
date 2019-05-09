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
 package fr.amapj.service.services.edgenerator.excel.producteur;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.edgenerator.excel.EGAvoirs;
import fr.amapj.service.services.edgenerator.excel.EGRemise;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.remiseproducteur.RemiseDTO;
import fr.amapj.service.services.remiseproducteur.RemiseProducteurService;


/**
 * Permet la generation d'une synthese des paiements à l'attention du producteur
 * 
 * La page 1 est une synthese des paiements mois par mois
 * Les pages suivantes sont les remises réellement réalisées
 * 
 * 
 *
 */
public class EGPaiementProducteur extends AbstractExcelGenerator
{
	
	Long modeleContratId;
	
	public EGPaiementProducteur(Long modeleContratId)
	{
		this.modeleContratId = modeleContratId;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		ModeleContrat mc = em.find(ModeleContrat.class, modeleContratId);
		
		// On récupère d'abord toutes les remises réalisées
		List<RemiseDTO> remises = new RemiseProducteurService().getAllRemise(modeleContratId);
		
		// On récupère ensuite les informations sur les remises prévisionnelles
		List<RemiseDTO> nextRemises = new RemiseProducteurService().getNextRemise(modeleContratId);
		
		// On récupère les informations sur les avoirs initiaux
		int mntAvoir = new GestionContratService().getMontantAvoir(em, mc);
		
		// Création de la page de garde 
		createPageGarde(em,et,remises,nextRemises,mntAvoir,mc);
		
		// Création ensuite de la page sur les avoirs initiaux
		if (mntAvoir>0)
		{
			new EGAvoirs(modeleContratId).fillExcelFile(em, et);
		}
		
		// Création ensuite d'une page par remise réelle
		for (RemiseDTO remiseDTO : remises)
		{
			new EGRemise(remiseDTO.id).fillExcelFile(em, et);
		}
		
	}

	
	private void createPageGarde(EntityManager em, ExcelGeneratorTool et, List<RemiseDTO> remises, List<RemiseDTO> nextRemises, int mntAvoir, ModeleContrat mc)
	{
		
		
		// Calcul du nombre de colonnes :  Mois + date + montant + nb de chéques + état
		et.addSheet("Bilan paiement", 5, 18);
				
		et.addRow("Bilan des paiements",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		et.addRow("Nom du contrat : "+mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow("Nom du producteur : "+mc.getProducteur().nom,et.grasGaucheNonWrappe);
		et.addRow("Ordre des chèques : "+mc.getLibCheque(),et.grasGaucheNonWrappe);
		

		// Une ligne vide
		et.addRow();
		
		// Création de la ligne titre des colonnes
		et.addRow();
		et.setRowHeigth(2);
		et.setCell(0,"Mois",et.grasCentreBordure);
		et.setCell(1,"Date réelle de remise",et.grasCentreBordure);
		et.setCell(2,"Montant" ,et.grasCentreBordure);
		et.setCell(3,"Nb de chèques" ,et.grasCentreBordure);
		et.setCell(4,"Etat de la remise" ,et.grasCentreBordure);
		
		// Une ligne pour les avoirs
		addRowAvoir(et,mntAvoir);
		
		// Une ligne pour chaque remise réelle
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		for (RemiseDTO remiseDTO : remises)
		{
			addRow(remiseDTO,et,df,false);
		}
		
		// Une ligne pour chaque remise prévisionnelle
		for (RemiseDTO remiseDTO : nextRemises)
		{
			addRow(remiseDTO,et,df,true);
		}
		
		// Une ligne vide
		et.addRow("",et.grasGaucheNonWrappe);
		
		//
		addRowCumul(et, remises,nextRemises,em,mc);
	}

	
	/**
	 * La ligne avoir est toujours présente 
	 * 
	 * @param et
	 * @param mntAvoir
	 */
	private void addRowAvoir(ExcelGeneratorTool et, int mntAvoir)
	{
		et.addRow();
		et.setCell(0,"Avoirs initiaux",et.grasGaucheNonWrappeBordure);
		 
		et.setCell(1,"",et.nonGrasCentreBordure);		
		et.setCellPrix(2,mntAvoir,et.prixCentreBordure);
		et.setCell(3,"",et.nonGrasCentreBordure);
		et.setCell(4,"",et.grasCentreBordure);
		
	}

	private void addRow(RemiseDTO remiseDTO, ExcelGeneratorTool et, SimpleDateFormat df,boolean isPrevision)
	{
		et.addRow();
		et.setCell(0,remiseDTO.moisRemise,et.grasGaucheNonWrappeBordure);
		
		String dateRemise = isPrevision ? "" : df.format(remiseDTO.dateReelleRemise); 
		et.setCell(1,dateRemise,et.nonGrasCentreBordure);
				
		et.setCellPrix(2,remiseDTO.mnt,et.prixCentreBordure);
		
		int nbCheque = isPrevision ? 0 : remiseDTO.nbCheque;
		et.setCellQte(3,nbCheque,et.nonGrasCentreBordure);
		
		//
		String etatRemise = isPrevision ? "PREVISION" : "FAIT";
		et.setCell(4,etatRemise,et.grasCentreBordure);
	}


	private void addRowCumul(ExcelGeneratorTool et, List<RemiseDTO> remises, List<RemiseDTO> nextRemises, EntityManager em, ModeleContrat mc)
	{
		// La ligne total payé 
		et.addRow();
		et.mergeCellsRight(0, 2);
		et.setCell(0,"Total payé ",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		et.setCellSumInColUp(2, 2+nextRemises.size(), remises.size()+1, et.prixCentreBordure);
		
		// La ligne total à payer 
		et.addRow();
		et.mergeCellsRight(0, 2);
		et.setCell(0,"Total restant à payer ",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		et.setCellSumInColUp(2, 3, nextRemises.size(), et.prixCentreBordure);
		
		// Une ligne vide
		et.addRow();
		
		// La ligne total commandé 
		int mntTotal = new GestionContratService().getMontantCommande(em, mc);
		et.addRow();
		et.mergeCellsRight(0, 2);
		et.setCell(0,"Montant total des produits",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		et.setCellPrix(2, mntTotal, et.prixCentreBordure);
	}
	



	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
		return "bilan-paiement-"+mc.getNom();
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "le bilan des paiements d'un producteur";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	public static void main(String[] args) throws IOException
	{
		new EGPaiementProducteur(4342L).test();
	}

}
