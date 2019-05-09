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
 package fr.amapj.view.views.listeadherents;

import java.util.ArrayList;
import java.util.List;

import fr.amapj.common.CollectionUtils;
import fr.amapj.service.services.edgenerator.excel.EGListeAdherent;
import fr.amapj.service.services.edgenerator.excel.EGListeAdherent.Type;
import fr.amapj.service.services.parametres.ParametresService;
import fr.amapj.service.services.parametres.paramecran.PEListeAdherentDTO;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.service.services.utilisateur.util.UtilisateurUtil;
import fr.amapj.view.engine.excelgenerator.LinkCreator;
import fr.amapj.view.engine.listpart.ButtonType;
import fr.amapj.view.engine.listpart.StandardListPart;
import fr.amapj.view.engine.popup.corepopup.CorePopup;


/**
 * Page permettant de presenter la liste des utilisateurs
 * 
 */
public class ListeAdherentsView extends StandardListPart<UtilisateurDTO> 
{
	PEListeAdherentDTO p;
	
	public ListeAdherentsView()
	{
		super(UtilisateurDTO.class,false);
		p = new ParametresService().getPEListeAdherentDTO();	
	}
	
	
	@Override
	protected String getTitle() 
	{
		return "Liste des adhérents";
	}


	@Override
	protected void drawButton() 
	{
		if (p.canAccessEmail)
		{	
			addButton("Envoyer un mail à tous ...",ButtonType.ALWAYS,()->handleSendMail());
		}
		
		addSearchField("Rechercher par le nom ou le prénom");
	}

	@Override
	protected void addExtraComponent() 
	{
		addComponent(LinkCreator.createLink(new EGListeAdherent(Type.STD,p)));
	}

	@Override
	protected void drawTable() 
	{
		// Gestion de la liste des colonnes visibles
		List<String> cols = new ArrayList<>();
		cols.add("nom");
		cols.add("prenom");
		
		if (p.canAccessEmail)
		{	
			cols.add("email");
		}
		if (p.canAccessTel1)
		{	
			cols.add("numTel1");
		}
		if (p.canAccessTel2)
		{	
			cols.add("numTel2");
		}
		
		cdesTable.setVisibleColumns(cols.toArray());
		
		cdesTable.setColumnHeader("nom","Nom");
		cdesTable.setColumnHeader("prenom","Prénom");
		cdesTable.setColumnHeader("email","E mail");
		cdesTable.setColumnHeader("numTel1","Numéro Tel 1");
		cdesTable.setColumnHeader("numTel2","Numéro Tel 2");
	}



	@Override
	protected List<UtilisateurDTO> getLines() 
	{
		List<UtilisateurDTO> us = new UtilisateurService().getAllUtilisateurs(false);
		
		for (UtilisateurDTO u : us)
		{
			if (UtilisateurUtil.canSendMailTo(u.email)==false)
			{
				u.email = "Pas d'email";
			}
		}
		
		return us;
	}


	@Override
	protected String[] getSortInfos() 
	{
		return new String[] { "nom" , "prenom" };
	}
	
	protected String[] getSearchInfos()
	{
		return new String[] { "nom" , "prenom" };
	}
	
	
	private void handleSendMail()
	{
		String mails = getAllEmails();
		PopupCopyAllMail popup = new PopupCopyAllMail(mails);
		CorePopup.open(popup);
	}
	
	
	public String getAllEmails()
	{
		List<UtilisateurDTO> us = new UtilisateurService().getAllUtilisateurs(false);
		
		// On supprime tous les utilisateurs sans e mail 
		us.removeIf(u-> UtilisateurUtil.canSendMailTo(u.email)==false);
		
		//
		return CollectionUtils.asString(us, ",", u -> u.email);
	}
}
