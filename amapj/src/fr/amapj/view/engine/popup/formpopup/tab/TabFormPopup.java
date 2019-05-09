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
 package fr.amapj.view.engine.popup.formpopup.tab;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;
import fr.amapj.view.engine.popup.formpopup.AbstractFormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;

/**
 * Popup sous forme de tab pour la gestion des saisie
 *  
 */
@SuppressWarnings("serial")
abstract public class TabFormPopup extends AbstractFormPopup implements SelectedTabChangeListener
{
	protected String saveButtonTitle = "Sauvegarder";
	protected String quitButtonTitle = "Quitter";
	
	protected Button saveButton;
	protected Button quitButton;
	
	private List<TabInfo> tabInfos = new ArrayList<TabInfo>();
	
	private TabSheet tabSheet;

	private boolean errorInInitialCondition = false;
	
	private VerticalLayout vLayout;
	
	private int selectedTab = -1;
	
	
	/**
	 * Should be overriden
	 * @return
	 */
	protected String checkInitialCondition()
	{
		return null;
	}
	
	
	/**
	 * Retourne null si tout est ok, sinon retourne une liste de messages d'erreur
	 * @return
	 */ 
	abstract protected void  performSauvegarder() throws OnSaveException;
	
	/**
	 * Retourne la liste des étapes
	 */
	abstract protected void addTabInfo();
	
	
	
	
	
	public void addTab(String tabName,TabDraw tabDraw)
	{
		tabInfos.add(new TabInfo(tabName, tabDraw));
	}
	
	
	public TabFormPopup()
	{
		// Par défaut, la taille est à 80%  pour tous les tab popup 
		setHeight("80%");
	}
	
	
	protected void createContent(VerticalLayout contentLayout)
	{
		//
		contentLayout.addStyleName("wizard-popup"); // TODO 
			
		// Vérification des conditions initiales
		String str = checkInitialCondition();
		if (str!=null)
		{
			errorInInitialCondition = true;
			Label label = new Label(str,ContentMode.HTML);
			label.setStyleName(ChameleonTheme.LABEL_BIG);
			contentLayout.addComponent(label);
			return;
		}
		
		
		// Mise en place des onglets
		addTabInfo();
	    tabSheet = new TabSheet();
	    for (int i = 0; i < tabInfos.size(); i++)
		{
			TabInfo tabInfo = tabInfos.get(i);
		
	    	tabInfo.verticalLayout = new VerticalLayout(); 
	    	tabInfo.verticalLayout.setData(i);
	    	tabSheet.addTab(tabInfo.verticalLayout, tabInfo.tabName);
		}
	    tabSheet.addSelectedTabChangeListener(this);
		contentLayout.addComponent(tabSheet);
		
		vLayout = new VerticalLayout();
		contentLayout.addComponent(vLayout);
		
		// Affichage du premier onglet 
		updateForm(tabInfos.get(0));
		selectedTab = 0;

	}
	
	
	@Override
	public void selectedTabChange(SelectedTabChangeEvent event)
	{
		int newTabIndex = getSelectedTabIndex();
		TabInfo tabInfo = tabInfos.get(newTabIndex);
		
		boolean ret = saveForm(tabInfo);
		if (ret)
		{
			updateForm(tabInfo);
			selectedTab = newTabIndex;
		}
		else
		{
			tabSheet.removeSelectedTabChangeListener(this);
			tabSheet.setSelectedTab(selectedTab);
			tabSheet.addSelectedTabChangeListener(this);
		}
	}
	
	private int getSelectedTabIndex()
	{
		return (Integer) ((VerticalLayout) tabSheet.getSelectedTab()).getData();
	}

	
	/**
	 * Construction de la form
	 */
	private void updateForm(TabInfo tabInfo)
	{
		vLayout.removeAllComponents();
		
		
		// Construction de la forme
		form = new FormLayout();
		form.setWidth("100%");
		form.setImmediate(true);


		//
		binder = new FieldGroup();
		binder.setBuffered(true);
		binder.setItemDataSource(item);
		
		//
		validatorManager.reset();
		
		//
		tabInfo.tabDraw.drawTab();
		
		//
		vLayout.addComponent(form);
		vLayout.setComponentAlignment(form, Alignment.MIDDLE_LEFT);
		
	}
	
	
	/**
	 * Sauvegarde de la forme dans le modele et realisation des vérifications
	 * 
	 * Retourne true si tout est OK
	 * 
	 * @param tabInfo
	 */
	private boolean saveForm(TabInfo tabInfo)
	{
		doCommit();
		
		// Verification des validateurs
		List<String> msg = validatorManager.validate();
		if (msg.size()>0)
		{
			msg.add(0, "Merci de corriger les points suivants :");
			MessagePopup.open(new MessagePopup("Notification", msg));
			return false;
		}
		
		// Verifications spécifiques
		if (tabInfo.tabValidate!=null)
		{
			String str  = tabInfo.tabValidate.validate();
			if (str!=null)
			{
				List<String> strs = new ArrayList<String>();
				strs.add(str);
				MessagePopup.open(new MessagePopup("Notification", strs));
				return false;
			}
		}
		
		return true;
	}
	

	protected void createButtonBar()
	{
		if (errorInInitialCondition)
		{
			addButton("OK", new Button.ClickListener()
			{
				@Override
				public void buttonClick(ClickEvent event)
				{
					close();
				}
			});
			return ;
		}
		
		addButton("Annuler", new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleAnnuler();
			}
		});
		
	
		saveButton = addDefaultButton(saveButtonTitle, new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleSave();
			}
		});
	}
	

	private void handleAnnuler()
	{
		binder.discard();
		close();
	}
	

	private void handleSave()
	{
		// On sauvegarde d'abord l'onglet sur lequel on est
		TabInfo tabInfo = tabInfos.get(selectedTab);
		boolean ret = saveForm(tabInfo);
		if (ret==false)
		{
			return;
		}
		
		// On sauvegarde ensuite tous les autres, sauf celui que l'on a déjà fait 
		for (int i = 0; i < tabInfos.size(); i++)
		{
			if (i!=selectedTab)
			{
				tabInfo = tabInfos.get(i);
				ret = saveForm(tabInfo);
				if (ret==false)
				{
					tabSheet.removeSelectedTabChangeListener(this);
					tabSheet.setSelectedTab(i);
					tabSheet.addSelectedTabChangeListener(this);
					return;
				}
			}
		}
				
		
		// On appelle ensuite la logique de sauvegarde metier  
		try
		{
			// Sauvegarde 
			performSauvegarder();
		}
		catch(OnSaveException e)
		{
			MessagePopup.open(new MessagePopup("Erreur", e.getAllMessages()));
			return ;
		}
		catch(Exception e)
		{
			ErrorPopup.open(e);
			return;
		}

		close();
	}
	
		
	protected void setEmptyTitle()
	{
	/*	hTitre.setValue("");
		hTitre.setStyleName("");*/
	}
	
	
	protected void setStepTitle(String message)
	{
	//	hTitre.setValue("Etape "+(pageNumber+1)+" : "+message);
	}
	
}
