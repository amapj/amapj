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
 package fr.amapj.view.samples.test003;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;



import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.samples.VaadinTest;


/**
 * 
 * ChameleonThemeEditor
 * 
 * 
 *
 */

@SuppressWarnings("serial")
public class Test003 implements VaadinTest
{

	final Window main = new Window("Vaadin Chameleon Theme");

	HorizontalSplitPanel root = new HorizontalSplitPanel();

	private class PageChangeListener implements ClickListener
	{
		public void buttonClick(final ClickEvent event)
		{
			final Button button = event.getButton();
			final Component componentToSwitch = buttonComponentMap.get(button);
			switchTo(componentToSwitch, button);
		}
	}

	VerticalLayout previewTabs;
	VerticalLayout compoundTabs;
	private final Map<Button, Component> buttonComponentMap = new LinkedHashMap<Button, Component>();
	private Button shownTab = null;
	private final ClickListener tabChangeListener = new PageChangeListener();

	CssLayout previewArea = new CssLayout();
	//StyleEditor editor = new StyleEditor();
	Button editorToggle = new Button();
	
	UI ui;
	
	
	@Override
	public void buildView(VaadinRequest request, UI ui)
	{
		this.ui = ui;
		initView();
		try
		{
			buildComponentPreviews();
		}
		catch (ValueOutOfBoundsException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//showWelcomeScreen();
	}

	private void showWelcomeScreen()
	{
		CustomLayout welcome = new CustomLayout("welcome");
		welcome.setSizeFull();
		root.setSecondComponent(welcome);
	}

	private void initView()
	{
		ui.addWindow(main);

		AbsoluteLayout al = new AbsoluteLayout();
		al.setSizeFull();

		//al.addComponent(editor.getStyleElement(), "bottom:0;left:0;");
		al.addComponent(root);

		root.setSizeFull();
		root.setSplitPosition(15);
		root.setStyleName("small previews");

		main.setContent(al);

		previewArea.setWidth("100%");
		previewTabs = new VerticalLayout();
		previewTabs.setSizeFull();
		previewTabs.setHeight(null);
		compoundTabs = new VerticalLayout();
		compoundTabs.setSizeFull();
		compoundTabs.setHeight(null);

		VerticalLayout menu = new VerticalLayout();
		menu.setSizeFull();
		menu.setStyleName("sidebar-menu");

		menu.addComponent(new Label("Single Components"));
		menu.addComponent(previewTabs);
		menu.addComponent(new Label("Compound Styles"));
		menu.addComponent(compoundTabs);

		root.setFirstComponent(menu);

		editorToggle.setIcon(new ThemeResource("bucket.png"));
		editorToggle.setDescription("Show color editor");
		editorToggle.setStyleName("icon-only");
		editorToggle.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				toggleEditor();
			}
		});
		/*editor.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				editorToggle.removeStyleName("down");
			}
		});*/
		toggleEditor();

		CssLayout toolbar = new CssLayout();
		toolbar.setWidth("100%");
		toolbar.setStyleName("toolbar");
		toolbar.addComponent(editorToggle);

		final Window downloadWindow = new Window("Download Theme");
		GridLayout l = new GridLayout(3, 2);
		l.setSizeUndefined();
		l.setMargin(true);
		l.setSpacing(true);
		downloadWindow.setContent(l);
		downloadWindow.setModal(true);
		downloadWindow.setResizable(false);
		downloadWindow.setCloseShortcut(KeyCode.ESCAPE, null);
		downloadWindow.addStyleName("opaque");
		Label caption = new Label("Theme Name");
		l.addComponent(caption);
		l.setComponentAlignment(caption, Alignment.MIDDLE_CENTER);
		final TextField name = new TextField();
		name.setValue("my-chameleon");
		name.addValidator(new RegexpValidator("[a-zA-Z0-9\\-_\\.]+", "Only alpha-numeric characters allowed"));
		name.setRequired(true);
		name.setRequiredError("Please give a name for the theme");
		downloadWindow.setContent(name);
		Button dl = new Button("Download", new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if (name.isValid())
				{
					ui.removeWindow(downloadWindow);
					//main.open(editor.getDownloadResource((String) name.getValue(), getMainWindow().getApplication()));
				}
			}
		});
		dl.setClickShortcut(KeyCode.ENTER, null);
		dl.addStyleName("default small wide");
		downloadWindow.setContent(dl);
		Label info = new Label("This is the name you will use to set the theme in your application code, i.e. <code>setTheme(\"my-cameleon\")</code>.",
				Label.CONTENT_XHTML);
		info.addStyleName("tiny");
		info.setWidth("200px");
		l.addComponent(info, 1, 1, 2, 1);

		Button download = new Button(null, new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				ui.addWindow(downloadWindow);
				name.focus();
			}
		});
		download.setDescription("Donwload the current theme");
		download.setIcon(new ThemeResource("download.png"));
		download.setStyleName("icon-only");
		toolbar.addComponent(download);

		menu.addComponent(toolbar);
		menu.setExpandRatio(toolbar, 1);
		menu.setComponentAlignment(toolbar, Alignment.BOTTOM_CENTER);

	}

	public void addTab(final VerticalLayout menu, final Component content, final String caption)
	{
		if (content == null || caption == null)
		{
			throw new NullPointerException("Arguments may not be null");
		}

		final Button button = new NativeButton(caption, tabChangeListener);
		button.setWidth("100%");

		menu.addComponent(button);
		buttonComponentMap.put(button, content);
	}

	private void switchTo(final Component componentToSwitch, final Button button)
	{
		if (shownTab != null)
		{
			shownTab.removeStyleName("tab-selected");
		}
		shownTab = button;
		if (shownTab != null)
		{
			shownTab.addStyleName("tab-selected");
		}

		root.setSecondComponent(previewArea);
		previewArea.removeAllComponents();
		previewArea.addComponent(componentToSwitch);
	}

	private void toggleEditor()
	{
		/* TODO
		if (main.getChildWindows().contains(editor))
		{
			main.removeWindow(editor);
			editorToggle.removeStyleName("down");
		}
		else
		{
			main.addWindow(editor);
			editorToggle.addStyleName("down");
		}*/

	}

	private void buildComponentPreviews() throws ValueOutOfBoundsException
	{
		// Compound styles need to be added first so that basic style are shown
		// first
		addTab(compoundTabs, getCompoundButtons(), "Buttons");
		addTab(compoundTabs, getCompoundMenus(), "Menus");

		addTab(previewTabs, getLabelPreviews(), "Labels");
		addTab(previewTabs, getButtonPreviews(), "Buttons");
		addTab(previewTabs, getTextFieldPreviews(), "Text fields");
		addTab(previewTabs, getSelectPreviews(), "Selects");
		addTab(previewTabs, getDateFieldPreviews(), "Date fields");
		addTab(previewTabs, getSliderPreviews(), "Sliders");
		addTab(previewTabs, getPanelPreviews(), "Panels");
		addTab(previewTabs, getSplitPreviews(), "Split panels");
		addTab(previewTabs, getTabsheetPreviews(), "Tab sheets");
		addTab(previewTabs, getAccordionPreviews(), "Accordions");
		addTab(previewTabs, getTablePreviews(), "Tables");
		addTab(previewTabs, getProgressIndicatorPreviews(), "Progress indicators");
		addTab(previewTabs, getTreePreviews(), "Trees");
		addTab(previewTabs, getPopupViewPreviews(), "Popup views");
		addTab(previewTabs, getMenuBarPreviews(), "Menu bars");
		addTab(previewTabs, getWindowPreviews(), "Windows");
	}

	private Layout getLabelPreviews()
	{
		Layout grid = getPreviewLayout("Labels");

		Label label = new Label("<h4>Paragraph Header</h4>Plain text, lorem ipsum dolor sit amet consectetur amit.", Label.CONTENT_XHTML);
		label.setWidth("200px");
		grid.addComponent(label);

		label = new Label("Big plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("big");
		grid.addComponent(label);

		label = new Label("Small plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("small");
		grid.addComponent(label);

		label = new Label("Tiny plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("tiny");
		grid.addComponent(label);

		label = new Label("<h1>Top Level Header</h1>", Label.CONTENT_XHTML);
		label.setSizeUndefined();
		grid.addComponent(label);
		label.setDescription("Label.addStyleName(\"h1\");<br>or<br>new Label(\"&lt;h1&gt;Top Level Header&lt;/h1&gt;\", Label.CONTENT_XHTML);");

		label = new Label("<h2>Second Header</h2>", Label.CONTENT_XHTML);
		label.setSizeUndefined();
		grid.addComponent(label);
		label.setDescription("Label.addStyleName(\"h2\");<br>or<br>new Label(\"&lt;h2&gt;Second Header&lt;/h2&gt;\", Label.CONTENT_XHTML);");

		label = new Label("<h3>Subtitle</h3>", Label.CONTENT_XHTML);
		label.setSizeUndefined();
		grid.addComponent(label);
		label.setDescription("Label.addStyleName(\"h3\");<br>or<br>new Label(\"&lt;h3&gt;Subtitle&lt;/h3&gt;\", Label.CONTENT_XHTML);");

		label = new Label("<h4>Paragraph Header</h4>Plain text, lorem ipsum dolor sit amet consectetur amit.", Label.CONTENT_XHTML);
		label.setWidth("200px");
		label.setStyleName("color");
		grid.addComponent(label);

		label = new Label("Big plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("big color");
		grid.addComponent(label);

		label = new Label("Small plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("small color");
		grid.addComponent(label);

		label = new Label("Tiny plain text, lorem ipsum dolor sit amet consectetur amit.");
		label.setWidth("200px");
		label.setStyleName("tiny color");
		grid.addComponent(label);

		label = new Label("Top Level Header");
		label.setSizeUndefined();
		label.setStyleName("h1 color");
		grid.addComponent(label);

		label = new Label("Second Header");
		label.setSizeUndefined();
		label.setStyleName("h2 color");
		grid.addComponent(label);

		label = new Label("Subtitle");
		label.setSizeUndefined();
		label.setStyleName("h3 color");
		grid.addComponent(label);

		label = new Label("Warning text, lorem ipsum dolor sit.");
		label.setStyleName("warning");
		grid.addComponent(label);

		label = new Label("Error text, lorem ipsum dolor.");
		label.setStyleName("error");
		grid.addComponent(label);

		label = new Label("Big warning text");
		label.setStyleName("big warning");
		grid.addComponent(label);

		label = new Label("Big error text");
		label.setStyleName("big error");
		grid.addComponent(label);

		label = new Label("Loading text...");
		label.setStyleName("h3 loading");
		grid.addComponent(label);

		return grid;
	}

	private Layout getButtonPreviews()
	{
		Layout grid = getPreviewLayout("Buttons");

		Button button = new Button("Button");
		grid.addComponent(button);

		button = new Button("Default");
		button.setStyleName("default");
		grid.addComponent(button);

		button = new Button("Small");
		button.setStyleName("small");
		grid.addComponent(button);

		button = new Button("Small Default");
		button.setStyleName("small default");
		grid.addComponent(button);

		button = new Button("Big");
		button.setStyleName("big");
		grid.addComponent(button);

		button = new Button("Big Default");
		button.setStyleName("big default");
		grid.addComponent(button);

		button = new Button("Disabled");
		button.setEnabled(false);
		grid.addComponent(button);

		button = new Button("Disabled default");
		button.setEnabled(false);
		button.setStyleName("default");
		grid.addComponent(button);

		button = new Button("Link style");
		button.setStyleName(BaseTheme.BUTTON_LINK);
		grid.addComponent(button);

		button = new Button("Disabled link");
		button.setStyleName(BaseTheme.BUTTON_LINK);
		button.setEnabled(false);
		grid.addComponent(button);

		button = new Button("120px overflows out of the button");
		button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		button.setWidth("120px");
		grid.addComponent(button);

		button = new Button("Small");
		button.setStyleName("small");
		button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(button);

		button = new Button("Big");
		button.setStyleName("big");
		button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(button);

		button = new Button("Big Default");
		button.setStyleName("big default");
		button.setIcon(new ThemeResource("../runo/icons/32/document-txt.png"));
		grid.addComponent(button);

		button = new Button("Big link");
		button.setStyleName(BaseTheme.BUTTON_LINK + " big");
		button.setIcon(new ThemeResource("../runo/icons/32/document.png"));
		grid.addComponent(button);

		button = new Button("Borderless");
		button.setStyleName("borderless");
		button.setIcon(new ThemeResource("../runo/icons/32/note.png"));
		grid.addComponent(button);

		button = new Button("Borderless icon on top");
		button.setStyleName("borderless icon-on-top");
		button.setIcon(new ThemeResource("../runo/icons/32/note.png"));
		grid.addComponent(button);

		button = new Button("Icon on top");
		button.setStyleName("icon-on-top");
		button.setIcon(new ThemeResource("../runo/icons/32/users.png"));
		grid.addComponent(button);

		button = new Button("Wide Default");
		button.setStyleName("wide default");
		grid.addComponent(button);

		button = new Button("Wide");
		button.setStyleName("wide");
		grid.addComponent(button);

		button = new Button("Tall");
		button.setStyleName("tall");
		grid.addComponent(button);

		button = new Button("Wide, Tall & Big");
		button.setStyleName("wide tall big");
		grid.addComponent(button);

		button = new Button("Icon on right");
		button.setStyleName("icon-on-right");
		button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(button);

		button = new Button("Big icon");
		button.setStyleName("icon-on-right big");
		button.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(button);

		button = new Button("Toggle (down)");
		button.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if (event.getButton().getStyleName().endsWith("down"))
				{
					event.getButton().removeStyleName("down");
				}
				else
				{
					event.getButton().addStyleName("down");
				}
			}
		});
		button.addStyleName("down");
		grid.addComponent(button);
		button.setDescription(button.getDescription() + "<br><strong>Stylename switching logic must be done separately</strong>");

		button = new Button();
		button.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if (event.getButton().getStyleName().endsWith("down"))
				{
					event.getButton().removeStyleName("down");
				}
				else
				{
					event.getButton().addStyleName("down");
				}
			}
		});
		button.addStyleName("icon-only");
		button.addStyleName("down");
		button.setIcon(new ThemeResource("../runo/icons/16/user.png"));
		grid.addComponent(button);
		button.setDescription(button.getDescription() + "<br><strong>Stylename switching logic must be done separately</strong>");

		Link l = new Link("Link: vaadin.com", new ExternalResource("http://vaadin.com"));
		grid.addComponent(l);

		l = new Link("Link: vaadin.com", new ExternalResource("http://vaadin.com"));
		l.setIcon(new ThemeResource("../runo/icons/32/globe.png"));
		grid.addComponent(l);

		return grid;
	}

	private Layout getTextFieldPreviews()
	{
		Layout grid = getPreviewLayout("Text fields");

		TextField tf = new TextField();
		tf.setValue("Text field");
		grid.addComponent(tf);

		tf = new TextField();
		tf.setValue("Small field");
		tf.setStyleName("small");
		grid.addComponent(tf);

		tf = new TextField();
		tf.setValue("Big field");
		tf.setStyleName("big");
		tf.setComponentError(new UserError("Test error"));
		grid.addComponent(tf);

		tf = new TextField();
		tf.setInputPrompt("Search field");
		tf.setStyleName("search");
		grid.addComponent(tf);

		tf = new TextField();
		tf.setInputPrompt("Small search");
		tf.setStyleName("search small");
		grid.addComponent(tf);

		tf = new TextField();
		tf.setInputPrompt("Big search");
		tf.setStyleName("search big");
		grid.addComponent(tf);

		tf = new TextField("Error");
		tf.setComponentError(new UserError("Test error"));
		grid.addComponent(tf);

		tf = new TextField();
		tf.setInputPrompt("Error");
		tf.setComponentError(new UserError("Test error"));
		grid.addComponent(tf);

		tf = new TextField();
		tf.setInputPrompt("Small error");
		tf.setStyleName("small");
		tf.setComponentError(new UserError("Test error"));
		grid.addComponent(tf);

		TextArea ta = new TextArea();
		ta.setInputPrompt("Multiline");
		ta.setRows(4);
		grid.addComponent(ta);

		ta = new TextArea();
		ta.setInputPrompt("Small multiline");
		ta.setStyleName("small");
		ta.setRows(4);
		grid.addComponent(ta);

		ta = new TextArea();
		ta.setInputPrompt("Big multiline");
		ta.setStyleName("big");
		ta.setRows(4);
		grid.addComponent(ta);

		return grid;
	}

	private Layout getPanelPreviews()
	{
		Layout grid = getPreviewLayout("Panels");

		Panel panel = new DemoPanel("Panel");
		panel.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(panel);

		panel = new DemoPanel();
		grid.addComponent(panel);

		panel = new DemoPanel("Borderless Panel");
		panel.setStyleName("borderless");
		grid.addComponent(panel);

		panel = new DemoPanel();
		panel.setStyleName("borderless");
		grid.addComponent(panel);

		panel = new DemoPanel("Bubble panel");
		panel.setStyleName("bubble");
		panel.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(panel);

		panel = new DemoPanel();
		panel.setStyleName("bubble");
		grid.addComponent(panel);

		panel = new DemoPanel("Borderless Bubble");
		panel.setStyleName("borderless bubble");
		grid.addComponent(panel);

		panel = new DemoPanel();
		panel.setStyleName("borderless bubble");
		grid.addComponent(panel);

		panel = new DemoPanel("Light panel");
		panel.setStyleName(ChameleonTheme.PANEL_LIGHT);
		panel.setIcon(new ThemeResource("../runo/icons/16/document.png"));
		grid.addComponent(panel);

		panel = new DemoPanel();
		panel.setStyleName(ChameleonTheme.PANEL_LIGHT);
		grid.addComponent(panel);

		panel = new DemoPanel("Borderless Light");
		panel.setStyleName("borderless " + ChameleonTheme.PANEL_LIGHT);
		grid.addComponent(panel);

		panel = new DemoPanel();
		panel.setStyleName("borderless " + ChameleonTheme.PANEL_LIGHT);
		grid.addComponent(panel);

		return grid;
	}

	private Layout getSplitPreviews()
	{
		Layout grid = getPreviewLayout("Split panels");

		AbstractSplitPanel panel = new VerticalSplitPanel();
		panel.setWidth("230px");
		panel.setHeight("130px");
		grid.addComponent(panel);

		panel = new VerticalSplitPanel();
		panel.setWidth("230px");
		panel.setHeight("130px");
		panel.setStyleName("small");
		grid.addComponent(panel);

		panel = new HorizontalSplitPanel();
		panel.setWidth("230px");
		panel.setHeight("130px");
		grid.addComponent(panel);

		panel = new HorizontalSplitPanel();
		panel.setWidth("230px");
		panel.setHeight("130px");
		panel.setStyleName("small");
		grid.addComponent(panel);

		return grid;
	}

	private Layout getDateFieldPreviews()
	{
		Layout grid = getPreviewLayout("Date fields");

		Date dateValue = null;
		try
		{
			dateValue = new SimpleDateFormat("yyyyMMdd", Locale.US).parse("20110101");
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		DateField date = new DateField();
		date.setValue(dateValue);
		date.setResolution(DateField.RESOLUTION_MIN);
		grid.addComponent(date);

		date = new DateField("Small date");
		date.setValue(dateValue);
		date.setResolution(DateField.RESOLUTION_YEAR);
		date.setStyleName("small");
		grid.addComponent(date);

		date = new DateField("Big date");
		date.setValue(dateValue);
		date.setResolution(DateField.RESOLUTION_MONTH);
		date.setStyleName("big");
		grid.addComponent(date);

		date = new InlineDateField("Inline date");
		date.setValue(dateValue);
		date.setResolution(DateField.RESOLUTION_DAY);
		grid.addComponent(date);

		date = new InlineDateField("Inline date, year resolution");
		date.setValue(dateValue);
		date.setResolution(DateField.RESOLUTION_MONTH);
		grid.addComponent(date);

		return grid;
	}

	Layout getTabsheetPreviews()
	{
		Layout grid = getPreviewLayout("Tab sheets");

		TabSheet tabs = new DemoTabsheet(false);
		grid.addComponent(tabs);

		tabs = new DemoTabsheet(true);
		grid.addComponent(tabs);

		tabs = new DemoTabsheet(false);
		tabs.setStyleName("borderless");
		grid.addComponent(tabs);

		tabs = new DemoTabsheet(true);
		tabs.setStyleName("borderless open-only-closable");
		grid.addComponent(tabs);

		return grid;
	}

	Layout getAccordionPreviews()
	{
		Layout grid = getPreviewLayout("Accordions");

		Accordion tabs = new DemoAccordion(false);
		grid.addComponent(tabs);

		// tabs = new DemoAccordion(true);
		// grid.addComponent(tabs);

		tabs = new DemoAccordion(false);
		tabs.setStyleName("borderless");
		grid.addComponent(tabs);

		tabs = new DemoAccordion(true);
		tabs.setStyleName("opaque");
		grid.addComponent(tabs);

		tabs = new DemoAccordion(true);
		tabs.setStyleName("opaque borderless");
		grid.addComponent(tabs);

		return grid;
	}

	Layout getSliderPreviews() throws ValueOutOfBoundsException
	{
		Layout grid = getPreviewLayout("Sliders");

		Slider s = new Slider();
		s.setWidth("200px");
		s.setValue(new Double(50));
		grid.addComponent(s);

		s = new Slider();
		// TODO s.setOrientation(Slider.ORIENTATION_VERTICAL);
		s.setHeight("70px");
		s.setValue(new Double(50));
		grid.addComponent(s);

		return grid;
	}

	Layout getTablePreviews()
	{
		Layout grid = getPreviewLayout("Tables");

		Table t = getDemoTable(null);
		grid.addComponent(t);

		t = getDemoTable("small");
		grid.addComponent(t);

		t = getDemoTable("big");
		grid.addComponent(t);

		t = getDemoTable("striped");
		grid.addComponent(t);

		t = getDemoTable("small striped");
		grid.addComponent(t);

		t = getDemoTable("big striped");
		grid.addComponent(t);

		t = getDemoTable("strong");
		grid.addComponent(t);

		t = getDemoTable("small strong");
		grid.addComponent(t);

		t = getDemoTable("big strong");
		grid.addComponent(t);

		t = getDemoTable("borderless");
		grid.addComponent(t);

		t = getDemoTable("striped");
		t.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		t.setCaption(t.getCaption() + ", hidden headers");
		grid.addComponent(t);

		return grid;
	}

	Layout getSelectPreviews()
	{
		Layout grid = getPreviewLayout("Selects");

		ComboBox combo = new ComboBox();
		addSelectItems(combo, true, 100);
		grid.addComponent(combo);

		combo = new ComboBox();
		addSelectItems(combo, true, 100);
		combo.setStyleName("small");
		grid.addComponent(combo);

		combo = new ComboBox();
		addSelectItems(combo, true, 100);
		combo.setStyleName("big");
		grid.addComponent(combo);

		combo = new ComboBox();
		addSelectItems(combo, false, 5);
		combo.setStyleName("search");
		combo.setInputPrompt("Search combo");
		grid.addComponent(combo);

		combo = new ComboBox();
		addSelectItems(combo, false, 5);
		combo.setStyleName("small search");
		combo.setInputPrompt("Small search combo");
		grid.addComponent(combo);

		combo = new ComboBox();
		addSelectItems(combo, false, 5);
		combo.setStyleName("big search");
		combo.setInputPrompt("Big search combo");
		grid.addComponent(combo);

		NativeSelect s = new NativeSelect();
		addSelectItems(s, true, 10);
		grid.addComponent(s);

		s = new NativeSelect();
		addSelectItems(s, true, 10);
		s.setStyleName("small");
		grid.addComponent(s);

		s = new NativeSelect();
		addSelectItems(s, true, 10);
		s.setStyleName("big");
		grid.addComponent(s);

		combo = new ComboBox();
		addSelectItems(combo, false, 5);
		combo.setInputPrompt("Just click me");
		combo.setStyleName("select-button");
		// Must always specify width
		combo.setWidth("150px");
		grid.addComponent(combo);
		combo.setDescription(combo.getDescription()
				+ "<br><strong>You must always specify an explicit width for a combobox with this style, otherwise it will not work</strong>");

		return grid;
	}

	static void addSelectItems(AbstractSelect s, boolean selectFirst, int num)
	{
		s.setNullSelectionAllowed(false);
		for (int i = 0; i < num; i++)
		{
			s.addItem("Item " + i);
		}
		if (selectFirst)
		{
			s.select(s.getItemIds().iterator().next());
		}
	}

	Layout getProgressIndicatorPreviews()
	{
		Layout grid = getPreviewLayout("Progress Indicators");

		ProgressIndicator pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("Normal");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("ProgressIndicator.setStyleName(\"small\")");
		pi.setStyleName("small");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("ProgressIndicator.setStyleName(\"big\")");
		pi.setStyleName("big");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setIndeterminate(true);
		pi.setCaption("Indeterminate, style \"bar\"");
		pi.setStyleName("bar");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setIndeterminate(true);
		pi.setCaption("Indeterminate, style \"small bar\"");
		pi.setStyleName("small bar");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setIndeterminate(true);
		pi.setCaption("Indeterminate, style \"big bar\"");
		pi.setStyleName("big bar");
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("Indeterminate, default style");
		pi.setIndeterminate(true);
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("Indeterminate, style \"big\"");
		pi.setStyleName("big");
		pi.setIndeterminate(true);
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("Disabled");
		pi.setEnabled(false);
		grid.addComponent(pi);

		pi = new ProgressIndicator(0.5f);
		pi.setPollingInterval(100000000);
		pi.setCaption("Indeterminate bar disabled");
		pi.setIndeterminate(true);
		pi.setStyleName("bar");
		pi.setEnabled(false);
		grid.addComponent(pi);

		return grid;
	}

	Tree tree;

	Layout getTreePreviews()
	{
		Layout grid = getPreviewLayout("Trees");
		tree = new Tree();
		tree.setImmediate(true);
		// we'll use a property for caption instead of the item id ("value"),
		// so that multiple items can have the same caption
		tree.addContainerProperty("caption", String.class, "");
		tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		tree.setItemCaptionPropertyId("caption");
		for (int i = 1; i <= 3; i++)
		{
			final Object id = addCaptionedItem("Division " + i, null);
			tree.expandItem(id);
			addCaptionedItem("Team A", id);
			addCaptionedItem("Team B", id);
			tree.setItemIcon(id, new ThemeResource("../runo/icons/16/folder.png"));
		}
		grid.addComponent(tree);
		return grid;
	}

	Layout getPopupViewPreviews()
	{
		Layout grid = getPreviewLayout("Popup views");

		Label content = new Label("Simple popup content");
		content.setSizeUndefined();
		PopupView pv = new PopupView("Default popup", content);
		grid.addComponent(pv);

		return grid;
	}

	Layout getMenuBarPreviews()
	{
		Layout grid = getPreviewLayout("Menu bars");

		MenuBar menubar = new MenuBar();
		final MenuBar.MenuItem file = menubar.addItem("File", null);
		final MenuBar.MenuItem newItem = file.addItem("New", null);
		file.addItem("Open file...", null);
		file.addSeparator();

		newItem.addItem("File", null);
		newItem.addItem("Folder", null);
		newItem.addItem("Project...", null);

		file.addItem("Close", null);
		file.addItem("Close All", null);
		file.addSeparator();

		file.addItem("Save", null);
		file.addItem("Save As...", null);
		file.addItem("Save All", null);

		final MenuBar.MenuItem edit = menubar.addItem("Edit", null);
		edit.addItem("Undo", null);
		edit.addItem("Redo", null).setEnabled(false);
		edit.addSeparator();

		edit.addItem("Cut", null);
		edit.addItem("Copy", null);
		edit.addItem("Paste", null);
		edit.addSeparator();

		final MenuBar.MenuItem find = edit.addItem("Find/Replace", null);

		// Actions can be added inline as well, of course
		find.addItem("Google Search", null);
		find.addSeparator();
		find.addItem("Find/Replace...", null);
		find.addItem("Find Next", null);
		find.addItem("Find Previous", null);

		final MenuBar.MenuItem view = menubar.addItem("View", null);
		view.addItem("Show/Hide Status Bar", null);
		view.addItem("Customize Toolbar...", null);
		view.addSeparator();

		view.addItem("Actual Size", null);
		view.addItem("Zoom In", null);
		view.addItem("Zoom Out", null);

		grid.addComponent(menubar);

		return grid;
	}

	Layout getWindowPreviews()
	{
		Layout grid = getPreviewLayout("Windows");

		Button win = new Button("Open normal sub-window", new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				ui.addWindow(new Window("Normal window"));
			}
		});
		grid.addComponent(win);
		win.setDescription("new Window()");

		win = new Button("Open opaque sub-window", new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				Window w = new Window("Window.addStyleName(\"opaque\")");
				w.addStyleName("opaque");
				ui.addWindow(w);
			}
		});
		grid.addComponent(win);
		win.setDescription("Window.addStyleName(\"opaque\")");

		return grid;
	}

	private Object addCaptionedItem(String caption, Object parent)
	{
		// add item, let tree decide id
		final Object id = tree.addItem();
		// get the created item
		final Item item = tree.getItem(id);
		// set our "caption" property
		final Property p = item.getItemProperty("caption");
		p.setValue(caption);
		if (parent != null)
		{
			tree.setChildrenAllowed(parent, true);
			tree.setParent(id, parent);
			tree.setChildrenAllowed(id, false);
		}
		return id;
	}

	class DemoPanel extends Panel
	{
		DemoPanel()
		{
			super();
			setWidth("230px");
			setHeight("120px");
			setContent(new Label(
					"<h4>Panel content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum. Quisque elementum quam sed sem ultrices lobortis. Pellentesque non ligula ac dolor posuere tincidunt sed eu mi. Integer mattis fringilla nulla, ut cursus mauris scelerisque eu. Etiam bibendum placerat euismod. Nam egestas adipiscing orci sed tristique. Sed vitae enim nisi. Sed ac vehicula ipsum. Nulla quis quam nisi. Proin interdum lacus ipsum, at tristique nibh. Curabitur at ipsum sem. Donec venenatis aliquet neque, sit amet cursus lectus condimentum et. In mattis egestas erat, non cursus metus consectetur ac. Pellentesque eget nisl tellus.",
					Label.CONTENT_XHTML));
		}

		DemoPanel(String caption)
		{
			this();
			setCaption(caption);
		}

		@Override
		public void setStyleName(String style)
		{
			super.setStyleName(style);
			
			Label l = new Label(
					"<h4>"
							+ style
							+ " panel content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum. Quisque elementum quam sed sem ultrices lobortis. Pellentesque non ligula ac dolor posuere tincidunt sed eu mi. Integer mattis fringilla nulla, ut cursus mauris scelerisque eu. Etiam bibendum placerat euismod. Nam egestas adipiscing orci sed tristique. Sed vitae enim nisi. Sed ac vehicula ipsum. Nulla quis quam nisi. Proin interdum lacus ipsum, at tristique nibh. Curabitur at ipsum sem. Donec venenatis aliquet neque, sit amet cursus lectus condimentum et. In mattis egestas erat, non cursus metus consectetur ac. Pellentesque eget nisl tellus.",
					Label.CONTENT_XHTML);
			l.setDescription("Panel.setStyleName(\"" + style + "\")");
			setContent(l);
		}

	}

	class DemoTabsheet extends TabSheet
	{
		DemoTabsheet(boolean closable)
		{
			super();
			setWidth("230px");
			setHeight("140px");
			for (int i = 1; i < 4; i++)
			{
				VerticalLayout l = new VerticalLayout();
				l.setMargin(true);
				Tab t = addTab(l);
				t.setCaption("Tab " + i);
				t.setClosable(closable);
				if (i == 1)
				{
					l.addComponent(new Label(
							"<h4>Tab sheet content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum. Quisque elementum quam sed sem ultrices lobortis. Pellentesque non ligula ac dolor posuere tincidunt sed eu mi. Integer mattis fringilla nulla, ut cursus mauris scelerisque eu. Etiam bibendum placerat euismod. Nam egestas adipiscing orci sed tristique. Sed vitae enim nisi. Sed ac vehicula ipsum. Nulla quis quam nisi. Proin interdum lacus ipsum, at tristique nibh. Curabitur at ipsum sem. Donec venenatis aliquet neque, sit amet cursus lectus condimentum et. In mattis egestas erat, non cursus metus consectetur ac. Pellentesque eget nisl tellus.",
							Label.CONTENT_XHTML));
				}
				if (i == 3)
				{
					t.setIcon(new ThemeResource("../runo/icons/16/document.png"));
				}
			}
		}

		@Override
		public void setStyleName(String style)
		{
			super.setStyleName(style);
			Label l = new Label("<h4>" + style
					+ " tab sheet content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum.",
					Label.CONTENT_XHTML);
			l.setDescription("TabSheet.setStyleName(\"" + style + "\")");
			((VerticalLayout) getSelectedTab()).removeAllComponents();
			((VerticalLayout) getSelectedTab()).addComponent(l);
		}
	}

	class DemoAccordion extends Accordion
	{
		DemoAccordion(boolean closable)
		{
			super();
			setWidth("70%");
			setHeight("160px");
			for (int i = 1; i < 5; i++)
			{
				VerticalLayout l = new VerticalLayout();
				l.setMargin(true);
				Tab t = addTab(l);
				t.setCaption("Sheet " + i);
				t.setClosable(closable);
				if (i == 1)
				{
					l.addComponent(new Label(
							"<h4>Accordion content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum. Quisque elementum quam sed sem ultrices lobortis. Pellentesque non ligula ac dolor posuere tincidunt sed eu mi. Integer mattis fringilla nulla, ut cursus mauris scelerisque eu. Etiam bibendum placerat euismod. Nam egestas adipiscing orci sed tristique. Sed vitae enim nisi. Sed ac vehicula ipsum. Nulla quis quam nisi. Proin interdum lacus ipsum, at tristique nibh. Curabitur at ipsum sem. Donec venenatis aliquet neque, sit amet cursus lectus condimentum et. In mattis egestas erat, non cursus metus consectetur ac. Pellentesque eget nisl tellus.",
							Label.CONTENT_XHTML));
				}
				if (i == 3)
				{
					t.setIcon(new ThemeResource("../runo/icons/16/document.png"));
				}
			}
		}

		@Override
		public void setStyleName(String style)
		{
			super.setStyleName(style);
			Label l = new Label("<h4>" + style
					+ " accordion content</h4>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin malesuada volutpat vestibulum.",
					Label.CONTENT_XHTML);
			l.setDescription("Accordion.setStyleName(\"" + style + "\")");
			((VerticalLayout) getSelectedTab()).removeAllComponents();
			((VerticalLayout) getSelectedTab()).addComponent(l);
		}
	}

	public Table getDemoTable(String style)
	{
		Table t = new Table();
		t.setWidth("250px");
		t.setPageLength(5);
		t.setSelectable(true);
		t.setColumnCollapsingAllowed(true);
		t.setColumnReorderingAllowed(true);

		if (style != null)
		{
			t.setStyleName(style);
			t.setCaption("Table.addStyleName(\"" + style + "\")");
		}

		t.addContainerProperty("First", String.class, null);
		t.addContainerProperty("Second", String.class, null);
		t.addContainerProperty("Third", String.class, null);

		for (int j = 1; j < 100; j++)
		{
			t.addItem(new Object[] { "Foo " + j, "Bar " + j, "Lorem " + j }, j);
		}

		t.setColumnIcon("Third", new ThemeResource("../runo/icons/16/document.png"));
		t.select(1);

		return t;
	}

	Layout getCompoundButtons()
	{
		Layout grid = getPreviewLayout("Compound Buttons");

		Label title = new Label("Segment");
		title.setStyleName("h1");
		grid.addComponent(title);
		((GridLayout) grid).newLine();

		Label segments = new Label(
				"The segment control is just a set of buttons inside a HorizontalLayout. Use the structure shown on the right, <strong>and remember that you need to implement all logic yourself</strong>. This theme just provides suitable stylenames for you to use.",
				Label.CONTENT_XHTML);
		grid.addComponent(segments);
		segments = new Label(
				"HorizontalLayout.setStyleName(\"segment\") and .addStyleName(\"segment-alternate\")\n  +  Button.addStyleName(\"first\") and .addStyleName(\"down\")\n  +  Button\n\t...\n  +  Button.addStyleName(\"last\")",
				Label.CONTENT_PREFORMATTED);
		((GridLayout) grid).addComponent(segments, 1, 1, 2, 1);

		Segment segment = new Segment();
		segment.setCaption("Segment");
		Button b = new Button("One");
		b.setStyleName("down");
		b.setIcon(new ThemeResource("../runo/icons/16/document-txt.png"));
		segment.addButton(b).addButton(new Button("Two")).addButton(new Button("Three")).addButton(new Button("Four"));
		grid.addComponent(segment);

		segment = new Segment();
		segment.addStyleName("segment-alternate");
		segment.setCaption("Segment (alternate)");
		b = new Button("One");
		b.setStyleName("down");
		b.setIcon(new ThemeResource("../runo/icons/16/document-txt.png"));
		segment.addButton(b).addButton(new Button("Two")).addButton(new Button("Three")).addButton(new Button("Four"));
		grid.addComponent(segment);

		segment = new Segment();
		segment.setCaption("Small segment");
		b = new Button("Apples");
		b.setStyleName("small");
		b.addStyleName("down");
		segment.addButton(b);
		b = new Button("Oranges");
		b.setStyleName("small");
		segment.addButton(b);
		b = new Button("Bananas");
		b.setStyleName("small");
		segment.addButton(b);
		b = new Button("Grapes");
		b.setStyleName("small");
		segment.addButton(b);
		grid.addComponent(segment);

		return grid;
	}

	Layout getCompoundMenus()
	{
		Layout grid = getPreviewLayout("Compound Menus");

		Label title = new Label("Sidebar Menu");
		title.setStyleName("h1");
		grid.addComponent(title);
		((GridLayout) grid).newLine();

		Label menus = new Label(
				"<strong>The sidebar menu</strong> control is just a set of labels and buttons inside a CssLayout or a VerticalLayout. Use the structure shown on the right, <strong>and remember that you need to implement all logic yourself</strong>. This theme just provides suitable stylenames for you to use.<br><br>You can also use the <a href=\"http://vaadin.com/forum/-/message_boards/message/119172\">DetachedTabs add-on</a> inside the sidebar-menu, it will style automatically.<br><br><strong>Note: only NativeButtons are styled inside the menu, normal buttons are left untouched.</strong>",
				Label.CONTENT_XHTML);
		grid.addComponent(menus);
		menus = new Label(
				"CssLayout.setStyleName(\"sidebar-menu\")\n  +  Label\n  +  NativeButton\n  +  NativeButton\n\t...\n  +  Label\n  +  DetachedTabs\n\t...",
				Label.CONTENT_PREFORMATTED);
		grid.addComponent(menus);

		SidebarMenu sidebar = new SidebarMenu();
		sidebar.setWidth("200px");
		sidebar.addComponent(new Label("Fruits"));
		NativeButton b = new NativeButton("Apples");
		b.setIcon(new ThemeResource("../runo/icons/16/note.png"));
		sidebar.addButton(b);
		sidebar.setSelected(b);
		sidebar.addButton(new NativeButton("Oranges"));
		sidebar.addButton(new NativeButton("Bananas"));
		sidebar.addButton(new NativeButton("Grapes"));
		sidebar.addComponent(new Label("Vegetables"));
		sidebar.addButton(new NativeButton("Tomatoes"));
		sidebar.addButton(new NativeButton("Cabbages"));
		sidebar.addButton(new NativeButton("Potatoes"));
		sidebar.addButton(new NativeButton("Carrots"));
		grid.addComponent(sidebar);
		((GridLayout) grid).setColumnExpandRatio(0, 1);
		((GridLayout) grid).setColumnExpandRatio(1, 1);

		title = new Label("Toolbar");
		title.setStyleName("h1");
		grid.addComponent(title);
		((GridLayout) grid).newLine();

		CssLayout toolbars = new CssLayout();

		menus = new Label(
				"<strong>Toolbar</strong> is a simple CssLayout with a stylename. It provides the background and a little padding for its contents. Normally you will want to put buttons inside it, but segment controls fit in nicely as well.",
				Label.CONTENT_XHTML);
		grid.addComponent(menus);
		menus = new Label("CssLayout.setStyleName(\"toolbar\")", Label.CONTENT_PREFORMATTED);
		grid.addComponent(menus);

		CssLayout toolbar = new CssLayout();
		toolbar.setStyleName("toolbar");
		toolbar.setWidth("300px");

		Button b2 = new Button("Action");
		b2.setStyleName("small");
		toolbar.addComponent(b2);

		Segment segment = new Segment();
		segment.addStyleName("segment-alternate");
		b2 = new Button("Apples");
		b2.setStyleName("small");
		b2.addStyleName("down");
		segment.addButton(b2);
		b2 = new Button("Oranges");
		b2.setStyleName("small");
		segment.addButton(b2);
		toolbar.addComponent(segment);

		b2 = new Button("Notes");
		b2.setStyleName("small borderless");
		b2.setIcon(new ThemeResource("../runo/icons/16/note.png"));
		toolbar.addComponent(b2);
		toolbars.addComponent(toolbar);

		toolbar = new CssLayout();
		toolbar.setStyleName("toolbar");
		toolbar.setWidth("300px");

		b2 = new Button("Action");
		b2.setIcon(new ThemeResource("../runo/icons/32/document.png"));
		b2.setStyleName("borderless");
		toolbar.addComponent(b2);

		b2 = new Button("Action 2");
		b2.setStyleName("borderless");
		b2.setIcon(new ThemeResource("../runo/icons/32/user.png"));
		toolbar.addComponent(b2);

		b2 = new Button("Action 3");
		b2.setStyleName("borderless");
		b2.setIcon(new ThemeResource("../runo/icons/32/note.png"));
		toolbar.addComponent(b2);
		toolbars.addComponent(toolbar);

		grid.addComponent(toolbars);

		return grid;
	}

	GridLayout getPreviewLayout(String caption)
	{
		GridLayout grid = new GridLayout(3, 1)
		{
			@Override
			public void addComponent(Component c)
			{
				super.addComponent(c);
				setComponentAlignment(c, Alignment.MIDDLE_CENTER);
				if (c.getStyleName() != "")
				{
					((AbstractComponent) c).setDescription(c.getClass().getSimpleName() + ".addStyleName(\"" + c.getStyleName() + "\")");
				}
				else
				{
					((AbstractComponent) c).setDescription("new " + c.getClass().getSimpleName() + "()");
				}
			}
		};
		grid.setWidth("100%");
		grid.setSpacing(true);
		grid.setMargin(true);
		grid.setCaption(caption);
		grid.setStyleName("preview-grid");
		return grid;
	}



}
