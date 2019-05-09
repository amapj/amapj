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
 package fr.amapj.view.views.mescontrats;

import java.util.Date;
import java.util.List;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.model.models.param.paramecran.ImpressionContrat;
import fr.amapj.model.models.param.paramecran.PEMesContrats;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien.EGFeuilleDistributionAmapien;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.amapien.EGFeuilleDistributionAmapien.EGMode;
import fr.amapj.service.services.edgenerator.pdf.PGEngagement;
import fr.amapj.service.services.edgenerator.pdf.PGEngagement.PGEngagementMode;
import fr.amapj.service.services.editionspe.EditionSpeService;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

public class TelechargerMesContrat
{
	
	TelechargerPopup popup;
	PEMesContrats peMesContrats;
	List<ContratDTO> existingContrats;
	
	// Detrmine si il est nécessaire de metre le titre "Mes feuilles de distribution (format Excel)" et "Mes contrats d'engagement"
	boolean needTitle;
	
	/**
	 * Permet l'affichage d'un popup avec tous les fichiers à télécharger dans mes contrats 
	 */
	public void displayPopupTelechargerMesContrat(List<ContratDTO> existingContrats,PopupListener listener)
	{
		this.existingContrats = existingContrats;
		popup = new TelechargerPopup("Impression de mes contrats",80);
		
		peMesContrats = (PEMesContrats) new ParametresService().loadParamEcran(MenuList.MES_CONTRATS);
		needTitle = getNeedTitle();
		
		
		switch (peMesContrats.presentationImpressionContrat)
		{
		case CONTRAT_FIRST:
			displayBlocContrat();
			displayBlocEngagement();
			break;

		case ENGAGEMENT_FIRST:
			displayBlocEngagement();
			displayBlocContrat();
			break;

		case MELANGE:
			displayMelange();
			break;
			
		default:
			throw new AmapjRuntimeException();
		}
		
		CorePopup.open(popup,listener);
	}

	private boolean getNeedTitle()
	{
		// Si on ne gere pas les engagements, ce n'est pas necessaire de mettre un titre
		if (new EditionSpeService().ficheProducteurNeedEngagement()==false)
		{
			return false;
		}
		
		// Si on interdit d'afficher un des deux blocs : pas de titre
		if ((peMesContrats.canPrintContrat==ImpressionContrat.JAMAIS) || (peMesContrats.canPrintContratEngagement==ImpressionContrat.JAMAIS))
		{	
			return false;
		}
		
		// Sinon, il faut un titre
		return true;
	}

	private void displayBlocContrat()
	{
		if (peMesContrats.canPrintContrat==ImpressionContrat.JAMAIS)
		{
			return;
		}
		
		if (needTitle)
		{
			popup.addLabel("<b>Mes feuilles de distribution (format Excel)</b>");
		}
		
		for (ContratDTO c : existingContrats)
		{
			if (canPrintContrat(c))
			{
				popup.addGenerator(new EGFeuilleDistributionAmapien(EGMode.STD,c.modeleContratId,c.contratId));
			}
		}
	}
	

	private boolean canPrintContrat(ContratDTO c)
	{
		switch (peMesContrats.canPrintContrat)
		{
		case TOUJOURS:
			return true;
			
		case JAMAIS:
			return false;
			
		case APRES_DATE_FIN_DES_INSCRIPTIONS:
			Date dateRef = DateUtils.getDateWithNoTime();
			return dateRef.after(c.dateFinInscription);

		default:
			throw new AmapjRuntimeException();
		}
	}

	private void displayBlocEngagement()
	{
		if (peMesContrats.canPrintContratEngagement==ImpressionContrat.JAMAIS)
		{
			return;
		}
		
		if (needTitle)
		{
			popup.addLabel("<b>Mes contrats d'engagement</b>");
		}
		
		for (ContratDTO c : existingContrats)
		{
			if (canPrintContratEngagement(c))
			{
				popup.addGenerator(new PGEngagement(PGEngagementMode.UN_CONTRAT,c.modeleContratId,c.contratId,null));
			}
		}
	}

	
	private boolean canPrintContratEngagement(ContratDTO c)
	{
		// Si ce contrat ne gere pas les engagements : on ne peut pas l'imprimer 
		if (new EditionSpeService().needEngagement(c.modeleContratId)==false)
		{
			return false;
		}
		
		switch (peMesContrats.canPrintContratEngagement)
		{
		case TOUJOURS:
			return true;
			
		case JAMAIS:
			return false;
			
		case APRES_DATE_FIN_DES_INSCRIPTIONS:
			Date dateRef = DateUtils.getDateWithNoTime();
			return dateRef.after(c.dateFinInscription);

		default:
			throw new AmapjRuntimeException();
		}
	}
		
	private void displayMelange()
	{
		for (ContratDTO c : existingContrats)
		{
			if (canPrintContrat(c))
			{
				popup.addGenerator(new EGFeuilleDistributionAmapien(EGMode.STD,c.modeleContratId,c.contratId));
			}
			if (canPrintContratEngagement(c))
			{
				popup.addGenerator(new PGEngagement(PGEngagementMode.UN_CONTRAT,c.modeleContratId,c.contratId,null));
			}
		}
		
	}	
	
	
}
