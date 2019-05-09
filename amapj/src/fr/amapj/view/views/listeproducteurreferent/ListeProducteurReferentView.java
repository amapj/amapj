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
 package fr.amapj.view.views.listeproducteurreferent;

import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.listeproducteurreferent.DetailProducteurDTO;
import fr.amapj.service.services.listeproducteurreferent.ListeProducteurReferentService;
import fr.amapj.service.services.producteur.ProdUtilisateurDTO;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;
import fr.amapj.view.engine.template.FrontOfficeView;


/**
 * Page permettant à l'utilisateur de visualiser tous les producteurs et les référents
 * 
 *  
 *
 */
public class ListeProducteurReferentView extends FrontOfficeView
{
	
	
	
	static private String LABEL_TITRE = "titre";
	static private String LABEL_LIGNE = "ligne";
		
	static private String PANEL_PRODUCTEUR = "producteur";

	
	public String getMainStyleName()
	{
		return "producteurreferent";
	}

	/**
	 * 
	 */
	@Override
	public void enter()
	{
		
		List<DetailProducteurDTO> dtos = new ListeProducteurReferentService().getAllProducteurs();
		
		
		
		for (DetailProducteurDTO detailProducteurDTO : dtos)
		{
			Panel p0 = new Panel();
			p0.setWidth("100%");
			p0.addStyleName(PANEL_PRODUCTEUR);
			
			VerticalLayout vl1 = new VerticalLayout();
			vl1.setMargin(true);
			p0.setContent(vl1);
			addComponent(p0);
			
			// Le titre		
			String str = "Producteur : "+detailProducteurDTO.nom;
			Label l = new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_TITRE);
			vl1.addComponent(l);
			
			
			
			str = detailProducteurDTO.description;
			if (str!=null)
			{
				l =new Label(str, ContentMode.HTML);
				l.addStyleName(LABEL_LIGNE);
				vl1.addComponent(l);
			}
			
			str = formatUtilisateur(detailProducteurDTO.utilisateurs);
			l =new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_LIGNE);
			vl1.addComponent(l);
			
			str = formatReferent(detailProducteurDTO.referents);
			l =new Label(str, ContentMode.HTML);
			l.addStyleName(LABEL_LIGNE);
			vl1.addComponent(l);
		}
	}
	
	
	
	
	
	
	private String formatUtilisateur(List<ProdUtilisateurDTO> utilisateurs)
	{
		if (utilisateurs.size()==0)
		{
			return "";
		}
		
		String str = UtilisateurUtil.asStringPrenomFirst(utilisateurs, " et ");
		
		if (utilisateurs.size()==1)
		{
			return "Le producteur est "+str+"<br/>";
		}
		else
		{
			return "Les producteurs sont  "+str+"<br/>";
		}
	}
	
	
	private String formatReferent(List<ProdUtilisateurDTO> utilisateurs)
	{
		if (utilisateurs.size()==0)
		{
			return "";
		}
		
		String str = UtilisateurUtil.asStringPrenomFirst(utilisateurs, " et ");
		
		if (utilisateurs.size()==1)
		{
			return "Le référent est "+str+"<br/>";
		}
		else
		{
			return "Les référents sont "+str+"<br/>";
		}
	}
}
