/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.engine.menu;

import java.util.List;

import com.vaadin.navigator.View;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.param.EtatModule;
import fr.amapj.model.models.param.paramecran.common.AbstractParamEcran;
import fr.amapj.model.models.param.paramecran.common.ParamEcranConverter;
import fr.amapj.service.services.parametres.ParamEcranDTO;
import fr.amapj.service.services.parametres.ParametresDTO;

/**
 * Contient la description d'une entrée menu
 *
 */
public class MenuDescription 
{
	//
	private ModuleList module;
	
	// Role ayant acces à cette entrée de menu
	private RoleList role;
	
	// Nom du menu (element de la liste MenuList)
	private MenuList menuName;
	
	// Nom de la classe implementant la vue liée à ce menu
	private Class<? extends View> viewClass;
	
	private String categorie;
	
	
	
	/**
	 * Dans ce cas, cette entrée est accessible à tous
	 */
	public MenuDescription(MenuList menuName, Class<? extends View> viewClass )
	{
		this(menuName, viewClass, RoleList.ADHERENT, ModuleList.GLOBAL);
	}
	
	public MenuDescription(MenuList menuName, Class<? extends View> viewClass,RoleList role)
	{
		this(menuName, viewClass,role,ModuleList.GLOBAL);
	}
	
	
	
	public MenuDescription(MenuList menuName, Class<? extends View> viewClass,RoleList role,ModuleList module)
	{
		this.module = module;
		this.role =  role;
		this.menuName = menuName;
		this.viewClass = viewClass;
	}



	public MenuList getMenuName()
	{
		return menuName;
	}

	public Class<? extends View> getViewClass()
	{
		return viewClass;
	}
	
	
	public boolean hasRole(List<RoleList> roles)
	{
		return roles.contains(role);
	}
	
	
	/**
	 * Retourne true si cette entrée de menu est activé par son module
	 * @param param
	 * @return
	 */
	public boolean hasModule(ParametresDTO param)
	{
		switch (module)
		{
		case GLOBAL:
			return true;
		
		case PLANNING_DISTRIBUTION:
			return param.etatPlanningDistribution.equals(EtatModule.ACTIF);
			
		case GESTION_COTISATION:
			return param.etatGestionCotisation.equals(EtatModule.ACTIF);

		default:
			throw new AmapjRuntimeException("Erreur de programmation");
		}
	}
	
	/**
	 * Recherche si l'écran est accessible à cet utilisateur en fonction des
	 * paramètrages écran effectués
	 * @param roles
	 * @param dtos
	 * @return
	 */
	public boolean complyParamEcan(List<RoleList> roles,List<ParamEcranDTO> dtos)
	{
		ParamEcranDTO dto = findParamEcran(dtos);
		
		if (dto==null)
		{
			return true;
		}
		
		AbstractParamEcran ape = ParamEcranConverter.load(dto);
		
		return roles.contains(ape.getCanAccessEcran());
		
	}
	

	private ParamEcranDTO findParamEcran(List<ParamEcranDTO> dtos)
	{
		for (ParamEcranDTO dto : dtos)
		{
			if (dto.menu.equals(menuName))
			{
				return dto;
			}
		}
		return null;
	}

	public String getCategorie()
	{
		return categorie;
	}

	public MenuDescription setCategorie(String categorie)
	{
		this.categorie = categorie;
		return this;
	}
	
}
