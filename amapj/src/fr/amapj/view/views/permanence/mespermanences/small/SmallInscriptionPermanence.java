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
 package fr.amapj.view.views.permanence.mespermanences.small;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService.InscriptionMessage;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.common.gapviewer.DatePerDateViewer;
import fr.amapj.view.views.permanence.mespermanences.MesPermanencesUtils;
import fr.amapj.view.views.permanence.mespermanences.grille.InscriptionPopup;

/**
 * Popup permettant le choix des permanences par l'amapien 
 *  
 */
public class SmallInscriptionPermanence extends OKCancelPopup implements PopupListener
{
	
	private Long userId;
	
	private PeriodePermanenceDTO dto;
	
	private UnePeriodePermanenceDTO periodePermanenceDTO;
	
	
	private VerticalLayout compteur;
	
	private VerticalLayout planning;
	
	private DatePerDateViewer<PeriodePermanenceDateDTO> dateViewer;
	
	// Permet de choisir entre le mode visualiser seul et le mode modifier
	private boolean visualiser;
		
	/**
	 * 
	 */
	public SmallInscriptionPermanence(Long idPeriodePermanence , Long userId,boolean visualiser)
	{
		this.userId = userId;
		this.visualiser = visualiser;
		this.dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		
		if (visualiser)
		{
			popupTitle = "Visualisation des permanences"; 
		}
		else
		{
			popupTitle = "Choix des permanences";
		}
		hasSaveButton = false;
		cancelButtonTitle = "OK";
		
		computeSouhaiteeReelle();
		
		
	}
	
	private void computeSouhaiteeReelle()
	{
		periodePermanenceDTO = new MesPermanencesService().loadCompteurPeriodePermanence(dto.id, userId);
	}

	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		compteur = new VerticalLayout();
		compteur.addStyleName("popup-choix-permanence");
		contentLayout.addComponent(compteur);
		
		
		dateViewer = new DatePerDateViewer<PeriodePermanenceDateDTO>(dto.datePerms,e->e.datePerm,this); 
		contentLayout.addComponent(dateViewer.getComponent());
		
		VerticalLayout central = new VerticalLayout();
		contentLayout.addComponent(central);
		
		planning = new VerticalLayout();
		planning.addStyleName("popup-choix-permanence");
		central.addComponent(planning);
		
		
		onPopupClose();
	}

	@Override
	// Never used
	protected boolean performSauvegarder()
	{
		return true;
	}

	@Override
	public void onPopupClose()
	{
		//
		compteur.removeAllComponents();
		if (visualiser==false)
		{
			String cpt = MesPermanencesUtils.getLibCompteur(periodePermanenceDTO);
			BaseUiTools.addHtmlLabel(compteur, cpt, "compteur");
		}
		
		
		//
		planning.removeAllComponents();
		
		PeriodePermanenceDateDTO detail = dateViewer.getDate();
		
		String msg = getMessageListeInscrit(detail);
		BaseUiTools.addHtmlLabel(planning, msg, "liste-inscrits");
		
		
		// Indique si il est encore possible de s'inscire ou non pour cette date 
		boolean isModifiable = MesPermanencesUtils.isDateModifiable(periodePermanenceDTO,detail);
		
		if (visualiser)
		{
			doVisualiserPart(detail,isModifiable);
		}
		else
		{
			if (isModifiable)
			{
				doModifierPart(detail);
			}
			else
			{
				doVisualiserPart(detail,isModifiable);
			}
		}
	}
	
	


	private void doVisualiserPart(PeriodePermanenceDateDTO detail, boolean isModifiable)
	{
		boolean isInscrit = detail.isInscrit(userId);
		boolean placeDispo = isPlaceDispo(detail);	
		
		
		if (isModifiable)
		{
			BaseUiTools.addHtmlLabel(planning, "Il est encore possible de modifier ses choix sur cette date", "place-dispo");
		}
		else
		{
			BaseUiTools.addHtmlLabel(planning, "Il n'est plus possible de modifier ses choix sur cette date", "place-dispo");
		}
		
		
		// Si l'utilisateur est déjà inscrit pour cette date 
		if (isInscrit)
		{
			BaseUiTools.addHtmlLabel(planning, "Vous êtes inscrit pour cette permanence.", "place-dispo");
			return;
		}
		
		// Si pas de place disponible
		if (placeDispo==false)
		{
			BaseUiTools.addHtmlLabel(planning, "Il n'y a plus de place disponible à cette date.", "place-dispo");
			return;
		}
	}

	private void doModifierPart(PeriodePermanenceDateDTO detail)
	{
		
		boolean isInscrit = detail.isInscrit(userId);
		boolean placeDispo = isPlaceDispo(detail);
		
		
		// Si l'utilisateur est déjà inscrit pour cette date 
		if (isInscrit)
		{
			BaseUiTools.addHtmlLabel(planning, "Vous êtes inscrit pour cette permanence.", "place-dispo");
			Button b = new Button("Je ne souhaite plus venir à cette date.");
			b.addStyleName("suppress-inscrire");
			b.addClickListener(e ->	handleSuppressionInscription(detail));
				
			planning.addComponent(b);
			planning.setComponentAlignment(b, Alignment.MIDDLE_CENTER);

			return;
		}
		
		// Si pas de place disponible
		if (placeDispo==false)
		{
			BaseUiTools.addHtmlLabel(planning, "Il n'y a plus de place disponible à cette date.", "place-dispo");
			return;
		}
		
		// Cas standard : on peut s'inscrire 
		Button b = new Button("Je m'inscris à cette date");
		b.addStyleName("inscrire");
		b.addClickListener(e ->	handleInscription(detail));
			
		planning.addComponent(b);
		planning.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
		
	}



	private boolean isPlaceDispo(PeriodePermanenceDateDTO detail)
	{
		int nbPlace = detail.nbPlace-detail.getNbInscrit();
		if (nbPlace<=0)
		{
			return false;
		}
		return true;
	}

	private String getMessageListeInscrit(PeriodePermanenceDateDTO detail)
	{
		String msg = "";
		
		if (detail.nbPlace==0)
		{
			msg = msg + "Pas de permanence ce jour là.";
			return msg;
		}
		else if (detail.nbPlace==1)
		{
			msg = msg + "1 personne est nécessaire pour cette permanence.";
		}
		else
		{
			msg = msg + detail.nbPlace+" personnes sont nécessaires pour cette permanence.";
		}
		
		msg = msg+"<br/>";
		
		if (detail.getNbInscrit()==0)
		{
			msg = msg+ "Personne n'est encore inscrit.";
		}
		else if (detail.getNbInscrit()==1)
		{
			msg = msg+ "Une personne est inscrite :";
		}
		else
		{
			msg = msg+ detail.getNbInscrit()+" personnes sont inscrites.";
		}
		
		msg = msg+"<ul>";
		for (PermanenceCellDTO pc : detail.permanenceCellDTOs)
		{
			msg = msg+"<li>"+"Role "+pc.nomRole+" - "+pc.nom+" "+pc.prenom+"</li>";
		}
		msg = msg+"</ul>";
		
				
		return msg;
	}



	/**
	 * 
	 */
	private void handleInscription(PeriodePermanenceDateDTO detail)
	{
		//
		if (periodePermanenceDTO.nbInscription>=periodePermanenceDTO.nbSouhaite)
		{
			MessagePopup popup = new MessagePopup("Impossible de s'inscrire",ColorStyle.GREEN,"Vous êtes déjà inscrit sur suffisamment de dates.");
			MessagePopup.open(popup);
			return;
		}
		
		
		//
		InscriptionMessage msg = new MesPermanencesService().inscription(userId,detail.idPeriodePermanenceDate,null,RegleInscriptionPeriodePermanence.UNE_INSCRIPTION_PAR_DATE); // TODO gerer le role 
		if (msg!=null)
		{
			String lib = InscriptionPopup.computeLib(msg);
			MessagePopup popup = new MessagePopup("Impossible de s'inscrire",ColorStyle.RED,"Vous ne pouvez pas vous inscrire car "+lib);
			MessagePopup.open(popup,()->handleFinInscription());
		}
		else
		{
			handleFinInscription();
		}
	}

	private void handleFinInscription()
	{
		// On recharge d'abord la liste des infos 
		this.dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(dto.id);
		dateViewer.updateDates(dto.datePerms);
		
		// On recharge aussi les compteurs
		computeSouhaiteeReelle();
		
		// On réaffiche l'écran 
		onPopupClose();
		
	}
	
	
	private void handleSuppressionInscription(PeriodePermanenceDateDTO detail)
	{
		//
		new MesPermanencesService().deleteInscription(userId,detail.idPeriodePermanenceDate);
		handleFinInscription();
	}

	
}
