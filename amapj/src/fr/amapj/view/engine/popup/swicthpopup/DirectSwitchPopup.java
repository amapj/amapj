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
 package fr.amapj.view.engine.popup.swicthpopup;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Permet de créer un switch popup pour un affichage direct (mode simple)
 *
 */
public class DirectSwitchPopup
{

	static private class Internal extends SwitchPopup
	{
		public Internal(String title, int width)
		{
			super();
			popupTitle = title;
			setWidth(width);
		}


		@Override
		protected void loadFollowingPopups()
		{
			// Do nothing
			
		}
	}
	

	private Internal switchPopup;

	public DirectSwitchPopup(String title,int width)
	{
		this.switchPopup = new Internal(title, width);
	}
	
	
	public void setLine1(String line1)
	{
		switchPopup.line1 = line1;
	}
	
	public void addLine(String lib,CorePopup popup)
	{
		switchPopup.addLine(lib, popup);
	}
	
	
	public void addSeparator()
	{
		switchPopup.addSeparator();
	}
	
	/**
	 * Permet l'ouverture du switch popup
	 */
	public void open(PopupListener listener)
	{
		CorePopup.open(switchPopup, listener);
	}
	
	
}
