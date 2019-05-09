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
 package fr.amapj.view.views.permanence.permanencerole;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.TextArea;

import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.service.services.permanence.role.PermanenceRoleDTO;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.popup.formpopup.validator.UniqueInDatabaseValidator;

/**
 * La fiche rôle de permanence 
 * 
 *
 */
public class PermanenceRoleEditorPart extends WizardFormPopup
{

	private PermanenceRoleDTO dto;

	private boolean create;

	public enum Step
	{
		GENERAL ;
	}

	/**
	 * 
	 */
	public PermanenceRoleEditorPart(boolean create,PermanenceRoleDTO p)
	{
		this.create = create;
		
		setWidth(80);
		setHeight("90%");
		
		if (create)
		{
			popupTitle = "Création d'un rôle";
			this.dto = new PermanenceRoleDTO();
			this.dto.defaultRole = false;
			
		}
		else
		{
			popupTitle = "Modification d'un rôle";
			this.dto = p;
		}	
		
	
		
		item = new BeanItem<PermanenceRoleDTO>(this.dto);

	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
	}

	
	@Override
	protected String checkInitialCondition()
	{
		if (dto.defaultRole==true)
		{
			return "Vous ne pouvez pas modifier ce role, car c'est le role par défaut";
		}
		return null;
	}
	
	
	private void addFieldGeneral()
	{
		// Titre
		setStepTitle("les informations générales");
		
		// Champ 1
		IValidator uniq = new UniqueInDatabaseValidator(PermanenceRole.class,"nom",dto.id);
		IValidator notNull = new NotNullValidator();
		addTextField("Nom", "nom",uniq,notNull);
		
		TextArea f =  addTextAeraField("Description", "description");
		f.setMaxLength(20480);
		f.setHeight(5, Unit.CM);

	}
	
	
	
	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		new PermanenceRoleService().update(dto, create);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
