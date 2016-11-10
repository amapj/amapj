/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.views.mespermanences;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.service.services.edgenerator.excel.EGPlanningPermanence;
import fr.amapj.service.services.saisiepermanence.MesPermanencesDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;
import fr.amapj.service.services.saisiepermanence.PermanenceUtilisateurDTO;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.common.semaineviewer.SemaineViewer;


/**
 * Page permettant à l'utilisateur de visualiser ses distributions
 * et le planning global des distributions
 * 
 */
@SuppressWarnings("serial")
public class PlanningPermanenceView extends FrontOfficeView implements View , PopupListener
{

	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	
	private Label mesDistributions;
		
	private Label livraison;
	
	private SemaineViewer semaineViewer;

	public String getMainStyleName()
	{
		return "planning-permanence";
	}
	
	/**
	 * 
	 */
	@Override
	public void enter()
	{
		
		// Partie haute
		BaseUiTools.addStdLabel(this, "Les dates de mes permanences","titre");
		VerticalLayout vl1 = BaseUiTools.addPanel(this, "mes-permanences");
		mesDistributions = BaseUiTools.addHtmlLabel(vl1, "", "ligne");

		// Partie basse
		BaseUiTools.addStdLabel(this, "Le planning semaine par semaine","titre");
		VerticalLayout vl2 = BaseUiTools.addPanel(this, "semaine");
		
		
		semaineViewer = new SemaineViewer(this);
		vl2.addComponent(semaineViewer.getComponent());
		
		//
		livraison = new Label("",ContentMode.HTML);
		livraison.addStyleName("contenu-semaine");
		vl2.addComponent(livraison);
		vl2.setComponentAlignment(livraison, Alignment.MIDDLE_CENTER);
	
		// 
		Link link = LinkCreator.createLink(new EGPlanningPermanence(DateUtils.getDate()));
		addComponent(link);
		
		onPopupClose();
			
	}

	
	
	


	public void onPopupClose()
	{
		MesPermanencesDTO res = new PermanenceService().getMesDistributions(semaineViewer.getDate());
		
		// Partie haute
		StringBuffer buf = new StringBuffer();
		buf.append("Vous devez faire les permanences suivantes :<ul>");
		for (PermanenceDTO distribution : res.permanencesFutures)
		{
			buf.append("<li>"+df1.format(distribution.datePermanence)+"</li>");	
		}
		buf.append("</ul>");
		mesDistributions.setValue(buf.toString());
		
		// Partie basse
		semaineViewer.updateTitreValue(res.dateDebut, res.dateFin);
		
		
		buf = new StringBuffer();
		
		for (PermanenceDTO distribution : res.permanencesSemaine)
		{
			buf.append("<br/><br/><b><center>"+df1.format(distribution.datePermanence)+"</b></center>");		
			for (PermanenceUtilisateurDTO utilisateur : distribution.permanenceUtilisateurs)
			{
				buf.append("<br/><center>"+utilisateur.nom+" "+utilisateur.prenom+"</center>");
			}
		}
		
		livraison.setValue(buf.toString());
		
		
	}
}
