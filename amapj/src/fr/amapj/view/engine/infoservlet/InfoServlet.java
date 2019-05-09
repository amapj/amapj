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
 package fr.amapj.view.engine.infoservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import fr.amapj.common.CollectionUtils;
import fr.amapj.model.engine.tools.SpecificDbUtils;
import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;

/**
 * Servlet pour fournir des informations générales
 * 
 */
public class InfoServlet extends HttpServlet 
{
	
	public enum InfoType
	{
		MONITOR , 
		LISTE_AMAP ,
		MAINTENANCE
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{
		InfoType type = getInfoType(req.getPathInfo());
		
		switch (type)
		{
		case MONITOR:
			doMonitor(res);
			break;
		
		case LISTE_AMAP:
			doListeAmap(res);
			break;

		case MAINTENANCE:
			doMaintenance(res);
			break;
			
		default:
			// Do nothing 
			break;
		}	
	}



	private InfoType getInfoType(String pathInfo)
	{
		if (pathInfo==null)
		{
			return null;
		}
		
		if (pathInfo.startsWith("/monitor"))
		{
			return InfoType.MONITOR;
		}
		
		if (pathInfo.startsWith("/liste-amaps"))
		{
			return InfoType.LISTE_AMAP;
		}
		
		if (pathInfo.startsWith("/maintenance"))
		{
			return InfoType.MAINTENANCE;
		}
		
		return null;
		
	}

	/**
	 * Permet le monitoring des différentes valeurs du serveur
	 *  
	 * @param res
	 * @throws IOException 
	 */
	private void doMonitor(HttpServletResponse res) throws IOException
	{
		res.setContentType("application/json");
		
		MonitorInfo info =  MonitorInfo.calculateMonitorInfo();
		Gson gson = new Gson();
		
		PrintWriter out = res.getWriter();
		out.println(gson.toJson(info));
	}
	
	
	/**
	 * Permet d'afficher la liste des amaps presentes sur cette instance  
	 *  
	 * @param res
	 * @throws IOException 
	 */
	private void doListeAmap(HttpServletResponse res) throws IOException
	{
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		out.write("<html><head><title></title></head><body>");
		
		out.write("<h1>Liste des AMAPs</h1>");
		out.write(getAllInstances());
		out.write("</body></html>");
	}
	
	
	
	/**
	 *  
	 * @return
	 */
	public String getAllInstances()
	{
		List<String> strs = new ArrayList<String>();
		
		SpecificDbUtils.executeInAllDb(()->appendInfos(strs),true);
		
		// On supprime la base master
		strs.remove(0);
		
		return CollectionUtils.asString(strs, "");
	}
	
	
	private Void appendInfos(List<String> strs)
	{
		ParametresDTO dto = new ParametresService().getParametres();
		String str = "<b>"+dto.nomAmap+"</b><br/>"+dto.villeAmap+"<br/><a target=\"_blank\" href=\""+dto.url+"\">"+dto.url+"</a><br/><br/><br/>";
		strs.add(str);
		return null;
	}

	
	private void doMaintenance(HttpServletResponse res) throws IOException
	{
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		
		out.write("<html><head><title></title></head><body>");
		
		out.write("<h1>Application non disponible</h1>");
		out.write("<p>Désolé, mais l'application est en cours de maintenance. Merci de vous reconnecter plus tard.</p>");
		out.write("</body></html>");
		
	}
	
	

}
