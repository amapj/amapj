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
 package fr.amapj.service.services.editionspe;

import fr.amapj.model.models.editionspe.TypEditionSpecifique;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Gestion des Editions specifiques 
 * 
 * Permet de charger les informations d'entete des editions specifiques, sans le contenu 
 * 
 */
public class EditionSpeDTO implements TableItem
{
	public Long id;

	public String nom;
	
	public TypEditionSpecifique typEditionSpecifique;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getNom()
	{
		return nom;
	}

	public void setNom(String nom)
	{
		this.nom = nom;
	}

	public TypEditionSpecifique getTypEditionSpecifique()
	{
		return typEditionSpecifique;
	}

	public void setTypEditionSpecifique(TypEditionSpecifique typEditionSpecifique)
	{
		this.typEditionSpecifique = typEditionSpecifique;
	}


	
	

}
