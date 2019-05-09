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


/**
 * équivalent à WizardFormPopup mais avec une seule page
 *  
 */
abstract public class FormPopup extends WizardFormPopup
{
	
	/**
	 * Utilisé dans le cas ou il y a une seule page
	 */
	private enum DefaultStep
	{
		STEP1;
	}
	
	protected Class getEnumClass()
	{
		return DefaultStep.class;
	}
	
	@Override
	protected void configure()
	{
		add(DefaultStep.STEP1,()->addFields(),()->checkScreen());
	}

	abstract protected void addFields();
	
	/**
	 * Peut etre surhcargé au besoin par la classe fille
	 * Permet de faire un controle global des données saisies avant l'appel à handleSauvegarder
	 * 
	 * @return le message d'erreur eventuel à afficher , null si tout est OK
	 */
	protected String checkScreen()
	{
		return null;
	}
	
	
	
	
	
	
}
