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
 package fr.amapj.view.engine.tools;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * Outil pour créer les tables specifiques avec saisie dans la table 
 *
 */
public class TableBuilder
{
	public Label createLabel(String msg,int taille)
	{
		Label l = new Label(msg);
		l.addStyleName("align-center");
		l.setWidth(taille+"px");
		return l;
	}
	
	public CheckBox createCheckBox(boolean value,int taille)
	{
		CheckBox cb = new CheckBox();
		cb.addStyleName("align-center");
		cb.setValue(value);
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		return cb;
	}
	
	public Button createButton(String msg,int taille)
	{
		Button cb = new Button(msg);
		cb.addStyleName("align-center");
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		return cb;
	}
	
	public TextField createTextField(String value,int taille)
	{
		TextField tf = new TextField();
		tf.setValue(value);
		tf.setWidth(taille+"px");
		tf.setNullRepresentation("");
		tf.setImmediate(true);
		return tf;
	}
	
	
	/**
	 * PARTIE HEADER
	 */
	
	HorizontalLayout header1;
	String styleName;
	int height;
	
	public void startHeader(String styleName,int height)
	{
		header1 = new HorizontalLayout();
		header1.setHeight(null);
		header1.setWidth(null);
		
		this.styleName = styleName;
		this.height = height;
	}
	
	
	public void addHeaderBox(String msg,int taille)
	{
		Label hLabel = new Label(msg);
		hLabel.setWidth((taille+13)+"px");
		hLabel.setHeight(height+"px");
		hLabel.addStyleName(styleName);
		header1.addComponent(hLabel);
	}

	public HorizontalLayout getHeader() 
	{
		return header1;
	}

	
	
	
	


}
