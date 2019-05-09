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
 package fr.amapj.view.engine.listpart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.template.ListPartView;
import fr.amapj.view.engine.tools.TableItem;
import fr.amapj.view.engine.tools.TableTools;


/**
 * Ecran classique avec une liste d'elements 
 *
 */
@SuppressWarnings("serial")
abstract public class StandardListPart<T extends TableItem> extends ListPartView implements ComponentContainer ,  PopupListener
{

	private TextField searchField;

	private List<ButtonHandler> buttons = new ArrayList<ButtonHandler>();
	
	private String textFilter;

	private BeanItemContainer<T> mcInfos;

	protected Table cdesTable;
	
	private Class<T> beanClazz;
	
	// Par defaut, une seule ligne est selectionnable à la fois 
	private boolean multiSelect;

	public StandardListPart(Class<T> beanClazz,boolean multiSelect)
	{
		this.beanClazz = beanClazz;
		this.multiSelect = multiSelect;
	}
	
	abstract protected String getTitle();
	
	abstract protected void drawButton();
	
	abstract protected void drawTable();
	
	
	/**
	 * Composant entre le titre et la barre des boutons
	 */
	protected void addSelectorComponent()
	{
		
	}
	
	/**
	 * Composant entre la barre des boutons et la table
	 */
	protected void addExtraComponent()
	{
		
	}
	
	
	/**
	 * Retourne la liste des lignes à afficher 
	 * 
	 * Retourne null si l'ensemble des boutons doivent être desactivés (par exemple,si le selector est dans un etat tel que 
	 * l'ensemble des boutons doivent être inactif)
	 *  
	 * @return
	 */
	abstract protected List<T> getLines();
	
	abstract protected String[] getSortInfos();
	
	/**
	 * Permet à la classe fille d'indiquer si le tri est ascendant ou descendant 
	 * La taille du tableau retournée doit être identique à la taille retournée par String[] getSortInfos()
	 * 
	 * Par défaut, le tri est ascendant (true)
	 * @return
	 */
	protected boolean[] getSortAsc()
	{
		return null;
	}
	
	
	abstract protected String[] getSearchInfos();
	
	
	/**
	 * Permet à l'implémentation fille d'indiquer si l'utilisateur a le droit d'éditer cette ligne en particulier
	 * Dans le cas génaral, l'édition est autorisée
	 * @return
	 */
	protected boolean isEditAllowed()
	{
		return true;
	}
	
	
	@Override
	public void enterIn(ViewChangeEvent event)
	{
		buildMainArea();
	}
	
	
	public void addButton(String label,ButtonType type, ListPartButtonListener listener)
	{
		Button newButton = new Button(label);
		newButton.addClickListener(new Button.ClickListener()
		{

			@Override
			public void buttonClick(ClickEvent event)
			{
				listener.handleButtonPressed();
			}
		});
		
		//
		ButtonHandler handler = new ButtonHandler();
		handler.button = newButton;
		handler.type = type;
		buttons.add(handler);
	}
	
	
	public void addSearchField(String label)
	{
		searchField = new TextField();
		searchField.setInputPrompt(label);
		searchField.addTextChangeListener(new TextChangeListener()
		{

			@Override
			public void textChange(TextChangeEvent event)
			{
				textFilter = event.getText();
				updateFilters();
			}
		});
	}
	
	

	private void buildMainArea()
	{
		// Lecture dans la base de données
		mcInfos = new BeanItemContainer<T>(beanClazz);
			
		// Bind it to a component
		cdesTable = createTable(mcInfos);
		
		drawTable();
		

		cdesTable.setSelectable(true);
		cdesTable.setMultiSelect(multiSelect);
		cdesTable.setImmediate(true);

		// Activation ou desactivation des boutons delete et edit
		cdesTable.addValueChangeListener(new Property.ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				if (multiSelect)
				{
					Set s = (Set) event.getProperty().getValue();
					buttonBarEditMode( s.size()>0);
				}
				else
				{
					buttonBarEditMode(event.getProperty().getValue() != null);
				}
			}
		});

		cdesTable.setSizeFull();

		cdesTable.addItemClickListener(new ItemClickListener()
		{
			@Override
			public void itemClick(ItemClickEvent event)
			{
				if (event.isDoubleClick())
				{
					cdesTable.select(event.getItemId());
				}
			}
		});

		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.addStyleName("buttonbar");
		
		Label title2 = new Label(getTitle());
		title2.setSizeUndefined();
		title2.addStyleName("title");	
		
		drawButton();
		
		for (ButtonHandler handler : buttons) 
		{
			toolbar.addComponent(handler.button);	
		}
		
		if (searchField!=null)
		{
			toolbar.addComponent(searchField);
			toolbar.setWidth("100%");
			toolbar.setExpandRatio(searchField, 1);
			toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
		}

		
	
		addComponent(title2);
		addSelectorComponent();
		addComponent(toolbar);
		addExtraComponent();
		addComponent(cdesTable);
		setExpandRatio(cdesTable, 1);
		
		refreshTable();

	}



	private void updateFilters()
	{
		mcInfos.removeAllContainerFilters();
		if (textFilter != null && !textFilter.equals(""))
		{
			String[] searchInfos = getSearchInfos();
			Filter[] filters = new Filter[searchInfos.length];
			
			for (int i = 0; i < searchInfos.length; i++) 
			{
				String search = searchInfos[i];
				filters[i] = new Like(search, "%"+textFilter + "%", false);
			}
			Or or = new Or(filters);
			mcInfos.addContainerFilter(or);
		}
	}
	
	
	
	
	/**
	 * Permet de rafraichir la table
	 */
	public void refreshTable()
	{
		String[] sortColumns = getSortInfos(); 
		boolean[] ascColumns = getSortAsc();
		boolean[] sortAscending;
		if (ascColumns==null)
		{
			sortAscending = new boolean[sortColumns.length];
			Arrays.fill(sortAscending,true);
		}
		else 
		{
			if (ascColumns.length!=sortColumns.length)
			{
				throw new AmapjRuntimeException("Les deux méthodes getSortAsc et getSortInfos doivent retourner deux tableaux de même taille");
			}
			sortAscending = ascColumns;
		}
		
		List<T> res = getLines();
		
		if (res==null)
		{
			mcInfos.removeAllItems();
			buttonBarFull(false);
			return ;
		}
		
		
		boolean enabled;
		
		if (multiSelect==true)
		{
			enabled = TableTools.updateTableMultiselect(cdesTable, res, sortColumns, sortAscending);
		}
		else
		{
			enabled = TableTools.updateTable(cdesTable, res, sortColumns, sortAscending);
		}
		
		buttonBarFull(true);
		buttonBarEditMode(enabled);		
	}

	
	
	@Override
	public void onPopupClose()
	{
		refreshTable();
		
	}
	
	
	/**
	 * Permet d'activer ou de désactiver toute la barre des boutons
	 * 
	 */
	private void buttonBarFull(boolean enable)
	{
		for (ButtonHandler handler : buttons) 
		{
			handler.button.setEnabled(enable);
		}
	}
	
	/**
	 * Permet d'activer ou de désactiver les boutons de la barre 
	 * qui sont relatifs au mode édition, c'est à dire les boutons 
	 * Edit et Delete
	 */
	private void buttonBarEditMode(boolean enable)
	{
		if ( (enable==true) && isEditAllowed()==false)
		{
			enable = false;
		}
		
		
		for (ButtonHandler handler : buttons) 
		{
			if (handler.type==ButtonType.EDIT_MODE)
			{
				handler.button.setEnabled(enable);
			}
		}		
	}
	
	
	/**
	 * Retourne la liste des lignes selectionnées
	 * @return
	 */
	protected List<T> getSelectedLines()
	{
		if (multiSelect==false)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas utiliser cette methode en mono selection ");
		}
		
		List<T> res = new ArrayList<T>();
		Set s = (Set) cdesTable.getValue();
		res.addAll(s);
		return res;
	}
	
	
	/**
	 * Retourne la ligne selectionnée
	 * 
	 * Peux retourner null si il n'y a pas de ligne selectionnée
	 * @return
	 */
	protected T getSelectedLine()
	{
		if (multiSelect==true)
		{
			throw new AmapjRuntimeException("Vous ne pouvez pas utiliser cette methode en multi selection ");
		}
		
		T dto = (T) cdesTable.getValue();
		return dto;
	}

	public BeanItemContainer<T> getBeanItemContainer()
	{
		return mcInfos;
	}
	
	
	
	
}
