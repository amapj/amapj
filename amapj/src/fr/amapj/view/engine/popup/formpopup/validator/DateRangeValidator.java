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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.AbstractField;

import fr.amapj.common.DateUtils;


/**
 * Permet de valider que la date appartient bien à un intervalle donnée
 * 
 * Les bornes sont considérées comme des valeurs correctes
 * 
 * La valeur null est considérée comme correcte
 *
 */
public class DateRangeValidator implements IValidator
{
	
	private Date dateMin = null;

    private Date dateMax = null;

    
    /**
     * 
     */
	public DateRangeValidator(Date dateMin,Date dateMax)
	{
		super();
		this.dateMin = dateMin;
		this.dateMax = dateMax;
	}




	@Override
	public void performValidate(Object value,ValidatorHolder 	a)
	{
		if (value==null)
		{
			return;
		}

		Date val = DateUtils.suppressTime((Date) value);
				
	     if (dateMin!=null && val.before(DateUtils.suppressTime(dateMin)))
	     {
	    	 SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
	    	 a.addMessage("La date \""+a.title+"\" est trop petite. Elle doit être égale ou après  le "+df.format(dateMin));
	     }
	    		 
	     if (dateMax!=null && val.after(DateUtils.suppressTime(dateMax)))
	     {
	    	 SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
	    	 a.addMessage("La date \""+a.title+"\" est trop grande. Elle doit être égale ou avant le "+df.format(dateMax));
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
