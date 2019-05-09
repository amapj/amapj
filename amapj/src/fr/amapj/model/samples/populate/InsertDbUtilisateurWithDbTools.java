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
 package fr.amapj.model.samples.populate;

import javax.persistence.EntityManager;

import fr.amapj.model.engine.tools.TestTools;
import fr.amapj.model.engine.transaction.DbWrite;
import fr.amapj.model.engine.transaction.TransactionHelper;
import fr.amapj.model.models.fichierbase.Utilisateur;

/**
 * Cette classe permet de créer des utilisateur dans la base de données
 *
 */
public class InsertDbUtilisateurWithDbTools
{
	
	@DbWrite
	public void createData()
	{
		EntityManager em = TransactionHelper.getEm();
		

		Utilisateur u = new Utilisateur();
		u.setNom("nom_c");
		u.setPrenom("prenom_c");
		u.setEmail("c");
		
		
		em.persist(u);
		
		u = new Utilisateur();
		u.setNom("nom_d");
		u.setPrenom("prenom_d");
		u.setEmail("d");
		
		em.persist(u);
		
		
	}

	public static void main(String[] args)
	{
		TestTools.init();
		
		InsertDbUtilisateurWithDbTools insertDbRole = new InsertDbUtilisateurWithDbTools();
		System.out.println("Debut de l'insertion des données");
		insertDbRole.createData();
		System.out.println("Fin de l'insertion des données");

	}

}
