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
 package fr.amapj.view.views.gestioncontrat.editorpart;

import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;

/**
 * Permet de choisir ce que l'on veut modifier
 * dans le contrat : l'entete, les dates ou les produits
 */
public class ChoixModifEditorPart extends SwitchPopup
{

	private Long id;

	/**
	 * 
	 */
	public ChoixModifEditorPart(Long id)
	{
		this.id = id;

		popupTitle = "Modification d'un contrat";
		setWidth(50);

		if (id == null)
		{
			throw new RuntimeException("Le contrat a modifier ne peut pas etre null");
		}
	}

	@Override
	protected void loadFollowingPopups()
	{
		line1 = "Veuillez indiquer ce que vous souhaitez modifier :";

		addLine("Les informations d'entete (nom,description, date limite d'inscription,nature)", new ModifEnteteContratEditorPart(id));
		
		addSeparator();

		addLine("Les dates de livraisons", new ModifDateContratEditorPart(id));

		addLine("Les produits disponibles et les prix", new ModifProduitContratEditorPart(id));

		addLine("Barrer certaines dates ou certains produits", new BarrerDateContratEditorPart(id));
		
		addSeparator();
		
		addLine("La gestion des jokers", new ModifJokerContratEditorPart(id));
		
		addSeparator();
		
		addLine("Les informations de paiement", new ModifPaiementContratEditorPart(id));
		
		addLine("Supprimer / ajouter des dates de paiement", new ModifDatePaiementContratEditorPart(id));
		
		addSeparator();
		
		addLine("Modifier la mise en forme graphique", new MiseEnFormeModeleContratEditorPart(id));
		
	}

}
