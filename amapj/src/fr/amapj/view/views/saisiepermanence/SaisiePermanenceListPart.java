/*
 *  Copyright 2013-2016 Emmanuel BRUN (contact@amapj.fr)
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
 package fr.amapj.view.views.saisiepermanence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.amapj.common.DateUtils;
import fr.amapj.service.services.edgenerator.excel.EGPlanningPermanence;
import fr.amapj.service.services.saisiepermanence.PermanenceDTO;
import fr.amapj.service.services.saisiepermanence.PermanenceService;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.suppressionpopup.PopupSuppressionListener;
import fr.amapj.view.engine.popup.suppressionpopup.SuppressionPopup;
import fr.amapj.view.engine.popup.suppressionpopup.UnableToSuppressException;
import fr.amapj.view.engine.tools.DateToStringConverter;


/**
 * Saisie des distributions
 *
 */
@SuppressWarnings("serial")
public class SaisiePermanenceListPart extends StandardListPart<PermanenceDTO> implements PopupSuppressionListener 
{
	

	public SaisiePermanenceListPart()
	{
		super(PermanenceDTO.class,false);
	}
	
	@Override
	protected String getTitle() 
	{
		return "Planning des permanences";
	}


	@Override
	protected void drawButton() 
	{		
		addButton("Créer une nouvelle permanence",ButtonType.ALWAYS,()->handleAjouter());
		addButton("Modifier",ButtonType.EDIT_MODE,()->handleEditer());
		addButton("Supprimer",ButtonType.EDIT_MODE,()->handleSupprimer());
		addButton("Planifier",ButtonType.ALWAYS,()->handlePlanification());
		addButton("Supprimer plusieurs permanences",ButtonType.ALWAYS,()->handleDeleteList());
		
		addSearchField("Rechercher par nom");
		

	}

	@Override
	protected void addExtraComponent() 
	{
		// addComponent(LinkCreator.createLink(new EGPlanningPermanence(DateUtils.getDate())));
	}
	

	@Override
	protected void drawTable() 
	{
		// Titre des colonnes
		cdesTable.setVisibleColumns(new String[] { "datePermanence", "utilisateurs" , "numeroSession"});
		cdesTable.setColumnHeader("datePermanence","Date");
		cdesTable.setColumnHeader("utilisateurs","Personnes de permanence");
		cdesTable.setColumnHeader("numeroSession","Numéro de la session");
		
		cdesTable.setConverter("datePermanence", new DateToStringConverter());
	}



	@Override
	protected List<PermanenceDTO> getLines() 
	{
		return new PermanenceService().getAllDistributions();
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "datePermanence" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "utilisateurs" };
	}
	
	
	private void handlePlanification()
	{
		PopupPlanificationPermanence.open(new PopupPlanificationPermanence(),this);		
	}
	
	
	

	private void handleAjouter()
	{
		PopupSaisiePermanence.open(new PopupSaisiePermanence(null), this);
	}

	

	protected void handleEditer()
	{
		PermanenceDTO dto = getSelectedLine();
		PopupSaisiePermanence.open(new PopupSaisiePermanence(dto), this);
	}

	protected void handleSupprimer()
	{
		PermanenceDTO dto = getSelectedLine();
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
		String text = "Etes vous sûr de vouloir supprimer la permanence du "+df1.format(dto.datePermanence)+" ?";
		SuppressionPopup confirmPopup = new SuppressionPopup(text,dto.id);
		SuppressionPopup.open(confirmPopup, this);		
	}
	
	
	@Override
	public void deleteItem(Long idItemToSuppress) throws UnableToSuppressException
	{
		new PermanenceService().deleteDistribution(idItemToSuppress);
	}


	protected void handleDeleteList()
	{
		PopupDeletePermanence.open(new PopupDeletePermanence(), this);
	}
	
}
