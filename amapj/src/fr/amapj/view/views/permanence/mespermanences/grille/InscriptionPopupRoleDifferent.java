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

import fr.amapj.common.CollectionUtils;
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
import fr.amapj.view.views.permanence.mespermanences.grille.InscriptionPopupRoleDifferent.Tab.TabLine;

/**
 * Popup pour s'inscrire, avec la regle role different 
 *  
 */
public class InscriptionPopupRoleDifferent extends  CorePopup
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
	public InscriptionPopupRoleDifferent(Long idPeriodePermanenceDate,Long userId,Long idPeriodePermanence)
	{
		this.idPeriodePermanenceDate = idPeriodePermanenceDate;
		this.userId = userId;
		this.idPeriodePermanence = idPeriodePermanence;
		
		popupTitle = "S'inscrire";
		this.setWidth(40,850);
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
		
		
		List<RoleLine> roles = computeRoleLine();
		
		
		for (RoleLine role : roles)
		{
			TabLine line = new TabLine();
			
			//
			line.role = role;
			
			//
			line.col1 = role.nomRole;
			line.styleCol1 = "role";
			
			//
			int nbPlacesDispo = role.nbPlaceDispo;
			String str = "Aucune place disponible";
			if (nbPlacesDispo>1)
			{
				str = nbPlacesDispo+" places disponibles";
			}
			else if (nbPlacesDispo==1)
			{
				str = "Une place disponible";
			}
			line.col2 = str;
			line.styleCol2 = "role";
			
			// 
			String str2 = CollectionUtils.asString(role.utilisateurs, ", ");
			if (role.isInscrit)
			{
				line.col3 = str2;
				line.styleCol3 = "place-moi";
				
				line.col4 = "Je ne souhaite plus venir";
				line.styleCol4 = "suppress-inscrire";
			}
			else if (nbPlacesDispo==0)
			{
				line.col3 = str2;
				line.styleCol3 = "place-occupee";
			}
			else
			{
				line.col3 = str2;
				line.styleCol3 = "place-libre";
				
				if (needSubscribeMore)
				{
					line.col4 = "Je m'inscris";
					line.styleCol4 = "inscrire";
				}
			}
			
			tab.lines.add(line);

		}

		return tab;
	}


	private List<RoleLine> computeRoleLine()
	{
		List<RoleLine> roles = new ArrayList<InscriptionPopupRoleDifferent.RoleLine>();
		
		for (PermanenceCellDTO cell : dateDto.permanenceCellDTOs)
		{
			RoleLine line = findOrCreate(roles,cell.idRole,cell.nomRole);
			
			if (cell.idUtilisateur==userId)
			{
				line.isInscrit = true;
			}
			
			if (cell.idUtilisateur!=null)
			{
				line.utilisateurs.add(cell.nom+" "+cell.prenom);
			}
			else
			{
				line.nbPlaceDispo++;
			}
			
		}
		
		return roles;
	}
	
	private RoleLine findOrCreate(List<RoleLine> roles, Long idRole,String nomRole)
	{
		for (RoleLine roleLine : roles)
		{
			if (roleLine.idRole==idRole)
			{
				return roleLine;
			}
		}
		RoleLine roleLine = new RoleLine();
		roleLine.idRole = idRole;
		roleLine.nomRole = nomRole;
		
		roles.add(roleLine);
		
		return roleLine;
	}



	

	private void handleButton(RoleLine roleLine)
	{
		// L'utilisateur souhaite s'inscrire 
		if (roleLine.isInscrit==false)
		{
			InscriptionMessage msg = new MesPermanencesService().inscription(userId,idPeriodePermanenceDate,roleLine.idRole,RegleInscriptionPeriodePermanence.MULTIPLE_INSCRIPTION_SUR_ROLE_DIFFERENT);
			if (msg!=null)
			{
				String lib = InscriptionPopup.computeLib(msg);
				MessagePopup popup = new MessagePopup("Impossible de s'inscrire",ColorStyle.RED,"Vous ne pouvez pas vous inscrire car "+lib);
				MessagePopup.open(popup);
			}
		}
		// L'utilisateur souhaite se desincrire
		else 
		{
			new MesPermanencesService().deleteInscriptionRoleDifferent(userId,idPeriodePermanenceDate,roleLine.idRole);
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
		GridLayout gl = new GridLayout(4,1+tab.lines.size());
		gl.setWidth("800px");
		gl.setSpacing(false);
		
		contentLayout.addComponent(gl);
		
	
		// Construction du titre   
		Label l = new Label(tab.titre);
		l.addStyleName(tab.styleTitre);
		l.setWidth("100%");
		gl.addComponent(l,0,0,3,0);
		
		
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
			
			
			Label l3 = new Label(line.col3);
			l3.addStyleName(line.styleCol3);
			l3.setWidth("100%");
			l3.setHeight(height+"px");
			gl.addComponent(l3,2,i+1);
			
			
			if (line.col4!=null)
			{
				Button b = new Button(line.col4);
				b.addStyleName(line.styleCol4);
				b.addClickListener(e->handleButton(line.role));
				b.setWidth("100%");
				gl.addComponent(b,3,i+1);
				gl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
			}
			else
			{
				Label l4 = new Label("");
				l4.addStyleName(line.styleCol4);
				l4.setWidth("100%");
				l4.setHeight(height+"px");
				gl.addComponent(l4,3,i+1);
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
		gridHeaderLine.cells.add(line.col3);
		
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
			public RoleLine role;
			
			public String col1;
			public String styleCol1;
			
			public String col2;
			public String styleCol2;
			
			public String col3;
			public String styleCol3;
			
			public String col4;
			public String styleCol4;
			
		}
	
	}
	
	
	
	static public class RoleLine
	{
		public Long idRole;
		
		public String nomRole;
		
		public int nbPlaceDispo;
		
		public List<String> utilisateurs = new ArrayList<String>();
		
		// Indique si l'utilisateur courant est inscrit sur ce role
		public boolean isInscrit = false;
	}
	
	
}
