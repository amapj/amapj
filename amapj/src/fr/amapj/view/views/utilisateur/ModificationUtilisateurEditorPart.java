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
 package fr.amapj.view.views.utilisateur;

import com.vaadin.data.util.BeanItem;

import fr.amapj.service.services.utilisateur.UtilisateurDTO;
import fr.amapj.service.services.utilisateur.UtilisateurService;
import fr.amapj.view.engine.popup.formpopup.FormPopup;
import fr.amapj.view.engine.popup.formpopup.OnSaveException;
import fr.amapj.view.views.importdonnees.UtilisateurImporter;

/**
 * Permet de modifier les utilisateurs
 */
public class ModificationUtilisateurEditorPart extends FormPopup
{

	private UtilisateurDTO utilisateurDTO;



	/**
	 * 
	 */
	public ModificationUtilisateurEditorPart(Long idUtilisateur)
	{
		setWidth(80);
		popupTitle = "Modification d'un utilisateur";

		this.utilisateurDTO = new UtilisateurService().loadUtilisateurDto(idUtilisateur);
		item = new BeanItem<UtilisateurDTO>(utilisateurDTO);

	}
	
	@Override
	protected void addFields()
	{
		// Champ 1
		addTextField("Nom", "nom");

		// Champ 2
		addTextField("Prenom", "prenom");

		// Champ 3
		addTextField("E mail", "email");
		
		// Champ 4
		addTextField("Téléphone 1", "numTel1");
		
		// Champ 5
		addTextField("Téléphone 2", "numTel2");

		// Champ 6
		addTextField("Adresse", "libAdr1");
		
		// Champ 7
		addTextField("Code postal", "codePostal");
		
		// Champ 8
		addTextField("Ville", "ville");

	}

	

	@Override
	protected void performSauvegarder() throws OnSaveException
	{
		// Verification
		new UtilisateurImporter().checkThisElementAsException(utilisateurDTO);
		
		// Sauvegarde
		new UtilisateurService().updateUtilisateur(utilisateurDTO);
		
	}

	
}
