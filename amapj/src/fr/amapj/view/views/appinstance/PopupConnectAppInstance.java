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
 package fr.amapj.view.views.appinstance;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;

import fr.amapj.service.engine.sudo.SudoManager;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppInstanceService;
import fr.amapj.service.services.appinstance.ChoixSudoUtilisateurDTO;
import fr.amapj.service.services.appinstance.SudoUtilisateurDTO;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet uniquement de creer des instances
 * 
 *
 */
public class PopupConnectAppInstance extends WizardFormPopup
{

	private ChoixSudoUtilisateurDTO selected;
	
	private AppInstanceDTO appInstanceDTO;
	

	public enum Step
	{
		GENERAL , CONNECTION ;
	}

	/**
	 * 
	 */
	public PopupConnectAppInstance(AppInstanceDTO dto)
	{
		
		popupTitle = "Connection en tant que ...";
		saveButtonTitle = "OK";
		this.appInstanceDTO = dto;
		
		
		// Contruction de l'item
		selected = new ChoixSudoUtilisateurDTO();
		item = new BeanItem<ChoixSudoUtilisateurDTO>(selected);
	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.CONNECTION,()->addFieldConnexion());
	}

	private void addFieldGeneral()
	{
		
		//
		List<SudoUtilisateurDTO> sudoUtilisateurDTOs = new AppInstanceService().getSudoUtilisateurDto(appInstanceDTO);
		
		// Titre
		setStepTitle("choisir son utilisateur");
		
		// Champ 1
		addGeneralComboField("Utilisateur", sudoUtilisateurDTOs,"selected",null);
		
	}
	
	

	private void addFieldConnexion()
	{
		String nomInstance = appInstanceDTO.nomInstance;
		String session = UI.getCurrent().getSession().getCsrfToken();
		
		//
		String sudoKey = SudoManager.addSudoCredential(selected.selected,nomInstance,session);
		String url = selected.selected.url+"&sudo="+sudoKey;
		
		// Titre
		setStepTitle("cliquer sur le lien si dessous pour ouvrir l'application");
		
		// Champ 1
		Link link = new Link("Lien de connexion", new ExternalResource(url));
		link.setTargetName("_blank");
		form.addComponent(link);
		
	}

	

	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		// Do nothing
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
