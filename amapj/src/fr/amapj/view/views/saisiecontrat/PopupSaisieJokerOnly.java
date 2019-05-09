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
 package fr.amapj.view.views.saisiecontrat;

import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.views.saisiecontrat.ContratAboManager.ContratAbo;

/**
 * Popup pour la gestion seule des jokers
 * (le contrat n'est plus modifiable, on ne peut plus que modifier les jokers) 
 */
public class PopupSaisieJokerOnly extends PopupSaisieJoker
{
	// 
	private ContratAbo abo;
	
	private ContratDTO contratDTO;
		
	/** 
	 */
	public PopupSaisieJokerOnly(ContratAbo abo,ContratDTO contratDTO)
	{
		super(abo, contratDTO, false);
		this.abo = abo;
		this.contratDTO = contratDTO;
			
	}

	@Override
	protected boolean performSauvegarder()
	{
		//
		super.performSauvegarder();
		
		// On met à jour les quantités du contrat
		contratDTO.qte = new ContratAboManager().extractQte(abo, contratDTO);
			
		// On sauvegarde en base
		try
		{
			new MesContratsService().saveNewContrat(contratDTO, SessionManager.getUserId());
		}
		catch (OnSaveException e1)
		{
			String msg = "Sauvegarde impossible:"+e1.getMessage();
			MessagePopup p = new MessagePopup("Impossible de continuer",ContentMode.HTML,ColorStyle.RED,msg);
			MessagePopup.open(p);
			return false;
		}

		return true;
	}

	
}
