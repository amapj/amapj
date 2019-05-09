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

import java.util.Date;
import java.util.List;

import fr.amapj.model.models.acces.RoleList;
import fr.amapj.view.engine.ui.AppConfiguration;
import fr.amapj.view.views.common.contratselector.ContratSelectorSessionInfo;


/**
 * Parametres attachés à une session, après le login de l'utilisateur 
 * 
 * Attention : une session est ici dans le sens de une UI
 * 
 * Il est tout a fait possible d'avoir deux fenetres differentes dans le meme navigateur
 * et d'etre logué sous deux noms differents
 *
 */
public class SessionParameters
{
	
	public SessionParameters()
	{
		
	}
	public Long userId;
	
	// Liste des rôles de la session
	public List<RoleList> userRole;
	
	public String userNom;
	
	public String userPrenom;
	
	public String userEmail;
	
	public Date dateConnexion;
	
	public Long logId;
	
	public boolean isSudo;
	
	public ContratSelectorSessionInfo contratSelectorSessionInfo = new ContratSelectorSessionInfo();
	
	// Nom du fichier qui contiendra les logs
	public String logFileName;
	
	// Nombre d'erreurs vues par l'utilisateur
	private int nbError = 0;
	
	public void incNbError()
	{
		nbError++;
	}
	
	public int getNbError()
	{
		return nbError;
	}
	
	/**
	 * L'utilisateur est admin full si 
	 * -> la configuration l'autorise 
	 * OU
	 * -> il vient en sudo
	 */
	public boolean isAdminFull()
	{
		return AppConfiguration.getConf().isAdminFull() || isSudo;
	}
	
	
}
