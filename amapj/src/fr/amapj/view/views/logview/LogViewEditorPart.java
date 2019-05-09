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
 package fr.amapj.view.views.logview;

import com.rits.cloning.Cloner;
import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.logview.LogViewDTO;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;

/**
 * Permet uniquement de creer des contrats
 * 
 *
 */
public class LogViewEditorPart extends FormPopup
{
	private LogViewDTO producteurDTO;
	
	private LogViewDTO p;

	/**
	 * 
	 */
	public LogViewEditorPart(LogViewDTO p)
	{
		this.p = p;
		this.producteurDTO = new Cloner().deepClone(p);
		setWidth(80);	
		popupTitle = "Choix des paramètres de la requete";
		item = new BeanItem<LogViewDTO>(this.producteurDTO);

	}
	
	@Override
	protected void addFields()
	{		
		// 
		addTextField("Nom de l'instance", "dbName");
		
		//
		addComboEnumField("Connecté", "status");
		
		//
		addComboEnumField("User ou Deamon", "typLog");
			
		//
		addDateField("Date mini", "dateMin");
		
		addDateField("Date maxi", "dateMax");
		
		addIntegerField("Nombre d'erreurs", "nbError");
		
		// 
		addTextField("Nom de l'utilisateur", "nom");
		
		//
		addTextField("Ip", "ip");
		
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		Cloner cloner=new Cloner();		
		cloner.copyPropertiesOfInheritedClass(producteurDTO, p);	
	}
}
