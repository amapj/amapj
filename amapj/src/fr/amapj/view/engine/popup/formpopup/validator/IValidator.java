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



public interface IValidator
{
	
	/**
	 * Permet de valider la valeur du champ
	 * Le champ value contient la valeur non convertie
	 */
	public void performValidate(Object value,ValidatorHolder 	a);
	
	/**
	 * @return true si ce validator peut être utilisé en même temps que la saisie par l'utilisateur
	 * 
	 * Cela permet d'effacer l'erreur des que l'utilisateur a fait sa saisie
	 */
	public boolean canCheckOnFly();
	
	
	/**
	 * Retourne la liste des champs liés. Si un de ces champs est modifié
	 * alors le champ courant est revalidé  
	 * 
	 * @return
	 */
	public AbstractField[] revalidateOnChangeOf();

}
