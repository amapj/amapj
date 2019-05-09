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
 package fr.amapj.view.engine.popup.corepopup;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;

/**
 * Popup primitive , etant la base de tous les autres popup
 * Contient les outils pour créer une barre de bouton facilement
 *  
 */
@SuppressWarnings("serial")
abstract public class CorePopup 
{
	
	public enum PopupType
	{
		CENTERFIT ,
		FILL
	}
	
	public enum ColorStyle
	{
		GREEN , 
		RED 
	}
	
	
	//
	private Window window = new Window();
	
	//
	private PopupType popupType = PopupType.FILL;
	
	//
	protected CloseListener popupCloseListener;
	
	//
	protected String popupTitle="";
	
	//
	private HorizontalLayout popupButtonBarLayout;
	
	
	// Dans le cas FILL uniquement 
	private Integer popupWidth = 40;  // En pourcentage 
	private Integer minWidth = 400;   // En pixel 
	private Integer maxWidth = null;
	
	
	private String popupHeight= null; // En pourcentage
	private Integer minHeight = 400;   // En pixel 
	private Integer maxHeight = null;
	
	//
	private ColorStyle colorStyle = ColorStyle.GREEN;

	
	
	
	public void createPopup()
	{
		// Creation du contenu
		VerticalLayout contentLayout = new VerticalLayout();
		
		createContent(contentLayout);
		
		// Construction de la barre de boutons
		popupButtonBarLayout = new HorizontalLayout();
		popupButtonBarLayout.setMargin(false);
		popupButtonBarLayout.addStyleName("buttonbar");
		createButtonBar();
		popupButtonBarLayout.setSpacing(true);
		
		// Creation de la fenetre
		window.setClosable(true);
		window.setResizable(true);
		window.setModal(true);
		
		// Agencement suivant le type
		if (popupType==PopupType.CENTERFIT)
		{
			//
			contentLayout.setWidthUndefined();
			
			// 
			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.addStyleName("corepopup");
			mainLayout.setResponsive(true);
			mainLayout.addStyleName("centerfit-corepopup");
			mainLayout.addStyleName("centerfit-corepopup-"+getWidthRange());
			mainLayout.setWidth(null);
			
			//
			mainLayout.addComponent(contentLayout);
			
			//
			popupButtonBarLayout.setWidth(null);
			mainLayout.addComponent(popupButtonBarLayout);
			mainLayout.setComponentAlignment(popupButtonBarLayout, Alignment.MIDDLE_RIGHT);
			
			//
			window.setContent(mainLayout);
			window.setSizeUndefined();		
		}
		else
		{
			//
			contentLayout.setWidth("100%");
			
			
			// 
			VerticalLayout mainLayout = new VerticalLayout();
			mainLayout.addStyleName("corepopup");
			mainLayout.setResponsive(true);
			mainLayout.addStyleName("fill-corepopup");
			mainLayout.addStyleName("fill-corepopup-"+getWidthRange());
			mainLayout.setWidth("100%");
						
			//
			mainLayout.addComponent(contentLayout);
			
			
			
			//
			popupButtonBarLayout.setWidth(null);
			mainLayout.addComponent(popupButtonBarLayout);
			mainLayout.setComponentAlignment(popupButtonBarLayout, Alignment.MIDDLE_RIGHT);
			
			//
			window.setContent(mainLayout);
			computeWindowSize();
			
			//
			window.setHeight(popupHeight);
			
			
		}
		
	
		
	
		// Partie commune dans tous les cas
		window.center();
		window.setCaption(popupTitle);
		window.setStyleName("opaque");
		
		if (colorStyle==ColorStyle.RED)
		{
			window.setStyleName("corepopup-red");
		}
		
	}
	
	
	/**
	 * Il y a un bug qui empeche de faire ca plus proprement
	 * voir https://vaadin.com/forum#!/thread/7622424 
	 * 
	 * Pour les fenetres modales, on ne peut pas connaitre la taille de la fenetre entière du navigateur
	 * donc on est obligé de le faire nous même  
	 * 
	 * @return
	 */
	private String getWidthRange()
	{
		int browserWidth = 	UI.getCurrent().getPage().getBrowserWindowWidth();
		
		
		if (browserWidth<501)
		{
			return "size0_500px";
		}
		else if (browserWidth<801)
		{
			return "size501_800px";
		}
		else
		{
			return "size801px_";
		}
	}
	
	abstract protected void createContent(VerticalLayout contentLayout);

	abstract protected void createButtonBar();
	
	
	protected Button addButton(String title,Button.ClickListener listener)
	{
		Button saveButton = new Button(title, listener);
		popupButtonBarLayout.addComponent(saveButton);
		return saveButton;
	}
	
	protected Button addDefaultButton(String title,Button.ClickListener listener)
	{
		Button saveButton = addButton(title, listener);
		
		if (colorStyle==ColorStyle.RED)
		{
			saveButton.addStyleName("danger");
		}
		else
		{
			saveButton.addStyleName("primary");	
		}
		
		
		return saveButton;
	}
	
	protected void setButtonAlignement(Button b, Alignment a)
	{
		popupButtonBarLayout.setComponentAlignment(b,a);
	}

	
	protected void setType(PopupType popupType)
	{
		this.popupType = popupType;
	}
	
	
	protected void setWidth(int width)
	{
		popupWidth = width;
	}
	
	protected void setColorStyle(ColorStyle colorStyle)
	{
		this.colorStyle = colorStyle;
	}
	
	
	
	/**
	 * Normalement, il n'est pas utile d'appeler cette fonction
	 * 
	 * La hauteur se calcule automatiquement correctement
	 * 
	 * Si il y a vraimment un besoin, alors il faut faire setHeight("60%") par exemple 
	 * 
	 * @param height
	 */
	protected void setHeight(String height)
	{
		popupHeight = height;
	}
	
	
	/**
	 * Permet de positionner la taille de la fenetre en pourcentage, 
	 * en indiquant également une taille minimum en pixel
	 */
	protected void setWidth(int width,int minSize)
	{
		popupWidth = width;
		minWidth = minSize;
	}
	
	private void computeWindowSize()
	{
		String w;
		
		int browserWidth = 	UI.getCurrent().getPage().getBrowserWindowWidth();
		int computedWith = (int) (minWidth*100/browserWidth);
		
		if (computedWith>=100)
		{
			w="100%";
		}
		else if (computedWith>=popupWidth)
		{
			w = computedWith+"%";
		}
		else
		{
			w = popupWidth+"%";
		}
		
		window.setWidth(w);
	}
	
	protected void close()
	{
		window.close();
	}
	
	
	public void changeCloseListener(CloseListener cl)
	{
		window.removeCloseListener(popupCloseListener);
		window.addCloseListener(cl);
	}
	
	
	public Window getWindow()
	{
		return window;
	}
	
	
	

	static public void open(CorePopup popup)
	{
		open(popup,null);
	}
	
	static public void open(CorePopup popup,final PopupListener listener)
	{
		if (listener!=null)
		{
			popup.popupCloseListener = new CloseListener()
			{
				@Override
				public void windowClose(CloseEvent e)
				{
					try
					{
						listener.onPopupClose();
					}
					catch(Throwable t)
					{
						ErrorPopup.open(t);
					}
				}
			};
			popup.window.addCloseListener(popup.popupCloseListener);
		}
		popup.createPopup();
		UI.getCurrent().addWindow(popup.window);
	}
}
