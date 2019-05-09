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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse;

import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupAjoutDateLivraison;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupBarrerDateLivraison;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupDeBarrerDateLivraison;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupDeplacerDateLivraison;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.date.PopupSupprimerDateLivraison;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.grille.PopupBarrerProduit;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.joker.PopupModifJoker;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.produit.PopupProduitAjout;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.produit.PopupProduitModifPrix;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.produit.PopupProduitOrdreContrat;
import fr.amapj.view.views.gestioncontratsignes.modifiermasse.produit.PopupProduitSuppression;

/**
 * Permet de choisir son action 
 */
public class ModifierEnMasseContratSigne extends SwitchPopup
{
	
	private Long mcId;

	/**
	 * 
	 */
	public ModifierEnMasseContratSigne(Long mcId)
	{
		popupTitle = "Modifications en masse sur les contrats signés";
		setWidth(50);
		this.mcId = mcId;

	}

	@Override
	protected void loadFollowingPopups()
	{
		line1 = "Veuillez indiquer ce que vous souhaitez faire :";

		addLine("Ajouter des dates de livraison", new PopupAjoutDateLivraison(mcId));
		addLine("Déplacer une date de livraison", new PopupDeplacerDateLivraison(mcId));
		addLine("Barrer une ou plusieurs dates de livraison", new PopupBarrerDateLivraison(mcId));
		addLine("Dé barrer une ou plusieurs dates de livraison", new PopupDeBarrerDateLivraison(mcId));
		addLine("Supprimer une ou plusieurs dates de livraison", new PopupSupprimerDateLivraison(mcId)); 
		
		addSeparator();
		
		addLine("Ajouter des produits", new PopupProduitAjout(mcId));
		addLine("Supprimer des produits", new PopupProduitSuppression(mcId));
		addLine("Modifier les prix des produits", new PopupProduitModifPrix(mcId));
		addLine("Modifier l'ordre des produits dans le contrat", new PopupProduitOrdreContrat(mcId));
		
		addSeparator();
		
		addLine("Barrer / Ne pas barrer des produits sur certaines dates", new PopupBarrerProduit(mcId));
		
		addSeparator();
		
		addLine("Modifier les règles de gestion des jokers", new PopupModifJoker(mcId));

	}

}
