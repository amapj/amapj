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

import fr.amapj.service.services.appinstance.AppInstanceService;
import fr.amapj.view.engine.popup.copypopup.CopyPopup;
import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;

/**
 *  
 */
public class ChoixAppInstance extends SwitchPopup
{
	
	public ChoixAppInstance()
	{
		popupTitle = "Autres actions sur les instances";
		setWidth(60);
		
	}

	@Override
	protected void loadFollowingPopups()
	{
		line1 = "Veuillez indiquer ce que vous souhaitez faire :";

		addLine("Extraire les mails de tous les administrateurs", new CopyPopup("Mails des administrateurs", ()->new AppInstanceService().getAllMails()));
		
		addLine("Extraire les mails de tous les administrateurs + tresoriers + stats", new CopyPopup("Mails admin + stats", ()->new AppInstanceService().getStatInfo()));
		
	}

}
