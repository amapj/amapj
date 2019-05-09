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
import com.vaadin.ui.TextField;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;

/**
 * Permet de modifier le nombre de participations des utilisateurs 
 */
public class PopupUpdateNbParticipationForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	private ComplexTableBuilder<PeriodePermanenceUtilisateurDTO> builder;
	
	private List<PeriodePermanenceUtilisateurDTO> existingUtilisateurs;
	
	static public enum Step
	{
		INFOS , AJUSTER , CONFIRMATION
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.INFOS, ()->infos());
		add(Step.AJUSTER,()->addFieldAjuster(),()->checkAjuster());
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
	public PopupUpdateNbParticipationForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Modifier le nombre de participations des utilisateurs";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		// On sauvegarde dans une autre variable la liste des utilisateurs existants
		existingUtilisateurs = new ArrayList<PeriodePermanenceUtilisateurDTO>();
		for (PeriodePermanenceUtilisateurDTO ut : dto.utilisateurs)
		{
			existingUtilisateurs.add(ut.clone());	
		}
				
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}

	
	private void infos()
	{
		// Titre
		setStepTitle(" les informations générales.");
		
		//
		addLabel("Cet outil va vous permettre de modifier le nombre de participations des utilisateurs, même si des adhérents sont déjà inscrits", ContentMode.HTML);
	}
	
	private void addFieldAjuster()
	{		
		// Titre
		setStepTitle("nombre de participation");
		
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
		builder.addInteger("Nb participation", "nb", true, 100,  e->e.nbParticipation);
				
		addComplexTable(builder);
		
		
	}
	
	
	private String checkAjuster()
	{
		for (int i = 0; i < dto.utilisateurs.size(); i++)
		{
			PeriodePermanenceUtilisateurDTO lig = dto.utilisateurs.get(i);
			
			// 
			TextField tf = (TextField) builder.getComponent(i, "nb");
			
			if (tf.getConvertedValue()==null)
			{
				return "Il faut saisir une valeur pour "+lig.nom+" "+lig.prenom;
			}
			
			int nb = (Integer) tf.getConvertedValue();
			if (nb<0)
			{
				return "La valeur est négative pour "+lig.nom+" "+lig.prenom;
			}
			
			lig.nbParticipation = nb;
		
		}	
				
		return null;
	}
	
	
	/**
	 *  
	 */
	private void confirmation()
	{
		String info = new PeriodePermanenceUpdateService().getUpdateNbParticipationInfo(dto,existingUtilisateurs);
		// Titre
		setStepTitle("confirmation avant modification");
		
		
		addLabel("Vous allez apporter les modifications suivantes sur cette période de permanence:",ContentMode.HTML);
		
		addLabel(info,ContentMode.HTML);
		
		addLabel("Appuyez sur Sauvegarder pour réaliser cette modification, ou Annuler pour ne rien modifier",ContentMode.HTML);
		
	}


	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new PeriodePermanenceUpdateService().performUpdateNbParticipation(dto);
	}
	
}
