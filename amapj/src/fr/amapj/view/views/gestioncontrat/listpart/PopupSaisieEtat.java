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
 package fr.amapj.view.views.gestioncontrat.listpart;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.OptionGroup;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.contrat.modele.EtatModeleContrat;
import fr.amapj.service.services.archivage.ArchivageContratService;
import fr.amapj.service.services.gestioncontrat.GestionContratService;
import fr.amapj.service.services.gestioncontrat.ModeleContratSummaryDTO;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Popup pour la saisie de l'état du contrat
 *  
 */
public class PopupSaisieEtat extends WizardFormPopup
{
	private ModeleContratSummaryDTO mcDto;
	
	private OptionGroup group;
	
	private EtatModeleContrat selectedValue;
	
	
	static public enum Step
	{
		SAISIE , CONFIRMATION;	
	}
	
	
	@Override
	protected void configure()
	{
		add(Step.SAISIE,()->addSaisie(),()->checkSaisie());
		add(Step.CONFIRMATION, ()->addConfirmation());
	}
	
	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
	

	/**
	 * 
	 */
	public PopupSaisieEtat(ModeleContratSummaryDTO mcDto)
	{
		popupTitle = "Changement de l'état d'un contrat";
		this.mcDto = mcDto;
	}
	
	
	protected void addSaisie()
	{
		String intro = computeIntro();
		addLabel(intro, ContentMode.HTML);
		
		
		
		addLabel("Veuillez maintenant choisir le nouvel état de votre contrat", ContentMode.HTML);
		
		
		group = new OptionGroup();
		group.setHtmlContentAllowed(true);
		
		addLine(EtatModeleContrat.CREATION);
		
		addLine(EtatModeleContrat.ACTIF);
		
		addLine(EtatModeleContrat.ARCHIVE);
		
		form.addComponent(group);
		
			
	}

	private String computeIntro()
	{
		String str = "Votre contrat est actuellement dans l'état "+mcDto.etat+".<br/>";
		
		switch (mcDto.etat)
		{
		case CREATION:
			str = str +"Pour le moment, votre contrat n'est pas visible par les amapiens. Si vous le passez à l'état ACTIF, alors les amapiens pourront s'inscrire à ce contrat.";
			break;

		case ACTIF:
			str = str +"Il est donc visible par tous les amapiens.";
			break;
		}
		
		str = str +"<br/><br/>";
		
		return str;
	}
	
	
	static public class Line
	{
		public EtatModeleContrat etat;
		
		public Line(EtatModeleContrat etat)
		{
			this.etat = etat;
		}
		
		public String toString()
		{
			return etat+"<br/><br/>";
		}
		
	}


	private void addLine(EtatModeleContrat etatToDisplay)
	{
		Line line = new Line(etatToDisplay);
		group.addItem(line);
		
		if (etatToDisplay==mcDto.etat)
		{
			group.setItemEnabled(line, false);
		}
	}
	
	
	private String checkSaisie()
	{
		selectedValue = ((Line) group.getValue()).etat;
		
		if (selectedValue==null)
		{
			return "Vous devez saisir une valeur";
		}
			
		switch (mcDto.etat)
		{
		case CREATION:
			return checkFromCreationTo(selectedValue);

		case ACTIF:
			return checkFromActifTo(selectedValue);

		default:
			throw new AmapjRuntimeException("mcDto.etat="+mcDto.etat);
		}
		
		
	}
	
	


	private String checkFromCreationTo(EtatModeleContrat etatModeleContrat)
	{
		switch (etatModeleContrat)
		{
		// CREATION vers ACTIF : toujours possible 
		case ACTIF:
			return null;
		
		// CREATION vers ARCHIVE : toujours  impossible 
		case ARCHIVE:
			return "Vous ne pouvez pas passer un contrat de l'état CREATION vers ARCHIVE. Il doit d'abord passer par l'état ACTIF";
		
		default:
			throw new AmapjRuntimeException("etatModeleContrat="+etatModeleContrat);
		}
		
	}


	private String checkFromActifTo(EtatModeleContrat etatModeleContrat)
	{
		switch (etatModeleContrat)
		{
		// ACTIF vers CREATION : toujours possible 
		case CREATION:
			return null;
		
		// ACTIF vers ARCHIVE : possible apres verification 
		case ARCHIVE:
			String str = new ArchivageContratService().checkIfArchivable(mcDto.id);
			if (str!=null)
			{
				str = "Vous ne pouvez pas archiver ce contrat.<br/><br/>"+str;
			}
			return str;
		
		default:
			throw new AmapjRuntimeException("etatModeleContrat="+etatModeleContrat);
		}
	}

	
	private void addConfirmation()
	{
		String str = "Vous allez passer votre contrat du statut "+mcDto.etat+" au statut "+selectedValue+".<br/><br/>Cliquez sur Sauvegardez pour confirmer cette modification, ou Annuler pour ne rien faire.";
		
		addLabel(str, ContentMode.HTML);
	}
	
	

	protected void performSauvegarder()
	{
		new GestionContratService().updateEtat(selectedValue,mcDto.id);
	}
}
