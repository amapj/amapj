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
 package fr.amapj.view.views.permanence.periode;

import java.util.Collections;
import java.util.List;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SDUtilisateurPeriodePermanence;

/**
 * Popup pour la saisie de l'utilisateur
 *  
 */
public class PopupSaisieUtilisateur extends OKCancelPopup
{
	
	private Searcher box;
	
	private PeriodePermanenceDTO dto;
	
	private List<PeriodePermanenceUtilisateurDTO> toExclude;
	
	/**
	 * 
	 */
	public PopupSaisieUtilisateur(PeriodePermanenceDTO dto,List<PeriodePermanenceUtilisateurDTO> toExclude)
	{
		this.dto = dto;
		this.toExclude = toExclude;
		this.saveButtonTitle = "OK";
		
		popupTitle = "Selection de l'adhérent";
		
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		FormLayout f = new FormLayout();
		
		box = new Searcher(new SDUtilisateurPeriodePermanence(dto,toExclude));
		
		
		box.setWidth("80%");
		f.addComponent(box);
		contentLayout.addComponent(f);
		
		
		
	}

	protected boolean performSauvegarder()
	{
		Long userId = (Long) box.getConvertedValue();
		if (userId==null)
		{
			return false;
		}
	
		PeriodePermanenceUtilisateurDTO detail = new PeriodePermanenceService().createAffectAdherentDetailDTO(userId);
		dto.utilisateurs.add(detail);
		
		Collections.sort(dto.utilisateurs,(p1,p2)->compare(p1,p2));
		
		return true;
	}


	private int compare(PeriodePermanenceUtilisateurDTO p1, PeriodePermanenceUtilisateurDTO p2)
	{
		String n1 = p1.nom+" "+p1.prenom;
		String n2 = p2.nom+" "+p2.prenom;
		return n1.compareTo(n2);
	}

}
