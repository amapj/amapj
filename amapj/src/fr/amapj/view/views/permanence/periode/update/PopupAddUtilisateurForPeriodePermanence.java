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
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceUtilisateurDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.update.PeriodePermanenceUpdateService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.tools.table.complex.ComplexTableBuilder;
import fr.amapj.view.views.permanence.periode.PopupSaisieUtilisateur;

/**
 * Permet d'ajouter des utilisateurs 
 */
public class PopupAddUtilisateurForPeriodePermanence extends WizardFormPopup
{
	
	protected PeriodePermanenceDTO dto;
	
	private List<PeriodePermanenceUtilisateurDTO> existingUtilisateurs;
	
	private ComplexTableBuilder<PeriodePermanenceUtilisateurDTO> builder;
	
	static public enum Step
	{
		CHOIX_UTILISATEURS , AJUSTER
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.CHOIX_UTILISATEURS,()->addFieldUtilisateurs(),()->checkUtilisateur());
		add(Step.AJUSTER,()->addFieldAjuster(),()->checkAjuster());
	}
	

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	
	
	/**
	 * 
	 */
	public PopupAddUtilisateurForPeriodePermanence(Long id)
	{
		super();
		popupTitle = "Ajouter des utilisateurs à une période de permanence";
		setWidth(80);
				
		// Chargement de l'objet  à modifier
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(id);
		
		// On sauvegarde dans une autre variable la liste des utilisateurs existants
		existingUtilisateurs = new ArrayList<PeriodePermanenceUtilisateurDTO>();
		existingUtilisateurs.addAll(dto.utilisateurs);
		
		// On efface la liste des utilisateurs déjà présentses dans le dto  
		dto.utilisateurs.clear();
		
		item = new BeanItem<PeriodePermanenceDTO>(dto);
		
	}

	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("les personnes à ajouter");
		
	
		builder = new ComplexTableBuilder<PeriodePermanenceUtilisateurDTO>(dto.utilisateurs);
		builder.setPageLength(14);
		
		builder.addString("Nom", false, 300, e->e.nom);
		builder.addString("Prénom", false, 300,  e->e.prenom);
				
		addComplexTable(builder);
		
		Button b = new Button("Ajouter un adhérent");
		b.addClickListener(e->addAdherent());
		form.addComponent(b);
		
		Button d = new Button("Supprimer un adhérent");
		d.addClickListener(e->delAdherent());
		form.addComponent(d);

		
	}
	

	private void addAdherent()
	{
		PopupSaisieUtilisateur.open(new PopupSaisieUtilisateur(dto,existingUtilisateurs), ()->endAddAdherent());
	}
	
	private void endAddAdherent()
	{
		builder.reload(dto.utilisateurs);
	}
	
	
	private void delAdherent()
	{
		PeriodePermanenceUtilisateurDTO detail = builder.getSelectedLine();
		if (detail!=null)
		{
			dto.utilisateurs.remove(detail);
			builder.reload(dto.utilisateurs);
			Notification.show("Suppression", "Suppression faite", Notification.Type.HUMANIZED_MESSAGE);
		}
		else
		{
			Notification.show("Impossible", "Merci de sélectionner une ligne pour pouvoir lancer la suppression", Notification.Type.HUMANIZED_MESSAGE);
		}
	}
	
	private String checkUtilisateur()
	{
		if (dto.utilisateurs.size()==0)
		{
			return "Il faut au minimum 1 personne";
		}
			
		return null;
	}


	
	
	
	private void addFieldAjuster()
	{		
		// Titre
		setStepTitle("ajustement");
		
	
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


	protected void performSauvegarder()
	{	
		// Sauvegarde du contrat
		new PeriodePermanenceUpdateService().addUtilisateurs(dto);
	}
	
}
