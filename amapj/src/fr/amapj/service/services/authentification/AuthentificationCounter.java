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
 package fr.amapj.service.services.authentification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.amapj.common.DateUtils;


/**
 * Permet de compter les authentifications invalides 
 *
 */
public class AuthentificationCounter
{
	static public class Info
	{
		Date date;
		int unknowLogin;
		int badPassword;
	}
	
	private List<Info> infos;
	
	public AuthentificationCounter()
	{
		infos = new ArrayList<AuthentificationCounter.Info>();
	}
	
	
	synchronized public void addUnknow()
	{
		Date d = DateUtils.getDateWithNoTime();
		Info info = findInfo(d);
		info.unknowLogin++;
	}
	
	
	synchronized public void addBadPassword()
	{
		Date d = DateUtils.getDateWithNoTime();
		Info info = findInfo(d);
		info.badPassword++;
	}

	
	private Info findInfo(Date d)
	{
		for (Info info : infos)
		{
			if (info.date.equals(d))
			{
				return info;
			}
		}
		
		Info info = new Info();
		info.date = d;
		
		infos.add(info);
		
		return info;
	}

	
	synchronized public String getLastInfo()
	{
		// tri par date décroissante 
		Collections.sort(infos, (d1,d2)->d2.date.compareTo(d1.date));
		
		// 
		if (infos.size()==0)
		{
			return "No info";
		}
		
		Info info = infos.get(0);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return "Date :"+df.format(info.date)+" Erreur mot de passe:"+info.badPassword+" Erreur login:"+info.unknowLogin;
		
	}
	
	
	synchronized public String getAllInfos()
	{
		// tri par date décroissante 
		Collections.sort(infos, (d1,d2)->d2.date.compareTo(d1.date));
		
		// 
		if (infos.size()==0)
		{
			return "No info";
		}
		
		StringBuffer buf = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		for (Info info : infos)
		{
			buf.append("Date :"+df.format(info.date)+" Erreur mot de passe:"+info.badPassword+" Erreur login:"+info.unknowLogin+"<br/>");
		}
		
		return buf.toString(); 
		
	}

	
}
