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
 package fr.amapj.service.services.mescontrats;


import java.util.ArrayList;
import java.util.List;

/**
 * Informations sur les contrats d'un utilisateur
 *
 */
public class MesContratsDTO
{
	// Information sur l'adhesion
	public AdhesionDTO adhesionDTO = new AdhesionDTO();
	
	// Liste des nouveaux contrats auxquels peut subscrire l'utilisateur
	List<ContratDTO> newContrats;
	
	// Liste des contrats déjà souscrits
	List<ContratDTO> existingContrats;
	
	
	public MesContratsDTO()
	{
		newContrats = new ArrayList<ContratDTO>();
		existingContrats = new ArrayList<ContratDTO>();
	}


	public List<ContratDTO> getNewContrats()
	{
		return newContrats;
	}


	public void setNewContrats(List<ContratDTO> newContrats)
	{
		this.newContrats = newContrats;
	}


	public List<ContratDTO> getExistingContrats()
	{
		return existingContrats;
	}


	public void setExistingContrats(List<ContratDTO> existingContrats)
	{
		this.existingContrats = existingContrats;
	}
	
	

}
