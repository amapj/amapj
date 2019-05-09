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

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.contrat.modele.ModeleContrat;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;


/**
 * Listes des utilisateurs d'un contrat
 * 
 *  
 *
 */
public class EGUtilisateurContrat extends AbstractExcelGenerator
{
	
	Long idModeleContrat;
	
	public EGUtilisateurContrat(Long idModeleContrat)
	{
		this.idModeleContrat = idModeleContrat;
	}
		
	

	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		et.addSheet("Liste des souscripteurs", 8, 20);
		et.setColumnWidth(2, 40);
		et.setColumnWidth(5, 40);
		et.setColumnWidth(7, 40);
		
		
		ModeleContrat mc = em.find(ModeleContrat.class,idModeleContrat);
		List<Utilisateur> utilisateurs = new GestionContratSigneService().getAllUtilisateur(idModeleContrat);
		
		
		// Construction de l'entete
		contructEntete(et,mc);
		
		// Contruction d'une ligne pour chaque Utilisateur
		for (int i = 0; i < utilisateurs.size(); i++)
		{
			Utilisateur utilisateur = utilisateurs.get(i);
		
			contructRow(et,utilisateur);
		}	
		
	}
	
	private void contructEntete(ExcelGeneratorTool et, ModeleContrat mc)
	{
		
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		// Ligne de titre
		et.addRow("Liste des souscripteurs du contrat "+mc.getNom(),et.grasGaucheNonWrappe);
		et.addRow("Extrait le "+df1.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
			
		// Ligne vide
		et.addRow();
		

		// Ligne de Nom Prenom Email ... 
		et.addRow();
		et.setCell(0, "Nom", et.grasGaucheNonWrappeBordure);
		et.setCell(1, "Prénom", et.grasGaucheNonWrappeBordure);
		et.setCell(2, "E mail", et.grasGaucheNonWrappeBordure);
		et.setCell(3, "Tel1", et.grasGaucheNonWrappeBordure);
		et.setCell(4, "Tel2", et.grasGaucheNonWrappeBordure);
		et.setCell(5, "Adr", et.grasGaucheNonWrappeBordure);
		et.setCell(6, "Code Postal", et.grasGaucheNonWrappeBordure);
		et.setCell(7, "Ville", et.grasGaucheNonWrappeBordure);
		
	}

	
	

	private void contructRow(ExcelGeneratorTool et, Utilisateur u)
	{
		et.addRow();
			
		et.setCell(0, u.getNom(), et.grasGaucheNonWrappeBordure);
		et.setCell(1, u.getPrenom(), et.nonGrasGaucheBordure);
		et.setCell(2, u.getEmail(), et.nonGrasGaucheBordure);
		et.setCell(3, u.getNumTel1(), et.nonGrasGaucheBordure);
		et.setCell(4, u.getNumTel2(), et.nonGrasGaucheBordure);
		et.setCell(5, u.getLibAdr1(), et.nonGrasGaucheBordure);
		et.setCell(6, u.getCodePostal(), et.nonGrasGaucheBordure);
		et.setCell(7, u.getVille(), et.nonGrasGaucheBordure);
		
	}
	


	@Override
	public String getFileName(EntityManager em)
	{
		ModeleContrat mc = em.find(ModeleContrat.class,idModeleContrat);
		return "liste-souscripteurs-"+mc.getNom();
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		return "la liste des souscripteurs de ce contrat, avec leur e-mail et téléphone";
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
}
