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
 package fr.amapj.model.models.param.paramecran;

import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.common.AbstractParamEcran;



/**
 * Parametrage de l'écran réception des chèques
 */
public class PEReceptionCheque  extends AbstractParamEcran
{
	public ChoixOuiNon saisieAvoirNegatif = ChoixOuiNon.NON;
	
	public ChoixOuiNon saisieCommentaire1 = ChoixOuiNon.NON;
	
	public String libSaisieCommentaire1 = "Banque";
	
	public ChoixOuiNon saisieCommentaire2 = ChoixOuiNon.NON;
	
	public String libSaisieCommentaire2 = "Numéro chèque";

	public ChoixOuiNon getSaisieCommentaire1()
	{
		return saisieCommentaire1;
	}

	public void setSaisieCommentaire1(ChoixOuiNon saisieCommentaire1)
	{
		this.saisieCommentaire1 = saisieCommentaire1;
	}

	public String getLibSaisieCommentaire1()
	{
		return libSaisieCommentaire1;
	}

	public void setLibSaisieCommentaire1(String libSaisieCommentaire1)
	{
		this.libSaisieCommentaire1 = libSaisieCommentaire1;
	}

	public ChoixOuiNon getSaisieCommentaire2()
	{
		return saisieCommentaire2;
	}

	public void setSaisieCommentaire2(ChoixOuiNon saisieCommentaire2)
	{
		this.saisieCommentaire2 = saisieCommentaire2;
	}

	public String getLibSaisieCommentaire2()
	{
		return libSaisieCommentaire2;
	}

	public void setLibSaisieCommentaire2(String libSaisieCommentaire2)
	{
		this.libSaisieCommentaire2 = libSaisieCommentaire2;
	}

	public ChoixOuiNon getSaisieAvoirNegatif() 
	{
		return saisieAvoirNegatif;
	}

	public void setSaisieAvoirNegatif(ChoixOuiNon saisieAvoirNegatif) 
	{
		this.saisieAvoirNegatif = saisieAvoirNegatif;
	}
	
	
	
}
