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
 package fr.amapj.view.views.importdonnees;

import java.io.IOException;
import java.util.List;

import fr.amapj.common.StringUtils;
import fr.amapj.service.services.importdonnees.ImportDonneesService;
import fr.amapj.service.services.importdonnees.ImportProduitProducteurDTO;
import fr.amapj.view.views.importdonnees.tools.AbstractImporter;

@SuppressWarnings("serial")
public class ProduitImporter extends AbstractImporter<ImportProduitProducteurDTO>
{

	@Override
	public int getNumCol()
	{
		return 3;
	}

	@Override
	public List<ImportProduitProducteurDTO> getAllDataInDatabase()
	{
		List<ImportProduitProducteurDTO> existing = new ImportDonneesService().getAllProduits();
		return existing;
	}

	@Override
	public String checkBasic(ImportProduitProducteurDTO dto)
	{
		// NOM DU PRODUIT
		if (isEmpty(dto.produit))
		{
			return 	"Le nom du produit n'est pas renseigné. Il est obligatoire.";
		}
		
		String msg = checkLength(dto.produit, 1, 100, "nom du produit");
		if (msg!=null)
		{
			return msg;
		}
		

		// CONDITIONNEMENT DU PRODUIT
		if (isEmpty(dto.conditionnement))
		{
			return 	"Le conditionnement du produit n'est pas renseigné. Il est obligatoire.";
		}
		
		msg = checkLength(dto.conditionnement, 1, 500, "conditionnement du produit");
		if (msg!=null)
		{
			return msg;
		}
		
		
		// PRODUCTEUR
		if (isEmpty(dto.producteur))
		{
			return "Le nom du producteur n'est pas renseigné. Il est obligatoire.";
		}
		
		msg = checkLength(dto.producteur, 1, 100, "nom du producteur");
		if (msg!=null)
		{
			return msg;
		}
		
		
		
		
		return null;
		
	}
	
	@Override
	public ImportProduitProducteurDTO createDto(String[] strs)
	{
		ImportProduitProducteurDTO dto = new ImportProduitProducteurDTO();
		
		dto.producteur = strs[0];
		dto.produit = strs[1];
		dto.conditionnement = strs[2];
		
		return dto;
	}

	@Override
	public void saveInDataBase(List<ImportProduitProducteurDTO> prods)
	{	
		new ImportDonneesService().insertDataProduits(prods);
	}

	
	@Override
	public String checkDifferent(ImportProduitProducteurDTO dto1, ImportProduitProducteurDTO dto2)
	{	
		if (    dto1.producteur.equalsIgnoreCase(dto2.producteur)
				&& dto1.produit.equalsIgnoreCase(dto2.produit) 
				&& (StringUtils.equalsIgnoreCase(dto1.conditionnement, dto2.conditionnement)) )
		{
			return "Deux produits chez le même producteur ont le même nom et condtionnement alors que ceci est interdit.";
		}
		
		return null;
		
	}
	
	@Override
	public void dumpInfo(List<String> errorMessage,ImportProduitProducteurDTO dto)
	{
		errorMessage.add("Producteur:"+dto.producteur);
		errorMessage.add("Nom du produit:"+dto.produit);
		errorMessage.add("Conditionnement du produit:"+dto.conditionnement);
		
	}
	

	
	
	
	public static void main(String[] args) throws IOException
	{		
		ProduitImporter importer = new ProduitImporter();
		importer.test("liste-adherents-1.xls");
			
	}

}
