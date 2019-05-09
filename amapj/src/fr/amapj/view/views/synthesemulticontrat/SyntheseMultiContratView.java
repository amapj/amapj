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
 package fr.amapj.view.views.synthesemulticontrat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.DateUtils;
import fr.amapj.common.periode.PeriodeManager;
import fr.amapj.common.periode.PeriodeManager.Periode;
import fr.amapj.common.periode.TypPeriode;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.ChoixImpressionBilanLivraison;
import fr.amapj.model.models.param.paramecran.PESyntheseMultiContrat;
import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.service.services.edgenerator.excel.cheque.EGSyntheseCheque;
import fr.amapj.service.services.edgenerator.excel.cheque.EGSyntheseCheque.Mode;
import fr.amapj.service.services.edgenerator.excel.livraison.EGLivraisonAmapien;
import fr.amapj.service.services.edgenerator.pdf.PGLivraisonAmapien;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.template.BackOfficeLongView;


/**
 * Page permettant d'afficher les syntheses multi contrat 
 *  
 *
 */
public class SyntheseMultiContratView extends BackOfficeLongView
{

	@Override
	public String getMainStyleName()
	{
		return "import-donnees";
	}

	/**
	 * 
	 */
	@Override
	public void enterIn(ViewChangeEvent event)
	{
		addLabelH1(this, "Synthéses multi contrats");
	
		addPanel("Bilans des livraisons",getBilanLivraisonPanel());
		
		addPanel("Synthese des chèques",getSyntheseCheque());
	
		
	}




	private VerticalLayout getBilanLivraisonPanel()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		
		PESyntheseMultiContrat pe = (PESyntheseMultiContrat) new ParametresService().loadParamEcran(MenuList.SYNTHESE_MULTI_CONTRAT);
		
		// Ajout des bilans mensuels si necessaire
		if (pe.mensuelImpressionRecap==ChoixOuiNon.OUI)
		{
			LocalDateTime now = DateUtils.getLocalDateTime();
			PeriodeManager pm = new PeriodeManager(now, TypPeriode.MOIS, pe.mensuelNbJourAvant, pe.mensuelNbJourApres);
			List<Periode> periodes = pm.getAllPeriodes();
			
			if (periodes.size()>0)
			{
				addLabelBold(layout,"Les bilans mensuels de livraison");
				for (Periode p : periodes)
				{
					addOneBloc(layout,pe.mensuelFormat,p.typPeriode, DateUtils.asDate(p.startDate), DateUtils.asDate(p.endDate),pe.mensuelPdfEditionId);
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
				addLabelBold(layout,"Les bilans trimestriels de livraison");
				for (Periode p : periodes)
				{
					addOneBloc(layout,pe.trimestreFormat,p.typPeriode, DateUtils.asDate(p.startDate), DateUtils.asDate(p.endDate),pe.trimestrePdfEditionId);
				}
			}
			
		}
		
		return layout;
	}

	
	private void addOneBloc(VerticalLayout layout, ChoixImpressionBilanLivraison format, TypPeriode typPeriode, Date dateDebut, Date dateFin, Long pdfEditionId)
	{

		switch (format)
		{
		case TABLEUR:
			addGenerator(layout,new EGLivraisonAmapien(typPeriode,dateDebut,dateFin,null));
			break;
			
		case PDF:
			addGenerator(layout,new PGLivraisonAmapien(typPeriode,dateDebut,dateFin,null,pdfEditionId));
			break;
		
		case TABLEUR_ET_PDF:
			EGLivraisonAmapien eg = new EGLivraisonAmapien(typPeriode,dateDebut,dateFin,null);
			eg.setNameToDisplaySuffix(" (Format Tableur)");
			addGenerator(layout,eg);
			PGLivraisonAmapien pg = new PGLivraisonAmapien(typPeriode,dateDebut,dateFin,null,pdfEditionId);
			pg.setNameToDisplaySuffix(" (Format PDF)");
			addGenerator(layout,pg);
			break;

		default:
			throw new AmapjRuntimeException();
		}

	}
	
	
	private VerticalLayout getSyntheseCheque()
	{
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		
		
		addGenerator(layout,new EGSyntheseCheque(Mode.CHEQUE_A_REMETTRE,null));
		addGenerator(layout,new EGSyntheseCheque(Mode.CHEQUE_AMAP,null));
		addGenerator(layout,new EGSyntheseCheque(Mode.CHEQUE_REMIS_PRODUCTEUR,null));
		addGenerator(layout,new EGSyntheseCheque(Mode.TOUS,null));
		
		return layout;
	}
	
	
	// PARTIE OUTILS
	
	
	private void addPanel(String titre,VerticalLayout content)
	{
		Panel utilisateurPanel = new Panel(titre);
		utilisateurPanel.addStyleName("action");
		utilisateurPanel.setContent(content);
		addComponent(utilisateurPanel);
		addEmptyLine(this);
		
	}


	private void addGenerator(VerticalLayout layout, CoreGenerator coreGenerator)
	{
		Link l = LinkCreator.createLink(coreGenerator,false);
		layout.addComponent(l);
		
	}


	
	private Label addLabel(VerticalLayout layout, String str)
	{
		Label tf = new Label(str);	
		layout.addComponent(tf);
		return tf;
		
	}
	
	private Label addLabelBold(VerticalLayout layout, String str)
	{
		Label tf = new Label("<b>"+str+"</b>",ContentMode.HTML);	
		layout.addComponent(tf);
		return tf;
		
	}

	
	
	private void addButton(VerticalLayout layout, String str,ClickListener listener)
	{
		Button b = new Button(str);
		b.addStyleName(ChameleonTheme.BUTTON_BIG);
		b.addClickListener(listener);
		layout.addComponent(b);
		
	}

	
	private Label addLabelH1(VerticalLayout layout, String str)
	{
		Label tf = new Label(str);
		tf.addStyleName("titre");
		layout.addComponent(tf);
		return tf;

	}
	
	private Label addEmptyLine(VerticalLayout layout)
	{
		Label tf = new Label("<br/>",ContentMode.HTML);
		layout.addComponent(tf);
		return tf;

	}


}
