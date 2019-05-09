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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;


public class ValidatorHolder
{
	protected AbstractField f;
	protected String title;
	protected Object propertyId;
	
	protected List<String> errorMessage = new ArrayList<>();
	
	private IValidator validator;
	
		
	public ValidatorHolder(IValidator validator,AbstractField f,String title,Object propertyId)
	{
		this.validator = validator;
		
		this.f = f;
		this.title = title;
		this.propertyId = propertyId;
	}
	

	
	public void addMessage(String msg)
	{
		errorMessage.add(msg);
	}
	
	
	/**
	 * Retourne une liste vide si tout est ok, sinon retourne une liste de message d'erreur
	 * @return
	 */
	public List<String> validate()
	{
		errorMessage = new ArrayList<>();
		
		Object value = f.getValue();
		
		validator.performValidate(value,this);
		
		if (errorMessage.size()>0)
		{
			String str = computeHtml();
			
			f.setComponentError(new UserError(str,ContentMode.HTML,ErrorLevel.ERROR));
			
			if (validator.canCheckOnFly())
			{
				f.addValueChangeListener(e -> handleValueChange());
				AbstractField[] fields = validator.revalidateOnChangeOf();
				if (fields != null)
				{
					for (AbstractField field : fields)
					{
						field.addValueChangeListener(e -> handleValueChange());
					}
				}
			}
		}
		
		return errorMessage;
	}
	
	
	
	

	private void handleValueChange()
	{
		errorMessage = new ArrayList<>();
		
		Object value = f.getValue();
		
		validator.performValidate(value,this);
		
		// Si l'erreur a disparu
		if (errorMessage.size()==0)
		{
			// On efface l'erreur
			f.setComponentError(null);	
		}
		// Sinon on met à jour l'erreur
		else
		{
			String str = computeHtml();
			
			f.setComponentError(new UserError(str,ContentMode.HTML,ErrorLevel.ERROR));
		}
	}
	
	private String computeHtml()
	{
		StringBuffer buf = new StringBuffer();
		for (String msg : errorMessage)
		{
			buf.append("<big>");
			buf.append(msg);
			buf.append("</big>");
			buf.append("<br/>");
		}

		return buf.toString();
	}



	public AbstractField getField()
	{
		return f;
	}

	public String getTitle()
	{
		return title;
	}
	
	

}
