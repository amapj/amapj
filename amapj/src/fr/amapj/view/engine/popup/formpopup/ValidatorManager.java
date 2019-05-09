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
 package fr.amapj.view.engine.popup.formpopup;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;

import fr.amapj.view.engine.popup.formpopup.validator.ValidatorHolder;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;

public class ValidatorManager
{
	
	private List<ValidatorHolder> validatorHolders;

	public ValidatorManager()
	{
		validatorHolders = new ArrayList<>();
	}
	
	public void reset()
	{
		validatorHolders = new ArrayList<>();
	}
	
	public void add(AbstractField f,String title,Object propertyId,IValidator... validators)
	{
		for (int i = 0; i < validators.length; i++)
		{
			ValidatorHolder validatorHolder = new ValidatorHolder(validators[i], f, title, propertyId);
			validatorHolders.add(validatorHolder);	
		}
	}
	




	public List<String> validate()
	{
		List<String> res = new ArrayList<>();
		for (ValidatorHolder validatorHolder : validatorHolders)
		{
			res.addAll(validatorHolder.validate());
		}
		return res;
	}

	public String getTitle(AbstractField f)
	{
		return validatorHolders.stream().filter(e->e.getField()==f).map(e->e.getTitle()).findAny().orElse(null);
	}

}
