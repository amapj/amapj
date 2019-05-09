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
 package fr.amapj.view.engine.grid.booleangrid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.engine.grid.ErreurSaisieException;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.popup.corepopup.CorePopup;

/**
 * Popup pour la saisie des quantites 
 *  
 */
@SuppressWarnings("serial")
abstract public class PopupBooleanGrid extends CorePopup
{
	
	protected Table table;
	
	protected Button saveButton;
	protected String saveButtonTitle = "Sauvegarder";
	protected Button cancelButton;
	protected String cancelButtonTitle = "Annuler";
	
	protected BooleanGridParam param = new BooleanGridParam();
	
	private boolean errorInInitialCondition = false;
	
	/**
	 * 
	 */
	public PopupBooleanGrid()
	{
		setHeight("90%");	
	}
	
	abstract public void loadParam();
	

	abstract public void performSauvegarder();
	
		
	
	protected void createContent(VerticalLayout mainLayout)
	{
		// Indispensable : si cette ligne n'est pas présente , alors la barre de defilement horizontale disparait 
		setType(PopupType.CENTERFIT);
		
		// Vérification des conditions initiales
		String str = checkInitialCondition();
		if (str!=null)
		{
			errorInInitialCondition = true;
			popupTitle = "Impossible";
			displayErrorOnInitialCondition(str,mainLayout);
			return;
		}
		
		
		
		loadParam();
		
		if (param.messageSpecifique!=null)
		{
			mainLayout.addComponent(new Label(param.messageSpecifique,ContentMode.HTML));
		}
		
		// Construction des headers
		for (GridHeaderLine line : param.headerLines)
		{
			constructHeaderLine(mainLayout,line);
		}
		
		// Construction de la table de saisie 
		table =  new Table();
		table.addStyleName("no-vertical-lines");
		table.addStyleName("no-horizontal-lines");
		table.addStyleName("no-stripes");
		
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		
		// Colonne de gauche contenant un libellé
		table.addContainerProperty(new Integer(-1), Label.class, null);
		table.setColumnWidth(new Integer(-1), param.largeurCol);
		
		if (param.leftPartLine2!=null)
		{
			table.addContainerProperty(new Integer(-2), Label.class, null);
			table.setColumnWidth(new Integer(-2), param.largeurCol);
		}
		
		
		
		// Les autres colonnes correspondant à la saisie des boolean
		for (int i = 0; i < param.nbCol; i++)
		{
			table.addContainerProperty(new Integer(i), CheckBox.class, null);
			table.setColumnWidth(new Integer(i), param.largeurCol);
		}
		
		
		// Creation de toutes les cellules pour la saisie
		for (int i = 0; i < param.nbLig; i++)
		{
			addRow(i);
		}

		
		table.setEditable(true);

		table.setSelectable(true);
		table.setSortEnabled(false);
		table.setPageLength(getPageLength());
		
		mainLayout.addComponent(table);

	}
	
	
	protected void createButtonBar()
	{		
		if (errorInInitialCondition)
		{
			addButton("OK", e->	close());
			return ;
		}
		
		cancelButton = addButton(cancelButtonTitle, e->	handleAnnuler());
		saveButton = addDefaultButton(saveButtonTitle, e-> handleSauvegarder());
	}
	

	
	private void constructHeaderLine(VerticalLayout mainLayout, GridHeaderLine line)
	{
		HorizontalLayout header1 = new HorizontalLayout();
		if (line.height!=-1)
		{
			header1.setHeight(line.height+"px");
		}
		
		int index=0;
		for (String str : line.cells)
		{
			Label dateLabel = new Label(str);
			dateLabel.setSizeFull();
			if (line.styleName!=null)
			{
				dateLabel.addStyleName(line.styleName);
			}
			
			if (index==0)
			{
				dateLabel.setWidth((param.largeurCol+5)+"px");
			}
			else
			{
				dateLabel.setWidth((param.largeurCol+2)+"px");
			}
			
			header1.addComponent(dateLabel);
			index++;
			
		}
		mainLayout.addComponent(header1);
	}



	private int getPageLength()
	{
		WebBrowser webBrowser = UI.getCurrent().getPage().getWebBrowser();
		int pageLength = 15;
		
		// Pour ie 8 et inférieur : on se limite a 6 lignes, sinon ca rame trop
		if (webBrowser.isIE() && webBrowser.getBrowserMajorVersion()<9)
		{
			pageLength = 6;
		}
		
		pageLength = Math.min(pageLength, param.nbLig);
		return pageLength;
	}


	private void addRow(int lig)
	{
		List<Object> cells = new ArrayList<Object>();
		
		
		
		Label dateLabel = new Label(param.leftPartLine.get(lig));
		dateLabel.addStyleName("big");
		dateLabel.addStyleName("align-center");
		dateLabel.setWidth(param.largeurCol+"px");
		cells.add(dateLabel);
		
		if (param.leftPartLine2!=null)
		{
			dateLabel = new Label(param.leftPartLine2.get(lig));
			dateLabel.addStyleName("big");
			dateLabel.addStyleName("align-center");
			dateLabel.setWidth(param.largeurCol+"px");
			cells.add(dateLabel);
		}
		
		
		
		for (int j = 0; j < param.nbCol; j++)
		{
			boolean box = param.box[lig][j];
			
			CheckBox checkbox = new CheckBox();
			checkbox.setValue(box);
			checkbox.addStyleName("align-center");
			checkbox.addStyleName("big");
			checkbox.setWidth((param.largeurCol-10)+"px");
			cells.add(checkbox);
		}
		
		table.addItem(cells.toArray(), new Integer(lig));
		
	}


	/**
	 * Retourne la valeur dans la cellule sous la forme d'un boolean
	 * jette une exception si il y a une erreur
	 */
	private boolean readValueInCell(CheckBox tf)
	{
		return tf.getValue().booleanValue();
	}


	protected void handleAnnuler()
	{
		close();
	}

	protected void handleSauvegarder()
	{
		try
		{
			updateModele();
		}
		catch (ErreurSaisieException e)
		{
			Notification.show("Erreur de saisie");
			return ;
		}
		
		performSauvegarder();
		
		close();
	}
	
	
	/**
	 * Lecture de la table pour mettre à jour le modele
	 * @return
	 */
	private void updateModele() throws ErreurSaisieException
	{	
		for (int i = 0; i < param.nbLig; i++)
		{
			Item item = table.getItem(new Integer(i));
			
			for (int j = 0; j < param.nbCol; j++)
			{
				CheckBox tf = (CheckBox) item.getItemProperty(new Integer(j)).getValue();
				boolean val = readValueInCell(tf);
				param.box[i][j] = val;
			}
		}
	}
	
	
	/**
	 * Should be overriden
	 * @return
	 */
	protected String checkInitialCondition()
	{
		return null;
	}
	
	private void displayErrorOnInitialCondition(String str, VerticalLayout mainLayout)
	{
		Label label = new Label(str,ContentMode.HTML);
		label.setStyleName(ChameleonTheme.LABEL_BIG);
		mainLayout.addComponent(label);
	}

}
