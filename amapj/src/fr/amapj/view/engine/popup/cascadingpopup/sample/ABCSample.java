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
 package fr.amapj.view.engine.popup.cascadingpopup.sample;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.cascadingpopup.CInfo;
import fr.amapj.view.engine.popup.cascadingpopup.CascadingPopup;

public class ABCSample
{
	ABCData data = new ABCData();
	
	public void doIt(PopupListener listener)
	{
		CascadingPopup cascading = new CascadingPopup(listener,data);
		
		CInfo info = new CInfo();
		info.popup = new APopup(data);
		info.onSuccess = ()->successOfA();
		
		cascading.start(info);
		
	}

	private CInfo  successOfA()
	{
		if (data.choix==1)
		{
			CInfo info = new CInfo();
			info.popup = new APopup(data);
			info.onSuccess = ()->successOfA();
			
			return info;
		}
		
		if (data.choix==2)
		{
			CInfo info = new CInfo();
			info.popup = new BPopup(data);
			info.onSuccess = ()->successOfB();
			
			return info;
		}
		
		if (data.choix==3)
		{
			CInfo info = new CInfo();
			info.popup = new CPopup(data);
			info.onSuccess = ()->successOfC();
			
			return info;
		}
		
		
		return null;
	}
	
	
	private CInfo  successOfB()
	{
		if (data.choix==1)
		{
			CInfo info = new CInfo();
			info.popup = new APopup(data);
			info.onSuccess = ()->successOfA();
			
			return info;
		}
		
		if (data.choix==2)
		{
			CInfo info = new CInfo();
			info.popup = new BPopup(data);
			info.onSuccess = ()->successOfB();
			
			return info;
		}
		
		if (data.choix==3)
		{
			CInfo info = new CInfo();
			info.popup = new CPopup(data);
			info.onSuccess = ()->successOfC();
			
			return info;
		}
		
		
		return null;
	}
	
	
	private CInfo  successOfC()
	{
		if (data.choix==1)
		{
			CInfo info = new CInfo();
			info.popup = new APopup(data);
			info.onSuccess = ()->successOfA();
			
			return info;
		}
		
		if (data.choix==2)
		{
			CInfo info = new CInfo();
			info.popup = new BPopup(data);
			info.onSuccess = ()->successOfB();
			
			return info;
		}
		
		if (data.choix==3)
		{
			CInfo info = new CInfo();
			info.popup = new CPopup(data);
			info.onSuccess = ()->successOfC();
			
			return info;
		}
		
		
		return null;
	}
	
	
}
