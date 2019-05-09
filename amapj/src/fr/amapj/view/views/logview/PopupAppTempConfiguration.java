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

import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.ui.AppTempConfiguration;

/**
 * Permet uniquement de creer des contrats
 * 
 *
 */
public class PopupAppTempConfiguration extends FormPopup
{	

	private AppTempConfiguration dto;
	
	/**
	 * 
	 */
	public PopupAppTempConfiguration()
	{
		setWidth(80);	
		popupTitle = "Définition de la configuration temporaire";
		
		this.dto = new Cloner().deepClone(AppTempConfiguration.getTempConf());
		
		item = new BeanItem<AppTempConfiguration>(dto);

	}
	
	@Override
	protected void addFields()
	{				
		//
		addComboEnumField("Effacer les fichiers temporaires pour la génération des PDF", "effacerFichierTempPDF", new NotNullValidator());
				
	}



	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		Cloner cloner=new Cloner();		
		cloner.copyPropertiesOfInheritedClass(dto, AppTempConfiguration.getTempConf());	
	}
}
