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
 package fr.amapj.service.engine.tools;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class DbToDto
{

	/**
	 * Convertit le resultat d'une requete (donc une liste d'objet du modele) en une liste de DTO
	 */
	static public <MODEL,DTO> List<DTO> transform(Query q , ToDTO<MODEL, DTO> toDTO)
	{
		List<DTO> res = new ArrayList<>();
		
		
		List<MODEL> ps = q.getResultList();
		
		for (MODEL p : ps)
		{
			DTO dto = toDTO.toDTO(p);
			res.add(dto);
		}	
		return res;
	}
	
	
	/**
	 * Convertit le resultat d'une requete (donc une liste d'objet du modele) en une liste de DTO
	 */
	static public <MODEL,DTO> List<DTO> transform(List<MODEL> ps , ToDTO<MODEL, DTO> toDTO)
	{
		List<DTO> res = new ArrayList<>();
		
		for (MODEL p : ps)
		{
			DTO dto = toDTO.toDTO(p);
			res.add(dto);
		}	
		return res;
	}
	
	
	static public interface ToDTO<MODEL,DTO>
	{
		public DTO toDTO(MODEL t);
	}
	
}
