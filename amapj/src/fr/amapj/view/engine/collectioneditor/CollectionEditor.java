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
 package fr.amapj.view.engine.collectioneditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.common.AmapjRuntimeException;
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
 * A noter : la collection donnée en sortie est totalement independante de la collection 
 * donnée en entrée 
 * Tous les objets sont re créés , et les champs affichés sont copiés  
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
	
	

	private Table table;
	private BeanItem item;
	private Object propertyId;
	private Class<BEANTYPE> beanType;
	
	// La liste des objects graphiques permettant l'édition
	private RowList rows;
	
	// La liste des colonnes à afficher
	private List<ColumnInfo> columns;

	// Permet de desactiver les boutons monter, descendre,ajouter, supprimer
	private boolean btnAjouter = true;
	private boolean btnSupprimer = true;
	private boolean btnMonter = true;
	private boolean btnDescendre = true;
	
 
	
	
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
		columns.add(new ColumnInfo(propertyId, title, fieldType, true,defaultValue));
	}
	
	
	public void addColumn(String propertyId, String title,FieldType fieldType,boolean editable,Object defaultValue)
	{
		columns.add(new ColumnInfo(propertyId, title, fieldType, editable,defaultValue));
	}

	
	public void addSearcherColumn(String propertyId, String title, FieldType fieldType, Object defaultValue,SearcherDefinition searcher,Searcher linkedSearcher)
	{
		columns.add(new SearcherColumn(propertyId, title, fieldType, true,defaultValue,searcher,linkedSearcher));
	}
	
	public void addSearcherColumn(String propertyId, String title, FieldType fieldType, boolean editable,Object defaultValue,SearcherDefinition searcher,Searcher linkedSearcher)
	{
		columns.add(new SearcherColumn(propertyId, title, fieldType, editable,defaultValue,searcher,linkedSearcher));
	}
	
	
	

	private void buildTable()
	{
		table = new Table();
		table.addStyleName("no-stripes");
		table.addStyleName("no-vertical-lines");
		table.addStyleName("no-horizontal-lines");
		
		for (ColumnInfo col : columns)
		{
			Class clazz = getFieldAsClass(col.fieldType);
			table.addContainerProperty(col.title, clazz, null);
		}
		
		
		// Ajout des lignes 
		List<BEANTYPE> beans = (List<BEANTYPE>) item.getItemProperty(propertyId).getValue();
		for (BEANTYPE bean : beans)
		{
			addRow(bean);		
		}
		
		table.setPageLength(10);
		table.addActionHandler(this);

		table.setEditable(true);
		table.setSelectable(true);
		table.setImmediate(true);
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
		
		throw new AmapjRuntimeException();
	
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
		
		throw new AmapjRuntimeException();
		
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
			f.setReadOnly(!col.editable);
			
			
			// GESTION DU FOCUS : on sélectionne la ligne dont un élement a le focus
			if (shouldHaveFocusNotifier(f,col.editable))
			{
				FocusNotifier tf = (FocusNotifier) f;
				tf.addFocusListener(e->table.select(row.getItemId()));
			}
			row.addField(f);
		}
		
		// Ajout de la ligne et calcul de l'item id	et memorisation de l'id eventuel
		Object idInfo = getIdBeanInfo(beanItem);
		rows.add(row,idInfo);
		
		//
		table.addItem(row.getColumnTable(),row.getItemId());
		
		
	}
	
	
	



	/**
	 * Point delicat et etrange 
	 * 
	 * Pour les textfield avec setreadonly = true, il ne faut pas mettre le listener sur le focus
	 * Par contre, il faut bien le mettre sur les autres, comme par exemple les combo 
	 * 
	 */
	private boolean shouldHaveFocusNotifier(AbstractField f, boolean editable)
	{
		// 
		if ((f instanceof FocusNotifier)==false)
		{
			return false;
		}
		
		// Si c'est editable : on ajoute toujours le listener
		if (editable)
		{
			return true;
		}
		
		// Si text field non editable : pas de listener 
		if (f instanceof TextField)
		{
			return false;
		}
		else
		{
			return true;
		}
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


	@Override
	public Action[] getActions(Object target, Object sender)
	{
		List<Action> acts = new ArrayList<Action>();
		
		if (btnAjouter)
		{
			acts.add(add);
		}
		if (btnSupprimer)
		{
			acts.add(remove);
		}
		if (btnMonter)
		{
			acts.add(up);
		}
		if (btnDescendre)
		{
			acts.add(down);
		}
		
		return acts.toArray(new Action[acts.size()]);
		
	}

	@Override
	public Object getValue()
	{
		return computeValue();
	}

	@Override
	public void commit() throws SourceException, InvalidValueException
	{
		List<BEANTYPE> ls = computeValue();
		item.getItemProperty(propertyId).setValue(ls);
	}
	
	
	
	private List<BEANTYPE> computeValue()
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
				
				setIdBeanInfo(beanItem,row.getIdBeanInfo());
				
				ls.add(elt);
			}
			return ls;
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
		
		
		if (hasOneButtonOrMore())
		{
			addButtons(vl);
		}
		
		return vl;

	}


	private void addButtons(VerticalLayout vl)
	{
		HorizontalLayout buttons = new HorizontalLayout();
		if (btnAjouter)
		{
			buttons.addComponent(new Button(getMasterDetailAddItemCaption(), e->addRow(null)));
		}
		if (btnSupprimer)
		{
			buttons.addComponent(new Button(getMasterDetailRemoveItemCaption(), e->remove(getTable().getValue())));
		}
		if (btnMonter)
		{
			buttons.addComponent(new Button(getMasterDetailUpItemCaption(), e->	up(getTable().getValue())));
		}
		if (btnDescendre)
		{
			buttons.addComponent(new Button(getMasterDetailDownItemCaption(), e->down(getTable().getValue())));
		}
			
		vl.addComponent(buttons);
		
	}


	
	private boolean hasOneButtonOrMore()
	{
		return btnAjouter || btnSupprimer || btnMonter || btnDescendre;
	}
	
	

	/**
	 * Permet de desactiver/activer les boutons monter, descendre,ajouter, supprimer
	 */
	public void activeButton(boolean btnAjouter,boolean btnSupprimer,boolean btnMonter,boolean btnDescendre)
	{
		this.btnAjouter = btnAjouter;
		this.btnSupprimer = btnSupprimer;
		this.btnMonter = btnMonter;
		this.btnDescendre = btnDescendre;
	}
	
	
	public void disableAllButtons()
	{
		activeButton(false, false, false, false);
	}
	
	
	
	
	private String propertyIdBeanToPreserve;
	
	
	/**
	 * Permet de gerer un identifiant qui sera préservé lors du déplacement des lignes par monter / descendre 
	 * 
	 * Dans le cas des lignes ajoutés, cet identifiant reste null 
	 */
	public void addBeanIdToPreserve(String propertyIdBeanToPreserve)
	{
		this.propertyIdBeanToPreserve = propertyIdBeanToPreserve;
	}
	
	private Object getIdBeanInfo(BeanItem beanItem)
	{
		if (propertyIdBeanToPreserve==null)
		{
			return null;
		}
		if (beanItem==null)
		{
			return null;
		}
		return beanItem.getItemProperty(propertyIdBeanToPreserve).getValue();
	}
	
	
	private void setIdBeanInfo(BeanItem beanItem, Object idBeanInfo)
	{
		if (propertyIdBeanToPreserve==null)
		{
			return;
		}
		beanItem.getItemProperty(propertyIdBeanToPreserve).setValue(idBeanInfo);
	}

}
