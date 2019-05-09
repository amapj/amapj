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
 package fr.amapj.view.views.permanence.periode.update;

import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;

/**
 * Permet de choisir son action 
 */
public class ChoixActionModificationPeriodePermanence extends SwitchPopup
{
	
	private Long idPeriodePermanence;

	/**
	 * 
	 */
	public ChoixActionModificationPeriodePermanence(Long idPeriodePermanence)
	{
		popupTitle = "Modifications des périodes de permanence";
		setWidth(50);
		this.idPeriodePermanence = idPeriodePermanence;

	}

	@Override
	protected void loadFollowingPopups()
	{
		line1 = "Veuillez indiquer ce que vous souhaitez faire :";

		addLine("Modifier le nom, la description, la date de fin des inscriptions", new PopupModifEnteteForPeriodePermanence(idPeriodePermanence));
		addSeparator();
		
		addLine("Ajouter des dates de permanences", new PopupAddDateForPeriodePermanence(idPeriodePermanence));
		addLine("Supprimer des dates de permanences", new PopupDeleteDateForPeriodePermanence(idPeriodePermanence));
		addSeparator();
		
		addLine("Ajouter des participants", new PopupAddUtilisateurForPeriodePermanence(idPeriodePermanence));
		addLine("Supprimer des participants", new PopupDeleteUtilisateurForPeriodePermanence(idPeriodePermanence));
		addLine("Modifier le nombre de participations", new PopupUpdateNbParticipationForPeriodePermanence(idPeriodePermanence));
		addSeparator();
		
		addLine("Positionner les rôles pour toutes les dates en masse", new PopupUpdateAllRole(idPeriodePermanence));
		addSeparator();
		
		addLine("Modifier les régles d'inscriptions sur une date", new PopupRegleInscriptionPeriodePermanence(idPeriodePermanence));
	}

}
