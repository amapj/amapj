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

import com.google.gson.Gson;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.services.parametres.ParamEcranDTO;
import fr.amapj.view.engine.menu.MenuList;

public class ParamEcranConverter
{
	/**
	 * Permet de transformer un ParamEcranDTO en un objet de type AbstractParamEcran
	 * 
	 * p ne peut pas etre null 
	 */
	static public AbstractParamEcran load(ParamEcranDTO p)
	{
		Class clazz = ParamEcranInfo.findClazz(p.menu);
			
		AbstractParamEcran etiquetteDTO = (AbstractParamEcran) new Gson().fromJson(p.content, clazz);
		etiquetteDTO.setId(p.id);
		etiquetteDTO.setMenu(p.menu);
		
		return etiquetteDTO;
	}
	
	/**
	 * Permet de transformer un ParamEcran en un objet de type AbstractParamEcran
	 * 
	 * p ne peut pas etre null 
	 */
	static public AbstractParamEcran load(ParamEcran p)
	{
		Class clazz = ParamEcranInfo.findClazz(p.getMenu());
	
		AbstractParamEcran etiquetteDTO = (AbstractParamEcran) new Gson().fromJson(p.getContent(), clazz);
		etiquetteDTO.setId(p.getId());
		etiquetteDTO.setMenu(p.getMenu());
		
		return etiquetteDTO;
	}
	
	
	/**
	 * Permet de transformer un objet de type AbstractParamEcran en un objet ParamEcranDTO
	 * @return
	 */
	static public ParamEcranDTO save(AbstractParamEcran abstractParamEcran)
	{
	
		ParamEcranDTO editionSpeDTO = new ParamEcranDTO();
		editionSpeDTO.id = abstractParamEcran.getId();
		editionSpeDTO.menu = abstractParamEcran.getMenu();
		Gson gson = new Gson();
		
		editionSpeDTO.content = gson.toJson(abstractParamEcran);
		
		return editionSpeDTO;
	}
	
	/**
	 * Permet d'obtenir une instance du ParamEcran à partir du nom du menu 
	 * 
	 */
	static public AbstractParamEcran getNew(MenuList menuList)
	{	
		Class clazz = ParamEcranInfo.findClazz(menuList);
		try
		{
			AbstractParamEcran pe =  (AbstractParamEcran) clazz.newInstance();
			pe.setMenu(menuList);
			return pe;
		} 
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new AmapjRuntimeException(e);
		}	
	}
	
}
