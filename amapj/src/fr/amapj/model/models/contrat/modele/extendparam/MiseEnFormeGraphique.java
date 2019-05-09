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
 package fr.amapj.model.models.contrat.modele.extendparam;

import fr.amapj.model.models.param.ChoixOuiNon;

/**
 * Paramètres de mise en forme graphique pour le modele de contrat
 * 
 * A noter : ne pas mettre des valeurs par défaut à la déclaration des attributs
 * (du style public ChoixOuiNon paiementStdLib1Modifier = OUI; )
 * Utiliser à la place la méthode setDefault()
 */
public class MiseEnFormeGraphique
{
	
	public ChoixOuiNon paiementStdLib1Modifier;
	
	/**
	 * Dans le popup Paiement Standard",
	 * le libellé "Chèques à remettre à .... suivi du nom d'un des référents" est présent 
	 * Possibilité de le paramètrer ici
	 */
	public String paiementStdLib1;
	
	
	public ChoixOuiNon paiementStdLib2Modifier;
	
	/**
	 * Dans le popup Paiement Standard",
	 * le libellé "Une proposition de paiement a été calculée et est affichée ci dessous. Vous pouvez modifier cette proposition en saisissant directement les montants en face de chaque mois. Le dernier mois est calculé automatiquement pour ajuster le contrat" est présent 
	 * Possibilité de le paramètrer ici
	 */
	public String paiementStdLib2;
	
	
	/**
	 * Positionne les paramètres par défaut dans un objet  
	 */
	public void setDefault()
	{
		if (paiementStdLib1Modifier==null)
		{
			paiementStdLib1Modifier = ChoixOuiNon.NON;
		}
		
		// Ceci est obligatoire, sinon le default devient toujours différent de ce qui a été edité (différence entre "" et null)
		if (paiementStdLib1 == null)
		{
			paiementStdLib1 = "";
		}
		
		if (paiementStdLib2Modifier==null)
		{
			paiementStdLib2Modifier = ChoixOuiNon.NON;
		}
		
		// Ceci est obligatoire, sinon le default devient toujours différent de ce qui a été edité (différence entre "" et null)
		if (paiementStdLib2 == null)
		{
			paiementStdLib2 = "";
		}
			
	}
	
	
	
	

	public ChoixOuiNon getPaiementStdLib1Modifier()
	{
		return paiementStdLib1Modifier;
	}

	public void setPaiementStdLib1Modifier(ChoixOuiNon paiementStdLib1Modifier)
	{
		this.paiementStdLib1Modifier = paiementStdLib1Modifier;
	}

	public String getPaiementStdLib1()
	{
		return paiementStdLib1;
	}

	public void setPaiementStdLib1(String paiementStdLib1)
	{
		this.paiementStdLib1 = paiementStdLib1;
	}

	public ChoixOuiNon getPaiementStdLib2Modifier()
	{
		return paiementStdLib2Modifier;
	}

	public void setPaiementStdLib2Modifier(ChoixOuiNon paiementStdLib2Modifier)
	{
		this.paiementStdLib2Modifier = paiementStdLib2Modifier;
	}

	public String getPaiementStdLib2()
	{
		return paiementStdLib2;
	}

	public void setPaiementStdLib2(String paiementStdLib2)
	{
		this.paiementStdLib2 = paiementStdLib2;
	}
	
	
	
	
}
