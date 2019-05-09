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
 package fr.amapj.service.services.edgenerator.velocity;

import fr.amapj.model.models.fichierbase.Utilisateur;


public class VCPersonne
{
	public String nom;
	
	public String prenom;
	
	public String email;
	
	// numéro de  téléphone 1
	public String numTel1;
	
	// numéro de  téléphone 2
	public String numTel2;
	
	public String libAdr1;
	
	public String codePostal;

	public String ville;

	public void load(Utilisateur u)
	{
		if (u!=null)
		{
			nom = s(u.getNom());
			prenom = s(u.getPrenom());
			email = s(u.getEmail());
			numTel1 = s(u.getNumTel1());
			numTel2 = s(u.getNumTel2());
			libAdr1 = s(u.getLibAdr1());
			codePostal = s(u.getCodePostal());
			ville = s(u.getVille());
		}
		else
		{
			nom = "";
			prenom = "";
			email = "";
			numTel1 = "";
			numTel2 = "";
			libAdr1 = "";
			codePostal = "";
			ville = "";
		}
	}
	
	/**
	 * Permet d'escaper les caracteres HTML  
	 */
	private String s(String value)
	{
		return VCBuilder.s(value);
	}
	

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public String getPrenom()
	{
		return prenom;
	}

	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getNumTel1()
	{
		return numTel1;
	}

	public void setNumTel1(String numTel1)
	{
		this.numTel1 = numTel1;
	}

	public String getNumTel2()
	{
		return numTel2;
	}

	public void setNumTel2(String numTel2)
	{
		this.numTel2 = numTel2;
	}

	public String getLibAdr1()
	{
		return libAdr1;
	}

	public void setLibAdr1(String libAdr1)
	{
		this.libAdr1 = libAdr1;
	}

	public String getCodePostal()
	{
		return codePostal;
	}

	public void setCodePostal(String codePostal)
	{
		this.codePostal = codePostal;
	}

	public String getVille()
	{
		return ville;
	}

	public void setVille(String ville)
	{
		this.ville = ville;
	}
	
	
	
	
}
