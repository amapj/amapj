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
 package fr.amapj.view.views.parametres.paramecran;

import com.vaadin.data.util.BeanItem;

import fr.amapj.model.models.acces.RoleList;
import fr.amapj.model.models.param.paramecran.PEListeAdherent;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Permet la saisie des paramètres de l'écran "liste des adhérents"
 * 
 *
 */
public class PEListeAdherentEditorPart extends WizardFormPopup
{

	private PEListeAdherent pe;

	public enum Step
	{
		GENERAL ;
	}

	/**
	 * 
	 */
	public PEListeAdherentEditorPart()
	{
		pe = (PEListeAdherent) new ParametresService().loadParamEcran(MenuList.LISTE_ADHERENTS);
		
		setWidth(80);
		popupTitle = "Paramètrage de l'écran \""+pe.getMenu().getTitle()+"\"";
		
		item = new BeanItem<PEListeAdherent>(this.pe);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
	}

	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les droits d'accès sur cet écran");
		
		RoleList[] enumsToExclude = new RoleList[] { RoleList.MASTER };
		
		
		addComboEnumField("L'écran en entier est visible par ", "canAccessEcran", enumsToExclude,new NotNullValidator());
		
		
		addComboEnumField("La colonne e-mail est visible par ", "canAccessEmail", enumsToExclude,new NotNullValidator());
		
		addComboEnumField("La colonne Tel1 est visible par ", "canAccessTel1", enumsToExclude,new NotNullValidator());
		
		addComboEnumField("La colonne Tel2 est visible par ", "canAccessTel2", enumsToExclude,new NotNullValidator());
		
		addComboEnumField("Les 3 colonnes Adr, Ville, CodePostal sont visibles par ", "canAccessAdress", enumsToExclude,new NotNullValidator());
		
	
	}


	

	@Override
	protected void performSauvegarder()
	{
		new ParametresService().update(pe);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
