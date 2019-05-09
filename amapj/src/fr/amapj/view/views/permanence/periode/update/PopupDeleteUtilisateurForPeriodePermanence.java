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
 package fr.amapj.view.views.permanence.periode.update;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de supprimer des utilisateurs d'une permanence 
 */
public class PopupDeleteUtilisateurForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	private ComplexTableBuilder<PeriodePermanenceUtilisateurDTO> builder;
	
	private List<PeriodePermanenceUtilisateurDTO> utilisateurToSuppress;
	
	static public enum Step
	{
		INFOS , CHOIX_UTILISATEURS , CONFIRMATION
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFOS, ()->infos());
		add(Step.CHOIX_UTILISATEURS,()->addFieldUtilisateurs(),()->checkUtilisateur());
		add(Step.CONFIRMATION, ()->confirmation());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupDeleteUtilisateurForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Enlever des utilisateurs de cette période de permanence";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		utilisateurToSuppress = new ArrayList<PeriodePermanenceUtilisateurDTO>();
				
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}

	
	private void infos()
	{
		// Titre
		setStepTitle(" les informations générales.");
		
		//
		addLabel("Cet outil va vous permettre d'enlever des utilisateurs de cette période de permanence, même si des utilisateurs sont déjà inscrits", ContentMode.HTML);
	}
	
	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("choix des les personnes à enlever");
		
		addLabel("Veuillez cocher en face des personnes à enelever de cette periode de permanence", ContentMode.HTML);
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
		builder.addCheckBox("A enlever", "cb", true, 150, e->e.toSuppress, null);
				
		addComplexTable(builder);
		
	}
	

	private String checkUtilisateur()
	{
		utilisateurToSuppress.clear();
		
		
		for (int i = 0; i < dto.utilisateurs.size(); i++)
		{
			PeriodePermanenceUtilisateurDTO lig = dto.utilisateurs.get(i);
			
			// 
			CheckBox cb = (CheckBox) builder.getComponent(i, "cb");
			
			if (cb.getValue()==true)
			{
				utilisateurToSuppress.add(lig);
			}
		}	
		
		if (utilisateurToSuppress.size()==0)
		{
			return "Vous devez supprimer au moins un utilisateur pour pouvoir continuer.";
		}
		
		return null;
	}
	
	
	/**
	 *  
	 */
	private void confirmation()
	{
		String info = new PeriodePermanenceUpdateService().getDeleteUtilisateurInfo(utilisateurToSuppress,dto.id);
		
		// Titre
		setStepTitle("confirmation avant modification");
		
		
		addLabel("Vous allez apporter les modifications suivantes sur cette période de permanence:",ContentMode.HTML);
		
		addLabel(info,ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier",ContentMode.HTML);
		
	}


	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new PeriodePermanenceUpdateService().performDeleteUtilisateur(dto.id,utilisateurToSuppress);
	}
	
}
