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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.common.DateUtils;
import fr.amapj.service.engine.generator.excel.AbstractExcelGenerator;
import fr.amapj.service.engine.generator.excel.ExcelFormat;
import fr.amapj.service.engine.generator.excel.ExcelGeneratorTool;
import fr.amapj.service.services.parametres.paramecran.PEListeAdherentDTO;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;


/**
 * Permet la generation de la liste des adhérents
 * 
 *  
 *
 */
public class EGListeAdherent extends AbstractExcelGenerator
{
	
	public enum Type
	{
		STD , AVEC_INACTIF , EXAMPLE;
	}

	Type type;
	
	PEListeAdherentDTO peListeAdherentDTO;
	
	
	public EGListeAdherent(Type type)
	{
		this.type = type;
		this.peListeAdherentDTO = new PEListeAdherentDTO(); // On met tous les droits
	}
	
	public EGListeAdherent(Type type,PEListeAdherentDTO peListeAdherentDTO)
	{
		this.type = type;
		this.peListeAdherentDTO = peListeAdherentDTO;
	}
	
	

	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		et.addSheet("Liste des adhérents", 9, 20);
		et.setColumnWidth(2, 40);
		et.setColumnWidth(5, 40);
		et.setColumnWidth(7, 40);
		
		
		List<UtilisateurDTO> utilisateurs;
		
		if (type==Type.EXAMPLE)
		{
			utilisateurs = new ArrayList<>();
			UtilisateurDTO dto = new UtilisateurDTO();
			dto.setNom("DURAND");
			dto.setPrenom("Paul");
			dto.setEmail("paul@gmail.com");
			dto.setNumTel1("01 23 45 67 89");
			dto.setNumTel2("06 12 34 56 78");
			dto.setLibAdr1("La grande rue");
			dto.setCodePostal("26150");
			dto.setVille("Die");
			
			utilisateurs.add(dto);
		}
		else if (type==Type.STD)
		{
			utilisateurs = new UtilisateurService().getAllUtilisateurs(false);
		}
		else
		{
			utilisateurs = new UtilisateurService().getAllUtilisateurs(true);
		}
		
		
		// Construction de l'entete
		contructEntete(et);
		
		// Contruction d'une ligne pour chaque Utilisateur
		for (int i = 0; i < utilisateurs.size(); i++)
		{
			UtilisateurDTO utilisateur = utilisateurs.get(i);
		
			contructRow(et,utilisateur);
		}	
		
	}
	
	private void contructEntete(ExcelGeneratorTool et)
	{
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		if (type!=Type.EXAMPLE)
		{
			
			// Ligne de titre
			et.addRow("Extrait le "+df1.format(DateUtils.getDate()),et.grasGaucheNonWrappe);
			
			// Ligne vide
			et.addRow();
		}

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
		
		if (type==Type.AVEC_INACTIF)
		{
			et.setCell(8, "Actif/Inactif", et.grasGaucheNonWrappeBordure);
		}
		
	}

	
	

	private void contructRow(ExcelGeneratorTool et, UtilisateurDTO u)
	{
		et.addRow();
		
		// On met les cadres
		for (int i = 0; i < 8; i++)
		{
			et.setCell(i, "", et.nonGrasGaucheBordure);
		}
		
		
		
		et.setCell(0, u.getNom(), et.grasGaucheNonWrappeBordure);
		
		et.setCell(0, u.getNom(), et.grasGaucheNonWrappeBordure);
		et.setCell(1, u.getPrenom(), et.nonGrasGaucheBordure);
		if (peListeAdherentDTO.canAccessEmail)
		{
			et.setCell(2, u.getEmail(), et.nonGrasGaucheBordure);
		}
		if (peListeAdherentDTO.canAccessTel1)
		{
			et.setCell(3, u.getNumTel1(), et.nonGrasGaucheBordure);
		}
		if (peListeAdherentDTO.canAccessTel2)
		{
			et.setCell(4, u.getNumTel2(), et.nonGrasGaucheBordure);
		}
		if (peListeAdherentDTO.canAccessAdress)
		{
			et.setCell(5, u.getLibAdr1(), et.nonGrasGaucheBordure);
			et.setCell(6, u.getCodePostal(), et.nonGrasGaucheBordure);
			et.setCell(7, u.getVille(), et.nonGrasGaucheBordure);
		}
		
		if (type==Type.AVEC_INACTIF)
		{
			et.setCell(8, u.getEtatUtilisateur().name(), et.grasGaucheNonWrappeBordure);
		}
	}
	


	@Override
	public String getFileName(EntityManager em)
	{
		return "liste-adherents";
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		if (type==Type.EXAMPLE)
		{
			return "un exemple de fichier pour charger les utilisateurs";
		}
		else if (type==Type.STD)
		{
			return "la liste des adhérents"; 
		}
		else
		{
			return "la liste des adhérents, y compris les inactifs";
		}
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
}
