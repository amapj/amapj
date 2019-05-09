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
 package fr.amapj.view.views.permanence.detailperiode;

import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;
import fr.amapj.view.views.permanence.detailperiode.mail.PopupEnvoiMailPlanningPermanence;

/**
 * Permet de choisir son action
 */
public class ChoixAutreAction extends SwitchPopup
{
	
	private Long idPeriodePermanence;
	
	/**
	 * 
	 */
	public ChoixAutreAction(Long idPeriodePermanence)
	{
		popupTitle = "Choix de l'action";
		setWidth(50);
		this.idPeriodePermanence = idPeriodePermanence;
		
	}

	@Override
	protected void loadFollowingPopups()
	{
		
		line1 = "Veuillez indiquer l'action que vous souhaitez réaliser";
		
		addLine("Effacer les inscriptions sur une liste de date", new PopupDeleteInscriptionPermanence(idPeriodePermanence));	
		addLine("Calculer automatiquement les inscriptions", new PopupCalculAutoPermanence(idPeriodePermanence));
		addLine("Envoyer un mail à tous les participants avec le planning joint", new PopupEnvoiMailPlanningPermanence(idPeriodePermanence));

	}
	
	

}
