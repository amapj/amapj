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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import fr.amapj.model.models.cotisation.PeriodeCotisation;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.gestioncotisation.BilanAdhesionDTO;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationUtilisateurDTO;


/**
 * Permet la generation du bilan des adhésions pour une année
 * 
 *  
 *
 */
public class EGBilanAdhesion extends AbstractExcelGenerator
{
	
	Long periodeCotisationId;
	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public EGBilanAdhesion(Long periodeCotisationId)
	{
		this.periodeCotisationId = periodeCotisationId;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		BilanAdhesionDTO bilan = new GestionCotisationService().loadBilanAdhesion(periodeCotisationId);
		
		// Calcul du nombre de colonnes :  Nom + prénom + montant du chéque + état du paiement + type du paiement + date de réception
		et.addSheet("Adhésion", 6, 20);
				
		et.addRow("Bilan des adhésions pour la période de "+bilan.periodeCotisationDTO.nom,et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		et.addRow("Nombre total d'adhésions : "+bilan.utilisateurDTOs.size(),et.grasGaucheNonWrappe);
		et.addRow("Nombre de paiements à récupérer : "+bilan.periodeCotisationDTO.nbPaiementARecuperer,et.grasGaucheNonWrappe);
		et.addRow("Nombre de paiements réceptionnés : "+bilan.periodeCotisationDTO.nbPaiementDonnes,et.grasGaucheNonWrappe);
		et.addRow("Ordre des chèques : "+bilan.periodeCotisationDTO.getLibCheque(),et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		

		// Création de la ligne titre des colonnes
		et.addRow();
		et.setCell(0,"Nom",et.grasCentreBordure);
		et.setCell(1,"Prénom",et.grasCentreBordure);
		et.setCell(2,"Montant",et.grasCentreBordure);
		et.setCell(3,"Etat du paiement",et.grasCentreBordure);
		et.setCell(4,"Type du paiement",et.grasCentreBordure);
		et.setCell(5,"Date de réception",et.grasCentreBordure);
		
		
		// Une ligne pour chaque adhésion
		for (PeriodeCotisationUtilisateurDTO u : bilan.utilisateurDTOs)
		{
			addRow(em,u,et);
		}
		
		// Une ligne vide
		et.addRow("",et.grasGaucheNonWrappe);
		
		addRowCumul(et, bilan.utilisateurDTOs.size());
		

	}

	private void addRow(EntityManager em, PeriodeCotisationUtilisateurDTO pu, ExcelGeneratorTool et)
	{
		Utilisateur u = em.find(Utilisateur.class, pu.idUtilisateur);
		
		et.addRow();
		et.setCell(0,u.getNom(),et.grasGaucheNonWrappeBordure);
		et.setCell(1,u.getPrenom(),et.nonGrasGaucheBordure);
		et.setCellPrix(2,pu.montantAdhesion,et.prixCentreBordure);
		et.setCell(3,pu.etatPaiementAdhesion.toString(),et.nonGrasCentreBordure);
		et.setCell(4,pu.typePaiementAdhesion.toString(),et.nonGrasCentreBordure);
		et.setCell(5,getDate(pu.dateReceptionCheque),et.nonGrasCentreBordure);
		
	}


	
	
	private String getDate(Date dateReceptionCheque)
	{
		if (dateReceptionCheque==null)
		{
			return "";
		}
		return df.format(dateReceptionCheque);
	}

	private void addRowCumul(ExcelGeneratorTool et, int nbAdhesion)
	{
		et.addRow();
		
		et.setCell(0,"Total",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		et.setCellSumInColUp(2, 2, nbAdhesion, et.prixCentreBordure);
		et.setCell(3,"",et.nonGrasGaucheBordure);
		et.setCell(4,"",et.nonGrasGaucheBordure);
		et.setCell(5,"",et.nonGrasGaucheBordure);
	}

	

	@Override
	public String getFileName(EntityManager em)
	{
		PeriodeCotisation pc = em.find(PeriodeCotisation.class, periodeCotisationId);
		return "bilan-adhésion-"+pc.getNom();
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		PeriodeCotisation pc = em.find(PeriodeCotisation.class, periodeCotisationId);
		return "le bilan des adhésions pour "+pc.getNom();
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	public static void main(String[] args) throws IOException
	{
		new EGBilanAdhesion(12652L).test();
	}

}
