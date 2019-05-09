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
 package fr.amapj.view.engine.popup.suppressionpopup;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;


/**
 * Popup pour la confirmation de la suppression d'un élément
 *  
 */
@SuppressWarnings("serial")
public class SuppressionPopup extends CorePopup
{

	private Button okButton;
	private String okButtonTitle = "Supprimer";
	private Button cancelButton;
	private String cancelButtonTitle = "Annuler";
	
	// Dans le mode secured, l'utilisateur doit taper le mot SUPPRIMER pour confirmer la suppression 
	private boolean secured;
	
	private String message;
	private Long idItemToSuppress;
	private PopupSuppressionListener listener;
	
	
	public SuppressionPopup(String message,Long idItemToSuppress)
	{
		this(message,idItemToSuppress,false);
	}
	
	
	public SuppressionPopup(String message,Long idItemToSuppress,boolean secured)
	{
		this.message = message;
		this.idItemToSuppress = idItemToSuppress;
		this.secured = secured;
		
		popupTitle = "Confirmation suppression";
		
	}
	
	
	

	protected void createContent(VerticalLayout contentLayout)
	{

		setWidth(40,450);
		
		// Construction de la zone de texte
		HorizontalLayout hlTexte = new HorizontalLayout();
		hlTexte.setMargin(true);
		hlTexte.setSpacing(true);
		hlTexte.setWidth("100%");
		
		
		Label textArea = new Label(message);
		textArea.setStyleName(ChameleonTheme.TEXTFIELD_BIG);
		textArea.setWidth("80%");
		
		hlTexte.addComponent(textArea);
		hlTexte.setExpandRatio(textArea, 1);
		hlTexte.setComponentAlignment(textArea, Alignment.MIDDLE_CENTER);
		
		contentLayout.addComponent(hlTexte);
		
		
		if (secured)
		{
			hlTexte = new HorizontalLayout();
			hlTexte.setMargin(true);
			hlTexte.setSpacing(true);
			hlTexte.setWidth("100%");
			
			
			textArea = new Label("Veuillez confirmer en saississant le mot SUPPRIMER dans le champ de saisie ci dessous");
			textArea.setStyleName(ChameleonTheme.TEXTFIELD_BIG);
			textArea.setWidth("80%");
			
			hlTexte.addComponent(textArea);
			hlTexte.setExpandRatio(textArea, 1);
			hlTexte.setComponentAlignment(textArea, Alignment.MIDDLE_CENTER);
			
			contentLayout.addComponent(hlTexte);
			
			
			hlTexte = new HorizontalLayout();
			hlTexte.setMargin(true);
			hlTexte.setSpacing(true);
			hlTexte.setWidth("100%");
			
			
			TextField textField = new TextField();
			textField.setStyleName(ChameleonTheme.TEXTFIELD_BIG);
			textField.setWidth("80%");
			textField.setImmediate(true);
			textField.setBuffered(false);
			
			hlTexte.addComponent(textField);
			hlTexte.setExpandRatio(textField, 1);
			hlTexte.setComponentAlignment(textField, Alignment.MIDDLE_CENTER);
			
			textField.addTextChangeListener(new TextChangeListener()
			{
				@Override
				public void textChange(TextChangeEvent event)
				{
					if (event.getText().equals("SUPPRIMER"))
					{
						okButton.setEnabled(true);
					}
					else
					{
						okButton.setEnabled(false);
					}
				}
			} );
			
			
			
			contentLayout.addComponent(hlTexte);
		}
		
	}
	
	protected void createButtonBar()
	{
		okButton = addButton(okButtonTitle, new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleSupprimer();
			}
		});
		if (secured)
		{
			okButton.setEnabled(false);
			okButton.setImmediate(true);
		}
		
		cancelButton = addDefaultButton(cancelButtonTitle, new Button.ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				handleAnnuler();
			}
		});
	}

	protected void handleAnnuler()
	{
		close();
	}

	protected void handleSupprimer()
	{
		try
		{
			listener.deleteItem(idItemToSuppress);
			Notification.show("Suppression", "Suppression faite", Notification.Type.HUMANIZED_MESSAGE);	
		}
		catch(UnableToSuppressException e)
		{
			String title = "Erreur à la suppression";
			String t1="Impossible de supprimer cet élément. Raison :";
			String t2 = e.getMessage();
			MessagePopup popup = new MessagePopup(title,ContentMode.HTML,ColorStyle.RED,t1,t2);
			CorePopup.open(popup);
		}
		catch(Exception e)
		{
			ErrorPopup.open("Impossible de supprimer cet element.",e);
		}
		
		close();
	}
	


	static public void open(SuppressionPopup popup, final PopupSuppressionListener listener)
	{
		popup.listener = listener;
		CorePopup.open(popup,listener);
	}

}
