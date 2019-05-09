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
 package fr.amapj.service.services.gestioncontratsigne;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Permet de depalcer une date de livraison pour un contrat signé
 *
 */
public class DeplacerDateLivraisonDTO 
{
	
	// Liste des dates de livraison de ce contrat
	public List<ModifDateLivraisonDTO> dateLivraisonDTOs = new ArrayList<DeplacerDateLivraisonDTO.ModifDateLivraisonDTO>();
		
	// Date selectionnée
	public ModifDateLivraisonDTO selected;
	
	// La nouvelle valeur de la date
	public Date actualDate;
	
	
	static public class ModifDateLivraisonDTO
	{
		public long idModeleContratDate;
		
		public Date dateLiv;
	}


	public ModifDateLivraisonDTO getSelected()
	{
		return selected;
	}


	public void setSelected(ModifDateLivraisonDTO selected)
	{
		this.selected = selected;
	}


	public Date getActualDate()
	{
		return actualDate;
	}


	public void setActualDate(Date actualDate)
	{
		this.actualDate = actualDate;
	}
	
	
	

}
