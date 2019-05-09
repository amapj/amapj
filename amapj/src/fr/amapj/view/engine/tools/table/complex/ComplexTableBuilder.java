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
 package fr.amapj.view.engine.tools.table.complex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.service.engine.generator.CoreGenerator;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.searcher.Searcher;
import fr.amapj.view.engine.searcher.SearcherDefinition;
import fr.amapj.view.engine.tools.table.TableColumnInfo;
import fr.amapj.view.engine.tools.table.TableColumnType;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.engine.widgets.IntegerTextFieldConverter;

/**
 * Outil pour créer les tables specifiques avec saisie dans la table 
 *
 */
public class ComplexTableBuilder<T>
{
	
	static public interface ToValue<T>
	{
		public Object toValue(T t);
	}
	
	static public interface CallBack<T>
	{
		public void onClick(T t);
	}
	
	
	static public interface ToGenerator<T>
	{
		public CoreGenerator getGenerator(T t);
	}
	
	
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	private List<TableColumnInfo<T>> cols;
	
	private Table t;
	
	private List<T> beans;
	
	private int pageLength = 15;
	
	public ComplexTableBuilder(List<T> beans)
	{
		this.beans = beans;
		
		cols = new ArrayList<TableColumnInfo<T>>();
		
	}
	
	//
	public void addString(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, editable,width, TableColumnType.STRING, toVal,null, null, null);
	}
	
	public void addString(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,editable,width, TableColumnType.STRING, toVal,null, null, null);
	}
	
	//
	public void addInteger(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, editable,width, TableColumnType.INTEGER, toVal,null, null, null);
	}
	
	public void addInteger(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,editable,width, TableColumnType.INTEGER, toVal,null, null, null);
	}
	
	//
	public void addCurrency(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, editable,width, TableColumnType.CURRENCY, toVal,null, null, null);
	}
	
	public void addCurrency(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,editable,width, TableColumnType.CURRENCY, toVal,null, null, null);
	}
	
	//
	public void addDate(String title, boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, null, editable,width, TableColumnType.DATE, toVal,null, null, null);
	}
	
	public void addDate(String title, String property,boolean editable,int width,ToValue<T> toVal)
	{
		addColumn(title, property,editable,width, TableColumnType.DATE, toVal,null, null, null);
	}
	
	
	//
	public void addCheckBox(String title, boolean editable,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, null,editable,width, TableColumnType.CHECK_BOX, toVal,onClic, null, null);
	}
		
	public void addCheckBox(String title, String property,boolean editable,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, property,editable,width, TableColumnType.CHECK_BOX, toVal,onClic, null, null);
	}
	
	
	//
	public void addButton(String title, int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, null,false,width, TableColumnType.BUTTON, toVal,onClic, null, null);
	}
		
	public void addButton(String title, String property,int width,ToValue<T> toVal,CallBack<T> onClic)
	{
		addColumn(title, property,false,width, TableColumnType.BUTTON, toVal,onClic, null, null);
	}
	
	
	public void addLink(String title, int width,ToValue<T> toVal,ToGenerator<T> generator)
	{
		addColumn(title, null,false,width, TableColumnType.LINK, toVal,null,generator, null);
	}
	
	
	//
	public void addSearcher(String title, boolean editable,int width,ToValue<T> toVal,SearcherDefinition searcher)
	{
		addColumn(title, null, editable,width, TableColumnType.SEARCHER, toVal,null, null, searcher);
	}
	
	public void addSearcher(String title, String property,boolean editable,int width,ToValue<T> toVal,SearcherDefinition searcher)
	{
		addColumn(title, property,editable,width, TableColumnType.SEARCHER, toVal,null, null, searcher);
	}
	
	
	
	
	
	private void addColumn(String title, String property,boolean editable, int width,TableColumnType type, ToValue<T> toVal,CallBack<T> onClic, ToGenerator<T> generator, SearcherDefinition searcher)
	{
		cols.add(new TableColumnInfo<T>(title, property,editable,width,type, toVal,onClic,generator,searcher));
	}
	
	
	public void buildComponent(Layout contentLayout)
	{
		
		startHeader("tete", 70);
		
		for (TableColumnInfo<T> col : cols)
		{
			addHeaderBox(col.title, col.width+13);
		}
		contentLayout.addComponent(header1);
		
		
		// Construction du contenu de la table
		t = new Table();
		int index = 0;
		for (TableColumnInfo<T> col : cols)
		{
			String property = col.property;
			if (property==null)
			{
				property = "property"+index;
			}
			t.addContainerProperty(property, getClass(col.type,col.editable), null);
			index++;
		}
		
		// Remplissage des lignes
		index = 0;
		for (T bean : beans)
		{
			Object[] cells = computeCell(bean);
			t.addItem(cells, index);
			index++;
		}
		

		t.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		t.setSelectable(true);
		t.setSortEnabled(false);
		t.setPageLength(pageLength);
		
		contentLayout.addComponent(t);
	}
	
	private Class<?> getClass(TableColumnType type,boolean editable)
	{
		switch (type)
		{
		case STRING:
		case INTEGER:
		case DATE:
		case CURRENCY:
			if (editable)
			{
				return TextField.class;
			}
			else
			{
				return Label.class;
			}
			
		case CHECK_BOX:
			return CheckBox.class;
			
		case BUTTON:
			return Button.class;
			
		case LINK:
			return Link.class;
			
		case SEARCHER:
			return ComboBox.class;
			

		default:
			throw new AmapjRuntimeException();
		}
	}
	
	
	
	private Object[] computeCell(T bean)
	{
		Object[] cells = new Object[cols.size()];
		
		int index = 0;
		for (TableColumnInfo<T> col : cols)
		{
			cells[index] = createPart(col,bean);
			index++;
		}
		
		return cells;
	}
	

	private Object createPart(TableColumnInfo<T> col, T bean)
	{
		switch (col.type)
		{
		case STRING:
			Object o = col.toVal.toValue(bean);
			if (o==null)
			{
				o="";
			}
			return createLabel( o.toString(),col.width);

		case DATE:
			return createLabel( df.format( (Date) col.toVal.toValue(bean)),col.width);
			
		case INTEGER:
			Integer cVal = (Integer) col.toVal.toValue(bean);
			if (col.editable)
			{
				return createIntegerEditableField(cVal,col.width);
			}
			else
			{
				return createLabel( Integer.toString( cVal),col.width);
			}
			
			
		case CURRENCY:
			Integer currentVal = (Integer) col.toVal.toValue(bean);
			if (col.editable)
			{
				return createCurrencyEditableField(currentVal,col.width);
			}
			else
			{
				return createLabel( new CurrencyTextFieldConverter().convertToString(currentVal),col.width);
			}
			
		case CHECK_BOX:
			return createCheckBox((Boolean) col.toVal.toValue(bean), col.width);
			
		case BUTTON:
			String str = (String) col.toVal.toValue(bean);
			if (str!=null)
			{
				return createButton( str, col.width,col.onClic,bean);
			}
			else
			{
				return null;
			}
			
		case LINK:
			return createLink( col.toVal.toValue(bean).toString(), col.width,col.generator,bean);
			
		case SEARCHER:
			return createSearcher( (Long) col.toVal.toValue(bean), col.searcher , col.editable , col.width,bean);

			
			
		default:
			throw new AmapjRuntimeException();
		}

	}

	
	
	private ComboBox createSearcher(Long value, SearcherDefinition searcher, boolean editable, int width, T bean)
	{
		Searcher box =   new Searcher(searcher,null);
		box.setConvertedValue(value);
		/**if (s.params!=null)
		{
			box.setParams(s.params);
		}
		if (s.linkedSearcher!=null)
		{
			box.setLinkedSearcher(s.linkedSearcher);
		}
		box.addStyleName("searcher");*/
		return box;
	}

	private TextField createIntegerEditableField(Integer currentVal,int taille)
	{
		TextField tf = new TextField();
		tf.addStyleName("align-center");
		tf.setConverter(new IntegerTextFieldConverter());
		tf.setConvertedValue(currentVal);
		tf.setNullRepresentation("");
		tf.setWidth(taille+"px");
		tf.setImmediate(true);
		return tf;
	}

	private TextField createCurrencyEditableField(Integer currentVal,int taille)
	{
		TextField tf = new TextField();
		tf.addStyleName("align-center");
		tf.setConverter(new CurrencyTextFieldConverter(true));
		tf.setConvertedValue(currentVal);
		tf.setNullRepresentation("");
		tf.setWidth(taille+"px");
		tf.setImmediate(true);
		return tf;
	}

	private Label createLabel(String msg,int taille)
	{
		Label l = new Label(msg);
		l.addStyleName("align-center");
		l.setWidth(taille+"px");
		return l;
	}
	
	private CheckBox createCheckBox(boolean value,int taille)
	{
		CheckBox cb = new CheckBox();
		cb.addStyleName("align-center");
		cb.setValue(value);
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		return cb;
	}
	
	private Button createButton(String msg,int taille, CallBack<T> onClic,T t)
	{
		Button cb = new Button(msg);
		cb.addStyleName("align-center");
		cb.setWidth(taille+"px");
		cb.setImmediate(true);
		cb.addClickListener(e->onClic.onClick(t));
		return cb;
	}
	
	private Link createLink(String msg,int taille, ToGenerator<T> generator,T t)
	{
		Link l = LinkCreator.createLink(generator.getGenerator(t));
		
		l.setCaption(msg);
		l.addStyleName("align-center");
		l.setWidth(taille+"px");
		l.setImmediate(true);
		
		return l;
	}
	
	
	
	private TextField createTextField(String value,int taille)
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
	
	private void startHeader(String styleName,int height)
	{
		header1 = new HorizontalLayout();
		header1.setHeight(null);
		header1.setWidth(null);
		
		this.styleName = styleName;
		this.height = height;
	}
	
	
	private void addHeaderBox(String msg,int taille)
	{
		Label hLabel = new Label(msg);
		hLabel.setWidth((taille+13)+"px");
		hLabel.setHeight(height+"px");
		hLabel.addStyleName(styleName);
		header1.addComponent(hLabel);
	}

	
	
	/**
	 * Retourne le composant à la ligne lineNumber et à la colonne property
	 */
	public AbstractField getComponent(int lineNumber,String property)
	{
		Item item = t.getItem(lineNumber);
		
		AbstractField tf = (AbstractField) item.getItemProperty(property).getValue();
		return tf;
	}
	
	/**
	 * Retourne le Button à la ligne lineNumber et à la colonne property
	 */
	public Button getButton(int lineNumber,String property)
	{
		Item item = t.getItem(lineNumber);
		
		Button tf = (Button) item.getItemProperty(property).getValue();
		return tf;
	}
	
	/**
	 * Retourne le Button correspondant à la ligne t et à la colonne property
	 */
	public Button getButton(T t,String property)
	{
		int lineNumber = beans.indexOf(t);
		return getButton(lineNumber, property);
	}
	
	
	
	/**
	 * Retourne le Label à la ligne lineNumber et à la colonne property
	 */
	public Label getLabel(int lineNumber,String property)
	{
		Item item = t.getItem(lineNumber);
		
		Label tf = (Label) item.getItemProperty(property).getValue();
		return tf;
	}
	
	/**
	 * Retourne le Label correspondant à la ligne t et à la colonne property
	 */
	public Label getLabel(T t,String property)
	{
		int lineNumber = beans.indexOf(t);
		return getLabel(lineNumber, property);
	}

	
	
	
	public void reload(List<T> beans)
	{
		this.beans = beans;
		
		t.removeAllItems();
		
		// Remplissage des lignes
		int index = 0;
		for (T bean : beans)
		{
			Object[] cells = computeCell(bean);
			t.addItem(cells, index);
			index++;
		}
		
	}

	public void setPageLength(int pageLength)
	{
		this.pageLength = pageLength;
	}
	
	/**
	 * @return la ligne sélectionnée, retourne null si aucune ligne selectionnée 
	 */
	public T getSelectedLine()
	{
		Integer index = (Integer) t.getValue();
		if (index==null)
		{
			return null;
		}
		
		T dto = beans.get(index); 
		return dto;
	}

	public void addStyleName(String styleName)
	{
		t.addStyleName(styleName);
		
	}

}
