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
 package fr.amapj.service.services.mespaiements;

import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * 
 *
 */
public class DetailPaiementAFournirDTO
{
	// Mois du paiement sousla forme Janvier 2014 , Fevrier 2014 , ..
	public String moisPaiement;
	
	// Montant du paiement
	public int montant;
	
	// Nombre de cheque
	public int nbCheque;
	

	public String formatPaiement()
	{
		String mt = new CurrencyTextFieldConverter().convertToString(montant)+" €";
		String str ;
		if (nbCheque==1)
		{
			str = "1 chèque de "+mt+" qui sera débité en "+moisPaiement;
			
		}
		else	
		{
			str = ""+nbCheque+" chèques de "+mt+" qui seront débités en "+moisPaiement;
		}
		return str;
	}
	
			
}
