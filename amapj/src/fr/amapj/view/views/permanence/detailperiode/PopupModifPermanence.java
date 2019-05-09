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
 package fr.amapj.view.views.permanence.detailperiode;

import java.util.List;

import com.vaadin.ui.ComboBox;

import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.searcher.SearcherDefinition;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;
import fr.amapj.view.views.searcher.SDUtilisateurPermanence;


/**
 * Permet la modification des personnes de permanences
 */
public class PopupModifPermanence extends WizardFormPopup
{

	private ComplexTableBuilder<PermanenceCellDTO> builder;
	
	PeriodePermanenceDateDTO dto;

	static public enum Step
	{
		CHOIX_UTILISATEURS;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.CHOIX_UTILISATEURS,()->addFieldUtilisateurs(),()->checkFieldUtilisateur());
	}
	
	
	public PopupModifPermanence(Long idPeriodePermanenceDate)
	{
		setWidth(80);
		popupTitle = "Modification d'une permanence";

		dto = new PeriodePermanenceService().loadOneDatePermanence(idPeriodePermanenceDate); 
		
	}
	
	

	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("les personnes de permanence");
		
		List<Utilisateur> allowed = new DetailPeriodePermanenceService().computeAllowedUser(dto.idPeriodePermanenceDate);
		SearcherDefinition searcher = new SDUtilisateurPermanence(allowed);
		
	
		builder = new ComplexTableBuilder<PermanenceCellDTO>(dto.permanenceCellDTOs);
		builder.setPageLength(8);
		
		
		builder.addString("Role", false, 300, e->e.nomRole);
		builder.addSearcher("Adhérent", "cb",true, 300, e->e.idUtilisateur, searcher);
		
		addComplexTable(builder);
		
		
		/** TODO Ajouter le surnombre 
		Button b = new Button("Ajouter un adhérent");
		b.addClickListener(e->addAdherent());
		form.addComponent(b);
		
		Button d = new Button("Supprimer un adhérent");
		d.addClickListener(e->delAdherent());
		form.addComponent(d);*/

		
	}
	
	private String checkFieldUtilisateur()
	{
		for (int i = 0; i < dto.permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = dto.permanenceCellDTOs.get(i);
			
			// 
			ComboBox cb = (ComboBox) builder.getComponent(i, "cb");
			
			lig.idUtilisateur = (Long) cb.getConvertedValue();
		}	
		
		// On vérifie ensuite que les regles d'inscriptions sont bien respectées
		return dto.checkRegleInscription();

	}


	@Override
	protected void performSauvegarder()
	{
		new DetailPeriodePermanenceService().updateDetailPeriodePermanence(dto);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	
}
