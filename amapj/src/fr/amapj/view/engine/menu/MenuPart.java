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
 package fr.amapj.view.engine.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import fr.amapj.service.services.authentification.PasswordManager;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.session.SessionManager;
import fr.amapj.service.services.session.SessionParameters;
import fr.amapj.view.engine.ui.AmapUI;
import fr.amapj.view.engine.ui.ValoMenuLayout;


/**
 * Classe de gestion du menu
 */

public class MenuPart
{
	private static final Logger logger = LogManager.getLogger();
	
	
	
	// Correspondance entre les boutons et les vues
	private Map<String, Button> viewNameToMenuButton;
	
	
	public MenuPart()
	{
	}
	
	public void buildMainView(final AmapUI ui,ValoMenuLayout root)
	{
		root.prepareForMainPage();
		
		viewNameToMenuButton = new HashMap<String, Button>();
		
		CssLayout menu = new CssLayout();
		CssLayout menuItemsLayout = new CssLayout(); 
		
		if (ui.getPage().getWebBrowser().isIE() && ui.getPage().getWebBrowser().getBrowserMajorVersion() == 9)
		{
			menu.setWidth("320px");
		}
		
		// Chargement de tous les menus accesibles par l'utilisateur
		// et création du "navigator"
		List<MenuDescription> allMenus = MenuInfo.getInstance().getMenu();
		
		Navigator nav = new Navigator(ui, root.getContentContainer());
		nav.addViewChangeListener(new ViewChangeListener()
		{
			
			@Override
			public boolean beforeViewChange(ViewChangeEvent event)
			{
				logger.info("Entrée dans l'écran {}",event.getViewName());
				return true;
			}
			
			@Override
			public void afterViewChange(ViewChangeEvent event)
			{
				menu.removeStyleName("valo-menu-visible");
			}
		});

	
		
		if (allMenus.size()>0)
		{
			MenuDescription first = allMenus.get(0);
			nav.setErrorView(first.getViewClass());
		}
		
		for (MenuDescription mD : allMenus)
		{
			nav.addView("/"+mD.getMenuName().name().toLowerCase(), mD.getViewClass());
		}

		// Création du menu 
		root.addMenu(buildMenu(menu,menuItemsLayout,allMenus,nav,ui));
		
	}


	
	private CssLayout buildMenu(CssLayout menu,CssLayout menuItemsLayout,List<MenuDescription> allMenus, Navigator navigator, AmapUI ui)
	{
		

		final HorizontalLayout top = new HorizontalLayout();
		top.setWidth("100%");
		top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		top.addStyleName("valo-menu-title");
		menu.addComponent(top);

		final Button showMenu = new Button("Menu", new ClickListener()
		{
			@Override
			public void buttonClick(final ClickEvent event)
			{
				if (menu.getStyleName().contains("valo-menu-visible"))
				{
					menu.removeStyleName("valo-menu-visible");
				} 
				else
				{
					menu.addStyleName("valo-menu-visible");
				}
			}
		});
		showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		showMenu.addStyleName("valo-menu-toggle");
		showMenu.setIcon(FontAwesome.LIST);
		menu.addComponent(showMenu);
		
		String nomAmap = new ParametresService().getParametres().nomAmap;
		Label title = new Label("<h2>"+nomAmap+"</h2>", ContentMode.HTML);
		title.setSizeUndefined();
		top.addComponent(title);
		top.setExpandRatio(title, 1);

		final MenuBar settings = new MenuBar();
		settings.addStyleName("user-menu");
		

		SessionParameters p = SessionManager.getSessionParameters();
		MenuItem settingsItem = settings.addItem(p.userPrenom+" "+p.userNom, null, null);
		settingsItem.addItem("Se déconnecter", new  MenuBar.Command()
		{
			@Override
			public void menuSelected(MenuItem selectedItem)
			{
				new PasswordManager().disconnect();
				ui.buildLoginView(null,null,null);	
			}
		});
		
		menu.addComponent(settings);
		

		menuItemsLayout.setPrimaryStyleName("valo-menuitems");
		menu.addComponent(menuItemsLayout);
		
		boolean first = true;
		String firstEntry=null;
		Button firstButton=null;

		
		for (MenuDescription menuDescription : allMenus)
		{
			final String view = menuDescription.getMenuName().name().toLowerCase();
			final String titleView = menuDescription.getMenuName().getTitle();
			
			
			if (menuDescription.getCategorie()!=null)
			{
				Label l = new Label(menuDescription.getCategorie(), ContentMode.HTML);
				l.setPrimaryStyleName("valo-menu-subtitle");
				l.addStyleName("h4");
				l.setSizeUndefined();
				menuItemsLayout.addComponent(l);
			}
			
			final Button b = new Button(titleView, new ClickListener()
			{
				@Override
				public void buttonClick(final ClickEvent event)
				{
					setSelected(event.getButton(),menuItemsLayout);
					navigator.navigateTo("/" +view);
				}
			});
			
			b.setId("amapj.menu."+view);
			b.setHtmlContentAllowed(true);
			b.setPrimaryStyleName("valo-menu-item");
			b.setIcon(menuDescription.getMenuName().getFont());
			menuItemsLayout.addComponent(b);
			
			viewNameToMenuButton.put("/"+view, b);
			
			if (first)
			{
				first = false;
				firstButton = b;
				firstEntry = view;
			}
		}
		
		
		
		// Gestion de l'url
		String f = Page.getCurrent().getUriFragment();
		if (f != null && f.startsWith("!"))
		{
			f = f.substring(1);
		}
		if (f == null || f.equals("") || f.equals("/"))
		{
			navigateWithProtect(navigator,"/"+firstEntry);
			setSelected(firstButton,menuItemsLayout);
		} 
		else
		{
			navigateWithProtect(navigator,f);
			setSelected(viewNameToMenuButton.get(f),menuItemsLayout);
		}
		

		return menu;
	}
	
	
	/**
	 * 	Voir https://vaadin.com/forum/#!/thread/4971527
	 *       https://dev.vaadin.com/ticket/13476
	 *  Ce patch permet d'eviter la propagation d'une erreur lors du chargement de la première page
	 *  
	 *  Cette fonction doit être appelée uniquement lors du chargement de la première page 
	 *  
	 *  Ceci résoud le problème dans tous les cas, sauf si le login et le mot de passe sont 
	 *  saisis dans l'url (ce qui correspond à un plantage avant la fin de la création de l'UI)
	 */ 
	private void navigateWithProtect(Navigator navigator,String navigationState)
	{
		try
		{
			navigator.navigateTo(navigationState);
		}
		catch(Exception e)
		{
			logger.error("Erreur lors du chargement de la première page",e);
		}
	}
	
	
	
	

	/**
	 * 
	 * @param viewName du style /xxxx
	 */
	private void setSelected(Button b,CssLayout menuItemsLayout)
	{
		for (final Iterator<Component> it = menuItemsLayout.iterator(); it.hasNext();)
		{
			it.next().removeStyleName("selected");
		}
		
		
		// b peut etre null dans le cas du error view provider 
		if (b!=null)
		{
			b.addStyleName("selected");
		}
	}
}
