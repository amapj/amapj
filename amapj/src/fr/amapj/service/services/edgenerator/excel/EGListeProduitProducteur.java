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
import fr.amapj.service.services.importdonnees.ImportDonneesService;
import fr.amapj.service.services.importdonnees.ImportProduitProducteurDTO;


/**
 * Permet la generation du fichier d'exemple pour charger les produits et les producteurs
 * 
 */
public class EGListeProduitProducteur extends AbstractExcelGenerator
{
	
	public enum Type
	{
		STD , EXAMPLE;
	}

	Type type;
	

	
	public EGListeProduitProducteur(Type type)
	{
		this.type = type;
	}
	
	

	@Override
	public void fillExcelFile(EntityManager em,ExcelGeneratorTool et)
	{
		et.addSheet("Liste des produits et des producteurs", 3, 60);
		
		List<ImportProduitProducteurDTO> prods;
		
		if (type==Type.EXAMPLE)
		{
			prods = new ArrayList<>();
			
			ImportProduitProducteurDTO dto = new ImportProduitProducteurDTO();
			dto.producteur = "EARL BIO LAIT";
			dto.produit = "Faisselle";
			dto.conditionnement = "le pot de 500 g";
			prods.add(dto);
			
			dto = new ImportProduitProducteurDTO();
			dto.producteur = "EARL BIO LAIT";
			dto.produit = "Yaourt";
			dto.conditionnement = "le pot de 1 kg";
			prods.add(dto);
			
			dto = new ImportProduitProducteurDTO();
			dto.producteur = "EARL PAIN";
			dto.produit = "Pain de seigle";
			dto.conditionnement = "la pièce de 900 g";
			prods.add(dto);
			
			dto = new ImportProduitProducteurDTO();
			dto.producteur = "EARL PAIN";
			dto.produit = "Pain de campagne";
			dto.conditionnement = "la pièce de 900 g";
			prods.add(dto);
			
			
		}
		else 
		{
			prods = new ImportDonneesService().getAllProduits();
		}
		
		// Construction de l'entete
		contructEntete(et);
		
		// Contruction d'une ligne pour chaque Utilisateur
		for (int i = 0; i < prods.size(); i++)
		{
			ImportProduitProducteurDTO utilisateur = prods.get(i);
		
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
		et.setCell(0, "Producteur", et.grasGaucheNonWrappeBordure);
		et.setCell(1, "Nom du produit", et.grasGaucheNonWrappeBordure);
		et.setCell(2, "Conditionnement du produit", et.grasGaucheNonWrappeBordure);
		
	}

	
	

	private void contructRow(ExcelGeneratorTool et, ImportProduitProducteurDTO u)
	{
		et.addRow();
		et.setCell(0, u.producteur, et.grasGaucheNonWrappeBordure);
		et.setCell(1, u.produit, et.nonGrasGaucheBordure);
		et.setCell(2, u.conditionnement, et.nonGrasGaucheBordure);
		
	}
	


	@Override
	public String getFileName(EntityManager em)
	{
		return "liste-produits";
	}
	

	@Override
	public String getNameToDisplay(EntityManager em)
	{
		if (type==Type.EXAMPLE)
		{
			return "un exemple de fichier pour charger les produits et les producteurs";
		}
		else
		{
			return "la liste des produits et producteurs";
		}
	}
	
	@Override
	public ExcelFormat getFormat()
	{
		return ExcelFormat.XLS;
	}
}
