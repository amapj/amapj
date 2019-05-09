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
 package fr.amapj.model.models.editionspe.engagement;

import fr.amapj.model.models.editionspe.AbstractPdfEditionSpeJson;
import fr.amapj.view.views.editionspe.engagement.EngagementTemplate;

/**
 * Paramétrage de l'édition des contrats d'engagement
 *
 */
public class EngagementJson extends AbstractPdfEditionSpeJson
{
	
	// Permet le choix du template
	transient private EngagementTemplate template;
	
	// Permet le choix du modele a tester
	transient public Long idModeleContrat;


	public EngagementTemplate getTemplate()
	{
		return template;
	}

	public void setTemplate(EngagementTemplate template)
	{
		this.template = template;
	}

	public Long getIdModeleContrat()
	{
		return idModeleContrat;
	}

	public void setIdModeleContrat(Long idModeleContrat)
	{
		this.idModeleContrat = idModeleContrat;
	}

}
