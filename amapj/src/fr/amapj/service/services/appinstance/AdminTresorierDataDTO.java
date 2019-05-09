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
 package fr.amapj.service.services.appinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Information tresorier / administrateurs sur toutes les instances, 
 * avec la liste des tresoriers et des admins
 *
 */
public class AdminTresorierDataDTO 
{
	static public class InstanceDTO
	{
		public String code;
		
		public String nom;
		
		public List<ContactDTO> admins;
		
		public List<ContactDTO> tresoriers;

		public int nbAccessLastMonth;
	}
	
	
	
	static public class ContactDTO
	{
		public String nom;
		
		public String prenom;
		
		public String email;

		public ContactDTO(String nom, String prenom, String email)
		{
			super();
			this.nom = nom;
			this.prenom = prenom;
			this.email = email;
		}
		
	}
	

	public Date extractionDate;
	
	public List<InstanceDTO> instances = new ArrayList<AdminTresorierDataDTO.InstanceDTO>();
	
}
