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
 package fr.amapj.view.views.mescontrats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DateUtils;
import fr.amapj.model.models.contrat.modele.NatureContrat;
import fr.amapj.service.services.mescontrats.ContratDTO;
import fr.amapj.service.services.mescontrats.MesContratsDTO;
import fr.amapj.service.services.mescontrats.MesContratsService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.saisiecontrat.ContratAboManager;
import fr.amapj.view.views.saisiecontrat.PopupSaisieJoker;
import fr.amapj.view.views.saisiecontrat.PopupSaisieJokerOnly;
import fr.amapj.view.views.saisiecontrat.SaisieContrat;
import fr.amapj.view.views.saisiecontrat.ContratAboManager.ContratAbo;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Page permettant à l'utilisateur de gérer ses contrats
 * 
 */
public class MesContratsView extends FrontOfficeView implements  PopupSuppressionListener
{
	
	
	static public String LABEL_RUBRIQUE = "rubrique";
	static public String LABEL_TITRECONTRAT = "titrecontrat";
	static public String PANEL_UNCONTRAT = "uncontrat";
	static public String BUTTON_PRINCIPAL = "principal";
	
	
	SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	VerticalLayout layout = null;
	public MesContratsDTO mesContratsDTO;
	
	public MesContratsViewAdhesionPart adhesionPart;

	@Override
	public String getMainStyleName()
	{
		return "contrat";
	}
	
	/**
	 * 
	 */
	@Override
	public void enter()
	{
		adhesionPart = new MesContratsViewAdhesionPart(this);	
		refresh();
	}

	
	/**
	 * Ajoute un label sur toute la largeur à la ligne indiquée
	 */
	private Label addLabel(VerticalLayout layout, String str)
	{
		Label tf = new Label(str);
		tf.addStyleName(LABEL_RUBRIQUE);
		layout.addComponent(tf);
		return tf;
		
	}

	
	
	private Button addButtonInscription(String str,final ContratDTO c)
	{
		Button b = new Button(str);
		b.addClickListener(e ->	handleInscription(c,ModeSaisie.STANDARD));
		return b;
	}
	
	private Button addButtonVoir(String str,final ContratDTO c)
	{
		Button b = new Button(str);
		b.addClickListener(e -> handleInscription(c,ModeSaisie.READ_ONLY));
		return b;
	}
	
	private Button addButtonJoker(String str,final ContratDTO c)
	{
		Button b = new Button(str);
		b.addClickListener(e -> handleJoker(c.modeleContratId,c.contratId));
		return b;
	}
	
	
	private void handleJoker(Long modeleContratId, Long contratId)
	{
		ContratDTO dto = new MesContratsService().loadContrat(modeleContratId, contratId);
		boolean isRegulier = new ContratAboManager().isRegulier(dto);
		if (isRegulier==false)
		{
			String msg = "Vous ne pouvez pas modifier vos jokers car ceux ci ont été modifiés par le référent.";
			MessagePopup p = new MessagePopup("Impossible de continuer",ContentMode.HTML,ColorStyle.RED,msg);
			MessagePopup.open(p);
			return;
		}

		ContratAbo abo = new ContratAboManager().computeContratAbo(dto);
		PopupSaisieJoker.open(new PopupSaisieJokerOnly(abo, dto));
		
	}

	private Button addButtonSupprimer(String str,final ContratDTO c)
	{
		Button b = new Button(str);
		b.addClickListener(e -> handleSupprimer(c));
		return b;
	}
	

	protected void handleSupprimer(final ContratDTO c)
	{
		String text = "Etes vous sûr de vouloir supprimer le contrat de "+c.nom+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,c.contratId);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new MesContratsService().deleteContrat(idItemToSuppress);
	}
	

	protected void handleInscription(ContratDTO c,ModeSaisie modeSaisie)
	{
		// Dans le cas de l'inscription à un nouveau contrat
		SaisieContrat.saisieContrat(c.modeleContratId,c.contratId,SessionManager.getUserId(),null,modeSaisie,this);
				
	}



	public void refresh()
	{
		mesContratsDTO = new MesContratsService().getMesContrats(SessionManager.getUserId());
		
		layout = this;
		layout.removeAllComponents();
		
		// Information sur le renouvellement de l'adhésion
		adhesionPart.addAhesionInfo(layout);
		
		// Le titre
		addLabel(layout,"Les nouveaux contrats disponibles");
		
		
		// la liste des nouveaux contrats 
		List<ContratDTO> newContrats = mesContratsDTO.getNewContrats();
		for (ContratDTO c : newContrats)
		{
			Panel p = new Panel();
			p.addStyleName(PANEL_UNCONTRAT);
			
			HorizontalLayout hl = new HorizontalLayout();
			hl.setMargin(true);
			hl.setSpacing(true);
			hl.setWidth("100%");
			
			VerticalLayout vl = new VerticalLayout();
			Label lab = new Label(c.nom);
			lab.addStyleName(LABEL_TITRECONTRAT);
			vl.addComponent(lab);
			
			String str = formatLibelleContrat(c,true);
			BaseUiTools.addHtmlLabel(vl, str, "libelle-contrat");
			
			
			hl.addComponent(vl);
			hl.setExpandRatio(vl, 1);
			
			VerticalLayout vl2 = new VerticalLayout();
			vl2.setWidth("115px");
			vl2.setSpacing(true);	
			
			Button b = addButtonInscription("S'inscrire",c);
			b.setWidth("100%");
			b.addStyleName(BUTTON_PRINCIPAL);
			vl2.addComponent(b);
			
			hl.addComponent(vl2);
			hl.setComponentAlignment(vl2, Alignment.MIDDLE_CENTER);
			
			p.setContent(hl);
			
			layout.addComponent(p);
			
		}
		
		
		// Le titre
		addLabel(layout,"Mes contrats existants");
	
		
		// la liste des contrats existants 
		List<ContratDTO> existingContrats = mesContratsDTO.getExistingContrats();
		for (ContratDTO c : existingContrats)
		{
			Panel p = new Panel();
			p.addStyleName(PANEL_UNCONTRAT);
			
			HorizontalLayout hl = new HorizontalLayout();
			hl.setMargin(true);
			hl.setSpacing(true);
			hl.setWidth("100%");
			
			VerticalLayout vl = new VerticalLayout();
			Label lab = new Label(c.nom);
			lab.addStyleName(LABEL_TITRECONTRAT);
			vl.addComponent(lab);
						
			String str = formatLibelleContrat(c,false);
			BaseUiTools.addHtmlLabel(vl, str, "libelle-contrat");
			
			hl.addComponent(vl);
			hl.setExpandRatio(vl, 1);
			
			VerticalLayout vl2 = new VerticalLayout();
			vl2.setWidth("115px");
			vl2.setSpacing(true);	
			
			if (c.isModifiable)
			{
				Button b = addButtonInscription("Modifier",c);
				b.setWidth("100%");
				vl2.addComponent(b);
			}
			
			if (c.isSupprimable)
			{
				Button b = addButtonSupprimer("Supprimer",c);
				b.setWidth("100%");
				vl2.addComponent(b);
			}
			
			if (c.isJoker)
			{
				Button b = addButtonJoker("Gérer jokers",c);
				b.setWidth("100%");
				vl2.addComponent(b);
			}
			
			
			
			Button v = addButtonVoir("Voir",c);
			v.addStyleName(BUTTON_PRINCIPAL);
			v.setWidth("100%");
			vl2.addComponent(v);
			
			
			hl.addComponent(vl2);
			hl.setComponentAlignment(vl2, Alignment.MIDDLE_CENTER);
			
			p.setContent(hl);
			
			layout.addComponent(p);
			
		}
		
		// Le bouton pour télécharger les contrats
		if (existingContrats.size()>0)
		{
			Button telechargerButton = new Button("Imprimer mes contrats ...");
			telechargerButton.setIcon(FontAwesome.PRINT);
			telechargerButton.addStyleName("borderless");
			telechargerButton.addStyleName("large");
			telechargerButton.addClickListener(e->handleTelecharger());
					
			layout.addComponent(telechargerButton);
			layout.setComponentAlignment(telechargerButton, Alignment.MIDDLE_LEFT);
		
		}
	}
	

	private void handleTelecharger()
	{
		new TelechargerMesContrat().displayPopupTelechargerMesContrat(mesContratsDTO.getExistingContrats(), this);
	}


	private String formatLibelleContrat(ContratDTO c,boolean isInscription)
	{
		if (c.nature==NatureContrat.CARTE_PREPAYEE)
		{
			return formatLibelleContratCartePrepayee(c);
		}
		else
		{
			return formatLibelleContratAboEtLibre(c,isInscription);
		}
	}
	
	private String formatLibelleContratAboEtLibre(ContratDTO c,boolean isInscription)
	{	
		// Ligne 1
		String str = c.description;
		str=str+"<br/>";
		
		// Ligne 2 - Les dates de livraisons
		if (c.nbLivraison==1)
		{
			str = str+"<b>Une seule livraison le "+df.format(c.dateDebut)+"</b>";
		}
		else
		{
			str = str+"<b>"+c.nbLivraison+" livraisons à partir du "+df.format(c.dateDebut)+" jusqu'au "+df.format(c.dateFin)+"</b>";
		}
		str=str+"<br/>";
		
		// Ligne 3 : modifiable ou non
		if (isInscription)
		{
			str = str+"Vous pouvez vous inscrire et modifier ce contrat jusqu'au "+df.format(c.dateFinInscription)+ " minuit.";
		}
		else
		{
			if (c.isModifiable)
			{
				str = str+"Ce contrat est modifiable jusqu'au "+df.format(c.dateFinInscription)+ " minuit.";
			}
			else if (c.isJoker)
			{
				str = str+"Ce contrat n'est plus modifiable, mais vous pouvez éventuellement ajuster vos jokers.";
			}
			else
			{	
				str = str+"Ce contrat n'est plus modifiable.";
			}
		}
		
		str=str+"<br/>";
		
		return str;
	}
	
	
	
	private String formatLibelleContratCartePrepayee(ContratDTO c)
	{	
		// Prochaine date de livraison 
		Date nextDateLiv = c.cartePrepayee.nextDateLiv;
		
		// Prochaine date de livraison modifiable 
		Date nextDateLivModifiable = c.cartePrepayee.nextDateLivModifiable;
		
		// Ligne 1
		String str = c.description;
		str=str+"<br/>";
		
		// Ligne 2 - Les dates de livraisons
		if (nextDateLiv==null)
		{
			str = str+"<b>Toutes les livraisons sont faites.</b>";
		}
		else
		{
			str = str+"<b>Prochaine livraison le "+df.format(nextDateLiv)+"</b>";
		}
		str=str+"<br/>";
		
		// Ligne : modifiable ou non 
		if (nextDateLivModifiable==null)
		{
			str = str+"Ce contrat n'est plus modifiable.";
		}
		else
		{
			Date datLimit = DateUtils.addDays(nextDateLivModifiable, -(c.cartePrepayee.cartePrepayeeDelai+1));
			str = str+"Vous pouvez modifier votre livraison du "+df.format(nextDateLivModifiable)+" jusqu'au "+df.format(datLimit)+ " minuit.";
		}
		
		
		str=str+"<br/>";
		
		return str;
	}



	

	@Override
	public void onPopupClose()
	{
		refresh();
		
	}


}
