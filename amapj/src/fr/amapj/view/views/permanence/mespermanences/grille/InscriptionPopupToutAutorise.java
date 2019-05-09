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
 package fr.amapj.view.views.permanence.mespermanences.grille;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.FormatUtils;
import fr.amapj.model.models.permanence.periode.RegleInscriptionPeriodePermanence;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService;
import fr.amapj.service.services.permanence.mespermanences.MesPermanencesService.InscriptionMessage;
import fr.amapj.service.services.permanence.mespermanences.UnePeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.permanence.mespermanences.MesPermanencesUtils;
import fr.amapj.view.views.permanence.mespermanences.grille.InscriptionPopupToutAutorise.Tab.TabLine;

/**
 * Popup pour se desinscrire
 *  
 */
public class InscriptionPopupToutAutorise extends  CorePopup
{
	
	private PeriodePermanenceDateDTO dateDto;
	
	private Long userId;
	
	private Long idPeriodePermanence;

	private VerticalLayout contentLayout;

	private Long idPeriodePermanenceDate;

	private UnePeriodePermanenceDTO periodePermanenceDTO;
	
	/**
	 * @param regleInscription 
	 * 
	 */
	public InscriptionPopupToutAutorise(Long idPeriodePermanenceDate,Long userId,Long idPeriodePermanence)
	{
		this.idPeriodePermanenceDate = idPeriodePermanenceDate;
		this.userId = userId;
		this.idPeriodePermanence = idPeriodePermanence;
		
		popupTitle = "S'inscrire";
		this.setWidth(40,650);
	}
	
	
	
	protected void createContent(VerticalLayout contentLayout)
	{
		this.contentLayout = contentLayout;
		contentLayout.addStyleName("grille-permanence");

		refresh();	
	}
	
	
	private void refresh()
	{
		contentLayout.removeAllComponents();
		this.dateDto = new PeriodePermanenceService().loadOneDatePermanence(idPeriodePermanenceDate);
		this.periodePermanenceDTO = new MesPermanencesService().loadCompteurPeriodePermanence(idPeriodePermanence, userId);
		
		
		String cpt = MesPermanencesUtils.getLibCompteur(periodePermanenceDTO);
		boolean needSubscribeMore = MesPermanencesUtils.needSubcribeMore(periodePermanenceDTO);
		Tab tab = createTab(needSubscribeMore);
		
		BaseUiTools.addHtmlLabel(contentLayout, cpt, "compteur");
		drawTab(tab);				
				
	}



	private Tab createTab(boolean needSubscribeMore)
	{
		Tab tab = new Tab();
		
		// Construction du titre 
		SimpleDateFormat df = FormatUtils.getFullDate();  
		tab.titre = df.format(dateDto.datePerm);
		if (dateDto.isDateComplete())
		{
			tab.styleTitre = "date-complete";
		}
		else
		{
			tab.styleTitre = "date-avec-dispo";
		}
		
		
		for (PermanenceCellDTO pc : dateDto.permanenceCellDTOs)
		{
			TabLine line = new TabLine();
			
			//
			line.cell = pc;
			
			//
			line.col1 = pc.lib;
			line.styleCol1 = "role";
			
			//
			if (pc.idUtilisateur==userId)
			{
				line.col2 = pc.nom+" "+pc.prenom;
				line.styleCol2 = "place-moi";
				
				line.col3 = "Je ne souhaite plus venir";
				line.styleCol3 = "suppress-inscrire";
			}
			else if (pc.idUtilisateur!=null)
			{
				line.col2 = pc.nom+" "+pc.prenom;
				line.styleCol2 = "place-occupee";
			}
			else
			{
				line.col2 = "Place libre";
				line.styleCol2 = "place-libre";
				
				if (needSubscribeMore)
				{
					line.col3 = "Je m'inscris";
					line.styleCol3 = "inscrire";
				}
			}
			
			tab.lines.add(line);

		}

		return tab;
	}


	private void handleButton(PermanenceCellDTO cell)
	{
		// L'utilisateur souhaite s'inscrire 
		if (cell.idUtilisateur==null)
		{
			InscriptionMessage msg = new MesPermanencesService().inscriptionToutAutorise(userId,cell.idPermanenceCell,idPeriodePermanenceDate);
			if (msg!=null)
			{
				String lib = InscriptionPopup.computeLib(msg);
				MessagePopup popup = new MessagePopup("Impossible de s'inscrire",ColorStyle.RED,"Vous ne pouvez pas vous inscrire car "+lib);
				MessagePopup.open(popup);
			}
		}
		// L'utilisateur souhaite se desincrire
		else if (cell.idUtilisateur==userId)
		{
			new MesPermanencesService().deleteInscriptionToutAutorise(userId,cell.idPermanenceCell,idPeriodePermanenceDate);
		}
		
		// On raraichit l'ecran 
		refresh();
	}
	

	protected void createButtonBar()
	{
		addButton("OK", e->close());
	}
	
	
	/**
	 * Permet de dessiner le tableau 
	 */
	public void drawTab(Tab tab)
	{
		GridLayout gl = new GridLayout(3,1+tab.lines.size());
		gl.setWidth("600px");
		gl.setSpacing(false);
		
		contentLayout.addComponent(gl);
		
	
		// Construction du titre   
		Label l = new Label(tab.titre);
		l.addStyleName(tab.styleTitre);
		l.setWidth("100%");
		gl.addComponent(l,0,0,2,0);
		
		
		List<TabLine> lines = tab.lines;
		for (int i = 0; i < lines.size(); i++)
		{	
			TabLine line = lines.get(i);
			
			int height = computeHeight(line);
			// La taille minimale est de 36 pixels, pour les boutons inscrire / desincrire
			height = Math.max(height,36);
			
			Label l1 = new Label(line.col1);
			l1.addStyleName(line.styleCol1);
			l1.setWidth("100%");
			l1.setHeight(height+"px");
			gl.addComponent(l1,0,i+1);
			
			
			Label l2 = new Label(line.col2);
			l2.addStyleName(line.styleCol2);
			l2.setWidth("100%");
			l2.setHeight(height+"px");
			gl.addComponent(l2,1,i+1);
			
			if (line.col3!=null)
			{
				Button b = new Button(line.col3);
				b.addStyleName(line.styleCol3);
				b.addClickListener(e->handleButton(line.cell));
				b.setWidth("100%");
				gl.addComponent(b,2,i+1);
				gl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
			}
			else
			{
				Label l3 = new Label("");
				l3.addStyleName(line.styleCol3);
				l3.setWidth("100%");
				l3.setHeight(height+"px");
				gl.addComponent(l3,2,i+1);
			}
		}
	}
	
	
	
	private int computeHeight(TabLine line)
	{
		// Chaque colonne fait 200px de large
		int cellWidth = 200;
		
		GridHeaderLine gridHeaderLine = new GridHeaderLine();
		gridHeaderLine.cells.add(line.col1);
		gridHeaderLine.cells.add(line.col2);
		
		GridSizeCalculator.autoSize(gridHeaderLine,cellWidth,"Arial",16);
		
		return gridHeaderLine.height;
	}



	static public class Tab
	{
		
		public String titre;
		
		public String styleTitre;
		
		public List<TabLine> lines = new ArrayList<TabLine>();
		
		
		static public class TabLine
		{
			public PermanenceCellDTO cell;
			
			public String col1;
			public String styleCol1;
			
			public String col2;
			public String styleCol2;
			
			public String col3;
			public String styleCol3;
			
		}
	
	}
}
