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
 package fr.amapj.view.views.mespaiements;

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.service.services.mespaiements.DetailPaiementAFournirDTO;
import fr.amapj.service.services.mespaiements.DetailPaiementFourniDTO;
import fr.amapj.service.services.mespaiements.MesPaiementsDTO;
import fr.amapj.service.services.mespaiements.MesPaiementsService;
import fr.amapj.service.services.mespaiements.PaiementAFournirDTO;
import fr.amapj.service.services.mespaiements.PaiementFourniDTO;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * Page permettant à l'utilisateur de visualiser tous ses paiements
 * 
 */
public class MesPaiementsView extends FrontOfficeView
{
	
	
	
	static private String LABEL_TEXTEFOND = "textefond";
	
	static private String LABEL_CONTRAT = "contrat";
	static private String LABEL_CHEQUEAFOURNIR = "chequeafournir";
	
	static private String LABEL_MOIS = "mois";
	static private String LABEL_CHEQUE = "cheque";	
	
	static private String PANEL_CONTRAT = "contrat";
	static private String PANEL_CHEQUEAFOURNIR = "chequeafournir";
	
	static private String PANEL_AVENIR = "avenir";
	static private String PANEL_MOIS = "mois";
	
	
	SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");

	
	
	public String getMainStyleName()
	{
		return "paiement";
	}
	
	/**
	 * Ajoute un label sur toute la largeur à la ligne indiquée
	 */
	private Label addLabel(String str)
	{
		Label tf = new Label(str);
		tf.addStyleName(LABEL_TEXTEFOND);
		addComponent(tf);
		return tf;
		
	}


	@Override
	public void enter()
	{
		MesPaiementsDTO mesPaiementsDTO = new MesPaiementsService().getMesPaiements(SessionManager.getUserId());
		
		// Le titre
		addLabel("Les chèques que je dois donner à l'AMAP");
		
		Panel p0 = new Panel();
		p0.setWidth("100%");
		p0.addStyleName(PANEL_CHEQUEAFOURNIR);
		
		VerticalLayout vl1 = new VerticalLayout();
		vl1.setMargin(true);
		p0.setContent(vl1);
		addComponent(p0);
	
		
		// la liste des chéques à donner
		List<PaiementAFournirDTO> paiementAFournirs = mesPaiementsDTO.paiementAFournir;
		
		if (paiementAFournirs.size()==0)
		{
			String str = "Vous êtes à jour de vos paiements, vous n'avez pas de chèques à fournir à l'AMAP <br/>";
			Label l = new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_CHEQUEAFOURNIR);
			vl1.addComponent(l);
		}
		
		for (PaiementAFournirDTO paiementAFournir : paiementAFournirs)
		{
			String str = formatContrat(paiementAFournir);
			Label l = new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_CONTRAT);
			
			Panel p1 = new Panel();
			p1.setContent(l);
			p1.addStyleName(PANEL_CONTRAT);
			vl1.addComponent(p1);
		
			
			for (DetailPaiementAFournirDTO detail : paiementAFournir.paiements)
			{
				str = detail.formatPaiement();
				Label ld = new Label(str, ContentMode.HTML);
				ld.addStyleName(LABEL_CHEQUEAFOURNIR);
				vl1.addComponent(ld);
			
			}
			
			// Une ligne vide
			vl1.addComponent(new Label("<br/>", ContentMode.HTML));
		}
		
		
		// Le titre
		addLabel("Le planning de mes paiements à venir mois par mois");
		
		
		Panel p = new Panel();
		p.setWidth("100%");
		p.addStyleName(PANEL_AVENIR);
		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		p.setContent(vl);
		addComponent(p);
	
		
		// la liste des chéques qui seront bientot encaissés		
		for (PaiementFourniDTO paiementFourni : mesPaiementsDTO.paiementFourni)
		{
			String str = formatMois(paiementFourni);
			Label l = new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_MOIS);
			
			Panel p1 = new Panel();
			p1.setContent(l);
			p1.addStyleName(PANEL_MOIS);
			vl.addComponent(p1);
		
			
			for (DetailPaiementFourniDTO detail : paiementFourni.paiements)
			{
				str = formatPaiement(detail);
				Label cheque = new Label(str, ContentMode.HTML);
				cheque.addStyleName(LABEL_CHEQUE);
				vl.addComponent(cheque);
			}
			
			// Une ligne vide
			vl.addComponent(new Label("<br/>", ContentMode.HTML));
		}
	}
	
	
	
	
	
	
	private String formatContrat(PaiementAFournirDTO paiementAFournir)
	{
		// Ligne 0
		String str = "Nom du contrat : "+paiementAFournir.nomContrat+"<br/>"+
					" Date limite de remise des chèques: "+df.format(paiementAFournir.dateRemise)+"<br/>"+
					" Ordre des chèques : "+paiementAFournir.libCheque;
						
		return str;
	}
	
	
	


	private String formatMois(PaiementFourniDTO paiementFourni)
	{
		String montant = new CurrencyTextFieldConverter().convertToString(paiementFourni.totalMois)+" €";
		String str = paiementFourni.moisPaiement+" - Total du mois : "+montant;
		return str;
	}




	private String formatPaiement(DetailPaiementFourniDTO detail)
	{
		String montant = new CurrencyTextFieldConverter().convertToString(detail.montant)+" €";
		String str = "Montant : "+montant+" - Contrat :"+detail.nomContrat+" - Ordre du chèque :"+detail.libCheque;
		
		if (detail.etatPaiement==EtatPaiement.A_FOURNIR)
		{
			str = str+" (Chèque à fournir à l'AMAP)";
		}
		
		return str;
	}


}
