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
 package fr.amapj.view.views.appinstance;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TextArea;

import fr.amapj.common.CollectionUtils;
import fr.amapj.service.services.appinstance.AppInstanceDTO;
import fr.amapj.service.services.appinstance.AppInstanceService;
import fr.amapj.service.services.appinstance.SqlRequestDTO;
import fr.amapj.service.services.appinstance.SqlRequestDTO.DataBaseResponseDTO;
import fr.amapj.service.services.appinstance.SqlRequestDTO.ResponseDTO;
import fr.amapj.service.services.appinstance.SqlRequestDTO.SqlType;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet uniquement de creer des instances
 * 
 *
 */
public class PopupSqlAppInstance extends WizardFormPopup
{

	private SqlRequestDTO selected;
	
	private List<AppInstanceDTO>  appInstanceDTOs;
	

	public enum Step
	{
		GENERAL , SQL_REQUEST , AFF_SQL_REQUEST , SQL_RESULT ;
	}

	/**
	 * 
	 */
	public PopupSqlAppInstance(List<AppInstanceDTO> appInstanceDTOs )
	{
		
		popupTitle = "Executer des requetes SQL";
		setWidth(90);
		saveButtonTitle = "OK";
		this.appInstanceDTOs = appInstanceDTOs;
		CollectionUtils.sort(this.appInstanceDTOs,e->e.nomInstance);
		
		
		// Contruction de l'item
		selected = new SqlRequestDTO();
		item = new BeanItem<SqlRequestDTO>(selected);
	}
	
	@Override
	protected void configure()
	{
		add(Step.GENERAL,()->addFieldGeneral());
		add(Step.SQL_REQUEST,()->addRequest());
		add(Step.AFF_SQL_REQUEST,()->addAffichageRequest());
		add(Step.SQL_RESULT,()->addAffichageResult());
	}


	private void addFieldGeneral()
	{
		
		// Titre
		setStepTitle("informations");

		// Champ 1 : le type de requetes
		addComboEnumField("Type des requetes", "sqlType");

		
		
		// Champ 2
		String str = "Cet outil permet d'executer X requetes SQL sur les bases sélectionnées<br/><br/>";
		
		str = str +"Le nombre de bases est : "+appInstanceDTOs.size()+" bases<br/><br/>";
		str = str +"Liste des bases <br/>";
		
		for (AppInstanceDTO appInstanceDTO : appInstanceDTOs)
		{
			str = str+ appInstanceDTO.nomInstance+"<br/>";
		}
		
		addLabel(str, ContentMode.HTML);
		
		
	}
	
	

	private void addRequest()
	{
		
		// Titre
		setStepTitle("saisir la liste des requetes SQL");
		
		// Champ 1
		TextArea f =  addTextAeraField("Listes des requetes", "requests");
		f.setHeight(8, Unit.CM);
		
	}
	
	private void addAffichageRequest()
	{
		// Titre
		setStepTitle("vérifier les requetes SQL");
		
		//
		computeRequest();
		
		
		String str = "Il y a "+selected.verifiedRequests.size()+" requetes à executer<br/><br/>"+
					"Voici la liste des requetes :<br/><br/>";

		int index = 1;
		for (String s : selected.verifiedRequests)
		{
			str = str + "Requete "+index+"<br/>"+SafeHtmlUtils.htmlEscape(s)+"<br/><br/>";
			index++;
		}
		
		
		addLabel(str, ContentMode.HTML);
	}

	

	private void computeRequest()
	{
		selected.verifiedRequests = new ArrayList<String>();
		
		String a = selected.requests.replaceAll("\r\n", "\n");
		a = a.replaceAll("\n\r", "\n");
		// On ajoute un retour à la ligne final
		a= a+"\n";
		
		String[] rs = a.split(";\n");
		for (int i = 0; i < rs.length; i++)
		{
			String r = rs[i].trim();
			if (r.length()>0)
			{
				selected.verifiedRequests.add(r);
			}
			
		}
	}
	
	
	private void addAffichageResult()
	{
		new AppInstanceService().executeSqlRequest(selected,appInstanceDTOs);
		
		// Titre
		setStepTitle("résultats des requetes SQL");
				
		String str = "";
		if (selected.success)
		{	
			str = str+"SUCCESS <br/><br/>";		
		}
		else
		{
			str = str+"<h1>!!! ECHEC !!! </h1><br/><br/>";
		}
		

		for (DataBaseResponseDTO dataBase : selected.responses)
		{
			str = str + addDataBase(dataBase);
		}
		
		addLabel(str, ContentMode.HTML);
		
		
	}

	private String addDataBase(DataBaseResponseDTO dataBase)
	{
		String str = "";
		
		if (selected.success)
		{	
			str = str+"OK pour la base "+dataBase.dbName+" <br/><br/>";
		}
		else
		{
			str = str+"<h1>!!! ECHEC !!! pour la base "+dataBase.dbName+"</h1><br/><br/>";
		}
		
		for (ResponseDTO responseDTO : dataBase.responses)
		{	
			str = str+"Requete "+responseDTO.index+" : "+responseDTO.sqlRequest+"<br/>";
			str = str + SafeHtmlUtils.htmlEscape(responseDTO.sqlResponse)+"<br/><br/>";
			if (selected.sqlType==SqlType.REQUETE_SQL_STANDARD)
			{
				str = str+"<table>";
				List<List<String>> lines = responseDTO.sqlResultSet;
				if (lines!=null)
				{
					for (List<String> ls : lines)
					{
						str = str+" <tr>";
						for (String string : ls)
						{
							if (string!=null)
							{
								string = SafeHtmlUtils.htmlEscape(string);
							}
							str = str +  "<td>"+string+"</td>";
						}
						str = str+" </tr>";
					}
				}
				str = str+" </table>";
				str = str+"<br/><br/>";
			}
			
		}
		return str;
	}

	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		// Do nothing
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
