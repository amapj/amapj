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
 package fr.amapj.view.engine.popup.formpopup.tab;

import com.vaadin.ui.VerticalLayout;

/**
 * 
 *
 */
public class TabInfo
{
	public String tabName;
	public TabDraw tabDraw;
	public TabValidate tabValidate;
	
	public VerticalLayout verticalLayout;

	public TabInfo(String tabName, TabDraw tabDraw)
	{
		this.tabDraw = tabDraw;
		this.tabName = tabName;
	}
	
	public TabInfo(String tabName, TabDraw tabDraw,TabValidate tabValidate)
	{
		this.tabDraw = tabDraw;
		this.tabName = tabName;
		this.tabValidate = tabValidate;
	}

};