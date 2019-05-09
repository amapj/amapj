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
 /**
 * Permet de definir l'état d'un paiement
 * 
 * 
 */
package fr.amapj.model.models.cotisation;

public enum EtatPaiementAdhesion
{
	// Le paiement n'a pas encore été donné par l'utilisateur
	A_FOURNIR ,
	
	// le paiement a été réceptionné à l'AMAP et donné à la banque
	ENCAISSE
	;

}
