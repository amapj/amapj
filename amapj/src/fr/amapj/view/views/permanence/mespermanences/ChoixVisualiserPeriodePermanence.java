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
 package fr.amapj.view.views.permanence.mespermanences;

import java.util.List;

import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.periode.SmallPeriodePermanenceDTO;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;
import fr.amapj.view.views.permanence.periode.grille.VisualiserPeriodePermanenceGrillePart;

/**
 * Permet de choisir sa periode de permanence à visualiser 
 */
public class ChoixVisualiserPeriodePermanence extends SwitchPopup
{
	
	/**
	 * 
	 */
	public ChoixVisualiserPeriodePermanence()
	{
		popupTitle = "Choix de la période de permanence à visualiser";
		setWidth(50);
		
	}

	@Override
	protected void loadFollowingPopups()
	{
		List<SmallPeriodePermanenceDTO> dtos = new MesPermanencesService().getAllPeriodeInFuture();
		
		
		line1 = "Veuillez indiquer la période de permanence que vous souhaitez visualiser :";
		
		for (SmallPeriodePermanenceDTO dto : dtos)
		{
			// TODO orenter vers SmallInscriptionPermanence dans le cas d'un telephone (petit ecran) 
			
			VisualiserPeriodePermanenceGrillePart part  = new VisualiserPeriodePermanenceGrillePart(dto.id,SessionManager.getUserId());	
			addLine("Planning permanence "+dto.nom, part);	
		}

	}

}
