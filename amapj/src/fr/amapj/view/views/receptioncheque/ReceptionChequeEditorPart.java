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
 package fr.amapj.view.views.receptioncheque;

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fr.amapj.model.models.contrat.reel.EtatPaiement;
import fr.amapj.model.models.param.ChoixOuiNon;
import fr.amapj.model.models.param.paramecran.PEReceptionCheque;
import fr.amapj.service.services.mescontrats.DatePaiementDTO;
import fr.amapj.service.services.mespaiements.MesPaiementsService;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.view.engine.menu.MenuList;
import fr.amapj.view.engine.popup.okcancelpopup.OKCancelPopup;
import fr.amapj.view.engine.tools.BaseUiTools;
import fr.amapj.view.engine.tools.TableBuilder;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;

/**
 * Popup pour la réception des chèques
 *  
 */
public class ReceptionChequeEditorPart extends OKCancelPopup
{
	
	private SimpleDateFormat df = new SimpleDateFormat("MMMMM yyyy");
	
	private Long idContrat;
	
	private String nomUtilisateur;
	
	private String prenomUtilisateur;
	
	private List<DatePaiementDTO> paiements;
	
	private Table t;
	
	private PEReceptionCheque peConf;
		
	
	
	
	public ReceptionChequeEditorPart(Long idContrat, String nomUtilisateur, String prenomUtilisateur)
	{
		super();
		setHeight("90%");
		this.idContrat = idContrat;
		this.nomUtilisateur = nomUtilisateur;
		this.prenomUtilisateur = prenomUtilisateur;
		
	}

	@Override
	protected void createContent(VerticalLayout contentLayout)
	{
		
	
		//
		paiements = new MesPaiementsService().getPaiementAReceptionner(idContrat);
		peConf = (PEReceptionCheque) new ParametresService().loadParamEcran(MenuList.RECEPTION_CHEQUES);
		//
		popupTitle = "Réception chèques";
		setWidth(60);
		
		// Premiere ligne de texte
		String msg = "<h2> Réception des chèques de "+prenomUtilisateur+" "+nomUtilisateur+"</h2>";
		Label lab = new Label(msg,ContentMode.HTML);
		contentLayout.addComponent(lab);
		
		if(paiements.size()==0)
		{
			BaseUiTools.addStdLabel(contentLayout, "Il n'y a pas de chèques à réceptionner.", null);
			return;
		}
		
		
		// Construction de l'entete de la table
		TableBuilder builder = new TableBuilder();
		
		builder.startHeader("tete", 70);
		builder.addHeaderBox("Date", 213);
		builder.addHeaderBox("Montant €", 163);
		builder.addHeaderBox("Cocher la case si le chèque a été donné", 163);
		if (peConf.saisieCommentaire1==ChoixOuiNon.OUI)
		{
			builder.addHeaderBox(peConf.libSaisieCommentaire1, 213);
		}
		if (peConf.saisieCommentaire2==ChoixOuiNon.OUI)
		{
			builder.addHeaderBox(peConf.libSaisieCommentaire2, 213);
		}
		contentLayout.addComponent(builder.getHeader());
		
		
		
		// Construction du contenu de la table
		t = new Table();
		
		int nbCol = 3;
		t.addContainerProperty("date", Label.class, null);
		t.addContainerProperty("montant", Label.class, null);
		t.addContainerProperty("box", CheckBox.class, null);
		if (peConf.saisieCommentaire1==ChoixOuiNon.OUI)
		{
			t.addContainerProperty("c1", TextField.class, null);
			nbCol++;
		}
		if (peConf.saisieCommentaire2==ChoixOuiNon.OUI)
		{
			t.addContainerProperty("c2", TextField.class, null);
			nbCol++;
		}
		
		for (int i=0;i<paiements.size();i++)
		{
			DatePaiementDTO p = paiements.get(i);
			
			Object[] cells = new Object[nbCol];
			
			int index=0;
			
			Label l = builder.createLabel(df.format(p.datePaiement),200);
			cells[index] = l;
			index++;
			
			
			l =  builder.createLabel(new CurrencyTextFieldConverter().convertToString(p.montant),150);
			cells[index] = l;
			index++;
			
			CheckBox cb =  builder.createCheckBox(p.etatPaiement==EtatPaiement.AMAP, 150);
			cells[index] = cb;
			index++;
			
			if (peConf.saisieCommentaire1==ChoixOuiNon.OUI)
			{
				TextField tf =  builder.createTextField(p.commentaire1,200);
				cells[index] = tf;
				index++;
			}
			
			if (peConf.saisieCommentaire2==ChoixOuiNon.OUI)
			{
				TextField tf =  builder.createTextField(p.commentaire2,200);
				cells[index] = tf;
				index++;
			}
			
			t.addItem(cells, i);
		}
		

		t.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		t.setSelectable(true);
		t.setSortEnabled(false);
		t.setPageLength(15);
		
		contentLayout.addComponent(t);
	
	}
	
	@Override
	protected void createButtonBar()
	{
		Button toutOK = addButton("J'ai bien reçu tous les chèques", e->handleToutSelectionner());
		setButtonAlignement(toutOK, Alignment.TOP_LEFT);
		
		super.createButtonBar();
	}
	
	
	protected void handleToutSelectionner()
	{
		for (int i = 0; i <paiements.size(); i++)
		{
			Item item = t.getItem(i);
			
			CheckBox tf = (CheckBox) item.getItemProperty("box").getValue();
			tf.setValue(Boolean.TRUE);
		}
		
	}



	public boolean performSauvegarder()
	{
		for (int i = 0; i < paiements.size(); i++)
		{
			DatePaiementDTO paiement = paiements.get(i);
			Item item = t.getItem(i);
			
			// case à cocher
			CheckBox cb = (CheckBox) item.getItemProperty("box").getValue();
			if (cb.getValue().booleanValue()==true)
			{
				paiement.etatPaiement=EtatPaiement.AMAP;
			}
			else
			{
				paiement.etatPaiement=EtatPaiement.A_FOURNIR;
			}
			
			// Commentaire 1
			if (peConf.saisieCommentaire1==ChoixOuiNon.OUI)
			{
				TextField tf = (TextField) item.getItemProperty("c1").getValue();
				paiement.commentaire1 = tf.getValue();
			}	
			
			// Commentaire 2
			if (peConf.saisieCommentaire2==ChoixOuiNon.OUI)
			{
				TextField tf = (TextField) item.getItemProperty("c2").getValue();
				paiement.commentaire2 = tf.getValue();
			}
		}
		
	
		new MesPaiementsService().receptionCheque(paiements);
		
		return true;
	}



	
	
}
