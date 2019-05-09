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
 package fr.amapj.view.engine.grid.integergrid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.grid.ErreurSaisieException;
import fr.amapj.view.engine.grid.GridHeaderLine;
import fr.amapj.view.engine.grid.GridIJData;
import fr.amapj.view.engine.grid.ShortCutManager;
import fr.amapj.view.engine.notification.NotificationHelper;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour la saisie des quantites 
 *  
 */
@SuppressWarnings("serial")
abstract public class PopupIntegerGrid extends CorePopup
{

	private Table table;

	private Label prixTotal;

	protected IntegerGridParam param = new IntegerGridParam();

	private ShortCutManager shortCutManager;
	
	private Label labelMessageSpecifiqueBottom;

	/**
	 * 
	 */
	public PopupIntegerGrid()
	{

	}

	abstract public void loadParam();

	/**
	 * Retourne true si il faut fermer le popup
	 * @return
	 */
	abstract public boolean performSauvegarder();
	
	abstract public int getLineHeight(boolean readOnly);
	
	abstract public int getHeaderHeight();

	protected void createContent(VerticalLayout mainLayout)
	{
		setType(PopupType.CENTERFIT);
		loadParam();
		param.initialize();

		if (param.messageSpecifique != null)
		{
			Label messageSpeLabel = new Label(param.messageSpecifique);
			messageSpeLabel.addStyleName("popup-integer-grid-message");
			mainLayout.addComponent(messageSpeLabel);
		}

		// Construction des headers
		for (GridHeaderLine line : param.headerLines)
		{
			constructHeaderLine(mainLayout, line);
		}

		// Construction de la table de saisie
		table = new Table();
		table.addStyleName("no-vertical-lines");
		table.addStyleName("no-horizontal-lines");
		table.addStyleName("no-stripes");
		

		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);

		
		// Colonne de gauche contenant un libellé
		table.addContainerProperty(new Integer(-1), Label.class, null);
		table.setColumnWidth(new Integer(-1), param.leftPartLineLargeur);
		

		// Les autres colonnes correspondant à la saisie des quantites
		for (int i = 0; i < param.nbCol; i++)
		{
			Class clzz;
			if (param.readOnly)
			{
				clzz = Label.class;
			}
			else
			{
				clzz = TextField.class;
			}
			table.addContainerProperty(new Integer(i), clzz, null);
			table.setColumnWidth(new Integer(i), param.largeurCol);
		}

		//
		if (param.readOnly==false)
		{
			shortCutManager = new ShortCutManager(param.nbLig, param.nbCol, param.excluded);
			shortCutManager.addShorcut(this.getWindow());
		}

		// Creation de toutes les cellules pour la saisie
		for (int i = 0; i < param.nbLig; i++)
		{
			addRow(i);
		}

		if (param.readOnly)
		{
			table.setEditable(false);
		}
		else
		{
			table.setEditable(true);
		}
		table.setSelectable(true);
		table.setSortEnabled(false);
		table.setPageLength(getPageLength());

		// Footer 0 pour avoir un espace
		HorizontalLayout footer0 = new HorizontalLayout();
		footer0.setWidth("200px");
		footer0.setHeight("20px");

		// Footer 1 avec le prix total
		HorizontalLayout footer1 = new HorizontalLayout();

		Label dateLabel = new Label(param.libPrixTotal);
		dateLabel.addStyleName("libprix");
		footer1.addComponent(dateLabel);
		
		prixTotal = new Label("");
		displayMontantTotal();
		prixTotal.addStyleName("prix");
		prixTotal.setWidth("100px");
		footer1.addComponent(prixTotal);
		
		// Construction globale
		mainLayout.addComponent(table);
		mainLayout.addComponent(footer0);
		mainLayout.addComponent(footer1);
		
		// Message spécifique en bas de popup
		if (param.messageSpecifiqueBottom != null)
		{
			labelMessageSpecifiqueBottom = new Label(param.messageSpecifiqueBottom);
			labelMessageSpecifiqueBottom.addStyleName("popup-integer-grid-message");
			mainLayout.addComponent(labelMessageSpecifiqueBottom);
		}

	}
		
	protected void createButtonBar()
	{
		if (param.readOnly)
		{
			Button ok = addDefaultButton("Continuer ...", e->handleContinuer());
		}
		else
		{
			if ((param.buttonCopyFirstLine==true) && (param.nbLig > 1))
			{
				Button copierButton = addButton("Copier la 1ère ligne partout", e->	handleCopier());
				setButtonAlignement(copierButton, Alignment.TOP_LEFT);
			}

			
			Button cancelButton = addButton("Annuler", e->handleAnnuler());
			
			Button saveButton = addDefaultButton("Continuer ...", e->handleSauvegarder());
			saveButton.addStyleName("primary");
			
		}
	}

	protected void handleCopier()
	{
		try
		{
			doHandleCopier();
		}
		catch (ErreurSaisieException e)
		{
			NotificationHelper.displayNotification("Erreur de saisie sur la premiere ligne - Impossible de copier");
		}
	}

	private void doHandleCopier() throws ErreurSaisieException
	{
		Item item = table.getItem(new Integer(0));

		for (int j = 0; j < param.nbCol; j++)
		{
			if (isExcluded(0, j) == false)
			{
				// Lecture de la valeur dans la case tout en haut
				TextField tf = (TextField) item.getItemProperty(new Integer(j)).getValue();
				int qteRef = readValueInCell(tf);

				// Copie de cette valeur dans toutes les cases en dessous
				for (int i = 0; i < param.nbLig; i++)
				{
					if (isExcluded(i, j) == false)
					{
						Item item1 = table.getItem(new Integer(i));
						TextField tf1 = (TextField) item1.getItemProperty(new Integer(j)).getValue();
						tf1.setConvertedValue(qteRef);
					}
				}
			}
		}
	}

	private void constructHeaderLine(VerticalLayout mainLayout, GridHeaderLine line)
	{
		HorizontalLayout header1 = new HorizontalLayout();
		if (line.height != -1)
		{
			header1.setHeight(line.height + "px");
		}

		int index=0;
		for (String str : line.cells)
		{
			Label dateLabel = new Label(str);
			dateLabel.setSizeFull();
			if (line.styleName != null)
			{
				dateLabel.addStyleName(line.styleName);
			}
			if (index==0)
			{
				dateLabel.setWidth((param.leftPartLineLargeur+5)+"px");
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
		Page page = UI.getCurrent().getPage();
		int pageLength = 15;
		
		// On limite le nombre de ligne pour ne pas avoir une double scroolbar
		
		//
		int lineHeight = getLineHeight(param.readOnly);   	
		
		// On cacule la place consommée par les headers, boutons, ...
		int headerAndButtonHeight = getHeaderHeight();
		
		
		int maxLineAvailable = (page.getBrowserWindowHeight()-headerAndButtonHeight)/lineHeight;
		
		// Il y a au moins 4 lignes visibles
		maxLineAvailable = Math.max(maxLineAvailable, 4);  						
		pageLength = Math.min(pageLength,maxLineAvailable);

		// Pour ie 8 et inférieur : on se limite a 6 lignes, sinon ca rame trop
		WebBrowser webBrowser = UI.getCurrent().getPage().getWebBrowser();
		if (webBrowser.isIE() && webBrowser.getBrowserMajorVersion() < 9)
		{
			pageLength = Math.min(pageLength,6);
		}

		//
		pageLength = Math.min(pageLength, param.nbLig);
		
		return pageLength;
	}

	/**
	 * Calcul de la largeur totale de la table
	 * @return
	 */
	/*private String getLargeurTotal()
	{
		return ( (param.leftPartLineLargeur+param.espaceInterCol) + (param.nbCol) * (param.largeurCol + param.espaceInterCol) )+ "px";
	}*/

	private void displayMontantTotal()
	{
		prixTotal.setValue(new CurrencyTextFieldConverter().convertToString(param.getMontantTotal()));
	}

	private void addRow(int lig)
	{
		List<Object> cells = new ArrayList<Object>();

		Label dateLabel = new Label(param.leftPartLine.get(lig),ContentMode.HTML);
		dateLabel.addStyleName(param.leftPartLineStyle);
		

		cells.add(dateLabel);
		for (int j = 0; j < param.nbCol; j++)
		{
			int qte = param.qte[lig][j];
			boolean isExcluded = isExcluded(lig, j);

			// En lecture simple
			if (param.readOnly)
			{
				//
				String txt;

				if (isExcluded)
				{
					txt = "XXXXXX";
				}
				else if (qte == 0)
				{
					txt = "";
				}
				else
				{
					txt = "" + qte;
				}
				Label tf = new Label(txt);
				tf.addStyleName("cell-voir");
				tf.setWidth((param.largeurCol - 10) + "px");
				cells.add(tf);
			}
			// En mode normal
			else 
			{
				// Si la cellule est exclue
				if (isExcluded)
				{
					TextField tf = new TextField();
					tf.setValue("XXXXXX");
					tf.setEnabled(false);
					tf.addStyleName("cell-voir");
					tf.setWidth((param.largeurCol - 10) + "px");
					cells.add(tf);
				}
				else
				{
					//
					final TextField tf = BaseUiTools.createQteField("");
					tf.setData(new GridIJData(lig, j));
					if (qte == 0)
					{
						tf.setConvertedValue(null);
					}
					else
					{
						tf.setConvertedValue(new Integer(qte));
					}
					tf.addValueChangeListener(new Property.ValueChangeListener()
					{
						@Override
						public void valueChange(ValueChangeEvent event)
						{
							try
							{
								GridIJData ij = (GridIJData) tf.getData();
								int qte = readValueInCell(tf);
								param.updateQte(ij.i(), ij.j(), qte);
								displayMontantTotal();
							}
							catch (ErreurSaisieException e)
							{
								NotificationHelper.displayNotificationQte();
							}
						}
					});
	
					tf.addStyleName("cell-saisie");
					tf.setWidth((param.largeurCol - 10) + "px");
					shortCutManager.registerTextField(tf);
					cells.add(tf);
				}
			}
		}

		table.addItem(cells.toArray(), new Integer(lig));

	}

	
	/**
	 * Indique si cette cellule est exclue de la saisie
	 * @param lig
	 * @param col
	 * @return
	 */
	private boolean isExcluded(int lig, int col)
	{
		if (param.excluded == null)
		{
			return false;
		}
		return param.excluded[lig][col];
	}

	/**
	 * Retourne la valeur dans la cellule sous la forme d'un entier
	 * jette une exception si il y a une erreur
	 */
	private int readValueInCell(TextField tf) throws ErreurSaisieException
	{
		try
		{
			Integer val = (Integer) tf.getConvertedValue();
			int qte = 0;
			if (val != null)
			{
				qte = val.intValue();
			}
			return qte;
		}
		catch (ConversionException e)
		{
			throw new ErreurSaisieException();
		}
	}

	
	protected void handleAnnuler()
	{
		close();
	}
	
	/**
	 * Cette méthode est appelée quand l'utilisateur clique sur "Continuer" et que l'on est en read Only 
	 */
	protected void handleContinuer()
	{
		close();
	}
	
	/**
	 * Cette méthode est appelée quand l'utilisateur clique sur "Continuer" et que l'on N'est PAS en read Only 
	 */	
	protected void handleSauvegarder()
	{
		try
		{
			updateModele();
		}
		catch (ErreurSaisieException e)
		{
			NotificationHelper.displayNotificationQte();
			return;
		}
		
		if ((param.allowedEmpty==false) && (isEmpty()==true))
		{
			NotificationHelper.displayNotification("Vous devez saisir une quantité avant de continuer");
			return;
		}

		boolean ret = performSauvegarder();
		if (ret==true)
		{
			close();
		}
	}

	/**
	 * Retourne true si la grille est vide (uniquement des 0)
	 * @return
	 */
	private boolean isEmpty()
	{
		for (int i = 0; i < param.nbLig; i++)
		{
			for (int j = 0; j < param.nbCol; j++)
			{
				if (param.qte[i][j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Lecture de la table pour mettre à jour contratDTO
	 * @return
	 */
	private void updateModele() throws ErreurSaisieException
	{
		for (int i = 0; i < param.nbLig; i++)
		{
			Item item = table.getItem(new Integer(i));

			for (int j = 0; j < param.nbCol; j++)
			{
				if (isExcluded(i, j)==false)
				{
					TextField tf = (TextField) item.getItemProperty(new Integer(j)).getValue();
					int qte = readValueInCell(tf);
					param.qte[i][j] = qte;
				}
			}
		}
	}
	
	protected void updateLabelMessageSpecifiqueBottom(String msg)
	{
		labelMessageSpecifiqueBottom.setValue(msg);
	}
	
	protected void updateFirstCol(int lineNumber,String msg)
	{
		Item item = table.getItem(new Integer(lineNumber));
		Label label = (Label) item.getItemProperty(new Integer(-1)).getValue();
		label.setValue(msg);
	}

	/**
	 * Cette methode doit etre appele apres la modification de la matrice de prix pour affichage correct du prix total  
	 */
	protected void updatePrixTotal()
	{
		param.initialize();
		displayMontantTotal();
	}
	
}
