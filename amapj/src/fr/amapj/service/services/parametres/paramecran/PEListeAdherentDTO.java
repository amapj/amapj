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
 package fr.amapj.service.services.parametres.paramecran;


/**
 * Parametrage de l'écran liste des adhérents
 */
public class PEListeAdherentDTO
{
	// Indique si l'utiisateur courant peut accéder aux e mails 
	public boolean canAccessEmail = true;

	// Indique si l'utiisateur courant peut accéder aux numéros de telephone 1 
	public boolean canAccessTel1 = true;

	// Indique si l'utiisateur courant peut accéder aux numéros de telephone 2 
	public boolean canAccessTel2 = true ;
	
	// Indique si l'utiisateur courant peut accéder aux 3 élements d'adresse
	public boolean canAccessAdress = true ;

}
