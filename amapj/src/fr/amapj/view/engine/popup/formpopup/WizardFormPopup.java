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
 package fr.amapj.view.engine.popup.formpopup;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.common.AmapjRuntimeException;
import fr.amapj.common.GenericUtils;
import fr.amapj.view.engine.popup.errorpopup.ErrorPopup;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;

/**
 * Popup contenant un formulaire basé sur un PropertysetItem ou sur un BeanItem
 * avec la gestion couplée d'un wizard
 *  
 */
abstract public class WizardFormPopup extends AbstractFormPopup
{
	protected String nextButtonTitle = "Etape suivante ...";
	protected Button nextButton;
	
	protected String previousButtonTitle = "Etape précédente ...";
	protected Button previousButton;
	
	protected Button cancelButton;
	
	protected String saveButtonTitle = "Sauvegarder";
		
	private VerticalLayout contentLayout;
	
	protected Label hTitre;
	
	private int pageNumber;
	private Object[] enumArray;

	private boolean errorInInitialCondition = false;
	
	
	public WizardFormPopup()
	{
		// Par défaut, la taille est à 80%  pour tous les wizards popup 
		setHeight("80%");
	}
	
	
	protected void createContent(VerticalLayout contentLayout)
	{
		//
		this.contentLayout = contentLayout;
		contentLayout.addStyleName("wizard-popup");
		EnumSet enums = EnumSet.allOf(getEnumClass());
		enumArray = enums.toArray();
		
		// Chargement de la définition de chaque étape  
		configure();
		
		// Verification de la configuration 
		for (Object en : enumArray)
		{
			DetailStepInfo stepInfo = details.get(en);
			if (stepInfo==null)
			{
				throw new AmapjRuntimeException("Wizard : Pas de configuration pour la partie "+en);
			}
		}
		
		// Vérification des conditions initiales
		String str = checkInitialCondition();
		if (str!=null)
		{
			errorInInitialCondition = true;
			displayErrorOnInitialCondition(str);
			return;
		}
		
		
		// Mise en place du titre
		hTitre = new Label("");
		hTitre.addStyleName("wizard-popup-etape");
		contentLayout.addComponent(hTitre);
		
		//
		updateForm();
		
	}
	
	/**
	 * Should be overriden
	 * @return
	 */
	protected String checkInitialCondition()
	{
		return null;
	}

	private void displayErrorOnInitialCondition(String str)
	{
		Label label = new Label(str,ContentMode.HTML);
		label.setStyleName(ChameleonTheme.LABEL_BIG);
		contentLayout.addComponent(label);
	}

	private void updateForm()
	{
		if (form!=null)
		{
			contentLayout.removeComponent(form);
		}
		
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
		
		// Déclaration des propriétés et construction des champs
		Enum current = (Enum) enumArray[pageNumber];
		DetailStepInfo stepInfo = details.get(current);
		stepInfo.drawScreen.action(); // Dessin de l'écran 
		
		contentLayout.addComponent(form);
		contentLayout.setComponentAlignment(form, Alignment.MIDDLE_LEFT);
		
	}

	protected void createButtonBar()
	{
		if (errorInInitialCondition)
		{
			addDefaultButton("OK", e->close());
			return ;
		}
		
		//	
		cancelButton = addButton("Annuler", e->handleAnnuler());
		
		//
		previousButton = addButton(previousButtonTitle, e->handlePrevious());
		previousButton.setEnabled(false);
		previousButton.setId("amapj.popup.previous");
		
		//
		nextButton = addDefaultButton(nextButtonTitle, e->handleNext());
		nextButton.setId("amapj.popup.next");
		
		// On rend invisible le bouton précédent dans le cas ou il y a une seule page
		if (enumArray.length==1)
		{
			previousButton.setVisible(false);		
			nextButton.setCaption(saveButtonTitle);
		}
	}
	

	private void handleAnnuler()
	{
		binder.discard();
		close();
	}
	
	private void handlePrevious()
	{
		doCommit();
		pageNumber--;
		updateButtonStatus();
		updateForm();
	}

	private void updateButtonStatus()
	{
		// Gestion de l'état et du libellé du bouton suivant
		if (pageNumber==enumArray.length-1)
		{
			nextButton.setCaption(saveButtonTitle);		
		}
		else	
		{
			nextButton.setCaption(nextButtonTitle);
		}
		nextButton.setEnabled(true);
		
		// Gestion de l'état et du libellé du bouton précédent
		if (pageNumber==0)
		{
			previousButton.setEnabled(false);		
			
		}
		else	
		{
			previousButton.setEnabled(true);
		}
		
	}

	private void handleNext()
	{
		doCommit();
		
		// On verifie d'abord les champs 1 par 1
		List<String> msg = validatorManager.validate();
		if (msg.size()>0)
		{
			msg.add(0, "Merci de corriger les points suivants :");
			MessagePopup.open(new MessagePopup("Notification", msg));
			return;
		}
		
		// On fait ensuite la verification globale 
		Enum current = (Enum) enumArray[pageNumber];
		DetailStepInfo stepInfo = details.get(current);
		if (stepInfo.check!=null)
		{
			String verifGlobal = stepInfo.check.action();
			if (verifGlobal!=null)
			{
				MessagePopup.open(new MessagePopup("Notification", ContentMode.HTML,ColorStyle.RED,verifGlobal));
				return;
			}
		}
		
		
		// Soit on sauvegarde
		if (pageNumber==enumArray.length-1)
		{
			handleSauvegarder();
		}
		// Soit on passe à la page suivante 
		else
		{
			pageNumber++;
			updateButtonStatus();
			updateForm();
		}
	}
	

	private void handleSauvegarder()
	{
		try
		{
			// Sauvegarde 
			performSauvegarder();
		}
		catch(OnSaveException e)
		{
			List<String> msgs = new ArrayList<String>();
			msgs.add("Une erreur est survenue durant la sauvegarde.");
			msgs.addAll(e.getAllMessages());
			MessagePopup.open(new MessagePopup("Erreur", msgs));
			return ;
		}
		catch(ConstraintViolationException e)
		{
			// TODO afficher plus clair 
			ErrorPopup.open(e);
			return;
		}
		catch(Exception e)
		{
			ErrorPopup.open(e);
			return;
		}

		close();
	}	
	
	/**
	 * Dans ce mode, on ne que revenir à la page précédente
	 */
	protected void setBackOnlyMode()
	{
		nextButton.setEnabled(false);
	}
	
	
	/**
	 * Retourne null si tout est ok, sinon retourne une liste de messages d'erreur
	 * @return
	 */ 
	abstract protected void  performSauvegarder() throws OnSaveException;
	
	/**
	 * Retourne la liste des étapes
	 * 
	 * ne doit pas retourner null
	 */
	abstract protected Class getEnumClass();

	/**
	 * Permet de déclarer le contenu des écrans 
	 */
	abstract protected void configure();
	
	static public class DetailStepInfo
	{
		public GenericUtils.VoidAction drawScreen;
		public GenericUtils.StringAction check;
	}
	
	private Map<Enum,DetailStepInfo> details = new HashMap<Enum,DetailStepInfo>();
	
	/**
	 * 
	 */
	protected void add(Enum step,GenericUtils.VoidAction drawScreen)
	{
		DetailStepInfo detail = new DetailStepInfo();
		detail.drawScreen = drawScreen;
		
		details.put(step,detail);
	}
	
	/**
	 * 
	 */
	public void add(Enum step,GenericUtils.VoidAction drawScreen,GenericUtils.StringAction check)
	{
		DetailStepInfo detail = new DetailStepInfo();
		detail.drawScreen = drawScreen;
		detail.check = check;
		
		details.put(step,detail);
	}
	
	
	protected void setStepTitle(String message)
	{
		hTitre.setValue("Etape "+(pageNumber+1)+" : "+message);
	}
	
	/**
	 * Permet de changer le bouton "Etape suivante" en "Sauvegarder" 
	 */
	protected void setNextButtonAsSave()
	{
		nextButton.setCaption(saveButtonTitle);
	}
	
	
	/**
	 * Permet de changer tous les boutons du bas en un seul bouton OK 
	 */
	protected void setAllButtonsAsOK()
	{
		nextButton.setCaption("OK");
		previousButton.setVisible(false);
		cancelButton.setVisible(false);
	}
	
	
}
