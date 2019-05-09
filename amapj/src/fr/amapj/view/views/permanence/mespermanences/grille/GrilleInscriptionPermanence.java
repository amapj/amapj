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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.view.views.permanence.grille.AbstractPeriodePermanenceGrillePart;
import fr.amapj.view.views.permanence.mespermanences.MesPermanencesUtils;

/**
 * Permet à un utilisateur de s'inscrire en grille  
 *
 */
public class GrilleInscriptionPermanence extends AbstractPeriodePermanenceGrillePart
{
	private UnePeriodePermanenceDTO periodePermanenceDTO;

	public GrilleInscriptionPermanence(Long idPeriodePermanence,Long userId)
	{
		super(idPeriodePermanence, userId);
	}
	
	@Override
	protected String getHeader()
	{
		periodePermanenceDTO = new MesPermanencesService().loadCompteurPeriodePermanence(dto.id, userId);
		String cpt = MesPermanencesUtils.getLibCompteur(periodePermanenceDTO);
		return cpt;
	}

	@Override
	protected Layout addSpecificButton(PeriodePermanenceDateDTO date)
	{
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("400px");
		hl.setHeight("45px");
		
		
		boolean isInscrit = date.isInscrit(userId);
		boolean isComplet = date.isDateComplete();
		// Indique si il est encore possible de s'inscire ou non pour cette date 
		boolean isModifiable = MesPermanencesUtils.isDateModifiable(periodePermanenceDTO,date);

		//
		if (isModifiable==false)
		{
			return null;
		}
		
		
		switch (periodePermanenceDTO.regleInscription)
		{
		case UNE_INSCRIPTION_PAR_DATE:
			return specificButtonInscriptionUniqueParDate(hl,isInscrit,isComplet,date);

		case MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT:
		case TOUT_AUTORISE:
			return specificButtonInscriptionMultipleParDate(hl,isInscrit,isComplet,date);

		default:
			throw new AmapjRuntimeException();
		}
	}

	/**
	 * Dessin des boutons dans le cas ou l'utilisateur peut s'inscrire une seule fois sur une date 
	 */
	private Layout specificButtonInscriptionUniqueParDate(HorizontalLayout hl, boolean isInscrit, boolean isComplet, PeriodePermanenceDateDTO date)
	{
		// Si l'utilisateur est inscrit pour cette date 
		if (isInscrit)
		{
			Button b = new Button("Je ne souhaite plus venir à cette date.");
			b.addStyleName("suppress-inscrire");
			b.addClickListener(e ->	handleSuppressionInscription(date));
				
			hl.addComponent(b);
			hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
			return hl;
		}
		
		// Si pas de place disponible
		if (isComplet==true)
		{
			return null;
		}
		
		// Cas standard : on peut s'inscrire 
		Button b = new Button("Je m'inscris à cette date");
		b.addStyleName("inscrire");
		b.addClickListener(e ->	handleInscription(date));
			
		hl.addComponent(b);
		hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
		return hl;
	}
	
	private void handleSuppressionInscription(PeriodePermanenceDateDTO date)
	{
		DesinscriptionPopup.open(new DesinscriptionPopup(date, userId), this);
	}

	private void handleInscription(PeriodePermanenceDateDTO date)
	{
		InscriptionPopup.open(new InscriptionPopup(date, userId,dto.id), this); 
	}
	
	
	
	/**
	 * Dessin des boutons dans le cas ou l'utilisateur peut s'inscrire plusieurs fois sur une date 
	 */
	private Layout specificButtonInscriptionMultipleParDate(HorizontalLayout hl, boolean isInscrit, boolean isComplet, PeriodePermanenceDateDTO date)
	{
		// Si l'utilisateur est inscrit pour cette date 
		if (isInscrit)
		{
			Button b = new Button("Je change mon choix pour cette date.");
			b.addStyleName("suppress-inscrire");
			b.addClickListener(e ->	handleMultipleInscription(date));
				
			hl.addComponent(b);
			hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
			return hl;
		}
		
		// Si pas de place disponible
		if (isComplet==true)
		{
			return null;
		}
		
		// Cas standard : on peut s'inscrire 
		Button b = new Button("Je m'inscris à cette date");
		b.addStyleName("inscrire");
		b.addClickListener(e ->	handleMultipleInscription(date));
			
		hl.addComponent(b);
		hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
		return hl;
	}

	private void handleMultipleInscription(PeriodePermanenceDateDTO date)
	{
		if (dto.regleInscription==RegleInscriptionPeriodePermanence.TOUT_AUTORISE)
		{
			InscriptionPopupToutAutorise.open(new InscriptionPopupToutAutorise(date.idPeriodePermanenceDate, userId,dto.id), this);
		}
		else
		{
			InscriptionPopupRoleDifferent.open(new InscriptionPopupRoleDifferent(date.idPeriodePermanenceDate, userId,dto.id), this);
		}
	}

	

	
}
