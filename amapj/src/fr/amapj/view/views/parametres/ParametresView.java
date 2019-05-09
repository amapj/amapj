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
 package fr.amapj.view.views.parametres;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import fr.amapj.service.services.parametres.ParametresDTO;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.popup.PopupListener;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.template.BackOfficeLongView;
import fr.amapj.view.views.parametres.paramecran.PEListeAdherentEditorPart;
import fr.amapj.view.views.parametres.paramecran.PELivraisonAmapienEditorPart;
import fr.amapj.view.views.parametres.paramecran.PELivraisonProducteurEditorPart;
import fr.amapj.view.views.parametres.paramecran.PEMesContratsEditorPart;
import fr.amapj.view.views.parametres.paramecran.PEMesLivraisonsEditorPart;
import fr.amapj.view.views.parametres.paramecran.PEReceptionChequeEditorPart;
import fr.amapj.view.views.parametres.paramecran.PESaisiePaiementEditorPart;
import fr.amapj.view.views.parametres.paramecran.PESyntheseMultiContratEditorPart;


/**
 * Page permettant à l'administrateur de modifier les paramètres généraux
 */
public class ParametresView extends BackOfficeLongView implements PopupListener
{

	ParametresDTO dto;
	
	TextField nomAmap;
	TextField villeAmap;
	
	
	@Override
	public String getMainStyleName()
	{
		return "parametres";
	}

	/**
	 * 
	 */
	@Override
	public void enterIn(ViewChangeEvent event)
	{		
		// Bloc identifiants
		FormLayout form1 = new FormLayout();
        form1.setMargin(false);
        form1.addStyleName("light");
        addComponent(form1);
        
        
        Label section = new Label("Paramètres de l'AMAP");
        section.addStyleName("h2");
        section.addStyleName("colored");
        form1.addComponent(section);
		
		nomAmap = addTextField("Nom de l'AMAP ",form1);
		villeAmap = addTextField("Ville de l'AMAP ",form1);
		

		
		addButton("Changer les paramètres généraux",e->handleChangerParam());
			
		
		
		final PopupListener listener = this;
		
		
		addButton("Ecran \"Mes contrats\" , Généralités",e -> CorePopup.open(new PEMesContratsEditorPart(),listener));
		addButton("Ecran \"Mes contrats\" , Saisie des paiements par l'amapien",e -> CorePopup.open(new PESaisiePaiementEditorPart(),listener));

		addButton("Ecran \"Mes livraisons\"",e -> 	CorePopup.open(new PEMesLivraisonsEditorPart(),listener));

		addButton("Ecran \"Liste des adhérents\"",e -> 	CorePopup.open(new PEListeAdherentEditorPart(),listener));
		
		addButton("Ecran \"Livraison d'un producteur\"",e -> 	CorePopup.open(new PELivraisonProducteurEditorPart(),listener));
		
		addButton("Ecran \"Livraison d'un amapien\"",e -> 	CorePopup.open(new PELivraisonAmapienEditorPart(),listener));
		
		addButton("Ecran \"Réception des chèques\"",e -> CorePopup.open(new PEReceptionChequeEditorPart(),listener));

		addButton("Ecran \"Synthèse multi contrats\"",e -> CorePopup.open(new PESyntheseMultiContratEditorPart(),listener));
		
		
		refresh();
		
	}



	
	
	private void handleChangerParam()
	{
		FormPopup.open(new PopupSaisieParametres(dto),this);
		
	}
	

	
	private void addButton(String str,ClickListener listener)
	{
		Button b = new Button(str);
		b.addClickListener(listener);
		addComponent(b);
		
	}

	@Override
	public void onPopupClose()
	{
		refresh();
	}

	private void refresh()
	{
		dto = new ParametresService().getParametres();
		
		setValue(nomAmap,dto.nomAmap);
		setValue(villeAmap,dto.villeAmap);		
	}
	
	
	
	// TOOLS


	private void setValue(TextField tf, String val)
	{
		tf.setReadOnly(false);
		tf.setValue(val);
		tf.setReadOnly(true);
	}
	
	
	private TextField addTextField(String lib,FormLayout form)
	{
		TextField name = new TextField(lib);
		// name.addStyleName(TEXTFIELD_COMPTEINPUT);
		name.setWidth("100%");
		name.setNullRepresentation("");
		name.setReadOnly(true);
		form.addComponent(name);

		return name;
	}


}
