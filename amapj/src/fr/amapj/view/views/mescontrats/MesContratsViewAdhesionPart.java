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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.model.models.param.paramecran.PEMesContrats;
import fr.amapj.service.services.edgenerator.pdf.PGBulletinAdhesion;
import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.gestioncotisation.PeriodeCotisationDTO;
import fr.amapj.service.services.mescontrats.AdhesionDTO;
import fr.amapj.service.services.mescontrats.AdhesionDTO.AffichageOnly;
import fr.amapj.service.services.mescontrats.MesContratsDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;


/**
 * Page permettant à l'utilisateur de gérer son adhesion 
 * 
 */
public class MesContratsViewAdhesionPart implements PopupSuppressionListener
{
	
	SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	private MesContratsView view;

	/**
	 * 
	 */
	public MesContratsViewAdhesionPart(MesContratsView view)
	{
		this.view = view;
	}

	
	private Button addButtonAdhesionAdherer(String str)
	{
		Button b = new Button(str);
		b.addClickListener(e -> handleAdhesionAdherer());
		return b;
	}
	
	private void handleAdhesionAdherer()
	{
		PopupAdhesion adhesion = new PopupAdhesion(view.mesContratsDTO.adhesionDTO,true);
		PopupAdhesion.open(adhesion, this);
	}
	


	private Button addButtonAdhesionVoir(String str)
	{
		Button b = new Button(str);
		b.addClickListener(e -> handleAdhesionVoir());
		return b;
	}
	
	private void handleAdhesionVoir()
	{
		PopupAdhesion adhesion = new PopupAdhesion(view.mesContratsDTO.adhesionDTO,false);
		PopupAdhesion.open(adhesion, this);
	}
	
	
	private Button addButtonAdhesionSupprimer(String str)
	{
		Button b = new Button(str);
		b.addClickListener(e ->	handleAdhesionSupprimer());
		return b;
	}
	
	private void handleAdhesionSupprimer()
	{
		String text = "Etes vous sûr de vouloir supprimer votre adhésion?";
		Long idAdhesion = view.mesContratsDTO.adhesionDTO.periodeCotisationUtilisateurDTO.id;
		SuppressionPopup confirmPopup = new SuppressionPopup(text,idAdhesion);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new GestionCotisationService().deleteAdhesion(idItemToSuppress);   
	}


	public void addAhesionInfo(VerticalLayout layout)
	{
		MesContratsDTO mesContratsDTO = view.mesContratsDTO;
		
		
		// Cas classique de renouvellement de l'adhésion
		if (mesContratsDTO.adhesionDTO.displayAdhesionTop())
		{
			
			Label lab = new Label("Renouvellement de votre adhésion à l'AMAP");
			lab.addStyleName(MesContratsView.LABEL_RUBRIQUE);
			layout.addComponent(lab);
			
			Panel p = new Panel();
			p.addStyleName(MesContratsView.PANEL_UNCONTRAT);
			
			HorizontalLayout hl = new HorizontalLayout();
			hl.setMargin(true);
			hl.setSpacing(true);
			hl.setWidth("100%");
			
			VerticalLayout vl = new VerticalLayout();
			Label lab1 = new Label("Adhésion pour "+mesContratsDTO.adhesionDTO.periodeCotisationDTO.nom);
			lab1.addStyleName(MesContratsView.LABEL_TITRECONTRAT);
			vl.addComponent(lab1);
						
			
			String str = formatLibelleAdhesion(mesContratsDTO.adhesionDTO);
			BaseUiTools.addHtmlLabel(vl, str, "libelle-contrat");
			
			addLinkImpressionBulletin(mesContratsDTO.adhesionDTO,vl);
	
			hl.addComponent(vl);
			hl.setExpandRatio(vl, 1);
			
			VerticalLayout vl2 = new VerticalLayout();
			vl2.setWidth("115px");
			vl2.setSpacing(true);	

			hl.addComponent(vl2);
			hl.setComponentAlignment(vl2, Alignment.MIDDLE_CENTER);

			
			if (mesContratsDTO.adhesionDTO.isCotisant())	
			{
				Button b = addButtonAdhesionAdherer("Modifier");
				b.setWidth("100%");
				vl2.addComponent(b);	
			
				b = addButtonAdhesionSupprimer("Supprimer");
				b.setWidth("100%");
				vl2.addComponent(b);
				
				Button v = addButtonAdhesionVoir("Voir");
				v.setWidth("100%");
				v.addStyleName(MesContratsView.BUTTON_PRINCIPAL);
				vl2.addComponent(v);
				
			}
			else
			{
				Button b = addButtonAdhesionAdherer("Adhérer");
				b.addStyleName(MesContratsView.BUTTON_PRINCIPAL);
				b.setWidth("100%");
				vl2.addComponent(b);
			}
			
			p.setContent(hl);
			layout.addComponent(p);
				
		}
		
		
		// Cas d'affichage uniquement, pendant 30 jours apres la fin des inscriptions  
		if (mesContratsDTO.adhesionDTO.affichageOnly!=null)
		{
			AffichageOnly aff = mesContratsDTO.adhesionDTO.affichageOnly;
			
			Label lab = new Label("Votre adhésion à l'AMAP");
			lab.addStyleName(MesContratsView.LABEL_RUBRIQUE);
			layout.addComponent(lab);
			
			Panel p = new Panel();
			p.addStyleName(MesContratsView.PANEL_UNCONTRAT);
			
			HorizontalLayout hl = new HorizontalLayout();
			hl.setMargin(true);
			hl.setSpacing(true);
			hl.setWidth("100%");
			
			VerticalLayout vl = new VerticalLayout();
			Label lab1 = new Label("Adhésion pour "+aff.nomPeriode);
			lab1.addStyleName(MesContratsView.LABEL_TITRECONTRAT);
			vl.addComponent(lab1);
						
			
			String str ="Vous avez renouvelé votre adhésion à l'AMAP. Montant : "+new CurrencyTextFieldConverter().convertToString(aff.montantAdhesion)+" €";
			BaseUiTools.addHtmlLabel(vl, str, "libelle-contrat");
			
			if (shouldDisplayLinkBulletinInAffichageOnly(aff))
			{
				Link l = LinkCreator.createLink(new PGBulletinAdhesion(aff.idPeriode, aff.idPeriodeUtilisateur, null));
				l.setCaption("Imprimer mon bulletin d'adhésion");
				l.setStyleName("adhesion");
				
				vl.addComponent(l);
			}
	
			hl.addComponent(vl);
			hl.setExpandRatio(vl, 1);
			
			VerticalLayout vl2 = new VerticalLayout();
			vl2.setWidth("115px");
			vl2.setSpacing(true);	

			hl.addComponent(vl2);
			hl.setComponentAlignment(vl2, Alignment.MIDDLE_CENTER);
			
			p.setContent(hl);
			layout.addComponent(p);
				
		}		
	}
	
	
	private boolean shouldDisplayLinkBulletinInAffichageOnly(AffichageOnly aff)
	{
		// si il n'y a pas de modele de bulletin : on ne met pas le lien 
		if (aff.idBulletin==null)
		{
			return false;
		}
		
		
		PEMesContrats peMesContrats = (PEMesContrats) new ParametresService().loadParamEcran(MenuList.MES_CONTRATS);
		switch (peMesContrats.canPrintAdhesion)
		{
			case JAMAIS:
				return false;
	
			case APRES_DATE_FIN_DES_INSCRIPTIONS : // On est dans le cas ou on est après la fin des inscriptions 
				return true;
				
			case TOUJOURS:
				return true;
			
			 
			default:
				throw new AmapjRuntimeException();
		}
	}
	
	
	
	
	
	
	
	

	/**
	 * Ajoute si cela est nécessaire le lien vers l'impression des bulletins d'adhesion
	 * @param adhesionDTO
	 * @param vl
	 */
	private void addLinkImpressionBulletin(AdhesionDTO adhesionDTO, VerticalLayout vl)
	{
		if (shouldDisplayLinkBulletin(adhesionDTO)==false)
		{
			return ;
		}
		
		Long idPeriode = adhesionDTO.periodeCotisationDTO.id;
		Long idPeriodeUtilisateur = adhesionDTO.periodeCotisationUtilisateurDTO.id;
		Link l = LinkCreator.createLink(new PGBulletinAdhesion(idPeriode, idPeriodeUtilisateur, null));
		l.setCaption("Imprimer mon bulletin d'adhésion");
		l.setStyleName("adhesion");
		
		vl.addComponent(l);
	}


	private boolean shouldDisplayLinkBulletin(AdhesionDTO adhesionDTO)
	{
		// Si il n'est pas cotisant ou si il n'y a pas de modele de bulletin : on ne met pas le lien 
		if (adhesionDTO.isCotisant()==false || adhesionDTO.periodeCotisationDTO.idBulletinAdhesion==null)
		{
			return false;
		}
		
		PEMesContrats peMesContrats = (PEMesContrats) new ParametresService().loadParamEcran(MenuList.MES_CONTRATS);
		switch (peMesContrats.canPrintAdhesion)
		{
			case JAMAIS:
				return false;
	
			case APRES_DATE_FIN_DES_INSCRIPTIONS : // On est dans le cas ou on est avant la fin des inscriptions 
				return false;
				
			case TOUJOURS:
				return true;
			
			 
			default:
				throw new AmapjRuntimeException();
		}
	}


	private String formatLibelleAdhesion(AdhesionDTO adhesionDTO)
	{
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		
		PeriodeCotisationDTO p = adhesionDTO.periodeCotisationDTO;
		// Ligne 0
		String str = "";
		
		// Ligne 1
		
		
		//  
		if (adhesionDTO.isCotisant())
		{
			str = str+"Vous avez renouvelé votre adhésion à l'AMAP. Montant : "
					 +new CurrencyTextFieldConverter().convertToString(adhesionDTO.periodeCotisationUtilisateurDTO.montantAdhesion)+" €"
					 + "<br/>Vous pouvez modifier votre choix  jusqu'au "+df.format(p.dateFinInscription)+ " minuit.";
		}
		else
		{
			str = str +"Il est temps d'adhérer pour la nouvelle saison !<br/>";
			
			str = str+"<b>Cette adhésion couvre la période du "+df2.format(p.dateDebut)+" au "+df2.format(p.dateFin)+"</b>";
			
			str=str+"<br/>";
			str = str+"Vous avez jusqu'au  "+df.format(p.dateFinInscription)+ " minuit pour adhérer à l'AMAP.";
		}
		
		str=str+"<br/>";
		
		return str;
	}



	@Override
	public void onPopupClose()
	{
		view.refresh();
		
	}


}
