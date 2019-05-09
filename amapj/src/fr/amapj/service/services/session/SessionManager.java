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
 package fr.amapj.service.services.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

import fr.amapj.model.engine.transaction.DataBaseInfo;
import fr.amapj.model.models.acces.RoleList;
import fr.amapj.service.services.appinstance.LogAccessDTO;
import fr.amapj.service.services.suiviacces.ConnectedUserDTO;

/**
 * Contient la liste de toutes les sessions (dans le sens UI)
 *  
 *
 */
public class SessionManager
{

	private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<BroadcastListener>();

	public static void register(BroadcastListener listener)
	{
		listeners.add(listener);
	}

	public static void unregister(BroadcastListener listener)
	{
		listeners.remove(listener);
	}

	public static void broadcast(final String message)
	{
		for (BroadcastListener listener : listeners)
		{
			listener.receiveBroadcast(message);
		}
	}
	
	/**
	 * Retourne le nombre d'erreur en fouillant dans lal iste des sessions actives
	 * @param dto
	 * @return
	 */
	public static int getNbError(LogAccessDTO dto)
	{
		for (BroadcastListener listener : listeners)
		{
			UI ui = (UI) listener;
			
			if (ui.getData()!=null)
			{	
				SessionParameters params = ((SessionData) ui.getData()).sessionParameters;	
				if (params!=null)
				{
					if (params.logId == dto.id)
					{
						return params.getNbError();
					}
				}
			}
		}
		return 0;
	}
	

	public static List<ConnectedUserDTO> getAllConnectedUser()
	{
		List<ConnectedUserDTO> us = new ArrayList<ConnectedUserDTO>();
		for (BroadcastListener listener : listeners)
		{
			ConnectedUserDTO u = new ConnectedUserDTO();
			UI ui = (UI) listener;
			
			u.agent = getAgentName(ui);
			
			if (ui.getData()!=null)
			{
				u.dbName = (((SessionData) ui.getData()).dataBaseInfo).getDbName();
				
				SessionParameters params = ((SessionData) ui.getData()).sessionParameters;
				
				if (params!=null)
				{
					u.nom = params.userNom;
					u.prenom = params.userPrenom;
					u.email = params.userEmail;
					u.date = params.dateConnexion;
					u.isLogged = true;
				}
				else
				{
					// Concerne les personnes sur la page de login
					u.nom = "--";
					u.prenom = "--";
					u.isLogged = false;
				}
			}
			
			us.add(u);
		}
		return us;
	}
	
	
	/**
	 * Permet d'obtenir le nom du navigateur en clair
	 */
	public static String getAgentName(UI ui)
	{
		if (ui.getSession()==null)
		{
			return "Session null";
		}
		
		
		WebBrowser browser = ui.getPage().getWebBrowser();
		if (browser.isChrome())
		{
			return "Chrome "+browser.getBrowserMajorVersion();
		}
		else if (browser.isFirefox())
		{
			return "Firefox "+browser.getBrowserMajorVersion();
		}
		else if (browser.isIE())
		{
			return "IE "+browser.getBrowserMajorVersion();
		}
		else if (browser.isOpera())
		{
			return "Opera "+browser.getBrowserMajorVersion();
		}
		else if (browser.isSafari())
		{
			return "Safari "+browser.getBrowserMajorVersion();
		}
		else if (browser.isEdge())
		{
			return "Edge "+browser.getBrowserMajorVersion();
		}
		else
		{
			return "XX";
		}
	}



	public interface BroadcastListener
	{
		public void receiveBroadcast(String message);
	}
	
	
	
	static public SessionParameters getSessionParameters()
	{
		return getSessionData().sessionParameters;
	}

	static public void setSessionParameters(SessionParameters param)
	{
		getSessionData().sessionParameters = param;
	}
	
	static public void initSessionData(DataBaseInfo dataBaseInfo)
	{
		SessionData data = new SessionData();
		data.dataBaseInfo = dataBaseInfo;
		UI.getCurrent().setData(data);
	}
	
	static private SessionData getSessionData()
	{
		return ((SessionData) UI.getCurrent().getData());
	}
	
	static public boolean canDisconnect()
	{
		return getSessionData()!=null && getSessionParameters()!=null;
	}
	
	
	
	
	
	static public Long getUserId()
	{
		return getSessionParameters().userId;
	}
	
	static public List<RoleList> getUserRoles()
	{
		return getSessionParameters().userRole;
	}
	
	
	static public DataBaseInfo getDb()
	{
		return getSessionData().dataBaseInfo;
	}
	
	
	
}