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
 package fr.amapj.view.views.editionspe;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.services.editionspe.EditionSpeDTO;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.swicthpopup.SwitchPopup;
import fr.amapj.view.views.editionspe.bilanlivraison.BilanLivraisonEditorPart;
import fr.amapj.view.views.editionspe.bulletinadhesion.BulletinAdhesionEditorPart;
import fr.amapj.view.views.editionspe.engagement.EngagementEditorPart;

/**
 * Permet de choisir l'edition specifique à créer ou a modifier 
 */
public class ChoixEditionSpecifiqueEditorPart extends SwitchPopup
{

	/**
	 * 
	 */
	public ChoixEditionSpecifiqueEditorPart()
	{
		popupTitle = "Choix de l'édition spécifique à créer";
		setWidth(50);
	}

	@Override
	protected void loadFollowingPopups()
	{
		line1 = "Veuillez indiquer ce que vous voulez créer :";

		addLine("Un nouveau modèle d'engagement", new EngagementEditorPart(true, null));
		
		addLine("Un nouveau modèle de bulletin d'adhésion", new BulletinAdhesionEditorPart(true, null));
		
		addLine("Une nouvelle étiquette", new EtiquetteProducteurEditorPart(true, null));
		
		addLine("Une nouvelle feuille d'émargement (mensuelle ou hebdomadaire)", new FeuilleEmargementEditorPart(true, null));
		
		addLine("Un nouveau bilan de livraison", new BilanLivraisonEditorPart(true, null));
		
	}
	
	/**
	 * Permet d'ouvrir le bon popup pour modifier cette édition
	 * @param dto
	 */
	static public void openEditorPart(EditionSpeDTO dto,PopupListener listener)
	{
		switch (dto.typEditionSpecifique)
		{
		case ETIQUETTE_PRODUCTEUR:
			open(new EtiquetteProducteurEditorPart(false, dto), listener);
			break;
			
		case FEUILLE_EMARGEMENT:
			open(new FeuilleEmargementEditorPart(false, dto), listener);
			break;
		
		case CONTRAT_ENGAGEMENT:
			open(new EngagementEditorPart(false, dto), listener);
			break;
			
		case BULLETIN_ADHESION:
			open(new BulletinAdhesionEditorPart(false, dto), listener);
			break;
			
		case BILAN_LIVRAISON:
			open(new BilanLivraisonEditorPart(false, dto), listener);
			break;
		
		default:
			throw new AmapjRuntimeException("Erreur"); 
			
		}
	}

}
