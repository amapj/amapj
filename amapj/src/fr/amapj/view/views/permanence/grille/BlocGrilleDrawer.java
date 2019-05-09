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

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.CollectionUtils;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridSizeCalculator;
import fr.amapj.view.views.permanence.grille.BlocGrille.BlocGrilleLine;

/**
 * 
 */
public class BlocGrilleDrawer
{	
	
	List<BlocGrille> blocGrilles;
	
	public BlocGrilleDrawer()
	{
		this.blocGrilles = new ArrayList<BlocGrille>(); 
	}
	
	public void addBloc(BlocGrille bloc)
	{
		blocGrilles.add(bloc);
	}
	
	protected void createContent(VerticalLayout contentLayout, int nbCol, AbstractPeriodePermanenceGrillePart grillePart)
	{
		List<List<BlocGrille>> lines = CollectionUtils.cutInSubList(blocGrilles, nbCol);
		
		for (List<BlocGrille> line : lines)
		{
			drawLine(contentLayout,line,grillePart);
			
			VerticalLayout spacer = new VerticalLayout();
			spacer.setHeight("10px");
			contentLayout.addComponent(spacer);
		}
	}
	
	
	private void drawLine(VerticalLayout contentLayout, List<BlocGrille> line, AbstractPeriodePermanenceGrillePart grillePart)
	{
		HorizontalLayout hl = new HorizontalLayout();
		
		// On calcule d'abord la hauteur de toutes les lignes 
		SizeInfo sizeInfo = computeSizeInfos(line);
		
		//
		for (int i = 0; i <  line.size()-1; i++)
		{			
			VerticalLayout vl = new VerticalLayout();
			draw(vl,line.get(i),sizeInfo,grillePart);
			
			hl.addComponent(vl);
			
			VerticalLayout spacer = new VerticalLayout();
			spacer.setWidth("10px");
			hl.addComponent(spacer);
				
		}
		
		
		VerticalLayout vl = new VerticalLayout();
		draw(vl,line.get(line.size()-1),sizeInfo,grillePart);
		hl.addComponent(vl);
		
		contentLayout.addComponent(hl);
		
	}

	

	private void draw(VerticalLayout contentLayout, BlocGrille bloc,SizeInfo sizeInfo, AbstractPeriodePermanenceGrillePart grillePart)
	{
		
		// Le titre
		HorizontalLayout header1 = new HorizontalLayout();
		Label l = new Label(bloc.titre);
		l.addStyleName(bloc.styleTitre);
		l.setWidth("400px");
		header1.addComponent(l);
		contentLayout.addComponent(header1);
		
		
		//
		for (int i = 0; i < bloc.lines.size(); i++)
		{
			BlocGrilleLine pc = bloc.lines.get(i);
			header1 = new HorizontalLayout();
			
			header1.setHeight(sizeInfo.heights[i]+"px");
			
			Label l1 = new Label(pc.col1);
			l1.setSizeFull();
			l1.addStyleName(pc.styleCol1);
			l1.setWidth("200px");
			header1.addComponent(l1);
			
			Label l2 = new Label(pc.col2);
			l2.setSizeFull();
			l2.addStyleName(pc.styleCol2);
			l2.setWidth("200px");
			header1.addComponent(l2);
			
			contentLayout.addComponent(header1);
		}
		
		// On place ensuite un rectangle vide pour permettre l'alignement des boutons
		int h = computeHeightAdditionalSpace(sizeInfo,bloc.lines.size());
		if (h!=0)
		{
			header1 = new HorizontalLayout();
			header1.setHeight(h+"px");
			contentLayout.addComponent(header1);
		}
		
		
		// La partie specifique
		Layout layout = grillePart.addSpecificButton(bloc.date);
		if (layout!=null)
		{
			contentLayout.addComponent(layout);
		}
		
		
	}

	private int computeHeightAdditionalSpace(SizeInfo sizeInfo, int start)
	{
		int height=0;
		for (int i = start; i < sizeInfo.heights.length; i++)
		{
			height = height+sizeInfo.heights[i];
		}
		
		return height;
	}
	
	private SizeInfo computeSizeInfos(List<BlocGrille> line)
	{
		int max = 0;
		for (BlocGrille blocGrille : line)
		{
			max = Math.max(max, blocGrille.lines.size());
		}
		
		SizeInfo sizeInfo = new SizeInfo();
		sizeInfo.heights = new int[max];
		
		for (int i = 0; i < max; i++)
		{
			sizeInfo.heights[i] = computeHeight(i, line);
		}
		return sizeInfo;
	}

	private int computeHeight(int i, List<BlocGrille> line)
	{
		GridHeaderLine gridHeaderLine = new GridHeaderLine();
		
		for (BlocGrille blocGrille : line)
		{
			if (i<blocGrille.lines.size())
			{
				gridHeaderLine.cells.add(blocGrille.lines.get(i).col1);
				gridHeaderLine.cells.add(blocGrille.lines.get(i).col2);
			}
		}
		
		GridSizeCalculator.autoSize(gridHeaderLine,200,"Arial",16);
		
		return gridHeaderLine.height;
	}

	
	public class SizeInfo
	{
		int[] heights;
	}
	
	
}
