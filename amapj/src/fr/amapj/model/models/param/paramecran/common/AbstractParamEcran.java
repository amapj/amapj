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
 package fr.amapj.model.models.param.paramecran.common;

import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.view.engine.menu.MenuList;


public class AbstractParamEcran implements Identifiable
{
	// L'id peut être null 
	// Si id est non null : ce parametrage provient de la base 
	// Si id est null : ce parametrage provient d'un new, ce sont donc les parametres par defaut  
	transient private Long id;

	transient private MenuList menu;	

	
	// Indique qui peut accéder à cet écran d'un point de vue global 
	private RoleList canAccessEcran = RoleList.ADHERENT;
	
	
	public AbstractParamEcran()
	{
	}
	

	public Long getId()
	{
		return id;
	}


	public void setId(Long id)
	{
		this.id = id;
	}


	public MenuList getMenu()
	{
		return menu;
	}


	public void setMenu(MenuList menu)
	{
		this.menu = menu;
	}


	public RoleList getCanAccessEcran()
	{
		return canAccessEcran;
	}


	public void setCanAccessEcran(RoleList canAccessEcran)
	{
		this.canAccessEcran = canAccessEcran;
	}
}
