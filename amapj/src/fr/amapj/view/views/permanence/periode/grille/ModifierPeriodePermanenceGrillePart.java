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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;

import fr.amapj.service.services.permanence.periode.PeriodePermanenceDateDTO;
import fr.amapj.view.views.permanence.grille.AbstractPeriodePermanenceGrillePart;

/**
 * Permet la modification de la grille  
 *
 */
public class ModifierPeriodePermanenceGrillePart extends AbstractPeriodePermanenceGrillePart
{

	public ModifierPeriodePermanenceGrillePart(Long idPeriodePermanence)
	{
		super(idPeriodePermanence, null);
	}
	
	@Override
	protected String getHeader()
	{
		return null;
	}

	@Override
	protected Layout addSpecificButton(PeriodePermanenceDateDTO date)
	{
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("400px");
		hl.setHeight("45px");
		
		Button b = new Button("Modifier");
		b.addStyleName("primary");	
		b.addClickListener(e->click(date));
		
		hl.addComponent(b);
		hl.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
		
		return hl;
		
	}

	private void click(PeriodePermanenceDateDTO date)
	{
		PopupModifRoleUtilisateur.open(new PopupModifRoleUtilisateur(date.idPeriodePermanenceDate),this);
	}

}
