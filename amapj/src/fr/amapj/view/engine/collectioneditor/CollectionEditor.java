/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.engine.collectioneditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.view.engine.collectioneditor.columns.ColumnInfo;
import fr.amapj.view.engine.collectioneditor.columns.SearcherColumn;
import fr.amapj.view.engine.enumselector.EnumSearcher;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.engine.searcher.SearcherDefinition;
import fr.amapj.view.engine.tools.BaseUiTools;

/**
 * Permet la saisie de multi valeur  dans un tableau
 * en lien avec un BeanItem 
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CollectionEditor<BEANTYPE> extends CustomField implements Action.Handler
{

	private final static Logger logger = LogManager.getLogger();
	
	
	final private Action add = new Action(getMasterDetailAddItemCaption());
	final private Action remove = new Action(getMasterDetailRemoveItemCaption());
	final private Action up = new Action(getMasterDetailUpItemCaption());
	final private Action down = new Action(getMasterDetailDownItemCaption());
	
	final private Action[] actions = new Action[] { add, remove , up , down};
	

	private Table table;
	private BeanItem item;
	private Object propertyId;
	private Class<BEANTYPE> beanType;
	
	// La liste des objects graphiques permettant l'édition
	private RowList rows;
	
	// La liste des colonnes à afficher
	private List<ColumnInfo> columns;

	// Permet de desactiver tous les boutons monter, descendre,ajouter, supprimer
	private boolean disableButton = false;
	

	/**
	 * 
	 * 
	 */
	public CollectionEditor(String caption,BeanItem item, Object propertyId,Class<BEANTYPE> beanType)
	{
		this.item = item;
		this.beanType = beanType;
		this.propertyId = propertyId;
		
		rows = new RowList();
		columns = new ArrayList<ColumnInfo>();
		
		setCaption(caption);
	}
	
	
	
	public void addColumn(String propertyId, String title,FieldType fieldType,Object defaultValue)
	{
		columns.add(new ColumnInfo(propertyId, title, fieldType, defaultValue));
	}
	
	public void addSearcherColumn(String propertyId, String title, FieldType fieldType, Object defaultValue,SearcherDefinition searcher,Searcher linkedSearcher)
	{
		columns.add(new SearcherColumn(propertyId, title, fieldType, defaultValue,searcher,linkedSearcher));
	}
	
	
	

	private void buildTable()
	{
		table = new Table();
		table.addStyleName("no-stripes");
		table.addStyleName("no-vertical-lines");
		table.addStyleName("no-horizontal-lines");
		
		
		/*
		 * Define the names and data types of columns. The "default value"
		 * parameter is meaningless here.
		 */
		for (ColumnInfo col : columns)
		{
			Class clazz = getFieldAsClass(col.fieldType);
			table.addContainerProperty(col.title, clazz, null);
		}
		
		
		/* Add  items in the table. */
	
		List<BEANTYPE> beans = (List<BEANTYPE>) item.getItemProperty(propertyId).getValue();
		for (BEANTYPE bean : beans)
		{
			addRow(bean);		
		}
		
		getTable().setPageLength(10);
		getTable().addActionHandler(this);

		getTable().setEditable(true);
		getTable().setSelectable(true);
		table.setSortEnabled(false);
	}
	
	
	private Class getFieldAsClass(FieldType fieldType)
	{
		switch (fieldType)
		{
		case STRING:
			return TextField.class;
			
		case SEARCHER:
			return ComboBox.class;

		case CURRENCY:
			return TextField.class;
		
		case QTE:
			return TextField.class;

		case INTEGER:
			return TextField.class;

		case DATE:
			return PopupDateField.class;

			
		case CHECK_BOX:
			return CheckBox.class;
			
		case COMBO:
			return ComboBox.class;

			
		}
		
		throw new RuntimeException("Erreur inattendue");
	
	}
	
	
	private AbstractField getField(FieldType fieldType,ColumnInfo col)
	{
		switch (fieldType)
		{
		case STRING:
			TextField str = new TextField();
			str.addStyleName("text");
			return str;
			 
		case SEARCHER:
			SearcherColumn s = (SearcherColumn) col;
			Searcher box =   new Searcher(s.searcher,null);
			if (s.params!=null)
			{
				box.setParams(s.params);
			}
			if (s.linkedSearcher!=null)
			{
				box.setLinkedSearcher(s.linkedSearcher);
			}
			box.addStyleName("searcher");
			return box;
			
		case CURRENCY:
			TextField currency  = BaseUiTools.createCurrencyField("",false);
			currency.addStyleName("currency");
			return currency;
			
		case QTE:
			TextField qte = BaseUiTools.createQteField("");
			qte.addStyleName("qte");
			return qte;
			
		case INTEGER:
			TextField integer = BaseUiTools.createIntegerField("");
			integer.addStyleName("integer");
			return integer;
			
		case DATE:
			PopupDateField dateField = BaseUiTools.createDateField("");
			dateField.addStyleName("date");
			return dateField;

			
		case CHECK_BOX:
			CheckBox checkBox = BaseUiTools.createCheckBoxField("");
			checkBox.addStyleName("checkbox");
			return checkBox;
			
			
		case COMBO:
			ComboBox combo = EnumSearcher.createEnumSearcher("", (Enum) col.defaultValue);
			combo.addStyleName("combo");
			return combo;


		}
		
		throw new RuntimeException("Erreur inattendue");
		
	}




	/**
	 * Permet l'ajout d'une ligne dans le tableau
	 * 
	 * Si bean est null, alors la ligne est chargé avec les valeurs par défaut 
	 * 
	 * @param bean
	 */
	private void addRow(BEANTYPE bean)
	{
		// Create the table row.
		final Row row = new Row();
		BeanItem beanItem = null;
		if (bean !=null)
		{
			beanItem = new BeanItem(bean);
		}
		
		
				
		// Ajout de toutes les colonnes
		for (ColumnInfo col : columns)
		{
			Object val1 = col.defaultValue;
			
			// Récupération des données
			if (beanItem !=null)
			{
				val1 = beanItem.getItemProperty(col.propertyId).getValue();
			}
					
			AbstractField f = getField(col.fieldType,col);
			f.setConvertedValue(val1);
			
			// GESTION DU FOCUS : on sélectionne la ligne dont un élement a le focus
			if (f instanceof FocusNotifier)
			{
				FocusNotifier tf = (FocusNotifier) f;
				tf.addFocusListener(new FocusListener()
				{	
					@Override
					public void focus(FocusEvent event)
					{
						table.select(row.getItemId());
					}
				});
			}
			
			row.addField(f);
		}
		
		// Ajout de la ligne et calcul de l'item id		 
		rows.add(row);
		
		//
		table.addItem(row.getColumnTable(),row.getItemId());
		
		
	}
	
	
	
	private void remove(Object itemId)
	{
		if (itemId==null)
		{
			return ;
		}
		
		//
		Object selectedRow = rows.remove(itemId);
		table.removeItem(itemId);
		if (selectedRow!=null)
		{
			table.select(selectedRow);
		}
	}
	
	
	protected Table getTable()
	{
		return table;
	}

	protected String getMasterDetailRemoveItemCaption()
	{
		return "Supprimer";
	}

	protected String getMasterDetailAddItemCaption()
	{
		return "Ajouter";
	}
	
	protected String getMasterDetailUpItemCaption()
	{
		return "Monter";
	}
	
	protected String getMasterDetailDownItemCaption()
	{
		return "Descendre";
	}
	

	public void handleAction(Action action, Object sender, Object target)
	{
		if (action == add)
		{
			addRow(null);
		} 
		else if (action == remove)
		{
			remove(target);
		}
		else if (action == up)
		{
			up(target);
		}
		else if (action == down)
		{
			down(target);
		}

	}

	private void down(Object target)
	{
		if (target==null)
		{
			return ;
		}
		
		int index = rows.getIndex(target);
		if (rows.canDown(index)==false)
		{
			return ;
		}
		Object itemId = rows.downRow(index);
		table.select(itemId);
		
	}



	private void up(Object target)
	{
		if (target==null)
		{
			return ;
		}
		
		int index = rows.getIndex(target);
		if (rows.canUp(index)==false)
		{
			return ;
		}
		Object itemId = rows.upRow(index);
		table.select(itemId);
		
	}



	public Action[] getActions(Object target, Object sender)
	{
		if (disableButton)
		{
			return new Action[0];
		}
		else
		{
			return actions;
		}
	}

	

	@Override
	public void commit() throws SourceException, InvalidValueException
	{
		try
		{
			List<BEANTYPE> ls = new ArrayList<BEANTYPE>();
			
			for (Row row : rows.getRows())
			{
				BEANTYPE elt = beanType.newInstance();
				BeanItem beanItem = new BeanItem(elt);
				
				int i=0;
				for (ColumnInfo col : columns)
				{
					Object val = row.getFieldValue(i);
					beanItem.getItemProperty(col.propertyId).setValue(val);
					i++;
				}
				
				ls.add(elt);
			}
			item.getItemProperty(propertyId).setValue(ls);
		}
		catch (InstantiationException  | IllegalAccessException  | ReadOnlyException e)
		{
			logger.warn("Commit failed", e);
			throw new RuntimeException("Erreur inattendue",e);
		}

	}

	@Override
	public Class<?> getType()
	{
		return List.class;
	}

	public Collection getElements()
	{
		return (Collection) getPropertyDataSource().getValue();
	}

	
	@Override
	protected Component initContent()
	{
		VerticalLayout vl = new VerticalLayout();
		vl.addStyleName("collection-editor");
		buildTable();
		vl.addComponent(getTable());
		
		
		if (disableButton==false)
		{
			addButtons(vl);
		}
		
		return vl;

	}


	private void addButtons(VerticalLayout vl)
	{
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponent(new Button(getMasterDetailAddItemCaption(), e->addRow(null)));
		buttons.addComponent(new Button(getMasterDetailRemoveItemCaption(), e->remove(getTable().getValue())));
		buttons.addComponent(new Button(getMasterDetailUpItemCaption(), e->	up(getTable().getValue())));
		buttons.addComponent(new Button(getMasterDetailDownItemCaption(), e->down(getTable().getValue())));
			
		vl.addComponent(buttons);
		
	}



	/**
	 * Permet de desactiver tous les boutons monter, descendre,ajouter, supprimer
	 */
	public void disableAllButtons()
	{
		disableButton = true;
		
	}

}
