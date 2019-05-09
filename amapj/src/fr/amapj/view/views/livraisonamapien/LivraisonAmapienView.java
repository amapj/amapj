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
 package fr.amapj.view.views.livraisonamapien;

import java.text.SimpleDateFormat;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.model.models.param.paramecran.PELivraisonAmapien;
import fr.amapj.service.services.meslivraisons.JourLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.JourLivraisonsDTO.InfoPermanence;
import fr.amapj.service.services.meslivraisons.MesLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.MesLivraisonsService;
import fr.amapj.service.services.meslivraisons.ProducteurLivraisonsDTO;
import fr.amapj.service.services.meslivraisons.QteProdDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.common.gapviewer.AbstractGapViewer;
import fr.amapj.view.views.common.gapviewer.GapViewerUtil;
import fr.amapj.view.views.common.utilisateurselector.UtilisateurSelectorPart;


/**
 * Affichage des livraisons pour un amapien donné 
 *
 */
@SuppressWarnings("serial")
public class LivraisonAmapienView extends FrontOfficeView implements PopupListener
{	
		
	static private String LABEL_DATEJOURLIV = "datejourliv";
	static private String LABEL_QTEPRODUIT = "qteproduit";	
	static private String PANEL_UNJOUR = "unjour";

	
		
	private VerticalLayout planning;
	
	private VerticalLayout livraison;
	
	private AbstractGapViewer semaineViewer;
	
	private UtilisateurSelectorPart utilisateurSelector;
	
	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");

	
	@Override
	public String getMainStyleName()
	{
		return "livraison-amapien";
	}

	/**
	 * 
	 */
	@Override
	public void enter()
	{
		utilisateurSelector = new UtilisateurSelectorPart(this);
		
		addComponent(utilisateurSelector.getChoixUtilisateurComponent());
		
		
		PELivraisonAmapien pe = (PELivraisonAmapien) new ParametresService().loadParamEcran(MenuList.LIVRAISON_AMAPIEN);
		
		semaineViewer = GapViewerUtil.createGapWiever(pe.modeAffichage, this);
		addComponent(semaineViewer.getComponent());
		
		VerticalLayout central = new VerticalLayout();
		addComponent(central);
		
		planning = new VerticalLayout();
		central.addComponent(planning);
		livraison = new VerticalLayout();
		central.addComponent(livraison);
		
		onPopupClose();
	}

	
	


	public void onPopupClose()
	{
		// On efface tout systematiquement  
		planning.removeAllComponents();
		livraison.removeAllComponents();
		
		// Recuperation de l'utilisateur 
		Long idUtilisateur = utilisateurSelector.getUtilisateurId();
		if (idUtilisateur==null)
		{
			return ;
		}
		
		MesLivraisonsDTO res = new MesLivraisonsService().getMesLivraisons(semaineViewer.getDateDebut(),semaineViewer.getDateFin(),SessionManager.getUserRoles(),idUtilisateur);
		
		// Une ligne vide
		BaseUiTools.addEmptyLine(planning);
		
		
		
		// Pour chaque jour, ajout des informations permanence et produits livrés
		for (JourLivraisonsDTO jour : res.jours)
		{
			VerticalLayout vl = BaseUiTools.addPanel(livraison, PANEL_UNJOUR);
			
			String dateMessage = df1.format(jour.date);
			BaseUiTools.addStdLabel(vl, dateMessage, LABEL_DATEJOURLIV);
			
			if (jour.permanences!=null)
			{
				for (InfoPermanence info : jour.permanences)
				{
					String msg = "<br/><h2><i><b>"+
								"Cet amapien est de permanence ce "+df1.format(jour.date)+" ("+info.periodePermanenceDTO.nom+")</br>"+
								"Liste des personnes de permanence : "+info.dateDTO.getNomInscrit()+
								"</i></b></h2>";
					BaseUiTools.addHtmlLabel(vl, msg, "");
				}
			}
			
			
			for (ProducteurLivraisonsDTO producteurLiv : jour.producteurs)
			{
				BaseUiTools.addBandeau(vl, producteurLiv.modeleContrat, "nomcontrat");
				
				for (QteProdDTO cell : producteurLiv.produits)
				{
					String content = cell.qte+" "+cell.nomProduit+" , "+cell.conditionnementProduit;
					BaseUiTools.addStdLabel(vl, content, LABEL_QTEPRODUIT);
				}
				
				if (BaseUiTools.isCompactMode()==false)
				{
					vl.addComponent(new Label("<br/>",ContentMode.HTML));
				}
				
			}
		}	
	}
}
