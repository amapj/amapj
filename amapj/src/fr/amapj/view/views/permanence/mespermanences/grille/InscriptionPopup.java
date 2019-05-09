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
 package fr.amapj.view.views.permanence.mespermanences.grille;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.FormatUtils;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService.InscriptionMessage;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SDRoleAllowed;

/**
 * Popup pour se desinscrire
 *  
 */
public class InscriptionPopup extends OKCancelPopup
{
	
	private PeriodePermanenceDateDTO date;
	
	private Long userId;
	
	private Long idPeriodePermanence;
	
	private Long selectedRole;
	
	private Searcher box;
	
	/**
	 * 
	 */
	public InscriptionPopup(PeriodePermanenceDateDTO date,Long userId,Long idPeriodePermanence)
	{
		this.userId = userId;
		this.idPeriodePermanence = idPeriodePermanence;
		
		popupTitle = "S'inscrire";
		this.date = date;
	}
	
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		//
		UnePeriodePermanenceDTO periodePermanenceDTO = new MesPermanencesService().loadCompteurPeriodePermanence(idPeriodePermanence, userId);
		
		//
		if (periodePermanenceDTO.nbInscription>=periodePermanenceDTO.nbSouhaite)
		{
			Label l = new Label("Il est impossible de vous inscrire car vous êtes déjà inscrit sur suffisamment de dates.");
			contentLayout.addComponent(l);
			hasSaveButton = false;
			return;
		}
		
		// On verifie si il faut faire un choix du rôle 
		List<Long> roleIds = findRolesId();
		
		if (roleIds.size()==0)
		{
			Label l = new Label("Impossible de vous inscrire - plus de roles.");
			contentLayout.addComponent(l);
			hasSaveButton = false;
			return;
		}
		
		
		if (roleIds.size()==1)
		{
			Label l = new Label("Etes vous sûr de vouloir vous inscrire pour la date du "+FormatUtils.getFullDate().format(date.datePerm)+" ? ");
			contentLayout.addComponent(l);
			selectedRole = roleIds.get(0);
			return;
		}
		
	
		Label l = new Label("Veuillez choisir votre rôle pour vous inscrire à la date du "+FormatUtils.getFullDate().format(date.datePerm)+" : ");
		contentLayout.addComponent(l);
		
		FormLayout f = new FormLayout();
		box = new Searcher(new SDRoleAllowed(roleIds));
		
		box.setWidth("80%");
		f.addComponent(box);
		contentLayout.addComponent(f);

		
	}

	private List<Long> findRolesId()
	{
		List<Long> res = new ArrayList<Long>();	
		
		for (PermanenceCellDTO cell : date.permanenceCellDTOs)
		{
			if ( (cell.idUtilisateur==null) && (res.contains(cell.idRole)==false) )
			{
				res.add(cell.idRole);
			}
		}
		return res;
	}


	protected boolean performSauvegarder()
	{
		if (selectedRole==null)
		{
			selectedRole = (Long) box.getConvertedValue();
			if (selectedRole==null)
			{
				return false;
			}
		}
		
		InscriptionMessage msg = new MesPermanencesService().inscription(userId,date.idPeriodePermanenceDate,selectedRole,RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE);
		if (msg!=null)
		{
			String lib = computeLib(msg);
			MessagePopup popup = new MessagePopup("Impossible de s'inscrire",ColorStyle.RED,"Vous ne pouvez pas vous inscrire car "+lib);
			MessagePopup.open(popup);
		}
		return true;
	}

	static public  String computeLib(InscriptionMessage msg)
	{
		switch (msg)
		{
		case DEJA_INSCRIT_CETTE_DATE:
			return "vous êtes déjà inscrit à cette date.";
		
		case NOMBRE_SUFFISANT:
			return "Vous êtes inscrit un nombre suffisant de fois sur la période.";
	
		case PAS_DE_PLACE_CETTE_DATE:
			return "il n'y a plus de place disponible à cette date.";
			
		case PLACE_NON_DISPONIBLE:
			return "cette place n'est plus disponible.";

			
		}
		return null;
	}
	
}
