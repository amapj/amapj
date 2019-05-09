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
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.formpopup.validator.EmailValidator;
import fr.amapj.view.views.importdonnees.tools.AbstractImporter;

@SuppressWarnings("serial")
public class UtilisateurImporter extends AbstractImporter<UtilisateurDTO>
{

	@Override
	public int getNumCol()
	{
		return 8;
	}

	@Override
	public List<UtilisateurDTO> getAllDataInDatabase()
	{
		List<UtilisateurDTO> existing = new UtilisateurService().getAllUtilisateurs(true);
		return existing;
	}

	@Override
	public String checkBasic(UtilisateurDTO dto)
	{
		if (isEmpty(dto.nom))
		{
			return 	"Le nom n'est pas renseigné. Il est obligatoire.";
		}

		if (isEmpty(dto.email) )
		{
			return "L'adresse e mail n'est pas renseignée. Elle est obligatoire. Si la personne n'a pas d'email, merci de mettre son nom ou prénom suivi d'un #. Exemple : geraldine#";
		}
		
		if (EmailValidator.isValidEmail(dto.email)==false)
		{
			return "L'adresse e mail n'est pas valide. Si la personne n'a pas d'email, merci de mettre son nom ou prénom suivi d'un #. Exemple : geraldine#";
		}
		
		return null;
		
	}
	
	@Override
	public UtilisateurDTO createDto(String[] strs)
	{
		UtilisateurDTO dto = new UtilisateurDTO();
		
		dto.nom = strs[0];
		dto.prenom = strs[1];
		dto.email = trimEmail(strs[2]);
		dto.numTel1 = strs[3];
		dto.numTel2 = strs[4];
		dto.libAdr1 = strs[5];
		dto.codePostal = strs[6];
		dto.ville = strs[7];
		
		return dto;
	}

	private String trimEmail(String str)
	{
		if (str==null)
		{
			return null;
		}
		return str.trim();
	}

	@Override
	public void saveInDataBase(List<UtilisateurDTO> utilisateurs)
	{	
		new UtilisateurService().insertAllUtilisateurs(utilisateurs);
	}

	
	@Override
	public String checkDifferent(UtilisateurDTO dto1, UtilisateurDTO dto2)
	{
		if (dto1.email.equalsIgnoreCase(dto2.email))
		{
			return "Deux utilisateurs ont la même adresse e mail alors que ceci est interdit.";
		}
			
		if (dto1.nom.equalsIgnoreCase(dto2.nom) && (StringUtils.equalsIgnoreCase(dto1.prenom, dto2.prenom)) )
		{
			return "Deux utilisateurs ont le même nom et prénom alors que ceci est interdit.";
		}
		
		return null;
		
	}
	
	@Override
	public void dumpInfo(List<String> errorMessage,UtilisateurDTO dto)
	{
		errorMessage.add("Nom:"+dto.nom);
		errorMessage.add("Prénom:"+dto.prenom);
		errorMessage.add("Email:"+dto.email);
		
	}
	

	
	
	
	public static void main(String[] args) throws IOException
	{		
		UtilisateurImporter importer = new UtilisateurImporter();
		importer.test("c:\\tmp\\liste-adherents (8).xls");
			
	}

}
