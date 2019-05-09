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
 package fr.amapj.view.views.advanced.devtools;

import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;

import fr.amapj.service.services.advanced.devtools.DevToolsService;
import fr.amapj.service.services.advanced.supervision.SupervisionService;
import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.formpopup.WizardFormPopup;

/**
 * Permet de modifier les paramètres mineurs des modeles de contrat
 * 
 *
 */
public class PopupJpaEntityEquality extends WizardFormPopup
{
	
	private Long id;
	

	
	public enum Step
	{
		INFOS , AFFICHAGE1 , RESET,AFFICHAGE2 , RECHERCHE , RECHERCHE2 , CHARGEMENT;
	}

	/**
	 * 
	 */
	public PopupJpaEntityEquality()
	{		
		setWidth(80);
		popupTitle = "Test de l'égalité des entités";
				
	}
	
	@Override
	protected void configure()
	{
		add(Step.INFOS,()->addFieldInfos());
		add(Step.AFFICHAGE1,()->addFieldAffichage1());
		add(Step.RESET,()->addFieldReset());
		add(Step.AFFICHAGE2,()->addFieldAffichage2());
		add(Step.RECHERCHE,()->addFieldRecherche());
		add(Step.RECHERCHE2,()->addFieldRecherche2());
		add(Step.CHARGEMENT,()->addFieldChargement());
	}

	

	private void addFieldInfos()
	{	
		setStepTitle("Infos");
				
		String str = "Cet outil permet de tester le cache JPA , son reset et l'impact sur l'opération == entre deux Long<br/>"+
					 "Les deux Long ayant la même valeur mais provenant avant et aprés le reset du cache JPA";
		
		addLabel(str, ContentMode.HTML);
	}
	
	private void addFieldAffichage1()
	{	
		setStepTitle("Affichage 1");

		String str = "Voici la liste de tous les utilisateurs de la base<br/>";
		
		List<UtilisateurDTO> users = new UtilisateurService().getAllUtilisateurs(false);
		for (UtilisateurDTO utilisateurDTO : users)
		{
			str = str+utilisateurDTO.nom+" "+utilisateurDTO.prenom+" Id = "+utilisateurDTO.id+" @Long id="+ System.identityHashCode(utilisateurDTO.id);
			id = utilisateurDTO.id;
		}
		
		
		
		
		addLabel(str, ContentMode.HTML);
		
	
		
	}
	
	
	private void addFieldReset()
	{	
		setStepTitle("Reset");

		String str = "Un appel au reset du cache JPA puis un garbage collector ont été faits";
		
		new SupervisionService().resetAllDataBaseCache();
		System.gc();
		
		addLabel(str, ContentMode.HTML);
	
	}
	
	
	private void addFieldAffichage2()
	{	
		setStepTitle("Affichage 2");

		String str = "Voici la liste de tous les utilisateurs de la base<br/>";
		
		List<UtilisateurDTO> users = new UtilisateurService().getAllUtilisateurs(false);
		for (UtilisateurDTO utilisateurDTO : users)
		{
			str = str+utilisateurDTO.nom+" "+utilisateurDTO.prenom+" Id = "+utilisateurDTO.id+" @Long id="+ System.identityHashCode(utilisateurDTO.id);
		}
		
		addLabel(str, ContentMode.HTML);
		
	
		
	}
	
	private void addFieldRecherche()
	{	
		setStepTitle("Recherche");

		String str = "On essaye maintenant de retrouver le dernier utilisateur en faisant un == entre deux Longs <br/>";
		
		boolean found = false;
		
		List<UtilisateurDTO> users = new UtilisateurService().getAllUtilisateurs(false);
		for (UtilisateurDTO utilisateurDTO : users)
		{
			if (utilisateurDTO.id==id)
			{
				found = true;
			}
		}
		
		
		if (found)
		{
			str = str+" Trouvé.";
		}
		else
		{
			str = str+" Impossible de retrouver l'élément !!!! ";  // On se retrouve dans ce cas là 
		}
		
		
		addLabel(str, ContentMode.HTML);
		
	
		
	}
	
	
	private void addFieldRecherche2()
	{	
		setStepTitle("Recherche");

		String str = "On fait la même chose avec un equals entre deux Longs <br/>";
		
		boolean found = false;
		
		List<UtilisateurDTO> users = new UtilisateurService().getAllUtilisateurs(false);
		for (UtilisateurDTO utilisateurDTO : users)
		{
			if (utilisateurDTO.id.equals(id))
			{
				found = true;
			}
		}
		
		
		if (found)
		{
			str = str+" Trouvé."; // On se retrouve dans ce cas là 
		}
		else
		{
			str = str+" Impossible de retrouver l'élément !!!! ";  
		}
		
		
		addLabel(str, ContentMode.HTML);
		
	
		
	}
	
	private void addFieldChargement()
	{	
		setStepTitle("Chargement");

		String str = "On essaye maintenant de faire un em.find() <br/>";
		
		str = str + "On obtient " +new DevToolsService().loadOneUtilisateur(id);   // Ca fonctionne normalement 
		
		addLabel(str, ContentMode.HTML);

	}
	
	


	@Override
	protected void performSauvegarder()
	{
	}

	@Override
	protected Class getEnumClass()
	{
		return Step.class;
	}
}
