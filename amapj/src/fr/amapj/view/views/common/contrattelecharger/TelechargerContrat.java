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
 package fr.amapj.view.views.common.contrattelecharger;

import fr.amapj.model.models.contrat.modele.GestionPaiement;
import fr.amapj.service.services.edgenerator.excel.EGBilanCompletCheque;
import fr.amapj.service.services.edgenerator.excel.EGCollecteCheque;
import fr.amapj.service.services.edgenerator.excel.EGUtilisateurContrat;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien.EGFeuilleDistributionAmapien;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien.EGLiasseFeuilleDistributionAmapien;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien.EGFeuilleDistributionAmapien.EGMode;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGFeuilleDistributionProducteur;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGSyntheseContrat;
import fr.amapj.service.services.edgenerator.pdf.PGEngagement;
import fr.amapj.service.services.edgenerator.pdf.PGEngagement.PGEngagementMode;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratDTO;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

public class TelechargerContrat
{

	/**
	 * Permet l'affichage d'un popup avec tous les fichiers à télécharger pour un modele de contrat
	 * 
	 * idContrat peut être null
	 */
	static public void displayPopupTelechargerContrat(Long idModeleContrat, Long idContrat,PopupListener listener)
	{
		TelechargerPopup popup = new TelechargerPopup("Télecharger ...",60);
		
		
		// 1 - Partie AMAPIEN si un contrat en particulier est sélectionné
		boolean needEngagement = new EditionSpeService().needEngagement(idModeleContrat);
		
		if (idContrat!=null)
		{
			popup.addGenerator(new EGFeuilleDistributionAmapien(EGMode.STD,idModeleContrat,idContrat));
			if (needEngagement)
			{
				popup.addGenerator(new PGEngagement(PGEngagementMode.UN_CONTRAT,idModeleContrat,idContrat,null));
			}
			popup.addSeparator();
		}
		
		

		// 2 - Partie dédiée au feuilles de distributions 
		popup.addGenerator(new EGFeuilleDistributionProducteur(idModeleContrat));
		popup.addGenerator(new EGLiasseFeuilleDistributionAmapien(idModeleContrat));
		popup.addGenerator(new EGSyntheseContrat(idModeleContrat));
		popup.addLabel("");
		
		// 3 - Partie dédiée au contrat d'engagement 
		if (needEngagement)
		{
			popup.addGenerator(new PGEngagement(PGEngagementMode.TOUS_LES_CONTRATS,idModeleContrat,null,null));
			popup.addLabel("");
		}
		
		// 4 - Partie dédiée au paiement du contrat 
		// Ces documents sont utilisables uniquement si le modele de contrat gere les paiements 
		if (isWithPaiement(idModeleContrat))
		{
			popup.addGenerator(new EGCollecteCheque(idModeleContrat));
			popup.addGenerator(new EGBilanCompletCheque(idModeleContrat));
			popup.addLabel("");
		}
		
		// 5 - Partie dédiée aux infos sur les amapiens de ce contrat 
		popup.addGenerator(new EGUtilisateurContrat(idModeleContrat));
		popup.addLabel("");
		

		// 6 - Partie dédiée aux contrats vierges
		popup.addGenerator(new EGFeuilleDistributionAmapien(EGMode.UN_VIERGE,idModeleContrat,null));
		if (needEngagement)
		{
			popup.addGenerator(new PGEngagement(PGEngagementMode.UN_VIERGE,idModeleContrat,null,null));
		}
	
				
		CorePopup.open(popup,listener);
	}

	private static boolean isWithPaiement(Long idModeleContrat) 
	{
		ModeleContratDTO dto = new GestionContratService().loadModeleContrat(idModeleContrat);
		return dto.gestionPaiement==GestionPaiement.GESTION_STANDARD;
	}
	
	
}
