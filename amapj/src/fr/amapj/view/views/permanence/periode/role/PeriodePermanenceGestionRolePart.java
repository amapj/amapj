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
 package fr.amapj.view.views.permanence.periode.role;

import fr.amapj.service.services.permanence.periode.role.UpdateRoleService;
import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;
import fr.amapj.view.views.permanence.periode.grille.ModifierPeriodePermanenceGrillePart;
import fr.amapj.view.views.permanence.periode.update.PopupAddDateForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupAddUtilisateurForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupDeleteDateForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupDeleteUtilisateurForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupModifEnteteForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupUpdateNbParticipationForPeriodePermanence;
import fr.amapj.view.views.permanence.periode.update.PopupUpdateAllRole;

/**
 * Permet de choisir son action 
 */
public class PeriodePermanenceGestionRolePart extends SwitchPopup
{
	
	private Long idPeriodePermanence;

	/**
	 * 
	 */
	public PeriodePermanenceGestionRolePart(Long idPeriodePermanence)
	{
		popupTitle = "Gestion des rôles";
		setWidth(50);
		this.idPeriodePermanence = idPeriodePermanence;

	}

	@Override
	protected void loadFollowingPopups()
	{
		boolean hasRole = new UpdateRoleService().hasGestionRole();
		
		if (hasRole==false)
		{
			header = "La gestion des rôles permet d'attribuer un rôle pour chaque place de permanence.<br/><br/> "
					+ "Par exemple, dans une permanence avec 3 personnes, on va pouvoir indiquer "
					+ "que la personne 1 occupe le rôle \"Pain\", que la personne 2 occupe le rôle \"Légumes\" et que la personne 3 occupe le rôle \"Fromages\".<br/><br/>"
					+ "Si vous voulez utiliser les rôles, vous devez d'abord définir la liste des rôles en allant dans \"PERMANENCE\\Rôles de permanence\"";
			
			line1 = "";
			
			return;
		}
		
		
		
		line1 = "Veuillez indiquer ce que vous souhaitez faire :";

		
		
	}

}
