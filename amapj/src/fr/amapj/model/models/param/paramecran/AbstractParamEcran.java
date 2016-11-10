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
 package fr.amapj.model.models.param.paramecran;

import com.google.gson.Gson;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.engine.Identifiable;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.service.services.parametres.ParamEcranDTO;
import fr.amapj.view.engine.menu.MenuList;


public class AbstractParamEcran implements Identifiable
{

	transient private Long id;

	
	transient private MenuList menu;
	
	// Indique qui peut accéder à cet écran d'un point de vue global 
	private RoleList canAccessEcran = RoleList.ADHERENT;
	
	
	public AbstractParamEcran()
	{
	}
	
	
	/**
	 * Permet de transformer un ParamEcranDTO en un objet JSON
	 *  
	 * @param p
	 * @return
	 */
	static public AbstractParamEcran load(ParamEcranDTO p)
	{
		Class clazz = findClazz(p.menu);
	
		
		AbstractParamEcran etiquetteDTO = (AbstractParamEcran) new Gson().fromJson(p.content, clazz);
		etiquetteDTO.setId(p.id);
		etiquetteDTO.setMenu(p.menu);
		
		return etiquetteDTO;
	}
	
	static public AbstractParamEcran load(ParamEcran p)
	{
		Class clazz = findClazz(p.getMenu());
	
		
		AbstractParamEcran etiquetteDTO = (AbstractParamEcran) new Gson().fromJson(p.getContent(), clazz);
		etiquetteDTO.setId(p.getId());
		etiquetteDTO.setMenu(p.getMenu());
		
		return etiquetteDTO;
	}
	

	private static Class findClazz(MenuList menu)
	{
		switch (menu)
		{
		case MES_CONTRATS:
			return PEMesContrats.class;
		
		case LISTE_ADHERENTS:
			return PEListeAdherent.class;
			
		case RECEPTION_CHEQUES:
			return PEReceptionCheque.class;
			
		case OUT_SAISIE_PAIEMENT:
			return PESaisiePaiement.class;
			
		
		default:
			throw new AmapjRuntimeException("Type non pris en compte");
		}
	}
	
	
	/**
	 * Permet de transformer l'objet courant en un objet ParamEcranDTO
	 * @return
	 */
	public ParamEcranDTO save()
	{
	
		ParamEcranDTO editionSpeDTO = new ParamEcranDTO();
		editionSpeDTO.id = id;
		editionSpeDTO.menu = menu;
		Gson gson = new Gson();
		
		editionSpeDTO.content = gson.toJson(this);
		
		return editionSpeDTO;
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
