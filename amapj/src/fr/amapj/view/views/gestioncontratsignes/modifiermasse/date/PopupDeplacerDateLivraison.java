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
 package fr.amapj.view.views.gestioncontratsignes.modifiermasse.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.common.DateUtils;
import fr.amapj.service.services.gestioncontratsigne.DeplacerDateLivraisonDTO;
import fr.amapj.service.services.gestioncontratsigne.DeplacerDateLivraisonDTO.ModifDateLivraisonDTO;
import fr.amapj.service.services.gestioncontratsigne.GestionContratSigneService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;

/**
 * Popup 
 * 
 *
 */
public class PopupDeplacerDateLivraison extends WizardFormPopup
{

	private DeplacerDateLivraisonDTO deplacerDto;


	public enum Step
	{
		INFO_GENERALES, SAISIE_DATE , CONFIRMATION;
	}

	/**
	 * 
	 */
	public PopupDeplacerDateLivraison(Long mcId)
	{
		setWidth(80);
		popupTitle = "Déplacer une date de livraison";

		// Chargement de l'objet à créer
		deplacerDto = new GestionContratSigneService().getDeplacerDateLivraisonDTO(mcId);
		
		item = new BeanItem<DeplacerDateLivraisonDTO>(deplacerDto);

	}
	
	@Override
	protected void configure()
	{
		add(Step.INFO_GENERALES,()->addFieldInfoGenerales());
		add(Step.SAISIE_DATE,()->addFieldSaisieDate());
		add(Step.CONFIRMATION,()->addFieldConfirmation());
	}

	private void addFieldInfoGenerales()
	{
		// Titre
		setStepTitle("les informations générales.");
		
		String str = 	"Cet outil va vous permettre de déplacer une date de livraison, pour tous les adhérents à ce contrat</br>"+
						"<br/>"+
						"Exemple de cas d'utilisation : un producteur a prévu de livrer ses produits le 20 janvier<br/>"+
						"Il est obligé de décaler au 27 janvier pour une raison quelconque.<br/>"+
						"Cet outil permet de déplacer la date, en gardant à l'identique les quantités commandées<br/>"+
						"<br/><br/>"+
						"Cet outil affiche la liste des e mails des personnes impactées, vous pourrez alors les avertir.<br/>";
										
		
		addLabel(str, ContentMode.HTML);
		

	}
	
	

	private void addFieldSaisieDate()
	{
		// Titre
		setStepTitle("le choix de la date à déplacer");
			
		SimpleDateFormat df = new SimpleDateFormat("EEEEE dd/MM/yyyy");
		//
		addGeneralComboField("La date à déplacer",deplacerDto.dateLivraisonDTOs,"selected",e->df.format(e.dateLiv));
		
		addDateField("La nouvelle date", "actualDate", new NotNullValidator());
		

	}
	
	private void addFieldConfirmation()
	{
		// Titre
		setStepTitle("confirmation");
		
		
		if (isModifPossible())
		{
		
			String info = new GestionContratSigneService().getDeplacerInfo(deplacerDto.selected.idModeleContratDate);
			
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String str = "La livraison du "+df.format(deplacerDto.selected.dateLiv)+" va être déplacée au "+df.format(deplacerDto.actualDate)+"."; 
			
			addLabel(str, ContentMode.HTML);
			
			addLabel(info, ContentMode.HTML);
			
			addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier", ContentMode.HTML);
		}
		else
		{
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String str = "La nouvelle date de livraison "+df.format(deplacerDto.actualDate)+" est déjà une date de livraison pour ce contrat."; 
			
			addLabel("Cette modification est impossible.", ContentMode.HTML);
			
			addLabel(str, ContentMode.HTML);
			
			addLabel("Appuyez sur Etape précédente pour modifier votre choix, ou Annuler pour abandonner", ContentMode.HTML);
			
		}
		
	}


	

	private boolean isModifPossible()
	{
		Date ref = DateUtils.suppressTime(deplacerDto.actualDate);
		
		for (ModifDateLivraisonDTO md : deplacerDto.dateLivraisonDTOs)
		{
			if (md.dateLiv.equals(ref))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	protected void performSauvegarder()
	{
		if (isModifPossible())
		{
			new GestionContratSigneService().performDeplacerDateLivraison(deplacerDto);
		}
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	
}
