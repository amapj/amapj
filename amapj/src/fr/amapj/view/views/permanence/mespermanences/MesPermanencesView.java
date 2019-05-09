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
 package fr.amapj.view.views.permanence.mespermanences;

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.services.edgenerator.excel.permanence.EGPlanningPermanence;
import fr.amapj.service.services.permanence.mespermanences.MesPermanenceDTO;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.SmallPeriodePermanenceDTO;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.template.FrontOfficeView;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.permanence.mespermanences.grille.GrilleInscriptionPermanence;
import fr.amapj.view.views.saisiecontrat.SaisieContrat.ModeSaisie;


/**
 * Page permettant à l'utilisateur de gérer ses permanences
 * 
 */
public class MesPermanencesView extends FrontOfficeView implements PopupListener
{
	
	private SimpleDateFormat df1 = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	
	static public String LABEL_RUBRIQUE = "rubrique";
	static public String LABEL_TITRECONTRAT = "titrecontrat";
	static public String PANEL_UNCONTRAT = "uncontrat";
	static public String BUTTON_PRINCIPAL = "principal";
	
	
	SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	VerticalLayout layout = null;
	public MesPermanenceDTO mesContratsDTO;
	
	
	
	
	@Override
	public String getMainStyleName()
	{
		return "mespermanences";
	}
	
	/**
	 * 
	 */
	@Override
	public void enter()
	{	
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

	
	
	private Button addButtonInscription(String str,final UnePeriodePermanenceDTO c)
	{
		Button b = new Button(str);
		b.addClickListener(e ->	handleInscription(c,ModeSaisie.STANDARD));
		return b;
	}
	
	protected void handleInscription(UnePeriodePermanenceDTO c,ModeSaisie modeSaisie)
	{
		// TODO aller vers l'autre ecran en commentaire sur un telephone, car il est adapté pour les petites tailles  
		//SmallInscriptionPermanence.open(new SmallInscriptionPermanence(c.idPeriodePermanence,SessionManager.getUserId(),false), this);
		GrilleInscriptionPermanence.open(new GrilleInscriptionPermanence(c.idPeriodePermanence,SessionManager.getUserId()), this);
	}



	public void refresh()
	{
		mesContratsDTO = new MesPermanencesService().getMesPermanenceDTO(SessionManager.getUserId());
		
		layout = this;
		layout.removeAllComponents();
		
		
		if (mesContratsDTO.mesPeriodesPermanences.size()>0)
		{
		
			// Le titre
			addLabel(layout,"S'inscrire aux permanences");
			
			
			// la liste des inscriptions possibles
			for (UnePeriodePermanenceDTO c : mesContratsDTO.mesPeriodesPermanences)
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
				
				String libButton = getLibButton(c);
				Button b = addButtonInscription(libButton,c);
				b.setWidth("100%");
				b.addStyleName(BUTTON_PRINCIPAL);
				vl2.addComponent(b);
				
				hl.addComponent(vl2);
				hl.setComponentAlignment(vl2, Alignment.MIDDLE_CENTER);
				
				p.setContent(hl);
				
				layout.addComponent(p);
				
			}
		}
		
		//
		addLabel(layout,"Les dates de mes permanences");
		
		VerticalLayout vl1 = BaseUiTools.addPanel(this, "mes-permanences");
		String mesPermanences = getLibMesPermanences();
		BaseUiTools.addHtmlLabel(vl1, mesPermanences, "ligne");
		
		
		
		
		addLabel(layout,"Consulter les plannings de permanence");
		
		VerticalLayout vl2 = BaseUiTools.addPanel(this, "mes-permanences");
		
		
		// Le bouton pour visualiser les permanences en ligne 
		Button onLineButton = new Button("Visualiser les plannings de permanence ...");
		onLineButton.setIcon(FontAwesome.EYE);
		onLineButton.addStyleName("borderless");
		onLineButton.addStyleName("large");
		onLineButton.addClickListener(e->handleVisualiser());
				
		vl2.addComponent(onLineButton);
		vl2.setComponentAlignment(onLineButton, Alignment.MIDDLE_LEFT);
				
		// Le bouton pour télécharger les permanences 
		Button telechargerButton = new Button("Télécharger les plannings de permanence au format tableur ...");
		telechargerButton.setIcon(FontAwesome.PRINT);
		telechargerButton.addStyleName("borderless");
		telechargerButton.addStyleName("large");
		telechargerButton.addClickListener(e->handleTelecharger());
				
		vl2.addComponent(telechargerButton);
		vl2.setComponentAlignment(telechargerButton, Alignment.MIDDLE_LEFT);
		
	}
	
	private void handleVisualiser()
	{
		ChoixVisualiserPeriodePermanence.open(new ChoixVisualiserPeriodePermanence(),this);
	}

	private void handleTelecharger()
	{
		TelechargerPopup popup = new TelechargerPopup("Téléchargement des plannings de permanence",80);
		List<SmallPeriodePermanenceDTO> dtos = new MesPermanencesService().getAllPeriodeInFuture();
		
		for (SmallPeriodePermanenceDTO dto : dtos)
		{
			popup.addGenerator(new EGPlanningPermanence(dto.id,null));
		}
		
		CorePopup.open(popup,this);
	}
	

	private String getLibMesPermanences()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("Vous devez faire les permanences suivantes :<ul>");
		for (PeriodePermanenceDateDTO permanence : mesContratsDTO.mesPermanencesFutures)
		{
			buf.append("<li>"+df1.format(permanence.datePerm)+"</li>");	
		}
		buf.append("</ul>");
		
		return buf.toString();
	}

	private String getLibButton(UnePeriodePermanenceDTO c)
	{
		if (c.nbInscription>=c.nbSouhaite)
		{
			return "Modifier";
		}
		else
		{
			return "S'inscrire";
		}
	}

	private String formatLibelleContrat(UnePeriodePermanenceDTO c,boolean isInscription)
	{
		switch (c.nature)
		{
		case INSCRIPTION_LIBRE_AVEC_DATE_LIMITE:
			return formatLibelleLibreDateLimite(c);
			
		case INSCRIPTION_LIBRE_FLOTTANT:
			return formatLibelleLibreSansDateLimite(c);
			
		default:
			throw new AmapjRuntimeException(""+c.nature);
		}

	}
	
	private String formatLibelleLibreDateLimite(UnePeriodePermanenceDTO c)
	{	
		// Ligne 1
		String str = c.description;
		str=str+"<br/>";
		
		// Ligne 2 - Les dates de livraisons
		str = str+"<b>Vous devez réaliser "+c.nbSouhaite+" permanences entre le "+df.format(c.dateDebut)+" et le "+df.format(c.dateFin)+"<br/>(à choisir parmi "+c.nbDatePermanence+" dates).</b>";
		str=str+"<br/>";
		
		// Ligne 3 et 4 : etat + limite  
		if (c.nbInscription>=c.nbSouhaite)
		{
			str = str+"Vous êtes bien inscrit sur "+c.nbInscription+" dates.";
			str=str+"<br/>";
			str = str+"Vous pouvez modifier vos choix jusqu'au "+df.format(c.dateFinInscription)+ " minuit.";
		}
		else if (c.nbInscription==0)
		{
			str = str+"Vous n'êtes pas inscrit, merci de vous inscrire.";
			str=str+"<br/>";
			str = str+"Vous devez vous inscrire avant le "+df.format(c.dateFinInscription)+ " minuit.";
		}
		else if (c.nbInscription<c.nbSouhaite)
		{
			str = str+"Vous êtes inscrit sur "+c.nbInscription+" permanences, merci de vous inscrire encore sur "+(c.nbSouhaite-c.nbInscription)+" dates";
			str=str+"<br/>";
			str = str+"Vous devez finaliser vos inscriptions avant le "+df.format(c.dateFinInscription)+ " minuit.";
		}
		str=str+"<br/>";
				
		return str;
	}
	
	
	
	private String formatLibelleLibreSansDateLimite(UnePeriodePermanenceDTO c)
	{	
		// Ligne 1
		String str = c.description;
		str=str+"<br/>";
		
		// Ligne 2 - Les dates de livraisons
		str = str+"<b>Vous devez réaliser "+c.nbSouhaite+" permanences entre le "+df.format(c.dateDebut)+" et le "+df.format(c.dateFin)+"<br/>(à choisir parmi "+c.nbDatePermanence+" dates).</b>";
		str=str+"<br/>";
		
		// Ligne 3 et 4 : etat + limite  
		if (c.nbInscription>=c.nbSouhaite)
		{
			str = str+"Vous êtes bien inscrit sur "+c.nbInscription+" dates.";
			str=str+"<br/>";
			str = str+"Vous pouvez modifier vos choix pour vos permanences placées après le "+df.format(c.firstDateModifiable);
		}
		else if (c.nbInscription==0)
		{
			str = str+"Vous n'êtes pas inscrit, merci de vous inscrire.";
			str=str+"<br/>";
			str = str+"Vous pouvez vous inscrire pour les permanences placées après le "+df.format(c.firstDateModifiable);
		}
		else if (c.nbInscription<c.nbSouhaite)
		{
			str = str+"Vous êtes inscrit sur "+c.nbInscription+" permanences, merci de vous inscrire encore sur "+(c.nbSouhaite-c.nbInscription)+" dates";
			str=str+"<br/>";
			str = str+"Vous pouvez vous inscrire pour les permanences placées après le "+df.format(c.firstDateModifiable);
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
