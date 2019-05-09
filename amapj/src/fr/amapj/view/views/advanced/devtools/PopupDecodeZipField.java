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
 package fr.amapj.view.views.advanced.devtools;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import fr.amapj.common.GzipUtils;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet de modifier les paramètres mineurs des modeles de contrat
 * 
 *
 */
public class PopupDecodeZipField extends WizardFormPopup
{
	
	private TextField tf;
	

	public enum Step
	{
		SAISIE,AFFICHAGE;
	}

	/**
	 * 
	 */
	public PopupDecodeZipField()
	{		
		setWidth(80);
		popupTitle = "Décodage d'un champ zippé de la base";
				
	}
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE,()->addFieldSaisie());
		add(Step.AFFICHAGE,()->addFieldAffichage());
	}

	

	private void addFieldSaisie()
	{	
		setStepTitle("Saisie");
				
		tf = new TextField("Contenu à décoder");
		tf.setWidth("90%");
		form.addComponent(tf);
	}
	
	private void addFieldAffichage()
	{	
		setStepTitle("Résultat");
				
		String str = tf.getValue();
		str = GzipUtils.uncompress(str);
		
		
		TextArea textArea = new TextArea();
		textArea.setHeight(8, Unit.CM);
		textArea.setValue(str);
		textArea.setWidth("90%");
		form.addComponent(textArea);
		
	}


	@Override
	protected void performSauvegarder()
	{
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
