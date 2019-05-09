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
 package fr.amapj.view.engine.ui;

import fr.amapj.model.models.param.ChoixOuiNon;

/**
 * Paramètres TEMPORAIRES  de configuration de l'application
 * 
 */
public class AppTempConfiguration
{

	static private AppTempConfiguration mainInstance;

	static public AppTempConfiguration getTempConf()
	{
		if (mainInstance == null)
		{
			mainInstance = new AppTempConfiguration();
		}
		return mainInstance;
	}

	
	// 
	private ChoixOuiNon effacerFichierTempPDF = ChoixOuiNon.OUI;

	
	public AppTempConfiguration()
	{
		
	}

	public ChoixOuiNon getEffacerFichierTempPDF()
	{
		return effacerFichierTempPDF;
	}


	public void setEffacerFichierTempPDF(ChoixOuiNon effacerFichierTempPDF)
	{
		this.effacerFichierTempPDF = effacerFichierTempPDF;
	}


	

}
