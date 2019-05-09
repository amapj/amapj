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
 package fr.amapj.view.views.producteur.livraison;

import java.text.SimpleDateFormat;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.model.models.param.paramecran.PELivraisonProducteur;
import fr.amapj.service.services.edgenerator.excel.feuilledistribution.producteur.EGFeuilleDistributionProducteur;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.meslivraisons.JourLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.MesLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.MesLivraisonsService;
import fr.amapj.service.services.meslivraisons.ProducteurLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.QteProdDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.common.gapviewer.AbstractGapViewer;
import fr.amapj.view.views.common.gapviewer.GapViewerUtil;
import fr.amapj.view.views.producteur.ProducteurSelectorPart;


/**
 * Page permettant au producteur de visualiser la livraison de la semaine
 * 
 */
public class ProducteurLivraisonsView extends FrontOfficeView implements PopupListener
{

	SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	
	
	private ProducteurSelectorPart producteurSelector;
	
	private VerticalLayout central;
	
	
	static private String LABEL_DATEJOURLIV = "datejourliv";
	static private String LABEL_QTEPRODUIT = "qteproduit";	
	static private String PANEL_UNJOUR = "unjour";

	
	private AbstractGapViewer gapViewer;
	
	
	public String getMainStyleName()
	{
		return "producteur-livraison";
	}

	/**
	 * 
	 */
	@Override
	public void enter()
	{
		producteurSelector = new ProducteurSelectorPart(this);
		
		addComponent(producteurSelector.getChoixProducteurComponent());

		PELivraisonProducteur pe = (PELivraisonProducteur) new ParametresService().loadParamEcran(MenuList.LIVRAISONS_PRODUCTEUR);
		
		gapViewer = GapViewerUtil.createGapWiever(pe.modeAffichage, this);
		
		addComponent(gapViewer.getComponent());
		
		central = new VerticalLayout();
		addComponent(central);

		
		onPopupClose();
	}

	@Override
	public void onPopupClose()
	{
		Long idProducteur = producteurSelector.getProducteurId();
		
		if (idProducteur==null)
		{
			central.removeAllComponents();
			return ;
		}
		
		//
		MesLivraisonsDTO res = new MesLivraisonsService().getLivraisonProducteur(gapViewer.getDateDebut(),gapViewer.getDateFin(), idProducteur);
		central.removeAllComponents();
		
		// Pour chaque jour, ajout des produits à livrer
		for (JourLivraisonsDTO jour : res.jours)
		{
			VerticalLayout vl = BaseUiTools.addPanel(central, PANEL_UNJOUR);
			
			String dateMessage = df1.format(jour.date);
			BaseUiTools.addStdLabel(vl, dateMessage, LABEL_DATEJOURLIV);
			
			for (ProducteurLivraisonsDTO producteurLiv : jour.producteurs)
			{
				BaseUiTools.addBandeau(vl, producteurLiv.modeleContrat, "nomcontrat");
				
				String msg = "<b>Quantités totales à livrer :</b><ul>";
				for (QteProdDTO cell : producteurLiv.produits)
				{
					msg+= "<li>"+cell.qte+" "+cell.nomProduit+" , "+cell.conditionnementProduit+"</li>";
				}
				msg+="</ul>";
				BaseUiTools.addHtmlLabel(vl, msg, LABEL_QTEPRODUIT);
				
				Button b = new Button("Voir le détail par amapien");
				b.setIcon(FontAwesome.CHEVRON_DOWN);
				vl.addComponent(b);
				
				Label detail = BaseUiTools.addHtmlLabel(vl, "", LABEL_QTEPRODUIT);
				
				b.addStyleName("icon-align-right");
				b.addStyleName("large");	
				b.addClickListener(e->buttonClick(detail,b,producteurLiv.idModeleContratDate));
				b.setData(new Boolean(false));
				
				
				//
				Link extractFile = LinkCreator.createLink(new EGFeuilleDistributionProducteur(producteurLiv.idModeleContrat,producteurLiv.idModeleContratDate));
				vl.addComponent(extractFile);
				
			}
		}		
	}


	public void buttonClick(Label detail,Button b,Long idModeleContratDate)
	{
		boolean status = (Boolean) b.getData();
		status = !status;
		b.setData(status);
		
		if (status)
		{
			//
			b.setCaption("Masquer le détail par amapien");
			b.setIcon(FontAwesome.CHEVRON_UP);
			//
			String content = new GestionContratService().getDetailContrat(idModeleContratDate);
			detail.setValue("<br/>"+content);
		}
		else
		{
			//
			b.setCaption("Voir le détail par amapien");
			b.setIcon(FontAwesome.CHEVRON_DOWN);
			//
			detail.setValue("");
		}	
	}
	
	
	
}
