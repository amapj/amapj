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
 package fr.amapj.model.models.param.paramecran.common;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.param.paramecran.PEListeAdherent;
import fr.amapj.model.models.param.paramecran.PELivraisonAmapien;
import fr.amapj.model.models.param.paramecran.PELivraisonProducteur;
import fr.amapj.model.models.param.paramecran.PEMesContrats;
import fr.amapj.model.models.param.paramecran.PEMesLivraisons;
import fr.amapj.model.models.param.paramecran.PEReceptionCheque;
import fr.amapj.model.models.param.paramecran.PESaisiePaiement;
import fr.amapj.model.models.param.paramecran.PESyntheseMultiContrat;
import fr.amapj.view.engine.menu.MenuList;

/**
 * Cette classe permet de stocker la correspondance entre l'écran 
 * et la classe stockant les paramètres 
 */

public class ParamEcranInfo
{
	public static Class findClazz(MenuList menu)
	{
		
		switch (menu)
		{
		case MES_CONTRATS:
			return PEMesContrats.class;
		
		case MES_LIVRAISONS:
			return PEMesLivraisons.class;
			
		case LISTE_ADHERENTS:
			return PEListeAdherent.class;
			
		case LIVRAISONS_PRODUCTEUR:
			return PELivraisonProducteur.class;
			
		case LIVRAISON_AMAPIEN:
			return PELivraisonAmapien.class;
			
		case RECEPTION_CHEQUES:
			return PEReceptionCheque.class;
			
		case SYNTHESE_MULTI_CONTRAT:
			return PESyntheseMultiContrat.class;
			
		case OUT_SAISIE_PAIEMENT:
			return PESaisiePaiement.class;
			
		
		default:
			throw new AmapjRuntimeException("Type non pris en compte");
		}
	}
}
