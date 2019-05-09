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
 package fr.amapj.view.views.meslivraisons;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.periode.PeriodeManager;
import fr.amapj.common.periode.PeriodeManager.Periode;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.ChoixImpressionBilanLivraison;
import fr.amapj.model.models.param.paramecran.PEMesLivraisons;
import fr.amapj.service.services.edgenerator.excel.livraison.EGLivraisonAmapien;
import fr.amapj.service.services.edgenerator.pdf.PGLivraisonAmapien;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.views.common.gapviewer.AbstractGapViewer;

public class TelechargerMesLivraisons
{
	
	private TelechargerPopup popup;
	private PEMesLivraisons pe;
	private Long idUtilisateur;

	/**
	 * Permet l'affichage d'un popup avec tous les fichiers à télécharger dans mes livraisons 
	 * @param idUtilisateur 
	 */
	public void displayPopupTelecharger(AbstractGapViewer gv, Long idUtilisateur, PopupListener listener)
	{
		popup = new TelechargerPopup("Impression de mes livraisons",80);
		this.idUtilisateur = idUtilisateur;
		
		pe = (PEMesLivraisons) new ParametresService().loadParamEcran(MenuList.MES_LIVRAISONS);
		
		// Ajout de ce qui est visible à l'écran
		if (pe.pageCouranteImpressionRecap==ChoixOuiNon.OUI)
		{
			addOneBloc(pe.pageCouranteFormat,gv.getTypPeriode(),gv.getDateDebut(),gv.getDateFin(),pe.pageCourantePdfEditionId);
		}
		
		
		// Ajout des bilans mensuels si necessaire
		if (pe.mensuelImpressionRecap==ChoixOuiNon.OUI)
		{
			LocalDateTime now = DateUtils.getLocalDateTime();
			PeriodeManager pm = new PeriodeManager(now, TypPeriode.MOIS, pe.mensuelNbJourAvant, pe.mensuelNbJourApres);
			List<Periode> periodes = pm.getAllPeriodes();
			
			if (periodes.size()>0)
			{
				popup.addLabel("<b>Les bilans mensuels de livraison</b>");
				for (Periode p : periodes)
				{
					addOneBloc(pe.mensuelFormat,p.typPeriode, DateUtils.asDate(p.startDate), DateUtils.asDate(p.endDate),pe.mensuelPdfEditionId);
				}
			}
			
		}
		
		// Ajout des bilans trimestres si necessaire
		if (pe.trimestreImpressionRecap==ChoixOuiNon.OUI)
		{
			LocalDateTime now = DateUtils.getLocalDateTime();
			PeriodeManager pm = new PeriodeManager(now, TypPeriode.TRIMESTRE, pe.trimestreNbJourAvant, pe.trimestreNbJourApres);
			List<Periode> periodes = pm.getAllPeriodes();
			
			if (periodes.size()>0)
			{
				popup.addLabel("<b>Les bilans trimestriels de livraison</b>");
				for (Periode p : periodes)
				{
					addOneBloc(pe.trimestreFormat,p.typPeriode, DateUtils.asDate(p.startDate), DateUtils.asDate(p.endDate),pe.trimestrePdfEditionId);
				}
			}
			
		}
		
		
		CorePopup.open(popup,listener);
	}

	private void addOneBloc(ChoixImpressionBilanLivraison format, TypPeriode typPeriode, Date dateDebut, Date dateFin, Long pdfEditionId)
	{

		switch (format)
		{
		case TABLEUR:
			popup.addGenerator(new EGLivraisonAmapien(typPeriode,dateDebut,dateFin,idUtilisateur));
			break;
			
		case PDF:
			popup.addGenerator(new PGLivraisonAmapien(typPeriode,dateDebut,dateFin,idUtilisateur,pdfEditionId));
			break;
		
		case TABLEUR_ET_PDF:
			EGLivraisonAmapien eg = new EGLivraisonAmapien(typPeriode,dateDebut,dateFin,idUtilisateur);
			eg.setNameToDisplaySuffix(" (Format Tableur)");
			popup.addGenerator(eg);
			PGLivraisonAmapien pg = new PGLivraisonAmapien(typPeriode,dateDebut,dateFin,idUtilisateur,pdfEditionId);
			pg.setNameToDisplaySuffix(" (Format PDF)");
			popup.addGenerator(pg);
			break;

		default:
			throw new AmapjRuntimeException();
		}

	}


	
}
