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
 package fr.amapj.view.views.permanence.grille;

import java.text.SimpleDateFormat;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.DebugUtil;
import fr.amapj.common.FormatUtils;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.service.services.permanence.periode.PeriodePermanenceService;
import fr.amapj.service.services.permanence.periode.PermanenceCellDTO;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.PopupType;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.views.permanence.grille.BlocGrille.BlocGrilleLine;

/**
 * 
 */
abstract public class AbstractPeriodePermanenceGrillePart extends CorePopup implements PopupListener
{	
	protected PeriodePermanenceDTO dto;
	
	protected Long userId;

	// Permet d'ajouter des boutons specfiques
	// Peut retourner null 
	abstract protected Layout addSpecificButton(PeriodePermanenceDateDTO date); 
	
	// Permet de dessiner un header 
	abstract protected String getHeader();
	
	private VerticalLayout contentLayout;
	
	// Nombre de colonne dans la grille (soit 1, soit 2 , soit 3)
	private int nbCol;
	
	public AbstractPeriodePermanenceGrillePart(Long idPeriodePermanence,Long userId)
	{
		this.userId = userId;
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(idPeriodePermanence);
		this.popupTitle = "Période de permanence "+dto.nom;
		
	
	}
	
	protected void createContent(VerticalLayout contentLayout)
	{
		this.contentLayout = contentLayout;
		
		int widthRatio = BaseUiTools.isWidthBelow(1600) ? 100 : 80;
		String height = BaseUiTools.isWidthBelow(1600) ? "100%" : "90%";
		
		nbCol = computeNbCol(widthRatio);
		
		setHeight(height);
		setWidth(widthRatio);
		
		contentLayout.addStyleName("grille-permanence");
			
		drawContent();
		
		
	}
	
	
	private int computeNbCol(int widthRatio)
	{
		int reelWidth = BaseUiTools.computePopupWidth(widthRatio);
		
		DebugUtil.trace("reelWidth="+reelWidth);
		if (reelWidth<860)
		{
			return 1;
		}
		
		if (reelWidth<1290)
		{
			return 2;
		}
		
		return 3;
	}

	private void drawContent()
	{
		contentLayout.removeAllComponents();
		
		String lib = getHeader();
		if (lib!=null)
		{
			BaseUiTools.addHtmlLabel(contentLayout, lib, "compteur");
		}
		
		BlocGrilleDrawer drawer = new BlocGrilleDrawer();
		for (PeriodePermanenceDateDTO date : dto.datePerms)
		{
			drawer.addBloc(createBloc(date));
		}
		
		drawer.createContent(contentLayout,nbCol,this);
	}
	
	@Override
	public void onPopupClose()
	{
		dto = new PeriodePermanenceService().loadPeriodePermanenceDTO(dto.id);
		drawContent();
	}
	

	private BlocGrille createBloc(PeriodePermanenceDateDTO date)
	{
		BlocGrille bloc = new BlocGrille();
		
		bloc.date = date;
		
		// La date 
		SimpleDateFormat df = FormatUtils.getFullDate();  
		bloc.titre = df.format(date.datePerm);
		if (date.isDateComplete())
		{
			bloc.styleTitre = "date-complete";
		}
		else
		{
			bloc.styleTitre = "date-avec-dispo";
		}
		
		
		for (PermanenceCellDTO pc : date.permanenceCellDTOs)
		{
			BlocGrilleLine line = new BlocGrilleLine();
			
			//
			line.col1 = pc.lib;
			line.styleCol1 = "role";
			
			
			if (pc.idUtilisateur==userId && userId!=null)
			{
				line.col2 = pc.nom+" "+pc.prenom;
				line.styleCol2 = "place-moi";
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
			}
			
			
			bloc.lines.add(line);
			
		}
		
		return bloc;
		
	}

	protected void createButtonBar()
	{
		addButton("OK", e->handleOK());
		
	}
	
	
	protected void handleOK()
	{
		close();
	}
	
	
	
	
}
