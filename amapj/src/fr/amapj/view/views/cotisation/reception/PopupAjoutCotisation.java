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
 package fr.amapj.view.views.cotisation.reception;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationUtilisateurDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.popup.formpopup.validator.IValidator;
import fr.amapj.view.engine.popup.formpopup.validator.NotNullValidator;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.views.searcher.SearcherList;

/**
 * Permet l'ajout d'une nouvelle cotisation
 * 
 *
 */
public class PopupAjoutCotisation extends WizardFormPopup
{

	private PeriodeCotisationUtilisateurDTO dto;
	private Long idPeriodeCotisation;


	public enum Step
	{
		SAISIE_MONTANT;
	}

	/**
	 * 
	 */
	public PopupAjoutCotisation(Long idPeriodeCotisation)
	{
		this.idPeriodeCotisation = idPeriodeCotisation;
		this.dto = new PeriodeCotisationUtilisateurDTO();
		dto.idPeriodeCotisation = idPeriodeCotisation;
		
		setWidth(40);
		popupTitle = "Ajout d'une nouvelle cotisation";
		
		item = new BeanItem<PeriodeCotisationUtilisateurDTO>(dto);

	}
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE_MONTANT,()->addFieldInfoGenerales());
	}

	

	private void addFieldInfoGenerales()
	{
		//
		IValidator notNull = new NotNullValidator();
		
		// 
		Searcher s = addSearcher("Nom de l'adhérent", "idUtilisateur", SearcherList.UTILISATEUR_SANS_ADHESION, null,notNull);
		s.setParams(idPeriodeCotisation);
		
		
		// 
		addCurrencyField("Montant", "montantAdhesion",false);

		//
		addComboEnumField("Etat du paiement", "etatPaiementAdhesion",notNull);
		
		//
		addComboEnumField("Type du paiement", "typePaiementAdhesion",notNull);

	}

	
	@Override
	protected void performSauvegarder()
	{
		new GestionCotisationService().createOrUpdateCotisation(true,dto);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
}
