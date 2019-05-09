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
 package fr.amapj.view.views.remiseproducteur;

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table.Align;

import fr.amapj.service.services.edgenerator.excel.EGRemise;
import fr.amapj.service.services.remiseproducteur.PaiementRemiseDTO;
import fr.amapj.service.services.remiseproducteur.RemiseDTO;
import fr.amapj.service.services.remiseproducteur.RemiseProducteurService;
import fr.amapj.view.engine.excelgenerator.TelechargerPopup;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;
import fr.amapj.view.engine.popup.corepopup.CorePopup.ColorStyle;
import fr.amapj.view.engine.popup.messagepopup.MessagePopup;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.DateTimeToStringConverter;
import fr.amapj.view.engine.tools.DateToStringConverter;
import fr.amapj.view.engine.widgets.CurrencyTextFieldConverter;
import fr.amapj.view.views.common.contratselector.ContratSelectorPart;


/**
 * Gestion des remises 
 */
@SuppressWarnings("serial")
public class RemiseProducteurListPart extends StandardListPart<RemiseDTO> implements PopupSuppressionListener 
{
	private ContratSelectorPart contratSelectorPart;

	public RemiseProducteurListPart()
	{
		super(RemiseDTO.class,false);
	}
	
	@Override
	protected String getTitle() 
	{
		return "Liste des remises aux producteurs";
	}


	@Override
	protected void drawButton() 
	{		
		addButton("Faire une remise",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Visualiser une remise",ButtonType.EDIT_MODE,()->handleVoir());
		addButton("Télécharger ...",ButtonType.EDIT_MODE,()->handleTelecharger());
		addButton("Supprimer une remise",ButtonType.EDIT_MODE,()->handleSupprimer());
	}

	@Override
	protected void addSelectorComponent()
	{
		// Partie choix du contrat
		contratSelectorPart = new ContratSelectorPart(this);
		HorizontalLayout toolbar1 = contratSelectorPart.getChoixContratComponent();
		
		addComponent(toolbar1);
		
		contratSelectorPart.fillAutomaticValues();
	}
	

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new Object[] { "moisRemise", "dateCreation" , "dateReelleRemise" , "mnt" });
		
		cdesTable.setColumnHeader("moisRemise","Mois remise");
		cdesTable.setColumnHeader("dateCreation","Date création");
		cdesTable.setColumnHeader("dateReelleRemise","Date réelle de la remise");
		cdesTable.setColumnHeader("mnt","Montant (en €)");
		cdesTable.setColumnAlignment("mnt",Align.RIGHT);
		
		//
		cdesTable.setConverter("dateCreation", new DateTimeToStringConverter());
		cdesTable.setConverter("dateReelleRemise", new DateToStringConverter());
		cdesTable.setConverter("mnt", new CurrencyTextFieldConverter());
	}



	@Override
	protected List<RemiseDTO> getLines() 
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		if (idModeleContrat==null)
		{
			return null;
		}
		return new RemiseProducteurService().getAllRemise(idModeleContrat);
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "dateTheoRemise" };
	}
	
	protected String[] getSearchInfos()
	{
		return null;
	}
	

	private void handleTelecharger()
	{
		RemiseDTO remiseDTO = getSelectedLine();
		TelechargerPopup popup = new TelechargerPopup("Remise à un producteur");
		popup.addGenerator(new EGRemise(remiseDTO.id));
		CorePopup.open(popup,this);
	}



	private void handleVoir()
	{
		RemiseDTO remiseDTO = getSelectedLine();
		String str = formatRemise(remiseDTO.id);
		MessagePopup.open(new MessagePopup("Visualisation d'une remise", ContentMode.HTML, ColorStyle.GREEN,str));	
	}
	
	

	private void handleSupprimer()
	{
		
		RemiseDTO remiseDTO = getSelectedLine();
		String text = "Etes vous sûr de vouloir supprimer la remise de "+remiseDTO.moisRemise+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,remiseDTO.id,true);
		SuppressionPopup.open(confirmPopup, this);	
		
	}
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new RemiseProducteurService().deleteRemise(idItemToSuppress);
	}


	private void handleAjouter()
	{
		Long idModeleContrat = contratSelectorPart.getModeleContratId();
		RemiseDTO remiseDTO = new RemiseProducteurService().prepareRemise(idModeleContrat);
		if (remiseDTO.preparationRemiseFailed==false)
		{
			CorePopup.open(new RemiseEditorPart(remiseDTO),this);
		}
		else
		{
			MessagePopup.open(new MessagePopup("Impossible de faire la remise.", ContentMode.HTML,ColorStyle.RED,"Il n'est pas possible de faire la remise à cause de :",remiseDTO.messageRemiseFailed));
		}
	}
	
	
	/**
	 * Formatage de la remise pour affichage dans un popup
	 * @param remiseDTO
	 * @return
	 */
	private String formatRemise(Long remiseId)
	{
		RemiseDTO remiseDTO = new RemiseProducteurService().loadRemise(remiseId);
				
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer buf = new StringBuffer();
		
		buf.append("Remise de chèques du mois de "+remiseDTO.moisRemise+"<br/>");
		buf.append(remiseDTO.nbCheque+" chèques dans cette remise<br/>");
		buf.append("Montant total :  "+new CurrencyTextFieldConverter().convertToString(remiseDTO.mnt)+" € <br/><br/>");
		buf.append("Date de création :  "+df.format(remiseDTO.dateCreation)+"<br/>");
		buf.append("Date réelle de remise :  "+df.format(remiseDTO.dateReelleRemise)+"<br/><br/>");
		
		for (PaiementRemiseDTO paiement : remiseDTO.paiements)
		{
			String txt = paiement.nomUtilisateur+" "+paiement.prenomUtilisateur+" - "+new CurrencyTextFieldConverter().convertToString(paiement.montant)+" € ";
			if (paiement.commentaire1!=null)
			{
				txt = txt+" - "+paiement.commentaire1;
			}
			if (paiement.commentaire2!=null)
			{
				txt = txt+" - "+paiement.commentaire2;
			}
			txt = txt+"<br/>";
			buf.append(txt);
		}
		
		
		return buf.toString();
	}

	
}
