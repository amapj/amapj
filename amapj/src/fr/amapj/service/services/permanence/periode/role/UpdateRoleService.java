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
 package fr.amapj.service.services.permanence.periode.role;

import java.util.List;

import javax.persistence.EntityManager;

import fr.amapj.model.engine.transaction.DbRead;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.model.models.permanence.reel.PermanenceCell;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceRoleDTO;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;

/**
 * Permet la gestion des roles dans une periode de permanences
 * 
 */
public class UpdateRoleService
{
	
	/**
	 * Permet de savoir si il existe des rôles
	 * @return
	 */
	@DbRead
	public boolean hasGestionRole()
	{
		return new PermanenceRoleService().getAllRoles().size()>1; 
	}
	
	
	

	/**
	 * Permet de positionner les roles sur toutes les dates en une seule fois 
	 * 
	 */
	@DbWrite
	public void setRole(PeriodePermanenceDTO dto)
	{
		EntityManager em = TransactionHelper.getEm();
	
		// On affecte d'abord à chaque permanence un role
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			Boucleur b = new Boucleur(dto.roles);
			
			for (PermanenceCellDTO cell : date.permanenceCellDTOs)
			{
				cell.idRole = b.getNext().idRole;
				saveCellRole(em,cell);
			}
		}		
	}
	
	private void saveCellRole(EntityManager em, PermanenceCellDTO cell)
	{
		PermanenceCell pc = em.find(PermanenceCell.class, cell.idPermanenceCell);
		pc.permanenceRole = em.find(PermanenceRole.class, cell.idRole);
	}

	public class Boucleur
	{
		List<PeriodePermanenceRoleDTO> roles;
		int index =0;

		public Boucleur(List<PeriodePermanenceRoleDTO> roles)
		{
			this.roles = roles;
		}

		public PeriodePermanenceRoleDTO getNext()
		{
			if (index>=roles.size())
			{
				index=0;
			}
			PeriodePermanenceRoleDTO res = roles.get(index);
			index++;
			return res;
		}
	}

	
	
	
}
