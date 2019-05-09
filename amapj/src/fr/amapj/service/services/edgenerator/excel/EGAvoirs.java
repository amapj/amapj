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
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.gestioncontratsigne.ContratSigneDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;


/**
 * Permet la generation du bilan des avoirs initiaux d'un contrat
 * 
 *  
 *
 */
public class EGAvoirs extends AbstractExcelGenerator
{
	
	Long modeleContratId;
	
	public EGAvoirs(Long modeleContratId)
	{
		this.modeleContratId = modeleContratId;
	}
	
	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		List<ContratSigneDTO> avoirs = new GestionContratSigneService().getAvoirsInfo(em,modeleContratId);
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);

		
		// Calcul du nombre de colonnes :  Nom + prénom + 1 montant de l'avoir
		et.addSheet("Avoirs", 3, 20);
				
		et.addRow("Liste des avoirs initiaux",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		
		et.addRow("Nom du contrat : "+mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow("Nom du producteur : "+mc.getProducteur().nom,et.grasGaucheNonWrappe);
		et.addRow(avoirs.size()+" adhérents possèdent un avoir",et.grasGaucheNonWrappe);
		et.addRow("",et.grasGaucheNonWrappe);
		

		// Création de la ligne titre des colonnes
		et.addRow();
		et.setCell(0,"Nom",et.grasCentreBordure);
		et.setCell(1,"Prénom",et.grasCentreBordure);
		et.setCell(2,"Montant avoir",et.grasCentreBordure);
		
		
		// Une ligne pour chaque avoir 
		for (ContratSigneDTO contratSigneDTO : avoirs)
		{
			addRow(contratSigneDTO,et);
		}
		
		// Une ligne vide
		et.addRow("",et.grasGaucheNonWrappe);
		
		addRowCumul(et, avoirs.size());
		

	}

	private void addRow(ContratSigneDTO contratSigneDTO, ExcelGeneratorTool et)
	{
		et.addRow();
		et.setCell(0,contratSigneDTO.nomUtilisateur,et.grasGaucheNonWrappeBordure);
		et.setCell(1,contratSigneDTO.prenomUtilisateur,et.nonGrasGaucheBordure);
		et.setCellPrix(2,contratSigneDTO.mntAvoirInitial,et.prixCentreBordure);
	}


	
	
	private void addRowCumul(ExcelGeneratorTool et, int nbPaiements)
	{
		et.addRow();
		
		et.setCell(0,"Total",et.grasGaucheNonWrappeBordure);
		et.setCell(1,"",et.nonGrasGaucheBordure);
		et.setCellSumInColUp(2, 2, nbPaiements, et.prixCentreBordure);
	}

	

	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class,modeleContratId);
		return "avoirs-"+mc.getNom();
	}

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "la liste des avoirs initiaux du contrat";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}

	public static void main(String[] args) throws IOException
	{
		new EGAvoirs(12652L).test();
	}

}
