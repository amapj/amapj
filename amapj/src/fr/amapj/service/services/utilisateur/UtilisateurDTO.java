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
 package fr.amapj.service.services.utilisateur;

import fr.amapj.model.models.fichierbase.EtatUtilisateur;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Permet la gestion des utilisateurs en masse
 * ou du changement de son état
 * 
 */
public class UtilisateurDTO implements TableItem
{
	public Long id;

	public String prenom;
	
	public String nom;
	
	public String roles;
	
	public String email;
	
	public EtatUtilisateur etatUtilisateur;
	
	public String numTel1;
	
	public String numTel2;
	
	public String libAdr1;
	
	public String codePostal;

	public String ville;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getPrenom()
	{
		return prenom;
	}

	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public EtatUtilisateur getEtatUtilisateur()
	{
		return etatUtilisateur;
	}

	public void setEtatUtilisateur(EtatUtilisateur etatUtilisateur)
	{
		this.etatUtilisateur = etatUtilisateur;
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

	public String getRoles()
	{
		return roles;
	}

	public void setRoles(String roles)
	{
		this.roles = roles;
	}
	
	
	
	
}
