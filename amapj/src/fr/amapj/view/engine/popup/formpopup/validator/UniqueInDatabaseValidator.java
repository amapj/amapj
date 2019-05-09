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
 package fr.amapj.view.engine.popup.formpopup.validator;

import com.vaadin.ui.AbstractField;

import fr.amapj.service.services.dbservice.DbService;


/**
 * Ne fonctionne que pour les zones de textes 
 *
 */
public class UniqueInDatabaseValidator implements IValidator
{
	
	private Class clazz;

    private String property;
    
    // Id de la fiche en cours
    // Dans le cas de la modification d'une fiche, il est nécessaire de valider comme correcte la valeur qui est dans la fiche
    // même si elle est bien sûr dans la base
    private Long id;

 
	public UniqueInDatabaseValidator(Class clazz,String property,Long id)
	{
		super();
		this.clazz = clazz;
		this.property = property;
		this.id = id;
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		String val = (String) value;
		if (val==null)
		{
			val="";
		}
	
		
		int nb = new DbService().count(clazz, property, val,id);
		if (nb>0)
		{
			a.addMessage("Le champ \""+a.title+"\" contient une valeur déjà utilisée. Merci de choisir une autre valeur.");
		}
	}




	@Override
	public boolean canCheckOnFly()
	{
		return true;
	}
	
	@Override
	public AbstractField[] revalidateOnChangeOf()
	{
		return null;
	}

}
