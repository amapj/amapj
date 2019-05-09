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
 * Parametrage de l'écran de saisie des paiements
 */
public class PESaisiePaiement  extends AbstractParamEcran
{
	// Indique si l'amapien peut modifier la proposition de paiement, ou s'il ne peut pas
	public ChoixOuiNon modificationPaiementPossible = ChoixOuiNon.OUI;
	
	// Mode de calcul de la proposition de paiement 
	public CalculPaiement modeCalculPaiement = CalculPaiement.STANDARD;
	
	// Montant minimum pour les chèques pour le calcul de la proposition 
	public int montantChequeMiniCalculProposition = 0;

	public ChoixOuiNon getModificationPaiementPossible() {
		return modificationPaiementPossible;
	}

	public void setModificationPaiementPossible(
			ChoixOuiNon modificationPaiementPossible) {
		this.modificationPaiementPossible = modificationPaiementPossible;
	}

	public CalculPaiement getModeCalculPaiement() {
		return modeCalculPaiement;
	}

	public void setModeCalculPaiement(CalculPaiement modeCalculPaiement) {
		this.modeCalculPaiement = modeCalculPaiement;
	}

	public int getMontantChequeMiniCalculProposition() {
		return montantChequeMiniCalculProposition;
	}

	public void setMontantChequeMiniCalculProposition(
			int montantChequeMiniCalculProposition) {
		this.montantChequeMiniCalculProposition = montantChequeMiniCalculProposition;
	}

	
	
	
	
	
	
}
