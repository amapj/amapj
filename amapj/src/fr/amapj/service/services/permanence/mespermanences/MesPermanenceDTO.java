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
 package fr.amapj.service.services.permanence.mespermanences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.amapj.model.models.permanence.periode.EtatPeriodePermanence;
import fr.amapj.model.models.permanence.periode.NaturePeriodePermanence;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.view.engine.tools.TableItem;

/**
 * Description d'une periode de permanence
 *
 */
public class MesPermanenceDTO 
{
	// Listes des periodes de permanences où l'utilisateur devra s'inscrire 
	public List<UnePeriodePermanenceDTO> mesPeriodesPermanences = new ArrayList<UnePeriodePermanenceDTO>();
	
	
	// Liste des permanences que l'utilisateur doit faire 
	public List<PeriodePermanenceDateDTO> mesPermanencesFutures = new ArrayList<PeriodePermanenceDateDTO>();
	
}
