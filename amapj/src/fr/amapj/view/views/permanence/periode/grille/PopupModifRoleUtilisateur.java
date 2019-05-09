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
 package fr.amapj.view.views.permanence.periode.grille;

import java.util.List;

import com.vaadin.data.util.BeanItem;

import fr.amapj.common.FormatUtils;
import fr.amapj.model.models.fichierbase.Utilisateur;
import fr.amapj.model.models.permanence.periode.PermanenceRole;
import fr.amapj.service.services.permanence.detailperiode.DetailPeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.service.services.permanence.role.PermanenceRoleService;
import fr.amapj.view.engine.collectioneditor.CollectionEditor;
import fr.amapj.view.engine.collectioneditor.FieldType;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;
import fr.amapj.view.engine.searcher.SearcherDefinition;
import fr.amapj.view.views.searcher.SDUtilisateurPermanence;
import fr.amapj.view.views.searcher.SearcherList;


/**
 * Permet la modification des roles et des personnes de permanences
 * et d'ajouter des roles 
 */
public class PopupModifRoleUtilisateur extends WizardFormPopup
{
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
	
	
	public PopupModifRoleUtilisateur(Long idPeriodePermanenceDate)
	{
		setWidth(80);
		

		dto = new PeriodePermanenceService().loadOneDatePermanence(idPeriodePermanenceDate); 
		
		item = new BeanItem<PeriodePermanenceDateDTO>(dto);
		
		popupTitle = "Modification du "+FormatUtils.getFullDate().format(dto.datePerm);
	}
	
	

	private void addFieldUtilisateurs()
	{
		// Titre
		setStepTitle("les rôles et les personnes de permanence");
		
		List<Utilisateur> allowed = new DetailPeriodePermanenceService().computeAllowedUser(dto.idPeriodePermanenceDate);
		SearcherDefinition searcher = new SDUtilisateurPermanence(allowed);
		
		Long idDefaultRole = new PermanenceRoleService().getIdDefaultRole();
		
		CollectionEditor<PermanenceCellDTO> f1 = new CollectionEditor<PermanenceCellDTO>("", (BeanItem) item, "permanenceCellDTOs", PermanenceCellDTO.class);
		f1.addSearcherColumn("idRole", "Role",FieldType.SEARCHER, idDefaultRole,SearcherList.PERIODE_PERMANENCE_ROLE,null);
		f1.addSearcherColumn("idUtilisateur", "Adhérent",FieldType.SEARCHER, null,searcher,null);
		
		binder.bind(f1, "permanenceCellDTOs");
		form.addComponent(f1);

	}
	
	
	private String checkFieldUtilisateur()
	{
		for (int i = 0; i < dto.permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = dto.permanenceCellDTOs.get(i);
			
			if (lig.idRole==null)
			{
				return "Il faut définir un rôle à la ligne "+(i+1);
			}
		}	
		
		// On vérifie ensuite que les regles d'inscriptions sont bien respectées
		return dto.checkRegleInscription();
	}
	
	
	
	
	
	private boolean isPresent(Long idUtilisateur, List<PermanenceCellDTO> permanenceCellDTOs, int indexNotToCheck)
	{
		for (int i = 0; i < dto.permanenceCellDTOs.size(); i++)
		{
			PermanenceCellDTO lig = dto.permanenceCellDTOs.get(i);
			if ( (i!=indexNotToCheck) && (lig.idUtilisateur==idUtilisateur) )
			{
				return true;
			}
		}
		return false;
	}


	@Override
	protected void performSauvegarder()
	{
		new DetailPeriodePermanenceService().updateRoleAndUtilisateur(dto);
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	
}
