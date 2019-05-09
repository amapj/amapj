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
 package fr.amapj.view.engine.notification;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class NotificationHelper
{
	
	static public void displayNotification(String message)
	{
		
		Notification notification = new Notification("Erreur",message,Type.ERROR_MESSAGE,true);
		notification.setPosition(Position.TOP_CENTER);
		notification.setDelayMsec(1000);
		notification.show(Page.getCurrent());
	}
	
	
	

	static public void displayNotificationQte()
	{
		displayNotification("Erreur dans la saisie d'une quantité<br/>Merci de corriger les cases rouges");
		
	}
	
	
	static public void displayNotificationMontant()
	{
		displayNotification("Erreur dans la saisie d'un montant<br/>Merci de corriger les cases rouges");
	}
	
	
	
}
