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
 package fr.amapj.view.views.mescontrats;

import java.text.SimpleDateFormat;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

import fr.amapj.service.services.gestioncotisation.GestionCotisationService;
import fr.amapj.service.services.mescontrats.AdhesionDTO;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour la saisie de la nouvelle adresse e mail
 *  
 */
@SuppressWarnings("serial")
public class PopupAdhesion extends OKCancelPopup
{
		
	private AdhesionDTO dto;

	private boolean canModify;

	private SimpleDateFormat df = new SimpleDateFormat("EEEEE dd MMMMM yyyy");
	
	private TextField textField;
	
	/**
	 * 
	 */
	public PopupAdhesion(AdhesionDTO dto,boolean canModify)
	{
		popupTitle = "Adhésion à l'AMAP";
		this.dto = dto;
		this.canModify = canModify;
		
		if (canModify)
		{
			saveButtonTitle = "J'adhère";
		}
		else
		{
			saveButtonTitle = "OK";
			hasCancelButton = false;
		}	
		
		// 
		setWidth(40, 450);
	}
	
	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		// 
		String entete = getEntete();
		Label l = new Label(entete,ContentMode.HTML);
		l.addStyleName(ChameleonTheme.LABEL_BIG);
		contentLayout.addComponent(l);
		
		//
		if (canModify==true)
		{
			FormLayout f = new FormLayout();
			textField = BaseUiTools.createCurrencyField("Montant de votre adhésion",false);
			textField.setConvertedValue(new Integer(getPropositionMontant()));
			textField.addStyleName("cell-saisie");
			f.addComponent(textField);
			contentLayout.addComponent(f);
		}
		
		//
		String bas = getBasPage();
		l = new Label(bas,ContentMode.HTML);
		l.addStyleName(ChameleonTheme.LABEL_BIG);
		contentLayout.addComponent(l);
		
		
		
	}

	private int getPropositionMontant()
	{
		if (dto.isCotisant())
		{
			return dto.periodeCotisationUtilisateurDTO.montantAdhesion;
		}
		else
		{
			return dto.periodeCotisationDTO.montantConseille;
		}
		
	}

	@Override
	protected boolean performSauvegarder()
	{
		// Si on est en lecture seule
		if (canModify==false)
		{
			return true;
		}
		
		int qte = 0;
		try
		{
			Integer val = (Integer) textField.getConvertedValue();
			
			if (val != null)
			{
				qte = val.intValue();
			}
		}
		catch (ConversionException e)
		{
			Notification.show("Erreur de saisie");
			return false;
		}
		
		if (qte<dto.periodeCotisationDTO.montantMini)
		{
			String str = "Le montant est insuffisant. Le montant minimum est "+new CurrencyTextFieldConverter().convertToString(dto.periodeCotisationDTO.montantMini)+" €";
			Notification.show(str);
			return false;
		}
		
		new GestionCotisationService().createOrUpdateAdhesion(dto,qte);				
		
		return true;
	}
	


	private String getEntete()
	{
		String str = "";
		
		if (canModify==false)
		{
			str = 	"Vous avez renouvelé votre adhésion avec un montant de <b>"+
					new CurrencyTextFieldConverter().convertToString(dto.periodeCotisationUtilisateurDTO.montantAdhesion)+
					" €</b><br/>";
		}
		else
		{
			str = "<b>Veuillez saisir ci dessous le montant de votre adhesion</b><br/>";
		}
		
		str = str + "Le montant conseillé est de "+new CurrencyTextFieldConverter().convertToString(dto.periodeCotisationDTO.montantConseille)+" €<br/>";
		
		str = str + "Le montant minimum est de "+new CurrencyTextFieldConverter().convertToString(dto.periodeCotisationDTO.montantMini)+" €<br/><br/>";
				
		return str;
	}
	
	private String getBasPage()
	{
		String str = "";
		
		if (dto.periodeCotisationDTO.libCheque!=null)
		{
			str = str + "Ordre du chèque : "+dto.periodeCotisationDTO.libCheque+"<br/>";
		}
		
		if (dto.periodeCotisationDTO.dateRemiseCheque!=null)
		{
			str = str + "Date de remise des chèques : "+df.format(dto.periodeCotisationDTO.dateRemiseCheque)+"<br/>";
		}
		
		if (dto.periodeCotisationDTO.textPaiement!=null)
		{
			str = str + dto.periodeCotisationDTO.textPaiement+"<br/>";
		}		
		return str;
	}



}
